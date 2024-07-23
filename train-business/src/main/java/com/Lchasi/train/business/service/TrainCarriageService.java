package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.business.domain.TrainCarriage;
import com.Lchasi.train.business.domain.TrainCarriageExample;
import com.Lchasi.train.business.mapper.TrainCarriageMapper;
import com.Lchasi.train.business.req.TrainCarriageQueryReq;
import com.Lchasi.train.business.req.TrainCarriageSaveReq;
import com.Lchasi.train.business.resp.TrainCarriageQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainCarriageService {

    @Autowired
    private TrainCarriageMapper trainCarriageMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param trainCarriageSaveReq
     */
    public void save(TrainCarriageSaveReq trainCarriageSaveReq) {
        DateTime now = DateTime.now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveReq, TrainCarriage.class);
        if(ObjectUtil.isNull(trainCarriage.getId())) {//为空则新增
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        }else {//修改信息
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("id desc");//格局id倒序
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<TrainCarriage> list = trainCarriageMapper.selectByExample(trainCarriageExample);
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<TrainCarriageQueryResp> list1 = BeanUtil.copyToList(list, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }
}