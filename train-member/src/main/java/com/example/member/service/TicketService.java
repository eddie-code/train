package com.example.member.service;

import com.example.common.resp.PageResp;
import com.example.member.req.TicketQueryReq;
import com.example.member.req.TicketSaveReq;
import com.example.member.resp.TicketQueryResp;

public interface TicketService {

    void save(TicketSaveReq req);

    PageResp<TicketQueryResp> queryList(TicketQueryReq req);

    void delete(Long id);

}