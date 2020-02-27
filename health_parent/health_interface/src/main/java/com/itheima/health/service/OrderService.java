package com.itheima.health.service;

import com.itheima.health.entity.Result;

import java.util.Map;

/**
 * 体检预约服务接口
 */
public interface OrderService {
    Result order(Map map) throws Exception;

    Map findById4Detail(Integer id);
}