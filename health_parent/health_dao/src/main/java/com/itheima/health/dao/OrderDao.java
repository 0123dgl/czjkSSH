package com.itheima.health.dao;

import com.itheima.health.pojo.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 持久层Dao接口
 */
@Repository
public interface OrderDao {


    List<Order> findByCondition(Order order);

    void add(Order order);

    Map findById4Detail(Integer id);
}