package com.example.${module}.controller;

import com.example.common.context.LoginMemberContext;
import com.example.common.resp.CommonResp;
import com.example.common.resp.PageResp;
import com.example.${module}.req.${Domain}QueryReq;
import com.example.${module}.req.${Domain}SaveReq;
import com.example.${module}.resp.${Domain}QueryResp;
import com.example.${module}.service.${Domain}Service;
<#--import jakarta.annotation.Resource;-->
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {

    @Autowired
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
        ${domain}Service.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req) {
<#--        // 线程本地变量获取会员ID-->
<#--        req.setMemberId(LoginMemberContext.getId());-->
        PageResp<${Domain}QueryResp> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }

}
