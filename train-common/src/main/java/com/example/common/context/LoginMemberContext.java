package com.example.common.context;

import com.example.common.resp.MemberLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lee
 * @description
 */
public class LoginMemberContext {

    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

    private static final ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    public static MemberLoginResp getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginResp member) {
        LoginMemberContext.member.set(member);
    }

    public static void remove() {
        LoginMemberContext.member.remove();
    }

    public static Long getId() {
        // 若线程变量没有会员ID的情况 try catch
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}

