package com.Lchasi.train.member.controller;

import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.member.req.MemberRegisterReq;
import com.Lchasi.train.member.service.MemberService;
import jakarta.annotation.Resource;
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
    public CommonResp<Long> register( MemberRegisterReq memberRegisterReq) {
        long register = memberService.register(memberRegisterReq);
        /*CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setContent(register);
        return commonResp;*/
        return new CommonResp<>(register);
    }

}
