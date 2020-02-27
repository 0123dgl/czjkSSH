package com.itheima.health.service;

import com.itheima.health.pojo.Member;

/**
 * 体检预约服务接口
 */
public interface MemberService {


    Member findByTelephone(String telephone);

    void add(Member member);
}