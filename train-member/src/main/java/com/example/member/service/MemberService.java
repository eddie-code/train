package com.example.member.service;

import com.example.member.req.MemberLoginReq;
import com.example.member.req.MemberRegisterReq;
import com.example.member.req.MemberSendCodeReq;
import com.example.member.resp.MemberLoginResp;

/**
 * @author lee
 * @description
 */
public interface MemberService {

    int count();

    long register(MemberRegisterReq req);

    void sendCode(MemberSendCodeReq req);

    MemberLoginResp login(MemberLoginReq req);

}
