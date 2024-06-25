package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.DailyTrainCarriage;
import com.example.business.enums.SeatColEnum;
import com.example.business.mapper.DailyTrainCarriageMapper;
import com.example.business.req.DailyTrainCarriageQueryReq;
import com.example.business.req.DailyTrainCarriageSaveReq;
import com.example.business.resp.DailyTrainCarriageQueryResp;
import com.example.business.service.DailyTrainCarriageService;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DailyTrainCarriageServiceImpl implements DailyTrainCarriageService {

    @Autowired
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Override
    public void save(DailyTrainCarriageSaveReq req) {
        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数, 页面就可以屏蔽列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateById(dailyTrainCarriage);
        }
    }

    @Override
    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<DailyTrainCarriage> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotNull(req.getDate())) {
            queryWrapper.lambda().eq(DailyTrainCarriage::getDate, req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            queryWrapper.lambda().like(DailyTrainCarriage::getTrainCode, req.getTrainCode());
        }

        queryWrapper.lambda()
                .orderByDesc(DailyTrainCarriage::getDate)
                .orderByAsc(DailyTrainCarriage::getTrainCode)
                .orderByAsc(DailyTrainCarriage::getIndex);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<DailyTrainCarriage> page = new Page<>(req.getPage(), req.getSize());
        Page<DailyTrainCarriage> pageInfo = dailyTrainCarriageMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<DailyTrainCarriage> dailyTrainCarriageList = pageInfo.getRecords();
        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteById(id);
    }
}
