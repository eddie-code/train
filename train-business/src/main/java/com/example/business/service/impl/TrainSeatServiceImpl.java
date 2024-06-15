package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.TrainCarriage;
import com.example.business.domain.TrainSeat;
import com.example.business.enums.SeatColEnum;
import com.example.business.mapper.TrainSeatMapper;
import com.example.business.req.TrainSeatQueryReq;
import com.example.business.req.TrainSeatSaveReq;
import com.example.business.resp.TrainSeatQueryResp;
import com.example.business.service.TrainCarriageService;
import com.example.business.service.TrainSeatService;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Autowired
    private TrainCarriageService trainCarriageService;

    @Override
    public void save(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateById(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<TrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .orderByDesc(TrainSeat::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<TrainSeat> page = new Page<>(req.getPage(), req.getSize());
        Page<TrainSeat> pageInfo = trainSeatMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<TrainSeat> trainSeatList = pageInfo.getRecords();
        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteById(id);
    }

    /**
     * 1、清空火车座位表
     * 2. 根据[车次编号]查询所有车厢，
     *      比如: 有两条数据, 一条是D1一等座, 排数是 1 , 一条是D1二等座, [排数=4]
     *      运行此接口生成火车座位号:
     *      1. 一等车座位号: AA、CC、DD、FF  (备注： 是按 SeatColEnum 枚举来生成, YDZ_开头有四条数据)
     *      2. 二等车座位号：AA、B、CC、DD、FF (备注： 是按 SeatColEnum 枚举来生成, EDZ_开头有五条数据, 但是查询所有车厢获取到数据[排数], 所以就 5条数据 * 排数=4, 就会生成 4 * 5 = 20 条数据)
     */
    @Transactional
    @Override
    public void genTrainSeat(String trainCode) {
        // 重覆生成两种做法： 1.存在就跳过; 2.先删除已有再插入(选择本次第二种)
        DateTime now = DateTime.now();
        // 1. 清空当前车次下的所有的座位记录
        QueryWrapper<TrainSeat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TrainSeat::getTrainCode, trainCode);
        int i = trainSeatMapper.delete(queryWrapper);
        log.info("清空当前车次下的所有的座位记录：{}", i);

        // 2. 查找当前车次下的所有的车厢
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
        log.info("当前车次下的车厢数：{}", carriageList.size());

        // 3. 循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : carriageList) {
            // 拿到车厢数据：行数、座位类型(得到列数)
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            int seatIndex = 1;

            // 根据车厢的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            log.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);

            // 循环行数, 从1开始
            for (int row = 1; row <= rowCount; row++) {
                // 循环列数
                for (SeatColEnum seatColEnum : colEnumList) {
                    // 构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    // - str：要填充的字符串
                    //- filler：要填充的字符串
                    //- length：要达到的字符串长度
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    int ii = trainSeatMapper.insert(trainSeat);
                    log.info("构造座位数据并保存数据库：{}", ii);
                }
            }
        }
    }

}
