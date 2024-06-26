package com.example.business.service;

import com.example.business.domain.TrainSeat;
import com.example.common.resp.PageResp;
import com.example.business.req.TrainSeatQueryReq;
import com.example.business.req.TrainSeatSaveReq;
import com.example.business.resp.TrainSeatQueryResp;

import java.util.List;

public interface TrainSeatService {

    void save(TrainSeatSaveReq req);

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    void delete(Long id);

    void genTrainSeat(String trainCode);

    List<TrainSeat> selectByTrainCode(String trainCode);
}