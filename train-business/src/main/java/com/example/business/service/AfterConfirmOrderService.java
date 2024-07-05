package com.example.business.service;

import com.example.business.domain.DailyTrainSeat;
import com.example.business.domain.DailyTrainTicket;

import java.util.List;

/**
 * @author lee
 * @description
 */
public interface AfterConfirmOrderService {

    void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> fianlSeatList);

}
