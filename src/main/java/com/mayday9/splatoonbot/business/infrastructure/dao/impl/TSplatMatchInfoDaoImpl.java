package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TSplatMatchInfo;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatMatchInfoDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TSplatMatchInfoMapper;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 喷喷比赛信息表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-09-29
 */
@Repository
public class TSplatMatchInfoDaoImpl extends BaseDaoImpl<TSplatMatchInfoMapper, TSplatMatchInfo> implements TSplatMatchInfoDao {


    /**
     * 新增
     *
     * @param tSplatMatchInfo 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TSplatMatchInfo tSplatMatchInfo) {
        return super.save(tSplatMatchInfo);
    }

    /**
     * 编辑
     *
     * @param tSplatMatchInfo 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TSplatMatchInfo tSplatMatchInfo) {
        return super.updateById(tSplatMatchInfo);
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
    public PageResult<TSplatMatchInfo> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TSplatMatchInfo> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TSplatMatchInfo::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TSplatMatchInfo::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TSplatMatchInfo::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TSplatMatchInfo::getxx1, searchDTO.getKeyword())
        // оr(). like(TSplatMatchInfo::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TSplatMatchInfo::getUpdateTime);
        List<TSplatMatchInfo> list = super.find(query);
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
        LambdaQueryWrapper<TSplatMatchInfo> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TSplatMatchInfo::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TSplatMatchInfo::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }

    @Override
    public List<TSplatMatchInfoVO> findMatchByDate(Date date, String matchType, Integer matchNumber) {
        return baseMapper.findMatchByDate(date, matchType, matchNumber);
    }

    @Override
    public List<TSplatMatchInfo> findMatch(Map<String, Object> params) {
        LambdaQueryWrapper<TSplatMatchInfo> query = Wrappers.lambdaQuery();
        if (params.containsKey("matchType")) {
            query.eq(TSplatMatchInfo::getMatchType, MapUtil.getStr(params, "matchType"));
        }
        if (params.containsKey("startTimeList")) {
            query.in(TSplatMatchInfo::getStartTime, MapUtil.get(params, "startTimeList", ArrayList.class));
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TSplatMatchInfo::getName, name);
        //}

        return baseMapper.selectList(query);
    }
}
