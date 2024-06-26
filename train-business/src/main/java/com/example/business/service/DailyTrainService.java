package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainQueryReq;
import com.example.business.req.DailyTrainSaveReq;
import com.example.business.resp.DailyTrainQueryResp;

import java.util.Date;

public interface DailyTrainService {

    void save(DailyTrainSaveReq req);

    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    void delete(Long id);

    /**
     * 生成某日所有车次信息，包括车次、车站、车厢、座位
     * @param date
     */
    void genDaily(Date date);
}