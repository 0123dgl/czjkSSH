package com.itheima.health.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    CheckItemDao checkItemDao;

    @Override
    public List<CheckItem> findAll() {
        List<CheckItem> list = checkItemDao.findAll();
        return list;
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //开发方式1: 传统开发,  写sql语句完成分页(数据库不同,运维改变数据库较难)
        //1. 查询总记录数,  定义sql, select count(*)  form t_checkitem where code=#{value} or mame=#{value}
        // long total;
        //2. 使用查询条件, 查询出当前页的数据, 定义sql select * form t_checkitem  where code=#{value} or mame=#{value} limt ?, ?
        //limt ?, ? (第一个? 表示当前页从 第几条开始检索, 默认是0, 第二个? 表示当前页最多显示的条数.
        //第一个?  就等于   (currentpage-1)*pagesize      第二个?  就等于 pagesize
        // List<CheckItem>  rows    封装 pageresult   返回  return new PageResult(total,rows);

        //开发方式二: 使用数据库分页插件 方法1       原理  不使用mysql的  limt进行查询.
        //1.  初始化 数据库分页数据
        PageHelper.startPage(currentPage, pageSize);

        //2. 查询  返回数据库分页插件内的page对象
        // Page<CheckItem> page = checkItemDao.findPage(queryString);
        // //封装PageResult
        // return new PageResult(page.getTotal(), page.getResult());

        //方法2  使用pageinfo对象
        List<CheckItem> list = checkItemDao.findPage(queryString);
        PageInfo<CheckItem> pageInfo = new PageInfo<>(list);

        // //封装PageResult
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void delete(Integer id) {


        //查询当前检查项是否和检查组关联
        long count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0){
            //当前检查项被引用，不能删除
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDCHECKITEMRROR);
        }checkItemDao.delete(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }
}
