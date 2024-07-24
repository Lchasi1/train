package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.business.req.TrainStationQueryReq;
import com.Lchasi.train.business.req.TrainStationSaveReq;
import com.Lchasi.train.business.resp.TrainStationQueryResp;
import com.Lchasi.train.business.service.TrainStationService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService trainStationService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody TrainStationSaveReq trainStationSaveReq) {
        trainStationService.save(trainStationSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> trainStationQueryList(@Valid TrainStationQueryReq req) {
        PageResp<TrainStationQueryResp> list = trainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }
}