package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainStationQueryReq;
import com.example.business.req.DailyTrainStationSaveReq;
import com.example.business.resp.DailyTrainStationQueryResp;

public interface DailyTrainStationService {

    void save(DailyTrainStationSaveReq req);

    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    void delete(Long id);

}