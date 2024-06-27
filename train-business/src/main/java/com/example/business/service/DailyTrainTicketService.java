package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainTicketQueryReq;
import com.example.business.req.DailyTrainTicketSaveReq;
import com.example.business.resp.DailyTrainTicketQueryResp;

public interface DailyTrainTicketService {

    void save(DailyTrainTicketSaveReq req);

    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    void delete(Long id);

}