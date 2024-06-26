package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.DailyTrain;
import com.example.business.domain.Train;
import com.example.business.mapper.DailyTrainMapper;
import com.example.business.req.DailyTrainQueryReq;
import com.example.business.req.DailyTrainSaveReq;
import com.example.business.resp.DailyTrainQueryResp;
import com.example.business.service.*;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    @Autowired
    private DailyTrainMapper dailyTrainMapper;

    @Autowired
    private TrainService trainService;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

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

        if (ObjectUtil.isNotNull(req.getDate())) {
            queryWrapper.lambda().eq(DailyTrain::getDate, req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getCode())) {
            queryWrapper.lambda().like(DailyTrain::getCode, req.getCode());
        }

        queryWrapper.lambda()
                .orderByDesc(DailyTrain::getDate)
                .orderByAsc(DailyTrain::getCode);

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

    public void genDaily(Date date) {
        // 查询所有车次
        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            log.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
            // 生成日期车次的数据
            genDailyTrain(date, train);
        }
    }

    @Transactional
    public void genDailyTrain(Date date, Train train) {
        log.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), train.getCode());

        // 删除该车次已有的数据
        QueryWrapper<DailyTrain> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrain::getCode, train.getCode())
                .eq(DailyTrain::getDate, date);
        dailyTrainMapper.delete(queryWrapper);

        // 生成该车次的数据
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        // 生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());

        // 生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());

        // 生成该车次的座位数据
//        dailyTrainSeatService.genDaily(date, train.getCode());

        // 生成该车次的余票数据
//        dailyTrainTicketService.genDaily(dailyTrain, date, train.getCode());

        // 生成令牌余量数据
//        skTokenService.genDaily(date, train.getCode());

        log.info("生成日期【{}】车次【{}】的信息结束", DateUtil.formatDate(date), train.getCode());
    }
}
