package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TBasicWxChatStatistics;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxChatStatisticsDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TBasicWxChatStatisticsMapper;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-12-04
 */
@Repository
public class TBasicWxChatStatisticsDaoImpl extends BaseDaoImpl<TBasicWxChatStatisticsMapper, TBasicWxChatStatistics> implements TBasicWxChatStatisticsDao {


    /**
     * 新增
     *
     * @param tBasicWxChatStatistics 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TBasicWxChatStatistics tBasicWxChatStatistics) {
        return super.save(tBasicWxChatStatistics);
    }

    /**
     * 编辑
     *
     * @param tBasicWxChatStatistics 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TBasicWxChatStatistics tBasicWxChatStatistics) {
        return super.updateById(tBasicWxChatStatistics);
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
    public PageResult<TBasicWxChatStatistics> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TBasicWxChatStatistics> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TBasicWxChatStatistics::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TBasicWxChatStatistics::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TBasicWxChatStatistics::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TBasicWxChatStatistics::getxx1, searchDTO.getKeyword())
        // оr(). like(TBasicWxChatStatistics::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TBasicWxChatStatistics::getUpdateTime);
        List<TBasicWxChatStatistics> list = super.find(query);
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
        LambdaQueryWrapper<TBasicWxChatStatistics> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TBasicWxChatStatistics::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TBasicWxChatStatistics::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }

    /**
     * 查询统计排名
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    @Override
    public List<TBasicWxChatStatistics> findStatisticsRankByDate(Date chatDate, String gid) {
        return baseMapper.findStatisticsRankByDate(chatDate, gid);
    }

    /**
     * 查询当月未发言用户
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    @Override
    public List<TBasicWxChatStatistics> findMonthTalkUserList(Date chatDate, String gid) {
        return baseMapper.findMonthTalkUserList(chatDate, gid);
    }

    @Override
    public TBasicWxChatStatistics findByGroupUserDate(Map<String, Object> param) {
        return baseMapper.findByGroupUserDate(param);
    }
}
