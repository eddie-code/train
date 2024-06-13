package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.TrainStationQueryReq;
import com.example.business.req.TrainStationSaveReq;
import com.example.business.resp.TrainStationQueryResp;

public interface TrainStationService {

    void save(TrainStationSaveReq req);

    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req);

    void delete(Long id);

}