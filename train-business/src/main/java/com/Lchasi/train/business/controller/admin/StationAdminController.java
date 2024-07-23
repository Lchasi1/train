package com.Lchasi.train.business.controller.admin;

import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.business.req.StationQueryReq;
import com.Lchasi.train.business.req.StationSaveReq;
import com.Lchasi.train.business.resp.StationQueryResp;
import com.Lchasi.train.business.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody StationSaveReq stationSaveReq) {
        stationService.save(stationSaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> stationQueryList(@Valid StationQueryReq req) {
        PageResp<StationQueryResp> list = stationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }
}