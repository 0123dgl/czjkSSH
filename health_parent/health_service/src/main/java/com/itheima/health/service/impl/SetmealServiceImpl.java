package com.itheima.health.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiniuUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SetmealServiceImpl implements SetmealService {
    //套餐
    @Autowired
    SetmealDao setmealDao;
    //
    @Autowired
    JedisPool jedisPool;


    //新增套餐  参数: 套餐类数据  检查项ID数组
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        // 1：保存 新增套餐数据
        setmealDao.add(setmeal);
        // 2：向套餐和检查组的中间表中插入数据
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            //绑定套餐和检查组的多对多关系
            setSetmealAndCheckGroup(setmeal.getId(), checkgroupIds);
        }
        //将图片名称保存到Redis
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());

        //新增套餐后需要重新生成静态页面
        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
    private void generateMobileStaticHtml() {
        // 查询所有套餐（从数据库查询） 因为使用了mapper的resultMap引入数据, 所以在查询所有套餐的时候, 检查组和检查项也会同步得到数据
        List<Setmeal> list = this.findAll();
        // 生成套餐列表的静态页面
        this.generateMobileSetmealListStaticHtml(list);
        // 生成套餐详情的静态页面
        this.generateMobileSetmealDetailStaticHtml(list);
    }

    // 生成套餐列表的静态页面
    private void generateMobileSetmealListStaticHtml(List<Setmeal> list) {
        Map<String, Object> map = new HashMap<>();
        map.put("setmealList", list);
        // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
        useFreeMarkerGenerateHtml("mobile_setmeal.ftl", "m_setmeal.html", map);
    }

    // 生成套餐详情的静态页面
    private void generateMobileSetmealDetailStaticHtml(List<Setmeal> list) {
        if (list != null && list.size() > 0) {
            for (Setmeal setmeal : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("setmeal", this.findById(setmeal.getId())); // 使用套餐id，查询套餐的详情（包括检查组的集合和检查项的集合）
                // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
                useFreeMarkerGenerateHtml("mobile_setmeal_detail.ftl", "setmeal_detail_" + setmeal.getId() + ".html", map);
            }
        }
    }

    // freemarker 页面静态化// 注入FreeMarkerConfigurer
    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;
    //读取外部的配置路径
    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String output_path;  // 等同于加载D:\\ideaProjects\\85\\health_parent\\healthmobile_web\\src\\main\\webapp\\pages

    // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
    private void useFreeMarkerGenerateHtml(String mobile_ftl, String mobile_html, Map<String, Object> map) {
        //  第一步：创建一个 Configuration 对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer writer = null;
        try {
            // 第二步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate(mobile_ftl);
            // 第三步：创建Writer对象，加载D:\\ideaProjects\\85\\health_parent\\healthmobile_web\\src\\main\\webapp\\pages\\m_setmeal.html
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output_path + "\\" + mobile_html))));
            // 第四步：调用模板对象的 process 方法输出文件（格式，定义html）。
            template.process(map, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //第八步：关闭流。
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.findPage(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    CheckGroupDao checkGroupDao;
    @Autowired
    CheckItemDao checkItemDao;


    @Override
    //使用resultmap方式实现
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
        return setmeal;
    }

    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id) {
        return setmealDao.findCheckGroupIdsBySetmealId(id);
    }

    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        // 使用套餐id，查询数据库对应的套餐，获取数据库存放的img
        Setmeal setmeal_db = setmealDao.findById(setmeal.getId());
        String img_db = setmeal_db.getImg();
        // 如果页面传递的图片名称和数据库存放的图片名称不一致，说明图片更新，需要删除七牛云之前数据库的图片
        if (setmeal.getImg() != null && !setmeal.getImg().equals(img_db)) {
            QiniuUtils.deleteFileFromQiniu(img_db);

            //将图片名称从Redis中删除，key值为setmealPicDbResources
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, img_db);
            //将图片名称从Redis中删除，key值为setmealPicResources
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, img_db);


            // 将页面更新的图片，存放到key值为SETMEAL_PIC_DB_RESOURCES的redis中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());


        }
        //3：更新套餐基本信息
        setmealDao.edit(setmeal);
        //1：根据套餐id删除中间表数据（清理原有关联关系）
        setmealDao.deleteAssociation(setmeal.getId());
        //2：向中间表(t_setmeal_checkgroup)插入数据（建立套餐和检查组关联关系）
        setSetmealAndCheckGroup(setmeal.getId(), checkgroupIds);

        //修改套餐后需要重新生成静态页面
        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    @Override
    public void delete(Integer id) {
        // 使用套餐id，查询套餐和检查组中间表
        Long count = setmealDao.findSetmealAndCheckGroupCountBySetmealId(id);
        // 存在数据
        if (count > 0) {
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDCHECKITEMRROR);
        }

        // 使用套餐id，查询数据库对应的套餐，获取数据库存放的img
        Setmeal setmeal_db = setmealDao.findById(id);
        // 获取存放的图片信息
        String img_db = setmeal_db.getImg();
        // 需要先删除七牛云之前数据库的图片
        if (img_db != null && !"".equals(img_db)) {
            QiniuUtils.deleteFileFromQiniu(img_db);
            //将图片名称从Redis中删除，key值为setmealPicDbResources
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, img_db);
            //将图片名称从Redis中删除，key值为setmealPicResources
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, img_db);


        }

        // 删除套餐
        setmealDao.delete(id);

        //删除套餐后需要重新生成静态页面
        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    //绑定套餐和检查组的多对多关系
    private void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkgroupIds) {
        for (Integer checkgroupId : checkgroupIds) {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("setmealId", setmealId);
            map.put("checkgroupId", checkgroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }
}
