package com.Lchasi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.member.domain.Member;
import com.Lchasi.train.member.domain.MemberExample;
import com.Lchasi.train.member.mapper.MemberMapper;
import com.Lchasi.train.member.req.MemberRegisterReq;
import com.Lchasi.train.member.req.MemberSendCodeReq;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return (int) memberMapper.countByExample(null);//更具条件去查询
    }

    /**
     * 手机号注册以及登录功能
     *
     * @param memberRegisterReq
     * @return
     */
    public long register(MemberRegisterReq memberRegisterReq) {
        String mobile = memberRegisterReq.getMobile();
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);//创建条件
        List<Member> list = memberMapper.selectByExample(example);

        if (CollUtil.isNotEmpty(list)) {
            //已经有人注册
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());//获得雪花算法的对象，并且指定当前机器，当前位置
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    public void sendCode(MemberSendCodeReq memberSendCodeReq) {
        String mobile = memberSendCodeReq.getMobile();
        //创建sql语句
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(example);

        //如果手机号不存在则插入记录
        if (CollUtil.isEmpty(list)) {
            log.info("手机号不存在，插入记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else{
            log.info("手机号存在，生成短信验证码");
        }

        //生产验证码
        String code = RandomUtil.randomString(4);
        code = "1111";//便于测试
        log.info("生成短信验证码：" + code);
        //保存短信记录表，手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        log.info("保存短信记录表");
        //对接短信通道，发送短信
        log.info("对接短信通道，发送短信");
    }
}
