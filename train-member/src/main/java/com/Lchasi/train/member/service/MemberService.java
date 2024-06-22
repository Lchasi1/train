package com.Lchasi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.Lchasi.train.member.domain.Member;
import com.Lchasi.train.member.domain.MemberExample;
import com.Lchasi.train.member.mapper.MemberMapper;
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
     * @param mobile
     * @return
     */
    public long register(String mobile){
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);//创建条件
        List<Member> list = memberMapper.selectByExample(example);

        if(CollUtil.isNotEmpty(list)){
            //已经有人注册
            throw new RuntimeException("手机号已被注册");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
