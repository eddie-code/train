package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.TrainQueryReq;
import com.example.business.req.TrainSaveReq;
import com.example.business.resp.TrainQueryResp;

import java.util.List;

public interface TrainService {

    void save(TrainSaveReq req);

    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    void delete(Long id);

    List<TrainQueryResp> queryAll();

}