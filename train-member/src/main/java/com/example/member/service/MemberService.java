package com.example.member.service;

import com.example.member.req.MemberRegisterReq;
import com.example.member.req.MemberSendCodeReq;

/**
 * @author lee
 * @description
 */
public interface MemberService {

    int count();

    long register(MemberRegisterReq req);

    void sendCode(MemberSendCodeReq req);

}
