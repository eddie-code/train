package com.example.business.req;

import com.example.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class DailyTrainSeatQueryReq extends PageReq {

    private String trainCode;

}
