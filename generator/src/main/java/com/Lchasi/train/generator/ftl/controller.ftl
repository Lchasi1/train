package com.Lchasi.train.member.controller;

import com.Lchasi.train.common.context.LoginMemberContext;
import com.Lchasi.train.common.resp.CommonResp;
import com.Lchasi.train.common.resp.PageResp;
import com.Lchasi.train.member.req.${Domain}QueryReq;
import com.Lchasi.train.member.req.${Domain}SaveReq;
import com.Lchasi.train.member.resp.${Domain}QueryResp;
import com.Lchasi.train.member.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${domain}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${domain}Service;

    //注册接口
    @PostMapping("/save")
    public CommonResp<Long> save(@Valid @RequestBody ${Domain}SaveReq ${domain}SaveReq) {
        ${domain}Service.save(${domain}SaveReq);
        return new CommonResp<>();
    }

    //url风格多个单词用横线连接并且小写
    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> ${domain}QueryList(@Valid ${Domain}QueryReq req) {
        req.setMemberId(LoginMemberContext.getId());//从token中获取
        PageResp<${Domain}QueryResp> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}