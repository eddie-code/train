package com.example.member.controller;

import com.example.common.context.LoginMemberContext;
import com.example.common.resp.CommonResp;
import com.example.member.req.PassengerQueryReq;
import com.example.member.req.PassengerSaveReq;
import com.example.member.resp.PassengerQueryResp;
import com.example.member.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lee
 * @description
 */
@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;


    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<List<PassengerQueryResp>> queryList(@Valid PassengerQueryReq req) {
        // 线程本地变量获取会员ID
        req.setMemberId(LoginMemberContext.getId());
        List<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }

}
