package com.mybaitsplus.devtools.core.base;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mybaitsplus.devtools.core.Constants;
import com.mybaitsplus.devtools.core.util.CacheUtil;
import com.mybaitsplus.devtools.core.util.DataUtil;
import com.mybaitsplus.devtools.core.util.ExceptionUtil;
import com.mybaitsplus.devtools.core.util.InstanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑层基类<br/>
 * 继承基类后必须配置CacheConfig(cacheNames="")
 *
 */
@Slf4j
public abstract class BaseService<M extends BaseMapper<T>, T extends Serializable> extends ServiceImpl<M, T> implements ApplicationContextAware {

    @Autowired
    protected M mapper;

    protected ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    /** 分页查询 */
    public Page<T> getObjPage(Map<String, Object> params) {
        Integer current = 1;
        Integer size = 10;
        String orderBy = null;//"id"
        if (DataUtil.isNotEmpty(params.get("pageNum"))) {
            current = Integer.valueOf(params.get("pageNum").toString());
        }
        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
            size = Integer.valueOf(params.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
            orderBy = (String)params.get("orderBy");
            params.remove("orderBy");
        }
        if (size == -1) {
            return new Page<T>();
        }
        Page<T>  page = new Page<T>(current, size);
        if(DataUtil.isNotEmpty(orderBy)){
            page.setOrderByField(orderBy);
        }
        page.setAsc(false);
        return page;
    }
    /** 分页查询 */
    public static  Page<Map<String, Object>> getPageMap(Map<String, Object> params) {
        Integer current = 1;
        Integer size = 10;
        String orderBy = null;//"id"
        if (DataUtil.isNotEmpty(params.get("pageNum"))) {
            current = Integer.valueOf(params.get("pageNum").toString());
        }
        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
            size = Integer.valueOf(params.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
            orderBy = (String)params.get("orderBy");
            params.remove("orderBy");
        }
        if (size == -1) {
            return new Page<Map<String, Object>>();
        }
        Page<Map<String, Object>>  page = new Page<Map<String, Object>>(current, size);
        if(DataUtil.isNotEmpty(orderBy)){
            page.setOrderByField(orderBy);
        }
        page.setAsc(false);
        return page;
    }

    /** 分页查询 */
    public static Page<Long> getPage(Map<String, Object> params) {
        int current = 1;
        int size = 10;
        String orderBy = null;//"id"
        if (DataUtil.isNotEmpty(params.get("pageNum"))) {
            current = Integer.valueOf(params.get("pageNum").toString());
        }
        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
            size = Integer.valueOf(params.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
            orderBy = (String)params.get("orderBy");
            params.remove("orderBy");
        }
        if (size == -1) {
            return new Page<Long>();
        }
        Page<Long> page = new Page<Long>(current, size);
        if(DataUtil.isNotEmpty(orderBy)){
            page.setOrderByField(orderBy);
        }
        page.setAsc(false);
        return page;
    }

    /** 根据Id查询(默认类型T) */
    public Page<T> getPage(Page<Long> ids) {
        if (ids != null) {
            Page<T> page = new Page<T>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<T> records = InstanceUtil.newArrayList();
            for (Long id : ids.getRecords()) {
                records.add(this.queryById(id));
            }
            page.setRecords(records);
            return page;
        }
        return new Page<T>();
    }

    /** 根据Id查询(默认类型T) */
    public Page<Map<String, Object>> getPageMap(Page<Long> ids) {
        if (ids != null) {
            Page<Map<String, Object>> page = new Page<Map<String, Object>>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<Map<String, Object>> records = InstanceUtil.newArrayList();
            for (Long id : ids.getRecords()) {
                records.add(InstanceUtil.transBean2Map(this.queryById(id)));
            }
            page.setRecords(records);
            return page;
        }
        return new Page<Map<String, Object>>();
    }

