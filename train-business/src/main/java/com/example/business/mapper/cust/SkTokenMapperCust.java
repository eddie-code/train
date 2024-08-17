package com.example.business.mapper.cust;

import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author lee
 * @description
 */
@Repository
public interface SkTokenMapperCust {

    /**
     * 更新 count 字段, 如果是 0 就不能在减了,
     * 所以语句就添加判断条件 if (`count` < 1, 0, `count` - 1)
     */
    int decrease(Date date, String trainCode, int decreaseCount);

}
