package com.example.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.exception.BusinessException;
import com.example.common.exception.BusinessExceptionEnum;
import com.example.common.util.JwtUtil;
import com.example.common.util.SnowUtil;
import com.example.member.domain.Member;
import com.example.member.mapper.MemberMapper;
import com.example.member.req.MemberLoginReq;
import com.example.member.req.MemberRegisterReq;
import com.example.member.req.MemberSendCodeReq;
import com.example.member.resp.MemberLoginResp;
import com.example.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    @Override
    public long register(MemberRegisterReq req) {

        String mobile = req.getMobile();

        Member memberDB = selectByMobile(mobile);

        // 手机号不存在, 则插入一条记录
        if (ObjectUtil.isNull(memberDB)) {
//            return list.get(0).getId();
//            throw new RuntimeException("手机号已经注册");
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
//        member.setId(System.currentTimeMillis());
//        member.setId(1L);
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();

        Member memberDB = selectByMobile(mobile);

        // 手机号不存在, 则插入一条记录
        if (ObjectUtil.isNull(memberDB)) {
            log.info("手机号不存在, 则插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            log.info("手机号存在, 不插入记录");
        }

        // 生成短信验证码
//        String code = RandomUtil.randomString(4);
        String code = "8888";
        log.info("生成短信验证码: {}", code);

        // 保存短信记录表： 手机号, 验证码, 有效期, 是否已使用, 业务类型, 发送时间, 使用时间
        log.info("保存短信记录表");

        // 对接短信平台, 发送短信
        log.info("对接短信平台");
    }

    public MemberLoginResp login(MemberLoginReq req) {
        String mobile = req.getMobile();
        String code = req.getCode();

        Member memberDB = selectByMobile(mobile);

        // 手机号不存在, 则插入一条记录
        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        // 校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
//        Map<String, Object> map = BeanUtil.beanToMap(memberLoginResp);
//        String key = "eddie";
//        String token = JWTUtil.createToken(map, key.getBytes());
        String token = JwtUtil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
        log.info("用户登录信息: {}", JSONUtil.toJsonStr(memberLoginResp));

        return memberLoginResp;
    }

    private Member selectByMobile(String mobile) {
        // 查询手机号是否存在
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("mobile", mobile));
        List<Member> list = memberMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
