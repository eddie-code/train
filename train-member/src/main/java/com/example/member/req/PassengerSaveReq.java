package com.example.member.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class PassengerSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * 会员id
     */
//    @NotNull(message = "【会员ID】不能为空")
    private Long memberId;

    /**
     * 姓名
     */
    @NotBlank(message = "【姓名】不能为空")
    private String name;

    /**
     * 身份证
     */
    @NotBlank(message = "【身份证】不能为空")
    private String idCard;

    /**
     * 旅客类型|枚举[PassengerTypeEnum]
     */
    @NotBlank(message = "【旅客类型】不能为空")
    private String type;

    /**
     * 新增时间
     * 在列表查询功能里, 前端得到的日期值, 其实是字符串, 后端保存接口里, 必需按这个格式转回日期
     * Date + @JsonFormat()
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}