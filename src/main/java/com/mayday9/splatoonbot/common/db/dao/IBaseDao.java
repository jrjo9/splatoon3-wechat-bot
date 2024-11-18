package com.mayday9.splatoonbot.common.db.dao;

import com.mayday9.splatoonbot.common.db.Props;
import com.mayday9.splatoonbot.common.db.Sort;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {

    /*默认批次提交数量*/
    int DEFAULT_BATCH_SIZE = 1000;

    T findById(Serializable id);

    T findOneBy(String propertyName, Object propertyValue);

    T findOneBy(String propertyName, Collection<?> propertyValue);

    T findOneBy(Map<String, Object> eqParams);

    T findOneBy(Map<String, Object> eqParams, Map<String, Object> neParams);

    T findOneBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams);

    T findOneBy(Props props);

    List<T> findByIds(Collection<? extends Serializable> ids);

    List<T> findBy(String propertyName, Object propertyValue);

    List<T> findBy(String propertyName, Collection<?> propertyValue);

    List<T> findBy(String propertyName, Object propertyValue, Sort sort);

    List<T> findAll();

    List<T> findAll(Sort sort);

    List<T> findBy(Map<String, Object> eqParams);

    List<T> findBy(Map<String, Object> eqParams, Map<String, Object> neParams);

    List<T> findBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams);

    List<T> findBy(Props props);

    T findActiveOneBy(String propertyName, Object propertyValue);

    T findActiveOneBy(String propertyName, Collection<?> propertyValue);

    T findActiveById(Serializable id);

    T findActiveOneBy(Map<String, Object> eqParams);

    T findActiveOneBy(Map<String, Object> eqParams, Map<String, Object> neParams);

    T findActiveOneBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams);

    T findActiveOneBy(Props props);

    List<T> findActiveByIds(Collection<? extends Serializable> ids);

    List<T> findActiveBy(String propertyName, Object propertyValue);

    List<T> findActiveBy(String propertyName, Collection<?> propertyValue);

    List<T> findActiveBy(String propertyName, Object propertyValue, Sort sort);

    List<T> findActiveBy(Props props);

    List<T> findAllActive();

    List<T> findAllActive(Sort sort);

    int count(Map<String, Object> eqParams);

    int count(Map<String, Object> eqParams, Map<String, Object> neParams);

    int count(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams);

    int count(Props props);

    int delete(Serializable id);

    int deleteById(Serializable id);

    int deleteByIds(Collection<? extends Serializable> ids);

    int deleteBy(String propertyName, Object propertyValue);

    int deleteBy(String propertyName, Collection<?> propertyValue);

    boolean save(T entity);

    boolean saveBatch(Collection<T> entityList);

    boolean saveBatch(Collection<T> entityList, int batchSize);

    boolean updateById(T entity);

    boolean updateBatchById(Collection<T> entityList);

    boolean updateBatchById(Collection<T> entityList, int batchSize);

    boolean saveOrUpdate(T entity);

    boolean saveOrUpdateBatch(Collection<T> entityList);

    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);
}
