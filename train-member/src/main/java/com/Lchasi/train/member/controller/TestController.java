package com.Lchasi.train.member.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return String.format("Hello %s!");
    }
}
