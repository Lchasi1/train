package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.Lchasi.train.business.domain.TrainCarriage;
import com.Lchasi.train.business.domain.TrainSeat;
import com.Lchasi.train.business.domain.TrainSeatExample;
import com.Lchasi.train.business.enums.SeatColEnum;
import com.Lchasi.train.business.mapper.TrainSeatMapper;
import com.Lchasi.train.business.req.TrainSeatQueryReq;
import com.Lchasi.train.business.req.TrainSeatSaveReq;
import com.Lchasi.train.business.resp.TrainSeatQueryResp;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TrainSeatService {

    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Autowired
    private TrainCarriageService trainCarriageService;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param trainSeatSaveReq
     */
    public void save(TrainSeatSaveReq trainSeatSaveReq) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveReq, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {//为空则新增
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {//修改信息
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("train_code asc, carriage_index asc,carriage_seat_index asc");
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<TrainSeat> list = trainSeatMapper.selectByExample(trainSeatExample);
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<TrainSeatQueryResp> list1 = BeanUtil.copyToList(list, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
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
        trainSeatMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据trainCode自动生产座位
     * @param trainCode
     */
    @Transactional//事务
    public void genSeat(String trainCode) {
        DateTime now = DateTime.now();
        //清空当前车次下所有的座位记录
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.createCriteria().andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);

        //查找当前车次下的所有车厢
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
        log.info("当前车次下的车厢数目：{}", carriageList.size());
        //循环生成每个车厢的座位
        for (TrainCarriage carriage : carriageList) {
            //拿到车厢数据：行数、座位类型（得到列数）
            Integer rowCount = carriage.getRowCount();
            String seatType = carriage.getSeatType();
            int seatIndex = 1;
            //根据车厢的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            log.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);
            //循环行数
            for (int row = 1; row <= rowCount; row++) {
                //循环列数
                for (SeatColEnum seatColEnum : colEnumList) {
                    //构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(carriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), (char) 0,2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }

            }


        }

    }
}