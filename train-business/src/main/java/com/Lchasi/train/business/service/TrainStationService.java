package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.business.domain.TrainStation;
import com.Lchasi.train.business.domain.TrainStationExample;
import com.Lchasi.train.business.mapper.TrainStationMapper;
import com.Lchasi.train.business.req.TrainStationQueryReq;
import com.Lchasi.train.business.req.TrainStationSaveReq;
import com.Lchasi.train.business.resp.TrainStationQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainStationService {

    @Autowired
    private TrainStationMapper trainStationMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param trainStationSaveReq
     */
    public void save(TrainStationSaveReq trainStationSaveReq) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(trainStationSaveReq, TrainStation.class);
        if(ObjectUtil.isNull(trainStation.getId())) {//为空则新增
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        }else {//修改信息
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("id desc");//格局id倒序
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<TrainStation> list = trainStationMapper.selectByExample(trainStationExample);
        PageInfo<TrainStation> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<TrainStationQueryResp> list1 = BeanUtil.copyToList(list, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }
}