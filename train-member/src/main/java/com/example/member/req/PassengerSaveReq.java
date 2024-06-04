package com.example.member.req;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class PassengerSaveReq {

    private Long id;

    private Long memberId;

    private String name;

    private String idCard;

    private String type;

    private Date createTime;

    private Date updateTime;

}