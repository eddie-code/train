package com.example.business.service;

import com.example.business.domain.ConfirmOrder;
import com.example.business.domain.DailyTrainSeat;
import com.example.business.domain.DailyTrainTicket;
import com.example.business.req.ConfirmOrderTicketReq;

import java.util.List;

/**
 * @author lee
 * @description
 */
public interface AfterConfirmOrderService {

    void afterDoConfirm(DailyTrainTicket dailyTrainTicket,
                        List<DailyTrainSeat> fianlSeatList,
                        List<ConfirmOrderTicketReq> tickets,
                        ConfirmOrder confirmOrder);

}
