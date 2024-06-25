package com.example.batch.feign;

import org.springframework.stereotype.Component;

@Component
public class BusinessFeignFallback implements BusinessFeign {
    @Override
    public String hello() {
        return "Fallback";
    }

//    @Override
//    public CommonResp<Object> genDaily(Date date) {
//        return null;
//    }
}
