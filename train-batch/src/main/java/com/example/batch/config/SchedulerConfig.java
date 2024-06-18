package com.example.batch.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author lee
 * @description 使用数据库模式需要的配置类
 */
@Configuration
public class SchedulerConfig {

    @Resource
    private MyJobFactory myJobFactory;

    /**
     * 1. 创建SchedulerFactoryBean
     * 2. 设置DataSource
     * 3. 设置JobFactory
     * 4. 设置StartupDelay
     *
     * @param dataSource 数据源是application.properties配置的
     * @return
     * @throws IOException
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("dataSource") DataSource dataSource) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobFactory(myJobFactory);
        // 程序启动后, 延迟2秒执行Quartz
        factory.setStartupDelay(2);
        return factory;
    }
}

