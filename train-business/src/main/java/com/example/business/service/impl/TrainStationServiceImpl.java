package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.TrainStation;
import com.example.business.mapper.TrainStationMapper;
import com.example.business.req.TrainStationQueryReq;
import com.example.business.req.TrainStationSaveReq;
import com.example.business.resp.TrainStationQueryResp;
import com.example.business.service.TrainStationService;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainStationServiceImpl implements TrainStationService {

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    public void save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {

            // 保存之前，先校验唯一键是否存在
            TrainStation trainStationDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(req.getTrainCode(), req.getName());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }

            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateById(trainStation);
        }
    }

    private TrainStation selectByUnique(String trainCode, Integer index) {
        QueryWrapper<TrainStation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TrainStation::getTrainCode, trainCode)
                .eq(TrainStation::getIndex, index)
                .orderByDesc(TrainStation::getCreateTime);
        List<TrainStation> list = trainStationMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private TrainStation selectByUnique(String trainCode, String name) {
        QueryWrapper<TrainStation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TrainStation::getTrainCode, trainCode)
                .eq(TrainStation::getName, name)
                .orderByDesc(TrainStation::getCreateTime);
        List<TrainStation> list = trainStationMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<TrainStation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(TrainStation::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<TrainStation> page = new Page<>(req.getPage(), req.getSize());
        Page<TrainStation> pageInfo = trainStationMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<TrainStation> trainStationList = pageInfo.getRecords();
        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainStationMapper.deleteById(id);
    }
}
