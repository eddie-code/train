package com.example.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

/**
 * @author lee
 * @description
 */
@Data
@ToString
public class MemberRegisterReq {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

}
