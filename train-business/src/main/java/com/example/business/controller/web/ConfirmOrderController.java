package com.example.business.controller.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.service.BeforeConfirmOrderService;
import com.example.business.service.ConfirmOrderService;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import com.example.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Autowired
    private BeforeConfirmOrderService beforeConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 当前环境，用于区分是否是测试环境
     */
    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private ConfirmOrderService confirmOrderService;

    /**
     * 接口的资源名称不要和接口路径一致，否则会导致限流后走不到降级方法中
     */
    @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {

        if (!env.equals("dev")) {
            // 图形验证码校验
            String imageCodeToken = req.getImageCodeToken();
            String imageCode = req.getImageCode();
            String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
            log.info("从redis中获取到的验证码：{}", imageCodeRedis);
            if (ObjectUtils.isEmpty(imageCodeRedis)) {
                return new CommonResp<>(false, "验证码已过期", null);
            }
            // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
            if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
                return new CommonResp<>(false, "验证码不正确", null);
            } else {
                // 验证通过后，移除验证码
                redisTemplate.delete(imageCodeToken);
            }
        }

        Long id = beforeConfirmOrderService.beforeDoConfirm(req);
        return new CommonResp<>(String.valueOf(id));
    }

    @GetMapping("/query-line-count/{id}")
    public CommonResp<Integer> queryLineCount(@PathVariable Long id) {
        Integer count = confirmOrderService.queryLineCount(id);
        return new CommonResp<>(count);
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数, 并且返回值类型必须和限流方法一致
     */
    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        log.info("ConfirmOrderController.doConfirm.doConfirmBlock.购票请求被限流：{}", req);
//        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setSuccess(false);
        // 当前抢票人数太多了，请稍候重试
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonResp;
    }

}
