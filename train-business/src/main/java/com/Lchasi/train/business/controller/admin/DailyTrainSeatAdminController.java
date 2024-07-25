package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.business.req.DailyTrainSeatQueryReq;
import com.Lchasi.train.business.req.DailyTrainSeatSaveReq;
import com.Lchasi.train.business.resp.DailyTrainSeatQueryResp;
import com.Lchasi.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody DailyTrainSeatSaveReq dailyTrainSeatSaveReq) {
        dailyTrainSeatService.save(dailyTrainSeatSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> dailyTrainSeatQueryList(@Valid DailyTrainSeatQueryReq req) {
        PageResp<DailyTrainSeatQueryResp> list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }
}