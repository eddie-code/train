package com.example.member.req;

import com.example.common.req.PageReq;
import lombok.Data;
import lombok.ToString;

/**
 * @author lee
 * @description
 */
@Data
@ToString
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}