package com.Lchasi.train.business.controller;

import com.Lchasi.train.business.resp.TrainQueryResp;
import com.Lchasi.train.business.service.TrainService;
import com.Lchasi.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;


    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> trainQueryList() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

}