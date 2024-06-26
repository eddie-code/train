package com.example.business.service;

import com.example.business.domain.TrainStation;
import com.example.common.resp.PageResp;
import com.example.business.req.TrainStationQueryReq;
import com.example.business.req.TrainStationSaveReq;
import com.example.business.resp.TrainStationQueryResp;

import java.util.List;

public interface TrainStationService {

    void save(TrainStationSaveReq req);

    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req);

    void delete(Long id);

    List<TrainStation> selectByTrainCode(String trainCode);
}