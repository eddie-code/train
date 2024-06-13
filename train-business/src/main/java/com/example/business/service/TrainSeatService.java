package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.TrainSeatQueryReq;
import com.example.business.req.TrainSeatSaveReq;
import com.example.business.resp.TrainSeatQueryResp;

public interface TrainSeatService {

    void save(TrainSeatSaveReq req);

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    void delete(Long id);

}