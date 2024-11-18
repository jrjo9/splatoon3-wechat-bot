package com.mayday9.splatoonbot.business.infrastructure.dao.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunInfo;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatSalmonRunInfoDao;
import com.mayday9.splatoonbot.business.infrastructure.mapper.TSplatSalmonRunInfoMapper;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import com.mayday9.splatoonbot.business.vo.TSplatSalmonRunInfoVO;
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
 * 喷喷鲑鱼跑打工信息表 数据访问实现类
 *
 * @author AutoGenerator
 * @since 2024-10-08
 */
@Repository
public class TSplatSalmonRunInfoDaoImpl extends BaseDaoImpl<TSplatSalmonRunInfoMapper, TSplatSalmonRunInfo> implements TSplatSalmonRunInfoDao {


    /**
     * 新增
     *
     * @param tSplatSalmonRunInfo 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean add(TSplatSalmonRunInfo tSplatSalmonRunInfo) {
        return super.save(tSplatSalmonRunInfo);
    }

    /**
     * 编辑
     *
     * @param tSplatSalmonRunInfo 编辑参数
     * @return 成功或失败
     */
    @Override
    public boolean edit(TSplatSalmonRunInfo tSplatSalmonRunInfo) {
        return super.updateById(tSplatSalmonRunInfo);
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
    public PageResult<TSplatSalmonRunInfo> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<TSplatSalmonRunInfo> query = Wrappers.lambdaQuery();
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(TSplatSalmonRunInfo::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(TSplatSalmonRunInfo::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(TSplatSalmonRunInfo::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(TSplatSalmonRunInfo::getxx1, searchDTO.getKeyword())
        // оr(). like(TSplatSalmonRunInfo::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(TSplatSalmonRunInfo::getUpdateTime);
        List<TSplatSalmonRunInfo> list = super.find(query);
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
        LambdaQueryWrapper<TSplatSalmonRunInfo> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(TSplatSalmonRunInfo::getId, id);
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TSplatSalmonRunInfo::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }

    /**
     * 查询打工日程数据
     *
     * @param params 参数
     * @return List<TSplatSalmonRunInfo>
     */
    @Override
    public List<TSplatSalmonRunInfo> findMatch(Map<String, Object> params) {
        LambdaQueryWrapper<TSplatSalmonRunInfo> query = Wrappers.lambdaQuery();
        if (params.containsKey("salmonRunType")) {
            query.eq(TSplatSalmonRunInfo::getSalmonRunType, MapUtil.getStr(params, "salmonRunType"));
        }
        if (params.containsKey("startTimeList")) {
            query.in(TSplatSalmonRunInfo::getStartTime, MapUtil.get(params, "startTimeList", ArrayList.class));
        }
        //if (Func.isNotBlank(name)) {
        //   query.eq(TSplatMatchInfo::getName, name);
        //}

        return baseMapper.selectList(query);
    }

    @Override
    public List<TSplatSalmonRunInfoVO> findMatchByDate(Date date, Integer matchNumber) {
        return baseMapper.findMatchByDate(date, matchNumber);
    }
}
