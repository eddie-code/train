package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.DailyTrainSeat;
import com.example.business.mapper.DailyTrainSeatMapper;
import com.example.business.req.DailyTrainSeatQueryReq;
import com.example.business.req.DailyTrainSeatSaveReq;
import com.example.business.resp.DailyTrainSeatQueryResp;
import com.example.business.service.DailyTrainSeatService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Override
    public void save(DailyTrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateById(dailyTrainSeat);
        }
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<DailyTrainSeat> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            queryWrapper.lambda().eq(DailyTrainSeat::getTrainCode, req.getTrainCode());
        }

        queryWrapper.lambda()
            .orderByDesc(DailyTrainSeat::getDate)
            .orderByAsc(DailyTrainSeat::getTrainCode)
            .orderByAsc(DailyTrainSeat::getCarriageIndex)
            .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<DailyTrainSeat> page = new Page<>(req.getPage(), req.getSize());
        Page<DailyTrainSeat> pageInfo = dailyTrainSeatMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<DailyTrainSeat> dailyTrainSeatList = pageInfo.getRecords();
        List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteById(id);
    }
}
