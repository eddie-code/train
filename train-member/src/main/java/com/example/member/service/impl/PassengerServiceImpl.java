package com.example.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.context.LoginMemberContext;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.member.domain.Passenger;
import com.example.member.mapper.PassengerMapper;
import com.example.member.req.PassengerQueryReq;
import com.example.member.req.PassengerSaveReq;
import com.example.member.resp.PassengerQueryResp;
import com.example.member.service.PassengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateById(passenger);
        }
    }

    @Override
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<Passenger> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(Passenger::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<Passenger> page = new Page<>(req.getPage(), req.getSize());
        Page<Passenger> pageInfo = passengerMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<Passenger> passengerList = pageInfo.getRecords();
        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        passengerMapper.deleteById(id);
    }

    /**
     * 查询我的所有乘客
     */
    @Override
    public List<PassengerQueryResp> queryMine() {
        QueryWrapper<Passenger> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Passenger::getMemberId, LoginMemberContext.getId())
                .orderByAsc(Passenger::getName);
        List<Passenger> list = passengerMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }

}
