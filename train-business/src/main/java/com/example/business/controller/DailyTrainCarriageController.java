package com.example.business.controller;

import com.example.common.context.LoginMemberContext;
import com.example.common.resp.CommonResp;
import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainCarriageQueryReq;
import com.example.business.req.DailyTrainCarriageSaveReq;
import com.example.business.resp.DailyTrainCarriageQueryResp;
import com.example.business.service.DailyTrainCarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-carriage")
public class DailyTrainCarriageController {

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        dailyTrainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(@Valid DailyTrainCarriageQueryReq req) {
        PageResp<DailyTrainCarriageQueryResp> list = dailyTrainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
