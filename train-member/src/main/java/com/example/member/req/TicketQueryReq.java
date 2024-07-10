package com.example.member.req;

import com.example.common.req.PageReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class TicketQueryReq extends PageReq {

    private Long memberId;

}
