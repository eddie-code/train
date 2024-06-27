package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.DailyTrainTicket;
import com.example.business.mapper.DailyTrainTicketMapper;
import com.example.business.req.DailyTrainTicketQueryReq;
import com.example.business.req.DailyTrainTicketSaveReq;
import com.example.business.resp.DailyTrainTicketQueryResp;
import com.example.business.service.DailyTrainTicketService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    @Autowired
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Override
    public void save(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateById(dailyTrainTicket);
        }
    }

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<DailyTrainTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(DailyTrainTicket::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<DailyTrainTicket> page = new Page<>(req.getPage(), req.getSize());
        Page<DailyTrainTicket> pageInfo = dailyTrainTicketMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<DailyTrainTicket> dailyTrainTicketList = pageInfo.getRecords();
        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteById(id);
    }
}
