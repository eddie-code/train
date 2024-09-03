//package com.example.business.config;
//
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
///**
// * @author lee
// * @description 不推荐使用, 推荐使用 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
// */
// @Component
// public class RocketMQConfig {
//
//     /**
//      * 新版本需要声明RocketMQTemplate，否则会注入失败
//      * @return
//      */
//     @Bean
//     public RocketMQTemplate rocketMQTemplate() {
//         RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
//         DefaultMQProducer producer = new DefaultMQProducer();
//         producer.setProducerGroup("default");
//         producer.setNamesrvAddr("http://localhost:9876");
//         producer.setSendMsgTimeout(3000);
//         rocketMQTemplate.setProducer(producer);
//         return rocketMQTemplate;
//     }
//
// }
