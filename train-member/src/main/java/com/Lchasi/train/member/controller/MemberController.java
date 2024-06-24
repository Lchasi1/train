package com.Lchasi.train.member.controller;

import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.member.req.MemberLoginReq;
import com.Lchasi.train.member.req.MemberRegisterReq;
import com.Lchasi.train.member.req.MemberSendCodeReq;
import com.Lchasi.train.member.resp.MemberLoginResp;
import com.Lchasi.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count() {
        int count = memberService.count();
        CommonResp<Integer> commonResp = new CommonResp<>();
        commonResp.setContent(count);
        return commonResp;
    }

    //注册接口
    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq memberRegisterReq) {
        long register = memberService.register(memberRegisterReq);
        /*CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setContent(register);
        return commonResp;*/
        return new CommonResp<>(register);
    }

    /**
     * 发送短信验证码
     *
     * @param MemberSendCodeReq
     * @return
     */
    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq MemberSendCodeReq) {
        memberService.sendCode(MemberSendCodeReq);
        return new CommonResp<>();
    }

    /**
     * 登录
     * @param memberLoginReq
     * @return
     */
    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid MemberLoginReq memberLoginReq) {
        MemberLoginResp memberLoginResp = memberService.login(memberLoginReq);
        return new CommonResp<>(memberLoginResp);
    }

}
