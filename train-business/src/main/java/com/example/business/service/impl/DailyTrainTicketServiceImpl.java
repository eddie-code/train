package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.DailyTrain;
import com.example.business.domain.DailyTrainTicket;
import com.example.business.domain.TrainStation;
import com.example.business.enums.SeatTypeEnum;
import com.example.business.enums.TrainTypeEnum;
import com.example.business.mapper.DailyTrainTicketMapper;
import com.example.business.req.DailyTrainTicketQueryReq;
import com.example.business.req.DailyTrainTicketSaveReq;
import com.example.business.resp.DailyTrainTicketQueryResp;
import com.example.business.service.DailyTrainSeatService;
import com.example.business.service.DailyTrainTicketService;
import com.example.business.service.TrainStationService;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    @Autowired
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private TrainStationService trainStationService;

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

    @Cacheable(value = "DailyTrainTicketService.queryList3")
    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req) {
        log.info("测试缓存击穿");
        return null;
    }

    /**
     * 1. 比如[DailyTrainTicketService.queryList]设置了60s过期,
     * 可以增加定时任务: 每隔30s, 调用queryList2()主动刷新缓存,
     * 此时, 缓存时间会变成60s, 可以从redis客户端工具查看超时时间
     *
     * 2. 针对数据库查询,
     * 增加分布式锁, 100个请求, 只有一个拿到锁, 去查数据库,
     * 其他99个请求都快速失败, 告诉用户稍后重试
     *
     */
    @CachePut(value = "DailyTrainTicketService.queryList")
    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req) {
        return queryList(req);
    }

//    @Cacheable(value = "DailyTrainTicketService.queryList")
    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        // 常见的缓存过期策略
        // TTL 超时时间
        // LRU 最近最少使用
        // LFU 最近最不经常使用
        // FIFO 先进先出
        // Random 随机淘汰策略
        //
        // 去缓存里取数据，因数据库本身就没数据而造成缓存穿透
        // if (有数据) { null []
        //     return
        // } else {
        //     去数据库取数据
        // }
        log.info("查询条件：{}", req);
        QueryWrapper<DailyTrainTicket> queryWrapper = new QueryWrapper<>();
        if (ObjUtil.isNotNull(req.getDate())) {
            queryWrapper.lambda().eq(DailyTrainTicket::getDate, req.getDate());
        }
        if (ObjUtil.isNotEmpty(req.getTrainCode())) {
            queryWrapper.lambda().eq(DailyTrainTicket::getTrainCode, req.getTrainCode());
        }
        if (ObjUtil.isNotEmpty(req.getStart())) {
            queryWrapper.lambda().eq(DailyTrainTicket::getStart, req.getStart());
        }
        if (ObjUtil.isNotEmpty(req.getEnd())) {
            queryWrapper.lambda().eq(DailyTrainTicket::getEnd, req.getEnd());
        }
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

    @Transactional
    @Override
    public void genDaily(DailyTrain dailyTrain, Date date, String trainCode) {

        log.info("生成日期【{}】车次【{}】的余票信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的余票信息
        QueryWrapper<DailyTrainTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrainTicket::getTrainCode, trainCode)
                .eq(DailyTrainTicket::getDate, date);
        dailyTrainTicketMapper.delete(queryWrapper);

        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            log.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        DateTime now = DateTime.now();

        // 计数席位
        int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
        int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
        int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
        int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());

        for (int i = 0; i < stationList.size(); i++) {
            // 得到出发站
            TrainStation trainStationStart = stationList.get(i);
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = (i + 1); j < stationList.size(); j++) {
                // 得到终点站
                TrainStation trainStationEnd = stationList.get(j);
                sumKM = sumKM.add(trainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());

                /**
                 * 票价 = 里程之和 * 座位单价 * 车次类型系数
                 * 阶梯价格是比较常见的设计, 比如：
                 *  1. 0~100km: 0.4/km
                 *  2. 100~200km: 0.3/km
                 *  坐一趟车, 开了150km, 那么票价就是 100*0.4 + (150-100) * 0.3 = 55
                 */
                String trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType); // EnumUtil 获取枚举工具类
                // 一等座
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                // 二等座
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                // 软卧
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                // 硬卧
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);

                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        log.info("生成日期【{}】车次【{}】的余票信息结束", DateUtil.formatDate(date), trainCode);

    }

    @Override
    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        QueryWrapper<DailyTrainTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DailyTrainTicket::getDate, date)
                .eq(DailyTrainTicket::getTrainCode, trainCode)
                .eq(DailyTrainTicket::getStart, start)
                .eq(DailyTrainTicket::getEnd, end);
        List<DailyTrainTicket> list = dailyTrainTicketMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
