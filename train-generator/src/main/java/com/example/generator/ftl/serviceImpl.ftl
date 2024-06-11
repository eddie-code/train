package com.example.${module}.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
<#--import com.github.pagehelper.PageHelper;-->
<#--import com.github.pagehelper.PageInfo;-->
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.common.context.LoginMemberContext;
import com.example.${module}.domain.${Domain};
<#--import com.example.${module}.domain.${Domain}Example;-->
import com.example.${module}.mapper.${Domain}Mapper;
import com.example.${module}.req.${Domain}QueryReq;
import com.example.${module}.req.${Domain}SaveReq;
import com.example.${module}.resp.${Domain}QueryResp;
import com.example.member.service.${Domain}Service;
<#--import jakarta.annotation.Resource;-->
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class ${Domain}ServiceImpl implements ${Domain}Service {

<#--    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);-->
    @Autowired
    private ${Domain}Mapper ${domain}Mapper;

    @Override
    public void save(${Domain}SaveReq req) {
        DateTime now = DateTime.now();
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        if (ObjectUtil.isNull(${domain}.getId())) {
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        } else {
            ${domain}.setUpdateTime(now);
<#--            ${domain}Mapper.updateByPrimaryKey(${domain});-->
            ${domain}Mapper.updateById(${domain});
        }
    }

    @Override
    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {
<#--        ${Domain}Example ${domain}Example = new ${Domain}Example();-->
<#--        ${domain}Example.setOrderByClause("id desc");-->
<#--        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();-->

<#--        LOG.info("查询页码：{}", req.getPage());-->
<#--        LOG.info("每页条数：{}", req.getSize());-->
<#--        PageHelper.startPage(req.getPage(), req.getSize());-->
<#--        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);-->

<#--        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);-->
<#--        LOG.info("总行数：{}", pageInfo.getTotal());-->
<#--        LOG.info("总页数：{}", pageInfo.getPages());-->
        log.info("查询条件：{}", req);
        QueryWrapper<${Domain}> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .orderByDesc(${Domain}::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<${Domain}> page = new Page<>(req.getPage(), req.getSize());
        Page<${Domain}> pageInfo = ${domain}Mapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<${Domain}> ${domain}List = pageInfo.getRecords();
        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
<#--        ${domain}Mapper.deleteByPrimaryKey(id);-->
        ${domain}Mapper.deleteById(id);
    }
}
