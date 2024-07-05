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
import com.example.business.mapper.cust.DailyTrainTicketMapperCust;
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

    @Autowired
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    /**
     * 选中座位后事务处理：
     * 座位表修改售卖情况sell；
     * 余票详情表修改余票；
     * 为会员增加购票记录
     * 更新确认订单为成功
     */
    @Transactional
    @Override
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> fianlSeatList) {
        for (DailyTrainSeat dailyTrainSeat : fianlSeatList) {
            if (StrUtil.isNotBlank(dailyTrainSeat.getSell())) {
                // 更新指定的列
                LambdaUpdateWrapper<DailyTrainSeat> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(DailyTrainSeat::getId, dailyTrainSeat.getId());

                DailyTrainSeat seatForUpdate = new DailyTrainSeat();
                seatForUpdate.setSell(dailyTrainSeat.getSell());
                seatForUpdate.setUpdateTime(new Date());
                dailyTrainSeatMapper.update(seatForUpdate, updateWrapper);

                // 计算这个站卖出去后，影响了哪些站的余票库存
                // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
                // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
                // 假设10个站，本次买4~7站
                // 原售：001000001
                // 购买：000011100
                // 新售：001011101
                // 影响：XXX11111X
                // Integer startIndex = 4;
                // Integer endIndex = 7;
                // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
                // Integer maxStartIndex = endIndex - 1;
                // Integer minEndIndex = startIndex + 1;
                // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
                Integer startIndex = dailyTrainTicket.getStartIndex();
                Integer endIndex = dailyTrainTicket.getEndIndex();
                char[] chars = seatForUpdate.getSell().toCharArray();
                Integer maxStartIndex = endIndex - 1;
                Integer minEndIndex = startIndex + 1;
                Integer minStartIndex = 0;
                for (int i = startIndex - 1; i >= 0; i--) {
                    char aChar = chars[i];
                    if (aChar == '1') {
                        minStartIndex = i + 1;
                        break;
                    }
                }
                log.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);

                Integer maxEndIndex = seatForUpdate.getSell().length();
                for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
                    char aChar = chars[i];
                    if (aChar == '1') {
                        maxEndIndex = i;
                        break;
                    }
                }
                log.info("影响到达站区间：" + minEndIndex + "-" + maxEndIndex);

                dailyTrainTicketMapperCust.updateCountBySell(
                        dailyTrainSeat.getDate(),
                        dailyTrainSeat.getTrainCode(),
                        dailyTrainSeat.getSeatType(),
                        minStartIndex,
                        maxStartIndex,
                        minEndIndex,
                        maxEndIndex);

            }
        }
    }
}