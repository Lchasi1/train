package com.Lchasi.train.member.controller;

import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.member.req.PassengerSaveReq;
import com.Lchasi.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.save(passengerSaveReq);
        return new CommonResp<>();
    }
}
