package com.Lchasi.train.member.controller;

import com.Lchasi.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public String count() {
        return "" + memberService.count();
    }

    //注册接口
    @PostMapping("/register")
    public long register(String mobile) {
        return memberService.register(mobile);
    }

}
