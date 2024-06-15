package com.example.business.service;

import com.example.business.domain.TrainCarriage;
import com.example.common.resp.PageResp;
import com.example.business.req.TrainCarriageQueryReq;
import com.example.business.req.TrainCarriageSaveReq;
import com.example.business.resp.TrainCarriageQueryResp;

import java.util.List;

public interface TrainCarriageService {

    void save(TrainCarriageSaveReq req);

    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    void delete(Long id);

    List<TrainCarriage> selectByTrainCode(String trainCode);

}