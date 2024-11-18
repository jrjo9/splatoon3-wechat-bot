package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TLangCn;
import com.mayday9.splatoonbot.business.infrastructure.dao.TLangCnDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TLangCnMapper;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-09-19
 */
@Repository
public class TLangCnDaoImpl extends BaseDaoImpl<TLangCnMapper, TLangCn> implements TLangCnDao {


    /**
     * 新增
     *
     * @param tLangCn 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TLangCn tLangCn) {
        return super.save(tLangCn);
    }

    /**
     * 编辑
     *
     * @param tLangCn 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TLangCn tLangCn) {
        return super.updateById(tLangCn);
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
    public PageResult<TLangCn> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TLangCn> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TLangCn::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TLangCn::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TLangCn::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TLangCn::getxx1, searchDTO.getKeyword())
        // оr(). like(TLangCn::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TLangCn::getUpdateTime);
        List<TLangCn> list = super.find(query);
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
        LambdaQueryWrapper<TLangCn> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TLangCn::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TLangCn::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }
}
