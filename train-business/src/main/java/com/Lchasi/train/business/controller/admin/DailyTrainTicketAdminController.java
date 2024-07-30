package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.business.req.DailyTrainTicketQueryReq;
import com.Lchasi.train.business.req.DailyTrainTicketSaveReq;
import com.Lchasi.train.business.resp.DailyTrainTicketQueryResp;
import com.Lchasi.train.business.service.DailyTrainTicketService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        dailyTrainTicketService.save(dailyTrainTicketSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> dailyTrainTicketQueryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }
    @GetMapping("/query-list2")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> dailyTrainTicketQueryList2(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList2(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }
}