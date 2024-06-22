package com.Lchasi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
import com.Lchasi.train.member.domain.Member;
import com.Lchasi.train.member.domain.MemberExample;
import com.Lchasi.train.member.mapper.MemberMapper;
import com.Lchasi.train.member.req.MemberRegisterReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return (int) memberMapper.countByExample(null);//更具条件去查询
    }

    /**
     * 手机号注册以及登录功能
     * @param memberRegisterReq
     * @return
     */
    public long register(MemberRegisterReq memberRegisterReq){
        String mobile = memberRegisterReq.getMobile();
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);//创建条件
        List<Member> list = memberMapper.selectByExample(example);

        if(CollUtil.isNotEmpty(list)){
            //已经有人注册
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
