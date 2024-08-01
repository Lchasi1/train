package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.Lchasi.train.business.domain.*;
import com.Lchasi.train.business.enums.ConfirmOrderStatusEnum;
import com.Lchasi.train.business.enums.SeatColEnum;
import com.Lchasi.train.business.enums.SeatTypeEnum;
import com.Lchasi.train.business.mapper.ConfirmOrderMapper;
import com.Lchasi.train.business.req.ConfirmOrderDoReq;
import com.Lchasi.train.business.req.ConfirmOrderQueryReq;
import com.Lchasi.train.business.req.ConfirmOrderTicketReq;
import com.Lchasi.train.business.resp.ConfirmOrderQueryResp;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ConfirmOrderService {

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void save(ConfirmOrderDoReq confirmOrderSaveReq) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveReq, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {//为空则新增
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {//修改信息
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");//格局id倒序
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<ConfirmOrder> list = confirmOrderMapper.selectByExample(confirmOrderExample);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list1 = BeanUtil.copyToList(list, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public void doConfirm(ConfirmOrderDoReq req) {
        //省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有限期内，tickets条数>0，同乘客同车次是否已买过
        String lockKey = req.getDate()+"-"+req.getTrainCode();//以同一天同一车次的票作为key
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);//判断key是否存在，如果存在则失败，不存在则放入

            if (setIfAbsent) {
                log.info("拿到锁");
            }else{
                //只是每抢到锁，并不知道票抢完了没，所以提示稍后再试
                log.info("没有拿到锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }
        try {
            //保存确认订单表，状态初始
            DateTime date = DateTime.now();
            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(date);
            confirmOrder.setUpdateTime(date);
            confirmOrder.setMemberId(LoginMemberContext.getId());
            String trainCode = req.getTrainCode();
            String start = req.getStart();
            String end = req.getEnd();
            List<ConfirmOrderTicketReq> tickets = req.getTickets();
            confirmOrder.setDate(req.getDate());
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setTickets(JSON.toJSONString(tickets));

            confirmOrderMapper.insert(confirmOrder);
            //查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            log.info("查出余票记录：{}", dailyTrainTicket);
            //扣减余票数量，并判断余票是否足够
            reduceTickets(req,dailyTrainTicket);

            // 最终的选座结果
            List<DailyTrainSeat> finalSeatList = new ArrayList<>();
            //计算相对第一个座位的偏移量

            //比如选择的是C1.D2则偏移量是：[0,5]
            //比如选择的是A1,B1,C1则偏移量是：[0,1,2]
            ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
            if(StrUtil.isNotBlank(ticketReq0.getSeat())){
                log.info("本次购票有选座");
                //查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个作为的偏移量
                List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                log.info("本次选座的座位类型包含的列：{}", colEnumList);

                // 组成和前端两排选座一样的列表，用于作参照的座位列表，例：referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
                List<String> referSeatList = new ArrayList<>();
                for (int i = 1; i <= 2; i++) {
                    for (SeatColEnum seatColEnum : colEnumList) {
                        referSeatList.add(seatColEnum.getCode() + i);
                    }
                }
                log.info("用于作参照的两排座位：{}", referSeatList);

                List<Integer> offsetList = new ArrayList<>();
                // 绝对偏移值，即：在参照座位列表中的位置
                List<Integer> aboluteOffsetList = new ArrayList<>();
                for (ConfirmOrderTicketReq ticketReq : tickets) {
                    int index = referSeatList.indexOf(ticketReq.getSeat());
                    aboluteOffsetList.add(index);
                }
                log.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);
                for (Integer index : aboluteOffsetList) {
                    int offset = index - aboluteOffsetList.get(0);
                    offsetList.add(offset);
                }
                log.info("计算得到所有座位的相对第一个座位的偏移值：{}", offsetList);

                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0], // 从A1得到A
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );

            }else{
                log.info("本次购票没有选座");
                for (ConfirmOrderTicketReq ticketReq : tickets) {
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticketReq.getSeatTypeCode(),
                            null,
                            null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    );
                }
            }
            log.info("最终选座：{}", finalSeatList);

            // 选中座位后事务处理：
            // 座位表修改售卖情况sell；
            // 余票详情表修改余票；
            // 为会员增加购票记录
            // 更新确认订单为成功
            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
            } catch (Exception e) {
                log.error("保存购票信息失败", e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
        }  finally {
            //删除分布式锁
            log.info("购票流程结束，释放锁");
            redisTemplate.delete(lockKey);
        }

    }
    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }

    /**
     * 挑座位，如果有选座，则一次性挑完，如果无选座，则一个一个挑
     * @param finalSeatList
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offsetList
     * @param startIndex
     * @param endIndex
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        log.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            log.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            log.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList){
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    log.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column，有值的话要比对列号
                if (StrUtil.isBlank(column)) {
                    log.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        log.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    log.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    log.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            log.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            log.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            log.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }
    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001，本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     *
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位与，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            log.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            log.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            log.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;

        }
    }

}