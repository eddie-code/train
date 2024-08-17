package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.SkTokenQueryReq;
import com.example.business.req.SkTokenSaveReq;
import com.example.business.resp.SkTokenQueryResp;

import java.util.Date;

public interface SkTokenService {

    void save(SkTokenSaveReq req);

    PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String code);

    boolean validSkToken(Date date, String trainCode, Long memberId);

}