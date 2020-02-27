package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {

    void add(CheckGroup checkGroup);

    // void addCheckGroupAndCheckItem(@Param(value = "checkGroupId") Integer checkGroupId, @Param(value = "checkitemId") Integer checkitemId);

    void addCheckGroupAndCheckItem(Map map);

    Page<CheckGroup> findPage(String queryString);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void edit(CheckGroup checkGroup);

    void deleteCheckGroupAndCheckItemById(Integer id);


    Long findCheckGroupAndCheckItemCountByCheckGroupId(Integer id);
    Long findSetmealAndCheckGroupCountByCheckGroupId(Integer id);
    void delete(Integer id);



    List findAll();

    List<CheckGroup> findCheckGroupListBysetmealId(Integer id);

}
