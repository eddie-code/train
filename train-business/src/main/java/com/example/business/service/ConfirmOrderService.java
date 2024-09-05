package com.example.business.service;

import com.example.business.dto.ConfirmOrderMQDto;
import com.example.common.resp.PageResp;
import com.example.business.req.ConfirmOrderQueryReq;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.resp.ConfirmOrderQueryResp;

public interface ConfirmOrderService {

    void save(ConfirmOrderDoReq req);

    PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req);

    void delete(Long id);

    void doConfirm(ConfirmOrderMQDto dto);

}