package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.TrainCarriageQueryReq;
import com.example.business.req.TrainCarriageSaveReq;
import com.example.business.resp.TrainCarriageQueryResp;

public interface TrainCarriageService {

    void save(TrainCarriageSaveReq req);

    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    void delete(Long id);

}