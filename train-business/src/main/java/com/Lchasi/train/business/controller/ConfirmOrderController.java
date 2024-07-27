package com.Lchasi.train.business.controller;

import com.Lchasi.train.business.req.ConfirmOrderDoReq;
import com.Lchasi.train.business.service.ConfirmOrderService;
import com.Lchasi.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    //注册接口
    @PostMapping("/do")
    public CommonResp<Long> doConfirm(@Valid @RequestBody ConfirmOrderDoReq confirmOrderSaveReq) {
        confirmOrderService.doConfirm(confirmOrderSaveReq);
        return new CommonResp<>();
    }

}