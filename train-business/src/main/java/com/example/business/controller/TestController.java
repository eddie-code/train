package com.example.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lee
 * @description
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "hello business!";
    }

}
