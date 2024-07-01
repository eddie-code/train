package com.example.member.service;

import com.example.common.resp.PageResp;
import com.example.member.req.PassengerQueryReq;
import com.example.member.req.PassengerSaveReq;
import com.example.member.resp.PassengerQueryResp;

import java.util.List;

public interface PassengerService {

    void save(PassengerSaveReq req);

    PageResp<PassengerQueryResp> queryList(PassengerQueryReq req);

    void delete(Long id);

    List<PassengerQueryResp> queryMine();
}