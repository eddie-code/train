package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.TrainCarriage;
import com.example.business.enums.SeatColEnum;
import com.example.business.mapper.TrainCarriageMapper;
import com.example.business.req.TrainCarriageQueryReq;
import com.example.business.req.TrainCarriageSaveReq;
import com.example.business.resp.TrainCarriageQueryResp;
import com.example.business.service.TrainCarriageService;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainCarriageServiceImpl implements TrainCarriageService {

    @Autowired
    private TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        log.info("根据车箱的座位类型，筛选出所有的列: {}", seatColEnums);
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateById(trainCarriage);
        }
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<TrainCarriage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(TrainCarriage::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<TrainCarriage> page = new Page<>(req.getPage(), req.getSize());
        Page<TrainCarriage> pageInfo = trainCarriageMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<TrainCarriage> trainCarriageList = pageInfo.getRecords();
        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainCarriageMapper.deleteById(id);
    }

    @Override
    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        QueryWrapper<TrainCarriage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TrainCarriage::getTrainCode, trainCode)
                .orderByAsc(TrainCarriage::getIndex);
        return trainCarriageMapper.selectList(queryWrapper);
    }

}
