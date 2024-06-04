package com.example.common.resp;

import lombok.Data;
import lombok.ToString;

/**
 * @author lee
 * @description
 */
@Data
@ToString
public class MemberLoginResp {

    private Long id;

    private String mobile;

    /**
     * jwt token
     */
    private String token;

}
