package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.business.domain.DailyTrainSeat;
import com.Lchasi.train.business.domain.DailyTrainSeatExample;
import com.Lchasi.train.business.mapper.DailyTrainSeatMapper;
import com.Lchasi.train.business.req.DailyTrainSeatQueryReq;
import com.Lchasi.train.business.req.DailyTrainSeatSaveReq;
import com.Lchasi.train.business.resp.DailyTrainSeatQueryResp;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DailyTrainSeatService {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param dailyTrainSeatSaveReq
     */
    public void save(DailyTrainSeatSaveReq dailyTrainSeatSaveReq) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(dailyTrainSeatSaveReq, DailyTrainSeat.class);
        if(ObjectUtil.isNull(dailyTrainSeat.getId())) {//为空则新增
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }else {//修改信息
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("train_code asc, carriage_index asc,carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<DailyTrainSeat> list = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainSeatQueryResp> list1 = BeanUtil.copyToList(list, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }
}