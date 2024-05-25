package com.example.common.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author lee
 * @description
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommonResp<T> {

    /**
     * 业务上的成功或失败
     */
    private boolean success = true;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回泛型数据，自定义类型
     */
    private T content;

    /**
     * 无参构造函数
     */
    public CommonResp(T content) {
        this.content = content;
    }

}
