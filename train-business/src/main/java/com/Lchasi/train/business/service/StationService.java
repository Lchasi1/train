package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.business.domain.Station;
import com.Lchasi.train.business.domain.StationExample;
import com.Lchasi.train.business.mapper.StationMapper;
import com.Lchasi.train.business.req.StationQueryReq;
import com.Lchasi.train.business.req.StationSaveReq;
import com.Lchasi.train.business.resp.StationQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StationService {

    @Autowired
    private StationMapper stationMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param stationSaveReq
     */
    public void save(StationSaveReq stationSaveReq) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(stationSaveReq, Station.class);
        if(ObjectUtil.isNull(station.getId())) {//为空则新增
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        }else {//修改信息
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("id desc");//格局id倒序
        StationExample.Criteria criteria = stationExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<Station> list = stationMapper.selectByExample(stationExample);
        PageInfo<Station> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<StationQueryResp> list1 = BeanUtil.copyToList(list, StationQueryResp.class);
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}