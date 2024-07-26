package com.Lchasi.train.business.controller;

import com.Lchasi.train.business.req.DailyTrainTicketQueryReq;
import com.Lchasi.train.business.resp.DailyTrainTicketQueryResp;
import com.Lchasi.train.business.service.DailyTrainTicketService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> dailyTrainTicketQueryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}