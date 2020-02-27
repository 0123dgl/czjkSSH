package com.itheima.health.service;

import com.itheima.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {


    void addList(List<OrderSetting> orderSettingList);

    List<Map> getOrderSettingByMonth(String date); //参数格式为：2019-03

    void editNumberByDate(OrderSetting orderSetting);
}
