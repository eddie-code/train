package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.StationQueryReq;
import com.example.business.req.StationSaveReq;
import com.example.business.resp.StationQueryResp;

public interface StationService {

    void save(StationSaveReq req);

    PageResp<StationQueryResp> queryList(StationQueryReq req);

    void delete(Long id);

}