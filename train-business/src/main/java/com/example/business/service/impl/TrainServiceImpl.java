package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.Train;
import com.example.business.mapper.TrainMapper;
import com.example.business.req.TrainQueryReq;
import com.example.business.req.TrainSaveReq;
import com.example.business.resp.TrainQueryResp;
import com.example.business.service.TrainService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class TrainServiceImpl implements TrainService {

    @Autowired
    private TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateById(train);
        }
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<Train> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(Train::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<Train> page = new Page<>(req.getPage(), req.getSize());
        Page<Train> pageInfo = trainMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<Train> trainList = pageInfo.getRecords();
        List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainMapper.deleteById(id);
    }
}
