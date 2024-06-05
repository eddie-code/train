package com.example.member.service;

import com.example.member.req.PassengerQueryReq;
import com.example.member.req.PassengerSaveReq;
import com.example.member.resp.PassengerQueryResp;

import java.util.List;

/**
 * @author lee
 * @description
 */
public interface PassengerService {

    void save(PassengerSaveReq req);

    List<PassengerQueryResp> queryList(PassengerQueryReq resp);

}
