package com.example.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.member.domain.Member;
import com.example.member.mapper.MemberMapper;
import com.example.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lee
 * @description
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    @Override
    public long register(String mobile) {

        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("mobile", mobile));
        List<Member> list = memberMapper.selectList(queryWrapper);

        if (CollUtil.isNotEmpty(list)) {
//            return list.get(0).getId();
            throw new RuntimeException("手机号已经注册");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

}
