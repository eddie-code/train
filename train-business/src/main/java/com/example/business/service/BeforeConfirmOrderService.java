package com.example.business.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.business.req.ConfirmOrderDoReq;

/**
 * @author lee
 * @description
 */
public interface BeforeConfirmOrderService {

    Long beforeDoConfirm(ConfirmOrderDoReq req);

    void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e);

}
