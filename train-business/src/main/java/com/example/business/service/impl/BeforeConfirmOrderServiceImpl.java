package com.example.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.business.enums.RedisKeyPreEnum;
import com.example.business.enums.RocketMQTopicEnum;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.service.BeforeConfirmOrderService;
import com.example.business.service.SkTokenService;
import com.example.common.context.LoginMemberContext;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class BeforeConfirmOrderServiceImpl implements BeforeConfirmOrderService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkTokenService skTokenService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    @Override
    public void beforeDoConfirm(ConfirmOrderDoReq req) {

        // 校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
        if (validSkToken) {
            log.info("令牌校验通过");
        } else {
            log.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        // 获取车次锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
        // setnx 设置分布式锁，5秒后自动释放
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            log.info("恭喜，抢到锁了！lockKey：{}", lockKey);
        } else {
            // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
            log.info("很遗憾，没抢到锁！lockKey：{}", lockKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

        // 发送MQ排队购票
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
