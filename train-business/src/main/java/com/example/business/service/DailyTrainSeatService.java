package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainSeatQueryReq;
import com.example.business.req.DailyTrainSeatSaveReq;
import com.example.business.resp.DailyTrainSeatQueryResp;

public interface DailyTrainSeatService {

    void save(DailyTrainSeatSaveReq req);

    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    void delete(Long id);

}