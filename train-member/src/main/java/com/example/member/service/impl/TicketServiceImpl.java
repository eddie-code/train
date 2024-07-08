package com.example.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.member.domain.Ticket;
import com.example.member.mapper.TicketMapper;
import com.example.member.req.TicketQueryReq;
import com.example.member.req.TicketSaveReq;
import com.example.member.resp.TicketQueryResp;
import com.example.member.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public void save(TicketSaveReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        } else {
            ticket.setUpdateTime(now);
            ticketMapper.updateById(ticket);
        }
    }

    @Override
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<Ticket> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(Ticket::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<Ticket> page = new Page<>(req.getPage(), req.getSize());
        Page<Ticket> pageInfo = ticketMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<Ticket> ticketList = pageInfo.getRecords();
        List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        ticketMapper.deleteById(id);
    }
}
