package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {

    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    Page<CheckItem> findPage(String queryString);

    void delete(Integer id);

    long findCountByCheckItemId(Integer id);


    CheckItem findById(Integer id);

    void edit(CheckItem checkItem);

    List<CheckItem> findCheckItemListById(Integer id);
}
