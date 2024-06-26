package com.example.business.service;

import com.example.business.domain.Train;
import com.example.business.req.TrainQueryReq;
import com.example.business.req.TrainSaveReq;
import com.example.business.resp.TrainQueryResp;
import com.example.common.resp.PageResp;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface TrainService {

    void save(TrainSaveReq req);

    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    void delete(Long id);

    List<TrainQueryResp> queryAll();

    List<Train> selectAll();
}