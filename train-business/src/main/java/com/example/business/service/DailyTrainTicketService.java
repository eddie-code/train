package com.example.business.service;

import com.example.business.domain.DailyTrain;
import com.example.business.domain.DailyTrainTicket;
import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainTicketQueryReq;
import com.example.business.req.DailyTrainTicketSaveReq;
import com.example.business.resp.DailyTrainTicketQueryResp;

import java.util.Date;

public interface DailyTrainTicketService {

    void save(DailyTrainTicketSaveReq req);

    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    void delete(Long id);

    void genDaily(DailyTrain dailyTrain, Date date, String code);

    DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end);

    PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req);

//    PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req);
}