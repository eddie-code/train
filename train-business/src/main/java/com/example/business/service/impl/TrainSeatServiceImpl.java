package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.TrainSeat;
import com.example.business.mapper.TrainSeatMapper;
import com.example.business.req.TrainSeatQueryReq;
import com.example.business.req.TrainSeatSaveReq;
import com.example.business.resp.TrainSeatQueryResp;
import com.example.business.service.TrainSeatService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Override
    public void save(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateById(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<TrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(TrainSeat::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<TrainSeat> page = new Page<>(req.getPage(), req.getSize());
        Page<TrainSeat> pageInfo = trainSeatMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<TrainSeat> trainSeatList = pageInfo.getRecords();
        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteById(id);
    }
}
