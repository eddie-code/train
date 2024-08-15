package com.example.business.service;

import com.example.business.domain.DailyTrainSeat;
import com.example.common.resp.PageResp;
import com.example.business.req.DailyTrainSeatQueryReq;
import com.example.business.req.DailyTrainSeatSaveReq;
import com.example.business.resp.DailyTrainSeatQueryResp;

import java.util.Date;
import java.util.List;

public interface DailyTrainSeatService {

    void save(DailyTrainSeatSaveReq req);

    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String code);

    int countSeat(Date date, String trainCode, String code);

    List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer index);

    int countSeat(Date date, String trainCode);
}