package com.Lchasi.train.batch.controller;

import com.Lchasi.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    @Resource
    BusinessFeign businessFeign;

    @GetMapping("/hello")
    public String hello() {
        String hello = businessFeign.hello();
        log.info(hello);
        return "Hello! Batch"+hello;
    }
}
