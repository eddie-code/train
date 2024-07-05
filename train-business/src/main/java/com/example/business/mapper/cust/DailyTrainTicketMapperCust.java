package com.example.business.mapper.cust;

import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author lee
 * @description
 */
@Repository
public interface DailyTrainTicketMapperCust {

    void updateCountBySell(Date date
            , String trainCode
            , String seatTypeCode
            , Integer minStartIndex
            , Integer maxStartIndex
            , Integer minEndIndex
            , Integer maxEndIndex);

}
