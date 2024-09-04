package com.example.business.mq;

import com.alibaba.fastjson.JSON;
import com.example.business.domain.ConfirmOrder;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.service.ConfirmOrderService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lee
 * @description 消费
 */
@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ConfirmOrderService confirmOrderService;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        ConfirmOrderDoReq dto = JSON.parseObject(new String(body), ConfirmOrderDoReq.class);
        log.info("ROCKETMQ收到消息：{}", new String(body));
        confirmOrderService.doConfirm(dto);
    }
}