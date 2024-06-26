package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainCarriageQueryReq;
import com.example.business.req.DailyTrainCarriageSaveReq;
import com.example.business.resp.DailyTrainCarriageQueryResp;

import java.util.Date;

public interface DailyTrainCarriageService {

    void save(DailyTrainCarriageSaveReq req);

    PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String code);
}