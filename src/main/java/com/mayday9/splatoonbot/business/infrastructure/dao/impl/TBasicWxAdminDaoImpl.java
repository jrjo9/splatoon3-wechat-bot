package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.business.entity.TBasicWxAdmin;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TBasicWxAdminMapper;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxAdminDao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * 微信人员信息表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-10-10
 */
@Repository
public class TBasicWxAdminDaoImpl extends BaseDaoImpl<TBasicWxAdminMapper, TBasicWxAdmin> implements TBasicWxAdminDao {


    /**
    * 新增
    * @param tBasicWxAdmin 编辑参数
    * @return 成功或失败
    */
    @Override
    public boolean add(TBasicWxAdmin  tBasicWxAdmin) {
        return super.save(tBasicWxAdmin);
    }

    /**
    * 编辑
    * @param tBasicWxAdmin 编辑参数
    * @return 成功或失败
    */
    @Override
    public boolean edit(TBasicWxAdmin  tBasicWxAdmin) {
        return super.updateById(tBasicWxAdmin);
    }

    /**
    * 批量删除
    * @param ids 主键ids
    * @return 成功或失败
    */
    @Override
    public int delete(Collection<? extends Serializable> ids) {
        return super.deleteByIds(ids);
    }

    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    @Override
    public PageResult<TBasicWxAdmin> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TBasicWxAdmin> query = Wrappers.lambdaQuery() ;
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TBasicWxAdmin::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TBasicWxAdmin::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TBasicWxAdmin::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TBasicWxAdmin::getxx1, searchDTO.getKeyword())
        // оr(). like(TBasicWxAdmin::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TBasicWxAdmin::getUpdateTime);
        List<TBasicWxAdmin> list = super.find(query);
        return new PageResult<>(list);
    }
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    @Override
    public boolean findByIdAndName(Serializable id, String name) {
        LambdaQueryWrapper<TBasicWxAdmin> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TBasicWxAdmin::getId, id);
        }
        //if (Func.isNotBlank(name)) {
         //   query.eq(TBasicWxAdmin::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }
}
