package com.example.member.service;

import com.example.common.resp.PageResp;
import com.example.common.req.MemberTicketReq;
import com.example.member.req.TicketQueryReq;
import com.example.member.resp.TicketQueryResp;

public interface TicketService {

    /**
     * 会员购买车票后新增保存
     *
     * @param req
     */
    void save(MemberTicketReq req);

    PageResp<TicketQueryResp> queryList(TicketQueryReq req);

    void delete(Long id);

}