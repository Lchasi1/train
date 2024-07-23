package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.business.req.TrainQueryReq;
import com.Lchasi.train.business.req.TrainSaveReq;
import com.Lchasi.train.business.resp.TrainQueryResp;
import com.Lchasi.train.business.service.TrainSeatService;
import com.Lchasi.train.business.service.TrainService;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @Resource
    private TrainSeatService trainSeatService;
    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody TrainSaveReq trainSaveReq) {
        trainService.save(trainSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> trainQueryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> list = trainService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> trainQueryList() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

    /**
     * 生产座位
     * @param trainCode
     * @return
     */
    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genSeat(trainCode);
        return new CommonResp<>();
    }
}