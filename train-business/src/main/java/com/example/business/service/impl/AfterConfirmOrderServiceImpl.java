package com.example.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.business.domain.ConfirmOrder;
import com.example.business.domain.DailyTrainSeat;
import com.example.business.domain.DailyTrainTicket;
import com.example.business.enums.ConfirmOrderStatusEnum;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.mapper.DailyTrainSeatMapper;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.req.ConfirmOrderTicketReq;
import com.example.business.service.AfterConfirmOrderService;
import com.example.common.resp.CommonResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class AfterConfirmOrderServiceImpl implements AfterConfirmOrderService {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     * 选中座位后事务处理：
     * 座位表修改售卖情况sell；
     * 余票详情表修改余票；
     * 为会员增加购票记录
     * 更新确认订单为成功
     */
    @Transactional
    @Override
    public void afterDoConfirm(List<DailyTrainSeat> fianlSeatList) {
        for (DailyTrainSeat dailyTrainSeat : fianlSeatList) {
            if (StrUtil.isNotBlank(dailyTrainSeat.getSell())) {
                // 更新指定的列
                LambdaUpdateWrapper<DailyTrainSeat> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(DailyTrainSeat::getId, dailyTrainSeat.getId())
                        .set(DailyTrainSeat::getSell, dailyTrainSeat.getSell())
                        .set(DailyTrainSeat::getUpdateTime, new Date());
                dailyTrainSeatMapper.update(updateWrapper);
            }
        }
    }
}