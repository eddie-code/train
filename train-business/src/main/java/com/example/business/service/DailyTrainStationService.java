package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainStationQueryReq;
import com.example.business.req.DailyTrainStationSaveReq;
import com.example.business.resp.DailyTrainStationQueryResp;

import java.util.Date;

public interface DailyTrainStationService {

    void save(DailyTrainStationSaveReq req);

    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String code);

    long countByTrainCode(Date date, String trainCode);
}