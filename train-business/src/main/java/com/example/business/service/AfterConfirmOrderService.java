package com.example.business.service;

import com.example.business.domain.DailyTrainSeat;

import java.util.List;

/**
 * @author lee
 * @description
 */
public interface AfterConfirmOrderService {

    void afterDoConfirm(List<DailyTrainSeat> fianlSeatList);

}
