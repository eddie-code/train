package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.business.domain.SkTokenExample;
import com.example.business.enums.RedisKeyPreEnum;
import com.example.business.mapper.cust.SkTokenMapperCust;
import com.example.business.service.DailyTrainSeatService;
import com.example.business.service.DailyTrainStationService;
import com.example.common.resp.PageResp;
import com.example.common.util.SnowUtil;
import com.example.business.domain.SkToken;
import com.example.business.mapper.SkTokenMapper;
import com.example.business.req.SkTokenQueryReq;
import com.example.business.req.SkTokenSaveReq;
import com.example.business.resp.SkTokenQueryResp;
import com.example.business.service.SkTokenService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SkTokenServiceImpl implements SkTokenService {

    @Autowired
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private SkTokenMapperCust skTokenMapperCust;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 初始化
     */
    @Override
    public void genDaily(Date date, String trainCode) {
        log.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
//        SkTokenExample skTokenExample = new SkTokenExample();
//        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
//        skTokenMapper.deleteByExample(skTokenExample);

        QueryWrapper<SkToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SkToken::getTrainCode, trainCode)
                .eq(SkToken::getDate, date);
        skTokenMapper.delete(queryWrapper);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        log.info("车次【{}】座位数：{}", trainCode, seatCount);

        // 添加 date 参数
        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        log.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4); // 总的数量 = 座位数 * 到站数
        log.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    @Override
    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateById(skToken);
        }
    }

    @Override
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        log.info("查询条件：{}", req);
        QueryWrapper<SkToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .orderByDesc(SkToken::getId);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        // 1. 分页查询
        Page<SkToken> page = new Page<>(req.getPage(), req.getSize());
        Page<SkToken> pageInfo = skTokenMapper.selectPage(page, queryWrapper);

        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        // 2. 将查询出来的数据转换为返回的对象
        List<SkToken> skTokenList = pageInfo.getRecords();
        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        // 3. 将分页查询的结果转换为返回的对象
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        skTokenMapper.deleteById(id);
    }

    /**
     * 获取令牌
     */
    @Override
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        log.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);

        // 先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            log.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
        } else {
            log.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
            return false;
        }

        // 先查询缓存
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount != null) {
            log.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count < 0L) {
                log.error("获取令牌失败：{}", skTokenCountKey);
                return false;
            } else {
                log.info("获取令牌后，令牌余数：{}", count);
                // 每次都刷新缓存, 防止热点key失效, 防止缓存穿透
                redisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
                // 每获取5个令牌更新一次数据库
                if (count % 5 == 0) {
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            // 缓存没有情况下, 再查询数据库
            log.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            QueryWrapper<SkToken> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(SkToken::getDate, date)
                    .eq(SkToken::getTrainCode, trainCode)
                    .orderByDesc(SkToken::getId);
            List<SkToken> tokenCountList = skTokenMapper.selectList(queryWrapper);
            if (CollUtil.isEmpty(tokenCountList)) {
                log.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }

            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0) {
                log.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            // 令牌还有余量
            // 令牌余数-1
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            log.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
            // 不需要更新数据库，只要放缓存即可
            redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            // skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }

        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
//        int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//        if (updateCount > 0) {
//            // updateCount 大于0，说明令牌有效，可以进入购票主流程
//            return true;
//        } else {
//            // updateCount 小于等于0，说明令牌无效，不能进入购票主流程
//            return false;
//        }
    }
}
