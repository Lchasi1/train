package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.business.domain.ConfirmOrder;
import com.Lchasi.train.business.domain.ConfirmOrderExample;
import com.Lchasi.train.business.mapper.ConfirmOrderMapper;
import com.Lchasi.train.business.req.ConfirmOrderQueryReq;
import com.Lchasi.train.business.req.ConfirmOrderSaveReq;
import com.Lchasi.train.business.resp.ConfirmOrderQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ConfirmOrderService {

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param confirmOrderSaveReq
     */
    public void save(ConfirmOrderSaveReq confirmOrderSaveReq) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveReq, ConfirmOrder.class);
        if(ObjectUtil.isNull(confirmOrder.getId())) {//为空则新增
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        }else {//修改信息
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
     * @param id
     */
    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }
}