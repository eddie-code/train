package com.example.member.req;

import com.example.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author lee
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}