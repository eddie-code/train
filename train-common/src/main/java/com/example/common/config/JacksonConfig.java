package com.example.common.config;

/**
 * @author lee
 * @description
 *
 * 统一注解，解决前后端交互Long类型精度丢失的问题
 * 公众号：甲蛙全栈
 * 关联视频课程《Spring Boot + Vue3 前后端分离 实战wiki知识库系统》
 * https://coding.imooc.com/class/474.html
 *
 * 这种全局方式：不推荐
 *
 */
// @Configuration
// public class JacksonConfig {
//     @Bean
//     public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
//         ObjectMapper objectMapper = builder.createXmlMapper(false).build();
//         SimpleModule simpleModule = new SimpleModule();
//         simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//         objectMapper.registerModule(simpleModule);
//         return objectMapper;
//     }
// }