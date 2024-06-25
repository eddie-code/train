package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainQueryReq;
import com.example.business.req.DailyTrainSaveReq;
import com.example.business.resp.DailyTrainQueryResp;

public interface DailyTrainService {

    void save(DailyTrainSaveReq req);

    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    void delete(Long id);

}