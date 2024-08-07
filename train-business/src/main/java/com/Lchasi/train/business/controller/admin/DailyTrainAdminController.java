package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.business.req.DailyTrainQueryReq;
import com.Lchasi.train.business.req.DailyTrainSaveReq;
import com.Lchasi.train.business.resp.DailyTrainQueryResp;
import com.Lchasi.train.business.service.DailyTrainService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody DailyTrainSaveReq dailyTrainSaveReq) {
        dailyTrainService.save(dailyTrainSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> dailyTrainQueryList(@Valid DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date date) {
            dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }
}