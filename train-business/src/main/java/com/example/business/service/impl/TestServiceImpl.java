package com.example.business.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.business.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @SentinelResource("hello2")
    @Override
    public void hello2(String var) throws InterruptedException {
        Thread.sleep(500);
        log.info("=== " + var + " ===");
    }

}
