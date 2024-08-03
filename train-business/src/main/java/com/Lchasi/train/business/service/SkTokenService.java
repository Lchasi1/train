package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.business.domain.SkToken;
import com.Lchasi.train.business.domain.SkTokenExample;
import com.Lchasi.train.business.enums.RedisKeyPreEnum;
import com.Lchasi.train.business.mapper.SkTokenMapper;
import com.Lchasi.train.business.mapper.cust.SkTokenMapperCust;
import com.Lchasi.train.business.req.SkTokenQueryReq;
import com.Lchasi.train.business.req.SkTokenSaveReq;
import com.Lchasi.train.business.resp.SkTokenQueryResp;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SkTokenService {

    @Autowired
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 初始化
     */
    public void genDaily(Date date, String trainCode) {
        log.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        log.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        log.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4);
        log.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param skTokenSaveReq
     */
    public void save(SkTokenSaveReq skTokenSaveReq) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(skTokenSaveReq, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {//为空则新增
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {//修改信息
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");//格局id倒序
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<SkToken> list = skTokenMapper.selectByExample(skTokenExample);
        PageInfo<SkToken> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<SkTokenQueryResp> list1 = BeanUtil.copyToList(list, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
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
        skTokenMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取令牌
     *
     * @param date
     * @param trainCode
     * @return
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        log.info("会员[{}] 获取日期[{}] 车次[{}]的令牌开始", memberId, DateUtil.formatDate(date), trainCode);
        //先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌,用来表示[谁能做什么]的一个凭证
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (setIfAbsent) {
            log.info("恭喜，抢到令牌锁了！ lockKey:{}", lockKey);
        } else {
            log.info("没有令牌锁 lockKey:{}", lockKey);
            return false;
        }
        //结合缓存，将redis与mysql结合，减轻数据库压力
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT+"-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object o = redisTemplate.opsForValue().get(skTokenCountKey);
        if(o != null) {//缓存中有数据
            log.info("缓存中有该车次的令牌大闸key：{}", skTokenCountKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey,1);//将缓存中的value减一
            if(count < 0L){
                log.info("获取缓存令牌失败：{}"+skTokenCountKey);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
            }else {
                log.info("获取令牌后，令牌余数：{}"+count);
                redisTemplate.expire(skTokenCountKey, 60, TimeUnit.MINUTES);
                //每5个更新数据库
                if(count % 5 == 0){
                    skTokenMapperCust.decrease(date,trainCode,5);
                }
                return true;
            }
        }else{
            log.info("缓存中没有该车次令牌大闸的key:{}", skTokenCountKey);
            //检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> list = skTokenMapper.selectByExample(skTokenExample);
            if(CollUtil.isNotEmpty(list)){
                log.info("找不到日期[{}]车次[{}]的令牌记录",DateUtil.formatDate(date), trainCode);
                return false;
            }
            SkToken skToken = list.get(0);
            if(skToken.getCount() <= 0){
                log.info("日期[{}]车次[{}]的令牌余量为0",DateUtil.formatDate(date), trainCode);
                return false;
            }
            //令牌还有余量
            //令牌余量减一
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            log.info("将该车次令牌大闸放入缓存中，key:{},count:{}", DateUtil.formatDate(date), count);
            redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }
        //令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程中判断库存，判断令牌肯定比判断库存效率高
//        int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//        if (updateCount > 0) {
//            return true;
//        } else {
//            return false;
//        }
    }
}