package com.example.batch.controller;

import com.example.batch.feign.BusinessFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private BusinessFeign businessFeign;

    @GetMapping("/hello")
    public String hello() {
        String hello = businessFeign.hello();
        log.info(hello);
        return "Hello World! Batch! ";
    }
}
