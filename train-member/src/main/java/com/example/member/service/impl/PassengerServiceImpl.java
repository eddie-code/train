package com.example.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.context.LoginMemberContext;
import com.example.common.util.SnowUtil;
import com.example.member.domain.Passenger;
import com.example.member.mapper.PassengerMapper;
import com.example.member.req.PassengerQueryReq;
import com.example.member.req.PassengerSaveReq;
import com.example.member.resp.PassengerQueryResp;
import com.example.member.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lee
 * @description
 */
@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        // 1. 获取当前登录用户的id
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
        LoginMemberContext.remove();
    }

    @Override
    public List<PassengerQueryResp> queryList(PassengerQueryReq resp) {
        QueryWrapper<Passenger> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(resp.getMemberId())) {
            queryWrapper.lambda()
                    .eq(Passenger::getMemberId, resp.getMemberId())
                    .orderByDesc(Passenger::getCreateTime);
        }
        List<Passenger> list = passengerMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }

}
