package com.mayday9.splatoonbot.common.db.dao.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.common.db.Props;
import com.mayday9.splatoonbot.common.db.Sort;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import com.mayday9.splatoonbot.common.util.core.Func;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseDaoImpl<M extends BaseMapper<T>, T> implements IBaseDao<T> {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    public M getBaseMapper() {
        return baseMapper;
    }

    protected Class<T> entityClass = currentModelClass();

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected Class<T> mapperClass = currentMapperClass();

    @Override
    public T findById(Serializable id) {
        if (id == null) {
            throw new RuntimeException("param id is required");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public T findActiveById(Serializable id) {
        if (id == null) {
            throw new RuntimeException("param id is required");
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(getColumnName("id"), id);
        queryWrapper.ne("is_delete", 1);

        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<T> findByIds(Collection<? extends Serializable> ids) {
        if (ids == null || ids.size() == 0) {
            return new ArrayList<>();
        }
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public List<T> findActiveByIds(Collection<? extends Serializable> ids) {
        if (ids == null || ids.size() == 0) {
            return new ArrayList<>();
        }

        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().in(!Objects.isNull(ids), getColumnName("id"), ids);
        queryWrapper.ne("is_delete", 1);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq
     */
    @Override
    public List<T> findBy(String propertyName, Object propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        return baseMapper.selectList(new QueryWrapper<T>().eq(columnName, propertyValue));
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq
     */
    @Override
    public List<T> findBy(Map<String, Object> eqParams) {
        if (Func.isEmpty(eqParams)) {
            return new ArrayList<>();
        }
        return baseMapper.selectList(queryWrapper(eqParams, null, null));
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne
     */
    @Override
    public List<T> findBy(Map<String, Object> eqParams, Map<String, Object> neParams) {
        return baseMapper.selectList(queryWrapper(eqParams, neParams, null));
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne,in
     */
    @Override
    public List<T> findBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        return baseMapper.selectList(queryWrapper(eqParams, neParams, inParams));
    }

    @Override
    public List<T> findBy(Props params) {
        return baseMapper.selectList(queryWrapper(params));
    }

    /**
     * 按字段查询，返回所有匹配的记录, in
     */
    @Override
    public List<T> findBy(String propertyName, Collection<?> propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        return baseMapper.selectList(new QueryWrapper<T>().in(!Objects.isNull(propertyValue), columnName, propertyValue));
    }

    @Override
    public List<T> findActiveBy(String propertyName, Object propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(columnName, propertyValue);
        queryWrapper.ne("is_delete", 1);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<T> findActiveBy(String propertyName, Collection<?> propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().in(!Objects.isNull(propertyValue), columnName, propertyValue);
        queryWrapper.ne("is_delete", 1);
        return baseMapper.selectList(queryWrapper);
    }

    //返回所有匹配的记录并排序
    @Override
    public List<T> findBy(String propertyName, Object propertyValue, Sort sort) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        QueryWrapper<T> query = new QueryWrapper<T>().eq(columnName, propertyValue);
        buildSort(sort, query);
        return baseMapper.selectList(query);
    }

    /**
     * 返回所有匹配的Active记录并排序
     */
    @Override
    public List<T> findActiveBy(String propertyName, Object propertyValue, Sort sort) {
        if (Func.isEmpty(propertyValue)) {
            return new ArrayList<>();
        }
        String columnName = getColumnName(propertyName);
        QueryWrapper<T> query = new QueryWrapper<T>().eq(columnName, propertyValue);
        query.ne("is_delete", 1);
        buildSort(sort, query);
        return baseMapper.selectList(query);
    }

    @Override
    public List<T> findActiveBy(Props params) {
        return baseMapper.selectList(queryActiveWrapper(params));
    }

    @Override
    public T findOneBy(String propertyName, Object propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return null;
        }
        String columnName = getColumnName(propertyName);
        PageHelper.startPage(1, 1);
        List<T> list = baseMapper.selectList(new QueryWrapper<T>().eq(columnName, propertyValue));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    @Override
    public T findOneBy(String propertyName, Collection<?> propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return null;
        }
        String columnName = getColumnName(propertyName);
        List<T> list = baseMapper.selectList(new QueryWrapper<T>().in(!Objects.isNull(propertyValue), columnName, propertyValue));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    //按字段查询，返回第一条Active 记录
    @Override
    public T findActiveOneBy(String propertyName, Object propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return null;
        }
        String columnName = getColumnName(propertyName);
        PageHelper.startPage(1, 1);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(columnName, propertyValue);
        queryWrapper.ne("is_delete", 1);
        List<T> list = baseMapper.selectList(queryWrapper);
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    //按字段查询，返回第一条Active 记录
    @Override
    public T findActiveOneBy(String propertyName, Collection<?> propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return null;
        }
        String columnName = getColumnName(propertyName);
        PageHelper.startPage(1, 1);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().in(!Objects.isNull(propertyValue), columnName, propertyValue);
        queryWrapper.ne("is_delete", 1);
        List<T> list = baseMapper.selectList(queryWrapper);
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq
     */
    @Override
    public T findOneBy(Map<String, Object> eqParams) {
        if (Func.isEmpty(eqParams)) {
            return null;
        }
        List<T> list = baseMapper.selectList(queryWrapper(eqParams, null, null));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq
     */
    @Override
    public T findActiveOneBy(Map<String, Object> eqParams) {
        if (Func.isEmpty(eqParams)) {
            return null;
        }
        List<T> list = baseMapper.selectList(queryActiveWrapper(eqParams, null, null));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne
     */
    @Override
    public T findOneBy(Map<String, Object> eqParams, Map<String, Object> neParams) {
        List<T> list = baseMapper.selectList(queryWrapper(eqParams, neParams, null));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne
     */
    @Override
    public T findActiveOneBy(Map<String, Object> eqParams, Map<String, Object> neParams) {
        List<T> list = baseMapper.selectList(queryActiveWrapper(eqParams, neParams, null));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne,in
     */
    @Override
    public T findOneBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        List<T> list = baseMapper.selectList(queryWrapper(eqParams, neParams, inParams));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    @Override
    public T findOneBy(Props params) {
        List<T> list = baseMapper.selectList(queryWrapper(params));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    /**
     * 按字段查询，返回所有匹配的记录, eq,ne,in
     */
    @Override
    public T findActiveOneBy(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        List<T> list = baseMapper.selectList(queryActiveWrapper(eqParams, neParams, inParams));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    @Override
    public T findActiveOneBy(Props params) {
        List<T> list = baseMapper.selectList(queryActiveWrapper(params));
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    public int count(Map<String, Object> eqParams) {
        Integer result = baseMapper.selectCount(queryWrapper(eqParams, null, null));
        return retCount(result);
    }

    public int count(Map<String, Object> eqParams, Map<String, Object> neParams) {
        Integer result = baseMapper.selectCount(queryWrapper(eqParams, neParams, null));
        return retCount(result);
    }

    public int count(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        Integer result = baseMapper.selectCount(queryWrapper(eqParams, neParams, inParams));
        return retCount(result);
    }

    public int count(Props params) {
        Integer result = baseMapper.selectCount(queryWrapper(params));
        return retCount(result);
    }

    /*Lambda方式自由组合查询条件*/
    public List<T> find(LambdaQueryWrapper<T> queryWrapper) {
        return baseMapper.selectList(queryWrapper);
    }

    /*Lambda方式自由组合查询条件，返回第一条记录*/
    public T findOne(LambdaQueryWrapper<T> queryWrapper) {
        PageHelper.startPage(1, 1);
        List<T> list = baseMapper.selectList(queryWrapper);
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    //返回表所有记录
    @Override
    public List<T> findAll() {
        return baseMapper.selectList(null);
    }

    //返回表所有Active的记录
    @Override
    public List<T> findAllActive() {
        return baseMapper.selectList(Wrappers.<T>query().ne("is_delete", 1));
    }

    //返回表所有记录并排序
    @Override
    public List<T> findAll(Sort sort) {
        QueryWrapper<T> query = new QueryWrapper<T>();
        buildSort(sort, query);
        return baseMapper.selectList(query);
    }

    //返回表所有Active记录并排序
    @Override
    public List<T> findAllActive(Sort sort) {
        QueryWrapper<T> query = new QueryWrapper<T>();
        query.ne("is_delete", 1);
        buildSort(sort, query);
        return baseMapper.selectList(query);
    }

    //组装排序语句
    private void buildSort(Sort sort, QueryWrapper<T> query) {
        if (sort != null) {
            List<Sort.SortItem> sortList = sort.getSortList();

            for (Sort.SortItem sortItem : sortList) {
                if (sortItem.getDirection() == Sort.Direction.ASC) {
                    query.orderByAsc(getColumnName(sortItem.getProperty()));
                } else {
                    query.orderByDesc(getColumnName(sortItem.getProperty()));
                }
            }
        }
    }

    @Override
    public int delete(Serializable id) {
        if (id == null) {
            throw new RuntimeException("param id is required");
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public int deleteById(Serializable id) {
        if (id == null) {
            throw new RuntimeException("param id is required");
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public int deleteBy(String propertyName, Object propertyValue) {
        if (Func.isEmpty(propertyValue)) {
            return 0;
        }
        String columnName = getColumnName(propertyName);
        return baseMapper.delete(new QueryWrapper<T>().eq(columnName, propertyValue));
    }

    @Override
    public int deleteBy(String propertyName, Collection<?> propertyValue) {
        if (propertyValue == null || propertyValue.size() == 0) {
            return 0;
        }
        String columnName = getColumnName(propertyName);
        return baseMapper.delete(new QueryWrapper<T>().in(!Objects.isNull(propertyValue), columnName, propertyValue));
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     * @deprecated 3.3.1
     */
    @Deprecated
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    @SuppressWarnings("unchecked")
    protected Class<T> currentMapperClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
    }

    @SuppressWarnings("unchecked")
    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(entityClass);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(entityClass));
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     * @see #getSqlStatement(SqlMethod)
     * @deprecated 3.4.0
     */
    @Deprecated
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(entityClass).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @return ignore
     */
    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            ensureCreateTime(entity);
            ensureUpdateTime(entity);
            sqlSession.insert(sqlStatement, entity);
        });
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    public T getById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据 ID 选择修改
     *
     * @param entity 实体对象
     */
    @Override
    public boolean updateById(T entity) {
        ensureUpdateTime(entity);
        return SqlHelper.retBool(getBaseMapper().updateById(entity));
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     */
    @Override
    public boolean save(T entity) {
        ensureCreateTime(entity);
        ensureUpdateTime(entity);
        return SqlHelper.retBool(getBaseMapper().insert(entity));
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            ensureCreateTime(entity);
            ensureUpdateTime(entity);
            return StringUtils.checkValNull(idVal)
                || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            ensureUpdateTime(entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            ensureUpdateTime(entity);
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        if (throwEx) {
            return baseMapper.selectOne(queryWrapper);
        }
        return SqlHelper.getObject(log, baseMapper.selectList(queryWrapper));
    }

    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, baseMapper.selectMaps(queryWrapper));
    }

    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    private <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return getBaseMapper().selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    /**
     * 执行批量操作
     *
     * @param consumer consumer
     * @since 3.3.0
     * @deprecated 3.3.1 后面我打算移除掉 {@link #executeBatch(Collection, int, BiConsumer)} }.
     */
    @Deprecated
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, consumer);
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
    }

    /**
     * 执行批量操作（默认批次提交数量{@link IService#DEFAULT_BATCH_SIZE}）
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @param <E>      泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    private void ensureCreateTime(T t) {
        setValueIfNull(t, "createTime", new Date());
    }

    private void ensureUpdateTime(T t) {
        setValueIfNull(t, "updateTime", new Date());
    }

    //如果属性为空,赋默认值
    private void setValueIfNull(T t, String propertyName, Object defaultValue) {
        Field field = getField(propertyName);
        if (field != null) {
            try {
                field.setAccessible(true);
//                if (field.get(t) == null) {
                field.set(t, defaultValue);
//                }
            } catch (IllegalAccessException e) {
                log.error("setValueIfNull error ,class=" + getEntityClass().getName() + ",propertyName=" + propertyName + "value=" + defaultValue, e);
            }
        }
    }

    //根据属性名或者字段名获取实体field
    private Field getField(String name) {
        Map<String, Field> fieldMap = ReflectionKit.getFieldMap(getEntityClass());
        if (fieldMap != null) {
            Field field = fieldMap.get(name);
            if (field != null) {
                return field;
            }
            for (Field f : fieldMap.values()) {
                TableField annotation = f.getAnnotation(TableField.class);
                if (annotation != null && name.equals(annotation.value())) {
                    return f;
                }
                TableId idAnnotation = f.getAnnotation(TableId.class);
                if (idAnnotation != null && name.equals(idAnnotation.value())) {
                    return f;
                }
            }
        }
        return null;
    }

    //通过实体属性获取对应的数据库字段名
    public String getColumnName(String propertyName) {
        Map<String, Field> fieldMap = ReflectionKit.getFieldMap(getEntityClass());
        if (fieldMap != null) {
            Field field = fieldMap.get(propertyName);

            if (field != null) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation != null) {
                    return annotation.value();
                }
                TableId idAnnotation = field.getAnnotation(TableId.class);
                if (idAnnotation != null) {
                    return idAnnotation.value();
                }
            }
        }
        throw new RuntimeException("获取不到属性" + propertyName + "的列名");
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static int retCount(Integer result) {
        return (null == result) ? 0 : result;
    }

    /**
     * map key value
     *
     * @param eqParams 相等
     * @param neParams 不相等
     * @param inParams 或
     * @return QueryWrapper
     */
    public QueryWrapper<T> queryActiveWrapper(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        QueryWrapper<T> queryWrapper = queryWrapper(eqParams, neParams, inParams);
        queryWrapper.ne("is_delete", 1);
        return queryWrapper;
    }

    /**
     * map key value
     *
     * @return QueryWrapper
     */
    public QueryWrapper<T> queryActiveWrapper(Props params) {
        QueryWrapper<T> queryWrapper = queryWrapper(params);
        queryWrapper.ne("is_delete", 1);
        return queryWrapper;
    }

    /**
     * map key value
     *
     * @return QueryWrapper
     */
    public QueryWrapper<T> queryWrapper(Props params) {
        QueryWrapper<T> query = new QueryWrapper<>();

        List<Props.PropertyItem> properties = params.getProperties();

        for (Props.PropertyItem property : properties) {

            Props.Direction key = property.getDirection();
            String propertyName = getColumnName(property.getPropertyName());
            Object propertyValue = property.getPropertyValue();

            switch (key) {
                case EQ:
                    query.eq(propertyName, propertyValue);
                    break;
                case LIKE:
                    query.like(propertyName, propertyValue);
                    break;
                case GE:
                    query.ge(propertyName, propertyValue);
                    break;
                case GT:
                    query.gt(propertyName, propertyValue);
                    break;
                case LE:
                    query.le(propertyName, propertyValue);
                    break;
                case LT:
                    query.lt(propertyName, propertyValue);
                    break;
                case NE:
                    query.ne(propertyName, propertyValue);
                    break;
                case IN:
                    if (propertyValue instanceof Collection) {
                        Collection values = (Collection) propertyValue;
                        query.in(!Objects.isNull(values), propertyName, values);
                    } else {
                        throw new RuntimeException("inParams value is not instanceof Collection");
                    }
                    break;
                case ASC:
                    query.orderByAsc(propertyName);
                    break;
                case DESC:
                    query.orderByDesc(propertyName);
                    break;
            }
        }

        return query;
    }

    /**
     * map key value
     *
     * @param eqParams 相等
     * @param neParams 不相等
     * @param inParams 或
     * @return QueryWrapper
     */
    public QueryWrapper<T> queryWrapper(Map<String, Object> eqParams, Map<String, Object> neParams, Map<String, Object> inParams) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (eqParams != null) {
            for (Map.Entry<String, Object> entry : eqParams.entrySet()) {
                queryWrapper.eq(getColumnName(entry.getKey()), entry.getValue());
            }
        }
        if (neParams != null) {
            for (Map.Entry<String, Object> entry : neParams.entrySet()) {
                queryWrapper.ne(getColumnName(entry.getKey()), entry.getValue());
            }
        }

        if (inParams != null) {
            for (Map.Entry<String, Object> entry : inParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Collection) {
                    Collection coll = (Collection) value;
                    queryWrapper.in(!Objects.isNull(coll), getColumnName(key), coll);
                } else {
                    throw new RuntimeException("inParams value is not instanceof Collection");
                }
            }
        }

        return queryWrapper;
    }

    /**
     * param template
     *
     * @param key   键
     * @param value 值
     * @return Map<String, Object>
     */
    public static Param addParam(String key, Object value) {
        return new Param(key, value);
    }

    public Map<String, Object> param(String key, Object value) {
        return new Param(key, value).param();
    }

    public static final class Param {

        private Map<String, Object> params = new ConcurrentHashMap<>();


        public Param(String key, Object value) {
            addParam(key, value);
        }

        public Param addParam(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Map<String, Object> param() {
            return this.params;
        }
    }
}
