package com.example.batch.feign;

import com.example.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * @author lee
 * @description
 */
//@FeignClient(value = "train-business", fallback = BusinessFeignFallback.class)
@FeignClient(name = "train-business", url = "http://127.0.0.1:8002/business")
//@FeignClient("train-business")
public interface BusinessFeign {

    @GetMapping("/hello")
    String hello();

    @GetMapping("/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

}
