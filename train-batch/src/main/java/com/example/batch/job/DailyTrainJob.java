package com.example.batch.job;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

/**
 * @author lee
 * @description
 */
@Slf4j
@DisallowConcurrentExecution
public class DailyTrainJob implements Job {

//    @Resource
//    BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("生成每日车次数据开始");

        log.info("生成每日车次数据结束");
        // 增加日志流水号
//        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
//        log.info("生成15天后的车次数据开始");
//        Date date = new Date();
//        DateTime dateTime = DateUtil.offsetDay(date, 15);
//        Date offsetDate = dateTime.toJdkDate();
//        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
//        log.info("生成15天后的车次数据结束，结果：{}", commonResp);
    }
}