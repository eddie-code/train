package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.*;
import com.example.business.enums.*;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.req.ConfirmOrderDoReq;
import com.example.business.req.ConfirmOrderQueryReq;
import com.example.business.req.ConfirmOrderTicketReq;
import com.example.business.resp.ConfirmOrderQueryResp;
import com.example.business.service.*;
import com.example.common.context.LoginMemberContext;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private AfterConfirmOrderService afterConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkTokenService skTokenService;

//    @Autowired
//    private RedissonClient redissonClient;

    @Override
    public void save(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateById(confirmOrder);
        }
    }

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<ConfirmOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .orderByDesc(ConfirmOrder::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<ConfirmOrder> page = new Page<>(req.getPage(), req.getSize());
        Page<ConfirmOrder> pageInfo = confirmOrderMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<ConfirmOrder> confirmOrderList = pageInfo.getRecords();
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        confirmOrderMapper.deleteById(id);
    }

    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    @Override
    public synchronized void doConfirm(ConfirmOrderDoReq req) {

//         // 校验令牌余量
//         boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
//         if (validSkToken) {
//             log.info("令牌校验通过");
//         } else {
//             log.info("令牌校验不通过");
//             throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
//         }
//
         // 获取分布式锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
        // setnx 设置分布式锁，5秒后自动释放
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            log.info("恭喜，抢到锁了！lockKey：{}", lockKey);
        } else {
            // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
            log.info("很遗憾，没抢到锁！lockKey：{}", lockKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

//        RLock lock = null;
        /*
            关于红锁，看16.7节：
            A B C D E
            1: A B C D E
            2: C D E
            3: C
        */
        try {
//            // 使用redisson，自带看门狗
//            lock = redissonClient.getLock(lockKey);
//
//            // 红锁的写法
//            // RedissonRedLock redissonRedLock = new RedissonRedLock(lock, lock, lock);
//            // boolean tryLock1 = redissonRedLock.tryLock(0, TimeUnit.SECONDS);
//
//            /**
//             waitTime – the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
//             leaseTime – lease time 锁时长，即n秒后自动释放锁
//             time unit – time unit 时间单位
//
//             Redisson提供的分布式锁是支持锁自动续期的，也就是说，如果线程仍旧没有执行完，
//             那么redisson会自动给redis中的目标key延长超时时间，
//             这在Redisson中称之为 Watch Dog 机制。默认情况下，看门狗的检查锁的超时时间是30秒钟，
//             也可以通过修改来另行指定。拿锁失败时会不停的重试，具有Watch Dog 自动延期机制
//             默认续30s 每隔30/3=10 秒续到30s
//             */
//            // boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS); // 不带看门狗
//            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS); // 带看门狗,
//            if (tryLock) {
//                log.info("恭喜，抢到锁了！");
//                // 可以把下面这段放开，只用一个线程来测试，看看redisson的看门狗效果
//                 for (int i = 0; i < 30; i++) {
//                     Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
//                     log.info("锁过期时间还有：{}", expire);
//                     Thread.sleep(1000);
//                 }
//            } else {
//                // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
//                log.info("很遗憾，没抢到锁");
//                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//            }

            // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

            Date date = req.getDate();
            String trainCode = req.getTrainCode();
            String start = req.getStart();
            String end = req.getEnd();
            List<ConfirmOrderTicketReq> tickets = req.getTickets();

//            // 保存确认订单表，状态初始
//            DateTime now = DateTime.now();
//            ConfirmOrder confirmOrder = new ConfirmOrder();
//            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setCreateTime(now);
//            confirmOrder.setUpdateTime(now);
//            // 因为此处不是通过请求获取 LoginMemberContext.getId(), 而是通过MQ发送, 所以会出现 null 情况, 从源头解决, 发送前添加 MemberId
////            confirmOrder.setMemberId(LoginMemberContext.getId());
//            confirmOrder.setMemberId(req.getMemberId());
//            confirmOrder.setDate(date);
//            confirmOrder.setTrainCode(trainCode);
//            confirmOrder.setStart(start);
//            confirmOrder.setEnd(end);
//            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
//            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
//            confirmOrder.setTickets(JSON.toJSONString(tickets));
//            confirmOrderMapper.insert(confirmOrder);

             // 从数据库里查出订单
            QueryWrapper<ConfirmOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(ConfirmOrder::getDate, req.getDate())
                    .eq(ConfirmOrder::getTrainCode, req.getTrainCode())
                    .eq(ConfirmOrder::getStatus, ConfirmOrderStatusEnum.INIT.getCode())
                    .orderByAsc(ConfirmOrder::getId);
            List<ConfirmOrder> list = confirmOrderMapper.selectList(queryWrapper);
            ConfirmOrder confirmOrder;
             if (CollUtil.isEmpty(list)) {
                 log.info("找不到原始订单，结束");
                 return;
             } else {
                 log.info("本次处理{}条确认订单", list.size());
                 confirmOrder = list.get(0);
             }

            // 查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            log.info("查出余票记录：{}", dailyTrainTicket);

            // 预扣减余票数量，并判断余票是否足够
            reduceTickets(req, dailyTrainTicket);

            // 最终的选座结果
            List<DailyTrainSeat> finalSeatList = new ArrayList<>();
            // 计算相对第一个座位的偏移值
            // 比如选择的是C1,D2，则偏移值是：[0,5]
            // 比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
            ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
            // 判断是否选择了座位
            if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
                log.info("本次购票有选座");
                // 查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏离值
                List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                log.info("本次选座的座位类型包含的列：{}", colEnumList);

                // 组成和前端两排选座一样的列表，用于作参照的座位列表，例：referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
                // C1 D2 的绝对偏移值=[1,6], 减去第1位的值, 最终得到相对偏移值[0,5]
                // 如果选择的是A1,C1,D1，则偏移值是：[0,1,2]， 减去第1位的值，最终得到相对偏移值[0,1,2]
                List<String> referSeatList = new ArrayList<>();
                for (int i = 1; i <= 2; i++) {
                    for (SeatColEnum seatColEnum : colEnumList) {
                        referSeatList.add(seatColEnum.getCode() + i);
                    }
                }
                log.info("用于作参照的两排座位：{}", referSeatList);

                List<Integer> offsetList = new ArrayList<>();
                // 绝对偏移值，即：在参照座位列表中的位置
                List<Integer> aboluteOffsetList = new ArrayList<>();
                for (ConfirmOrderTicketReq ticketReq : tickets) {
                    // indexOf 去查询数组中的某个索引号
                    int index = referSeatList.indexOf(ticketReq.getSeat());
                    aboluteOffsetList.add(index);
                }
                log.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);
                for (Integer index : aboluteOffsetList) {
                    int offset = index - aboluteOffsetList.get(0);
                    offsetList.add(offset);
                }
                log.info("计算得到所有座位的相对第一个座位的偏移值：{}", offsetList);

                getSeat(finalSeatList,
                        date, // 日期
                        trainCode,
                        ticketReq0.getSeatTypeCode(), // 第一个座位
                        ticketReq0.getSeat().split("")[0], // 第一个座位的座位号： 从A1得到A
                        offsetList, // 偏移值
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );

            } else {
                log.info("本次购票没有选座");
                for (ConfirmOrderTicketReq ticketReq : tickets) {
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticketReq.getSeatTypeCode(),
                            null,
                            null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    );
                }
            }

            log.info("最终选座：{}", finalSeatList);

            // 选中座位后事务处理：
            // 选中座位后事务处理：
            // 座位表修改售卖情况sell；
            // 余票详情表修改余票；
            // 为会员增加购票记录
            // 更新确认订单为成功
            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
            } catch (Exception e) {
                log.error("保存购票信息失败", e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }

            // 删除分布式锁
//            log.info("购票流程结束，释放锁！lockKey：{}", lockKey);
//            redisTemplate.delete(lockKey);
//        } catch (InterruptedException e) {
//            log.error("购票异常: ", e);
        } finally {
            // try finally不能包含加锁的那段代码，否则加锁失败会走到finally里，从而释放别的线程的锁
            log.info("购票流程结束，释放锁！lockKey：{}", lockKey);
            redisTemplate.delete(lockKey);
//            log.info("购票流程结束，释放锁！");
//            if (null != lock && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
        }
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        log.info("购票请求被限流：{}", req);
        // 当前抢票人数太多了，请稍候重试
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        // 获取每张票
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            // 获取n等座的枚举值
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            // jdk17 可以使用switch表达式支持枚举
            switch (seatTypeEnum) {
                // 根据枚举值，减少余票数量
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        // 余票不足
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }

    /**
     * 挑座位，如果有选座，则一次性挑完，如果无选座，则一个一个挑
     *
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offsetList
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        log.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            log.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            log.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    log.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column，有值的话要比对列号
                if (StrUtil.isBlank(column)) {
                    log.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        log.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    log.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    log.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            log.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            log.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            log.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001，本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     * <p>
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位与，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            log.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            log.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            log.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;

        }
    }

}
