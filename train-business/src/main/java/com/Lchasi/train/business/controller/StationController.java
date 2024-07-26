package com.Lchasi.train.business.controller;

import com.Lchasi.train.business.resp.StationQueryResp;
import com.Lchasi.train.business.service.StationService;
import com.Lchasi.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> stationQueryList() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }
}