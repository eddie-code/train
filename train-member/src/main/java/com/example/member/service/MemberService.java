package com.example.member.service;

import com.example.member.req.MemberRegisterReq;

/**
 * @author lee
 * @description
 */
public interface MemberService {

    int count();

    long register(MemberRegisterReq req);

}
