package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TBasicSignIn;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicSignInDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TBasicSignInMapper;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 签到表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-09-23
 */
@Repository
public class TBasicSignInDaoImpl extends BaseDaoImpl<TBasicSignInMapper, TBasicSignIn> implements TBasicSignInDao {


    /**
     * 新增
     *
     * @param tBasicSignIn 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TBasicSignIn tBasicSignIn) {
        return super.save(tBasicSignIn);
    }

    /**
     * 编辑
     *
     * @param tBasicSignIn 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TBasicSignIn tBasicSignIn) {
        return super.updateById(tBasicSignIn);
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
    public PageResult<TBasicSignIn> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TBasicSignIn> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TBasicSignIn::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TBasicSignIn::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TBasicSignIn::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TBasicSignIn::getxx1, searchDTO.getKeyword())
        // оr(). like(TBasicSignIn::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TBasicSignIn::getUpdateTime);
        List<TBasicSignIn> list = super.find(query);
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
        LambdaQueryWrapper<TBasicSignIn> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TBasicSignIn::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TBasicSignIn::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }

    @Override
    public TBasicSignIn findByDate(String wxid, String gid, Date date) {
        LambdaQueryWrapper<TBasicSignIn> query = Wrappers.lambdaQuery();
        query.eq(TBasicSignIn::getWxid, wxid);
        query.eq(TBasicSignIn::getGid, gid);
        query.apply(ObjectUtil.isNotEmpty(date), "DATE_FORMAT (sign_in_time,'%Y-%m-%d')={0}", DateUtil.format(date, "yyyy-MM-dd"));
        return baseMapper.selectOne(query);
    }
}
