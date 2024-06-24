package com.Lchasi.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.Lchasi.train.common.exception.BusinessException;
import com.Lchasi.train.common.exception.BusinessExceptionEnum;
import com.Lchasi.train.common.util.SnowUtil;
import com.Lchasi.train.member.domain.Member;
import com.Lchasi.train.member.domain.MemberExample;
import com.Lchasi.train.member.mapper.MemberMapper;
import com.Lchasi.train.member.req.MemberLoginReq;
import com.Lchasi.train.member.req.MemberRegisterReq;
import com.Lchasi.train.member.req.MemberSendCodeReq;
import com.Lchasi.train.member.resp.MemberLoginResp;
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
        Member memberDB = selectMobile(mobile);

        if (ObjectUtil.isNotEmpty(memberDB)) {
            //已经有人注册
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());//获得雪花算法的对象，并且指定当前机器，当前位置
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    /**
     * 发送验证码
     * @param memberSendCodeReq
     */
    public void sendCode(MemberSendCodeReq memberSendCodeReq) {
        String mobile = memberSendCodeReq.getMobile();
        //创建sql语句
        Member memberDB = selectMobile(mobile);

        //如果手机号不存在则插入记录
        if (ObjectUtil.isEmpty(memberDB)) {
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

    /**
     * 登录
     * @param memberLoginReq
     */
    public MemberLoginResp login(MemberLoginReq memberLoginReq) {
        String mobile = memberLoginReq.getMobile();
        String code = memberLoginReq.getCode();
        //创建sql语句
        Member memberDB = selectMobile(mobile);

        if (ObjectUtil.isEmpty(memberDB)) {
           //为空则不对，抛出异常
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        //校验短信验证码，可放置在数据库中
        if(!code.equals("1111")){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_NOT_ERROR);
        }
        return BeanUtil.copyProperties(memberDB, MemberLoginResp.class);

    }

    private Member selectMobile(String mobile) {
        MemberExample example = new MemberExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(example);
        if (CollUtil.isEmpty(list)) {
            return null;
        }else {
            return list.get(0);
        }

    }
}
