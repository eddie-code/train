package com.example.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lee
 * @description
 */
@RestController
@RefreshScope
public class TestController {

    @Value("${test.nacos}")
    private String testNacos;

//    @Autowired
//    Environment environment;

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello %s!", testNacos);
//        String port = environment.getProperty("local.server.port");
//        return String.format("Hello %s! 端口：%s", testNacos, port);
    }

}
