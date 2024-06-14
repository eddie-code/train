package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.Station;
import com.example.business.mapper.StationMapper;
import com.example.business.req.StationQueryReq;
import com.example.business.req.StationSaveReq;
import com.example.business.resp.StationQueryResp;
import com.example.business.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateById(station);
        }
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<Station> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(Station::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<Station> page = new Page<>(req.getPage(), req.getSize());
        Page<Station> pageInfo = stationMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<Station> stationList = pageInfo.getRecords();
        List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        stationMapper.deleteById(id);
    }

    @Override
    public List<StationQueryResp> queryAll() {
        QueryWrapper<Station> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(Station::getNamePinyin);
        List<Station> stationList = stationMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(stationList, StationQueryResp.class);
    }
}
