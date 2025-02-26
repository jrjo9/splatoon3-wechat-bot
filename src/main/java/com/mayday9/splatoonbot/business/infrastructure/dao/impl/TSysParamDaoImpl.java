package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSysParamDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TSysParamMapper;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 系统参数表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2025-02-25
 */
@Repository
public class TSysParamDaoImpl extends BaseDaoImpl<TSysParamMapper, TSysParam> implements TSysParamDao {


    /**
     * 新增
     *
     * @param tSysParam 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TSysParam tSysParam) {
        return super.save(tSysParam);
    }

    /**
     * 编辑
     *
     * @param tSysParam 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TSysParam tSysParam) {
        return super.updateById(tSysParam);
    }

    /**
     * 批量删除
     *
     * @param ids 主键ids
     * @return 成功或失败
     */
    @Override
    public int delete(Collection<? extends Serializable> ids) {
        return super.deleteByIds(ids);
    }

    /**
     * 关键字搜索
     *
     * @param searchDTO 搜索参数
     * @return 分页列表
     */
    @Override
    public PageResult<TSysParam> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TSysParam> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TSysParam::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TSysParam::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TSysParam::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TSysParam::getxx1, searchDTO.getKeyword())
        // оr(). like(TSysParam::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TSysParam::getUpdateTime);
        List<TSysParam> list = super.find(query);
        return new PageResult<>(list);
    }

    /**
     * 名称不重复
     *
     * @param id   主键
     * @param name 名称
     * @return 名称重复数量
     */
    @Override
    public boolean findByIdAndName(Serializable id, String name) {
        LambdaQueryWrapper<TSysParam> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TSysParam::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TSysParam::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }
}
