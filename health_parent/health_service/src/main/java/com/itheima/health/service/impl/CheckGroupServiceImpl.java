package com.itheima.health.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    CheckGroupDao checkGroupDao;


    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //第一步: 接收到检查组的对象checkgroup,,执行保存检查组,  保存检查组的同时,返回检查组的ID
        checkGroupDao.add(checkGroup);
        //第二步: 接收到Integer 类型的数组,  存放检查项的id, 和保存返回检查组id  ,向中间表中插入数据
        this.setCheckGroupAndCheckItem(checkGroup.getId(), checkitemIds);
        // 这里插入了过后可能没拿到新增的id,然后更新中间表的时候报了checkgroup_id不能为

    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {

//初始化分页对象
        PageHelper.startPage(currentPage, pageSize);
//查询
        Page<CheckGroup> page = checkGroupDao.findPage(queryString);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup = checkGroupDao.findById(id);
        return checkGroup;
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        List<Integer> list = checkGroupDao.findCheckItemIdsByCheckGroupId(id);


        return list;
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //修改编辑检查组的相关信息字段, 数据为空,  保存原来的数据.
        checkGroupDao.edit(checkGroup);
// 修改检查组和检查项的中间表
        //1. 使用检查组的id  先删除之前的数据
        checkGroupDao.deleteCheckGroupAndCheckItemById(checkGroup.getId());
        //2. 重新建立中间表的关系  之前做过, 直接调用就可以了
        this.setCheckGroupAndCheckItem(checkGroup.getId(), checkitemIds);
    }

    @Override
    public void delete(Integer id) {
        System.out.println(id);
        // 使用检查组id，查询检查组和检查项中间表
        Long count1 = checkGroupDao.findCheckGroupAndCheckItemCountByCheckGroupId(id);
        // 存在数据
        if (count1 > 0) {
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDCHECKITEMRROR);
        }
        // // 使用检查组id，查询套餐和检查组中间表
        Long count2 = checkGroupDao.findSetmealAndCheckGroupCountByCheckGroupId(id);
        // 存在数据
        if (count2 > 0) {
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDCHECKITEMRROR);
        }
        checkGroupDao.delete(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        List list = checkGroupDao.findAll();
        return list;
    }

    //向 检查组和检查项的中间表插入数据的方法
    private void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
        if (checkitemIds != null && checkitemIds.length > 0) {
            for (Integer checkItemId : checkitemIds) {
                //保存,(传递多个参数,要在dao的方法中, 通过@param 指定 方法参数的名称, 例如: @Param(value = **"checkGroupId"**) Integer checkGroupId, @Param(value = **"checkitemId"**)Integer checkitemId
                //checkGroupDao.addCheckGroupAndCheckItem(checkGroupId,checkitemId);
                //传递map方法(等同于javabean)

                System.out.println(checkGroupId);
                System.out.println(checkItemId);

                Map map = new HashMap();
                map.put("checkGroupid", checkGroupId);
                map.put("checkItemid", checkItemId);
                checkGroupDao.addCheckGroupAndCheckItem(map);
            }
        }
    }
}
