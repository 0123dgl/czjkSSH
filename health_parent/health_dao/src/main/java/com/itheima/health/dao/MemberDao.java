package com.itheima.health.dao;

import com.itheima.health.pojo.Member;
import org.springframework.stereotype.Repository;

/**
 * 持久层Dao接口
 */
@Repository
public interface MemberDao {
    // 添加会员
    public void add(Member member);
    // 根据手机号查询会员信息（唯一）
    public Member findByTelephone(String telephone);
}