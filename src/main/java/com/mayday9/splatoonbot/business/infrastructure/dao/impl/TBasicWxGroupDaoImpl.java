package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxGroupDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TBasicWxGroupMapper;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 微信群信息表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-09-25
 */
@Repository
public class TBasicWxGroupDaoImpl extends BaseDaoImpl<TBasicWxGroupMapper, TBasicWxGroup> implements TBasicWxGroupDao {


    /**
     * 新增
     *
     * @param tBasicWxGroup 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TBasicWxGroup tBasicWxGroup) {
        return super.save(tBasicWxGroup);
    }

    /**
     * 编辑
     *
     * @param tBasicWxGroup 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TBasicWxGroup tBasicWxGroup) {
        return super.updateById(tBasicWxGroup);
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
    public PageResult<TBasicWxGroup> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TBasicWxGroup> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TBasicWxGroup::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TBasicWxGroup::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TBasicWxGroup::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TBasicWxGroup::getxx1, searchDTO.getKeyword())
        // оr(). like(TBasicWxGroup::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TBasicWxGroup::getUpdateTime);
        List<TBasicWxGroup> list = super.find(query);
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
        LambdaQueryWrapper<TBasicWxGroup> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TBasicWxGroup::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TBasicWxGroup::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }

    @Override
    public TBasicWxGroup findGroupByGid(String gid) {
        LambdaQueryWrapper<TBasicWxGroup> query = Wrappers.lambdaQuery();
        query.eq(TBasicWxGroup::getGid, gid);
        return baseMapper.selectOne(query);
    }
}
