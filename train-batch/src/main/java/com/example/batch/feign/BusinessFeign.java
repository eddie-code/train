package com.example.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lee
 * @description
 */
//@FeignClient(value = "train-business", fallback = BusinessFeignFallback.class)
 @FeignClient(name = "train-business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {

//    @GetMapping("/business/hello")
    @GetMapping("/hello")
    String hello();

//    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
//    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

}
