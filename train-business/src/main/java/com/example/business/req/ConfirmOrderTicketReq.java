package com.example.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * @author lee
 * @description 描述购买票的信息
 */
@Data
@ToString
public class ConfirmOrderTicketReq {

    /**
     * 乘客ID
     * 后端需要做好参数合法性校验, 防止黑客绕过界面, 直接访问接口
     */
    @NotNull(message = "【乘客ID】不能为空")
    private Long passengerId;

    /**
     * 乘客票种
     */
    @NotBlank(message = "【乘客票种】不能为空")
    private String passengerType;

    /**
     * 乘客名称
     */
    @NotBlank(message = "【乘客名称】不能为空")
    private String passengerName;

    /**
     * 乘客身份证
     */
    @NotBlank(message = "【乘客身份证】不能为空")
    private String passengerIdCard;

    /**
     * 座位类型code
     */
    @NotBlank(message = "【座位类型code】不能为空")
    private String seatTypeCode;

    /**
     * 选座，可空，值示例：A1
     */
    private String seat;

}
