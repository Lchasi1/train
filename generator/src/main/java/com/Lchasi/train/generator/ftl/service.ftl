package com.Lchasi.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.${module}.domain.${Domain};
import com.Lchasi.train.${module}.domain.${Domain}Example;
import com.Lchasi.train.${module}.mapper.${Domain}Mapper;
import com.Lchasi.train.${module}.req.${Domain}QueryReq;
import com.Lchasi.train.${module}.req.${Domain}SaveReq;
import com.Lchasi.train.${module}.resp.${Domain}QueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ${Domain}Service {

    @Autowired
    private ${Domain}Mapper ${domain}Mapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param ${domain}SaveReq
     */
    public void save(${Domain}SaveReq ${domain}SaveReq) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(passengerSaveReq, ${Domain}.class);
        if(ObjectUtil.isNull(passenger.getId())) {//为空则新增
            ${domain}.setMemberId(LoginMemberContext.getId());
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(passenger);
        }else {//修改信息
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(passenger);
        }


    }

    /**
     * 会员端和控制台端共用同一个接口，控制台端查询所以用户，会员端查看自己，并实现分页功能
     *
     * @param req
     */
    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.setOrderByClause("id desc");//格局id倒序
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());//分页功能，查询第几页 ，几行数据
        List<${Domain}> list = ${domain}Mapper.selectByExample(passengerExample);
        PageInfo<${Domain}> pageInfo = new PageInfo<>(list);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<${Domain}QueryResp> list1 = BeanUtil.copyToList(list, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list1);
        return pageResp;
    }

    /**
     * 根据主键id删除
     * @param id
     */
    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}