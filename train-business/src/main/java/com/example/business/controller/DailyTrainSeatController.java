package com.example.business.controller;

import com.example.common.context.LoginMemberContext;
import com.example.common.resp.CommonResp;
import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainSeatQueryReq;
import com.example.business.req.DailyTrainSeatSaveReq;
import com.example.business.resp.DailyTrainSeatQueryResp;
import com.example.business.service.DailyTrainSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-seat")
public class DailyTrainSeatController {

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        PageResp<DailyTrainSeatQueryResp> list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }

}
