package com.example.business.service;

import com.example.common.resp.PageResp;
import com.example.business.req.ConfirmOrderQueryReq;
import com.example.business.req.ConfirmOrderSaveReq;
import com.example.business.resp.ConfirmOrderQueryResp;

public interface ConfirmOrderService {

    void save(ConfirmOrderSaveReq req);

    PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req);

    void delete(Long id);

}