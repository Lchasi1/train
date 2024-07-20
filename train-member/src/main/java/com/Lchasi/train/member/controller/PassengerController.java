package com.Lchasi.train.member.controller;

import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.member.req.PassengerQueryReq;
import com.Lchasi.train.member.req.PassengerSaveReq;
import com.Lchasi.train.member.resp.PassengerQueryResp;
import com.Lchasi.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<List<PassengerQueryResp>> passengerQueryList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());//从token中获取
        List<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }
}
