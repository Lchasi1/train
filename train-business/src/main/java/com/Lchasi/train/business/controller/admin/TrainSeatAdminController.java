package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.business.req.TrainSeatQueryReq;
import com.Lchasi.train.business.req.TrainSeatSaveReq;
import com.Lchasi.train.business.resp.TrainSeatQueryResp;
import com.Lchasi.train.business.service.TrainSeatService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Resource
    private TrainSeatService trainSeatService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody TrainSeatSaveReq trainSeatSaveReq) {
        trainSeatService.save(trainSeatSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> trainSeatQueryList(@Valid TrainSeatQueryReq req) {
        PageResp<TrainSeatQueryResp> list = trainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }
}