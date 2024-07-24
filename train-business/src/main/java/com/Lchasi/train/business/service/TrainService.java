package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.business.domain.Train;
import com.Lchasi.train.business.domain.TrainExample;
import com.Lchasi.train.business.mapper.TrainMapper;
import com.Lchasi.train.business.req.TrainQueryReq;
import com.Lchasi.train.business.req.TrainSaveReq;
import com.Lchasi.train.business.resp.TrainQueryResp;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
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
public class TrainService {

    @Autowired
    private TrainMapper trainMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param trainSaveReq
     */
    public void save(TrainSaveReq trainSaveReq) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(trainSaveReq, Train.class);
        if(ObjectUtil.isNull(train.getId())) {//为空则新增
            //保存之前，先效验唯一键是否存在
            Train train1 = selectByUnique(trainSaveReq.getCode());
            if(ObjectUtil.isNotEmpty(train1)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else {//修改信息
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }


    }
    private Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria()
                .andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(trainExample);
        if (ObjectUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");//格局id倒序
        TrainExample.Criteria criteria = trainExample.createCriteria();

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<Train> list = trainMapper.selectByExample(trainExample);
        PageInfo<Train> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<TrainQueryResp> list1 = BeanUtil.copyToList(list, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }

    public List<TrainQueryResp> queryAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");//格局id倒序
        List<Train> list = trainMapper.selectByExample(trainExample);
        return BeanUtil.copyToList(list, TrainQueryResp.class);
    }
}