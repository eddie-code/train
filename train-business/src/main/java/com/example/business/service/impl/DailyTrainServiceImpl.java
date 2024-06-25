package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.DailyTrain;
import com.example.business.mapper.DailyTrainMapper;
import com.example.business.req.DailyTrainQueryReq;
import com.example.business.req.DailyTrainSaveReq;
import com.example.business.resp.DailyTrainQueryResp;
import com.example.business.service.DailyTrainService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    @Autowired
    private DailyTrainMapper dailyTrainMapper;

    @Override
    public void save(DailyTrainSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateById(dailyTrain);
        }
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<DailyTrain> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(DailyTrain::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<DailyTrain> page = new Page<>(req.getPage(), req.getSize());
        Page<DailyTrain> pageInfo = dailyTrainMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<DailyTrain> dailyTrainList = pageInfo.getRecords();
        List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainMapper.deleteById(id);
    }
}
