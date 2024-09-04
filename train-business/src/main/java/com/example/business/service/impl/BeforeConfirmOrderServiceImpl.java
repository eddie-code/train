package com.example.business.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.business.domain.ConfirmOrder;
import com.example.business.enums.ConfirmOrderStatusEnum;
import com.example.business.enums.RedisKeyPreEnum;
import com.example.business.enums.RocketMQTopicEnum;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.req.ConfirmOrderTicketReq;
import com.example.business.service.BeforeConfirmOrderService;
import com.example.business.service.SkTokenService;
import com.example.common.context.LoginMemberContext;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import com.example.common.util.SnowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class BeforeConfirmOrderServiceImpl implements BeforeConfirmOrderService {

    @Autowired
    private SkTokenService skTokenService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    @Override
    public void beforeDoConfirm(ConfirmOrderDoReq req) {

        req.setMemberId(LoginMemberContext.getId());

        // 校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
        if (validSkToken) {
            log.info("令牌校验通过");
        } else {
            log.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        // 保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        // 因为此处不是通过请求获取 LoginMemberContext.getId(), 而是通过MQ发送, 所以会出现 null 情况, 从源头解决, 发送前添加 MemberId
//        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setMemberId(req.getMemberId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

        // 发送MQ排队购票
        req.setLogId(MDC.get("LOG_ID"));
        String reqJson = JSON.toJSONString(req);
        log.info("排队购票，发送mq开始，消息：{}", reqJson);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
        log.info("排队购票，发送mq结束");

    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     *
     * @param req
     * @param e
     */
    @Override
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        log.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

}
