package com.example.common.util;

import cn.hutool.core.util.IdUtil;

/**
 * @author lee
 * @description 封装hutool雪花算法
 */
public class SnowUtil {

    /**
     * 数据中心
     */
    private static long dataCenterId = 1;
    /**
     * 机器标识
     */
    private static long workerId = 1;

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}

