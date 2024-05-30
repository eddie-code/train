//package com.example.gateway.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author lee
// * @description 拦截器
// */
//@Component
//public class Test1Filter implements GlobalFilter, Ordered {
//
//    private static final Logger LOG = LoggerFactory.getLogger(Test1Filter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        LOG.info("Test1Filter");
//        // 进入下一个链条环境
//         return chain.filter(exchange);
//        // 不进入下一个链条环境
////        return exchange.getResponse().setComplete();
//    }
//
//    // 优先级: 从细到大
//    @Override
//    public int getOrder() {
//        LOG.info("Test1Filter getOrder");
//        return 0;
//    }
//}
