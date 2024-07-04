package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.DailyTrainSeat;
import com.example.business.domain.TrainSeat;
import com.example.business.domain.TrainStation;
import com.example.business.mapper.DailyTrainSeatMapper;
import com.example.business.req.DailyTrainSeatQueryReq;
import com.example.business.req.DailyTrainSeatSaveReq;
import com.example.business.resp.DailyTrainSeatQueryResp;
import com.example.business.service.DailyTrainSeatService;
import com.example.business.service.TrainSeatService;
import com.example.business.service.TrainStationService;
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
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Autowired
    private TrainStationService trainStationService;

    @Autowired
    private TrainSeatService trainSeatService;

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

    @Transactional
    @Override
    public void genDaily(Date date, String trainCode) {
        log.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的座位信息
        QueryWrapper<DailyTrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, trainCode);
        dailyTrainSeatMapper.delete(queryWrapper);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            log.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat : seatList) {
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        log.info("生成日期【{}】车次【{}】的座位信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public int countSeat(Date date, String trainCode, String seatType) {
        QueryWrapper<DailyTrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, trainCode);
        if (StrUtil.isNotBlank(seatType)) {
            queryWrapper.lambda().eq(DailyTrainSeat::getSeatType, seatType);
        }
        long l = dailyTrainSeatMapper.selectCount(queryWrapper);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }

    @Override
    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer index) {
        QueryWrapper<DailyTrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrainSeat::getDate, date)
                .eq(DailyTrainSeat::getTrainCode, trainCode)
                .eq(DailyTrainSeat::getCarriageIndex, index)
                .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);
        List<DailyTrainSeat> list = dailyTrainSeatMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list;
        }
        return null;
    }
}