    /** 根据Id查询(cls返回类型Class) */
    public <K> Page<K> getPage(Page<Long> ids, Class<K> cls) {
        if (ids != null) {
            Page<K> page = new Page<K>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<K> records = InstanceUtil.newArrayList();
            for (Long id : ids.getRecords()) {
                T t = this.queryById(id);
                K k = InstanceUtil.to(t, cls);
                records.add(k);
            }
            page.setRecords(records);
            return page;
        }
        return new Page<K>();
    }

    /** 根据Id查询(默认类型T) */
    public List<T> getList(List<Long> ids) {
        List<T> list = InstanceUtil.newArrayList();
        if (ids != null) {
            for (Long id : ids) {
                list.add(this.queryById(id));
            }
        }
        return list;
    }

    /** 根据Id查询(cls返回类型Class) */
    public <K> List<K> getList(List<Long> ids, Class<K> cls) {
        List<K> list = InstanceUtil.newArrayList();
        if (ids != null) {
            for (Long id : ids) {
                T t = this.queryById(id);
                K k = InstanceUtil.to(t, cls);
                list.add(k);
            }
        }
        return list;
    }

    @Transactional
    public void delete(Long id) {
        try {
            mapper.deleteById(id);
            String key = getCacheKey(id);
            if (StringUtils.isNotBlank(key)) {
                CacheUtil.getCache().del(getCacheKey(id));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    public T update(T record,Long id) {
        try {
            if (id == null) {
                mapper.insert(record);
            } else {
                T org = queryById(id);
                String lockKey = getLockKey(id);
                if (StringUtils.isBlank(lockKey)) {
                    T update = InstanceUtil.getDiff(org, record);
                    mapper.updateById(update);
                } else {
                    if (CacheUtil.getLock(lockKey)) {
                        try {
                            T update = InstanceUtil.getDiff(org, record);
                            mapper.updateById(update);
                            record = mapper.selectById(id);
                            CacheUtil.getCache().set(getCacheKey(id), record);
                        } finally {
                            CacheUtil.unlock(lockKey);
                        }
                    } else {
                        sleep(20);
                        return update(record,id);
                    }
                }
            }
        } catch (DuplicateKeyException e) {
            String msg = ExceptionUtil.getStackTraceAsString(e);
            log.error(Constants.Exception_Head + msg, e);
            throw new RuntimeException("已经存在相同的配置.");
        } catch (Exception e) {
            String msg = ExceptionUtil.getStackTraceAsString(e);
            log.error(Constants.Exception_Head + msg, e);
            throw new RuntimeException(msg);
        }
        return record;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public T queryById(Long id) {
        try {
            String key = getCacheKey(id);
            if (StringUtils.isBlank(key)) {
                return mapper.selectById(id);
            } else {
                T record = (T)CacheUtil.getCache().get(key);
                if (record == null) {
                    String lockKey = getLockKey(id);
                    if (CacheUtil.getLock(lockKey)) {
                        record = mapper.selectById(id);
                        CacheUtil.getCache().set(key, record);
                        CacheUtil.getCache().del(lockKey);
                    } else {
                        sleep(20);
                        return queryById(id);
                    }
                }
                return record;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Page<T> query(Map<String, Object> params) {
        Page<Long> page = getPage(params);
        page.setRecords(mapper.selectIdPage(page, params));
        return getPage(page);
    }

    public Page<Map<String, Object>> queryMap(Map<String, Object> params) {
        Page<Long> page = getPage(params);
        page.setRecords(mapper.selectIdPage(page, params));
        return getPageMap(page);
    }

    public List<T> queryList(Map<String, Object> params) {
        List<Long> ids = mapper.selectIdPage(params);
        List<T> list = getList(ids);
        return list;
    }

    protected <P> Page<P> query(Map<String, Object> params, Class<P> cls) {
        Page<Long> page = getPage(params);
        page.setRecords(mapper.selectIdPage(page, params));
        return getPage(page, cls);
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(RandomUtils.nextLong(10, millis));
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    /** 获取缓存键值 */
    protected String getCacheKey(Object id) {
        String cacheName = getCacheKey();
        if (StringUtils.isBlank(cacheName)) {
            return null;
        }
        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":").append(id).toString();
    }

    /** 获取缓存键值 */
    protected String getLockKey(Object id) {
        String cacheName = getCacheKey();
        if (StringUtils.isBlank(cacheName)) {
            return null;
        }
        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":LOCK:").append(id).toString();
    }

    /**
     * @return
     */
    private String getCacheKey() {
        Class<?> cls = getClass();
        String cacheName = Constants.cacheKeyMap.get(cls);
        if (StringUtils.isBlank(cacheName)) {
            CacheConfig cacheConfig = cls.getAnnotation(CacheConfig.class);
            if (cacheConfig == null) {
                return null;
            } else if (cacheConfig.cacheNames() == null || cacheConfig.cacheNames().length < 1) {
                cacheName = getClass().getName();
            } else {
                cacheName = cacheConfig.cacheNames()[0];
            }
            Constants.cacheKeyMap.put(cls, cacheName);
        }
        return cacheName;
    }

    public List<T> selectObjs(Map<String, Object> params) {
        return mapper.selectObjsPage(params);
    }

    public Page<T> selectPage(Map<String, Object> params) {
        Page<T> page = getObjPage(params);
        page.setRecords(mapper.selectObjsPage(page,params));
        return page;
    }

    public Page<Map<String, Object>> selectMaps(Map<String, Object> params) {
        Page<T> page = getObjPage(params);
        page.setRecords(mapper.selectObjsPage(page,params));
        return getObjPageMap(page);
    }

    /** 根据Id查询(默认类型T) */
    public Page<Map<String, Object>> getObjPageMap(Page<T> ids) {
        if (ids != null) {
            Page<Map<String, Object>> page = new Page<Map<String, Object>>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<Map<String, Object>> records = InstanceUtil.newArrayList();
            for (T id : ids.getRecords()) {
                records.add(InstanceUtil.transBean2Map(id));
            }
            page.setRecords(records);
            return page;
        }
        return new Page<Map<String, Object>>();
    }



    public void saveBatch(List<T> list){
        saveBatch(list,"",1000);
    }

    public void saveBatch(List<T> list,int batchCount){
        saveBatch(list,"",batchCount);
    }
    /**
     * suffix 表后缀
     */
    public void saveBatch(List<T> list,String suffix){
        saveBatch(list,suffix,1000);
    }

    /**
     * 批量操作
     * @param list	集合
     * @param batchCount 批量插入大小
     */
    public void saveBatch(List<T> list,String suffix,int batchCount){
        if (CollectionUtils.isEmpty(list)) {
            log.warn("Error: list must not be empty");
            return ;
        }
        int batchLastIndex = batchCount;// 每批最后一个的下标
        for (int index = 0; index < list.size();) {
            if (batchLastIndex >= list.size()) {
                batchLastIndex = list.size();
                mapper.insertBatch(suffix,list.subList(index, batchLastIndex));
                break;// 数据插入完毕，退出循环
            } else {
                mapper.insertBatch(suffix,list.subList(index, batchLastIndex));
                index = batchLastIndex;// 设置下一批下标
                batchLastIndex = index + (batchCount - 1);
            }
        }
    }

    public void updateBatch(List<T> list,String suffix){
        updateBatch(list,suffix,1000);
    }

    public void updateBatch(List<T> list,String suffix,int batchCount){
        if (CollectionUtils.isEmpty(list)) {
            log.warn("Error: list must not be empty");
            return ;
        }
        int batchLastIndex = batchCount;// 每批最后一个的下标
        for (int index = 0; index < list.size();) {
            if (batchLastIndex >= list.size()) {
                batchLastIndex = list.size();
                mapper.updateBatch(suffix, list.subList(index, batchLastIndex));
                break;// 数据插入完毕，退出循环
            } else {
                mapper.updateBatch(suffix, list.subList(index, batchLastIndex));
                index = batchLastIndex;// 设置下一批下标
                batchLastIndex = index + (batchCount - 1);
            }
        }
    }

}
