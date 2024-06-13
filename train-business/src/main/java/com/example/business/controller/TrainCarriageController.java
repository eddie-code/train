package com.example.business.controller;

import com.example.common.context.LoginMemberContext;
import com.example.common.resp.CommonResp;
import com.example.common.resp.PageResp;
import com.example.business.req.TrainCarriageQueryReq;
import com.example.business.req.TrainCarriageSaveReq;
import com.example.business.resp.TrainCarriageQueryResp;
import com.example.business.service.TrainCarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/train-carriage")
public class TrainCarriageController {

    @Autowired
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
