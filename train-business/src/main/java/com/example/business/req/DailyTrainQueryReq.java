package com.example.business.req;

import com.example.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class DailyTrainQueryReq extends PageReq {

    /**
     * Get请求时候需要使用 @DateTimeFormat 注解
     * Post请求才是 @JsonFormat 注解
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String code;

}
