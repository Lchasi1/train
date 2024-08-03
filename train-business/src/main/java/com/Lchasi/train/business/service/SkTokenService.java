package com.Lchasi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.business.domain.SkToken;
import com.Lchasi.train.business.domain.SkTokenExample;
import com.Lchasi.train.business.mapper.SkTokenMapper;
import com.Lchasi.train.business.req.SkTokenQueryReq;
import com.Lchasi.train.business.req.SkTokenSaveReq;
import com.Lchasi.train.business.resp.SkTokenQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SkTokenService {

    @Autowired
    private SkTokenMapper skTokenMapper;

    /**
     * 会员端保存信息，以及注册的更改信息
     *
     * @param skTokenSaveReq
     */
    public void save(SkTokenSaveReq skTokenSaveReq) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(skTokenSaveReq, SkToken.class);
        if(ObjectUtil.isNull(skToken.getId())) {//为空则新增
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        }else {//修改信息
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
     * @param id
     */
    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }
}