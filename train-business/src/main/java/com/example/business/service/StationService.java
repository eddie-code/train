package com.example.business.service;

import com.example.business.req.StationQueryReq;
import com.example.business.req.StationSaveReq;
import com.example.business.resp.StationQueryResp;
import com.example.common.resp.PageResp;

import java.util.List;

public interface StationService {

    void save(StationSaveReq req);

    PageResp<StationQueryResp> queryList(StationQueryReq req);

    void delete(Long id);

    List<StationQueryResp> queryAll();

}