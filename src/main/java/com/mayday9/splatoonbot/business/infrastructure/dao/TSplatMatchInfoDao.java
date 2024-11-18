package com.mayday9.splatoonbot.business.infrastructure.dao;

import com.mayday9.splatoonbot.business.entity.TSplatMatchInfo;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 喷喷比赛信息表 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-09-29
 */
public interface TSplatMatchInfoDao extends IBaseDao<TSplatMatchInfo> {

    /**
     * 新增
     *
     * @param tSplatMatchInfo 新增参数
     * @return 成功或失败
     */
    boolean add(TSplatMatchInfo tSplatMatchInfo);

    /**
     * 编辑
     *
     * @param tSplatMatchInfo 编辑参数
     * @return 成功或失败
     */
    boolean edit(TSplatMatchInfo tSplatMatchInfo);

    /**
     * 批量删除
     *
     * @param ids 主键ids
     * @return 成功或失败
     */
    int delete(Collection<? extends Serializable> ids);

    /**
     * 关键字搜索
     *
     * @param searchDTO 搜索参数
     * @return 分页列表
     */
    PageResult<TSplatMatchInfo> search(SearchDTO searchDTO);

    /**
     * 名称不重复
     *
     * @param id   主键
     * @param name 名称
     * @return 名称重复数量
     */
    boolean findByIdAndName(Serializable id, String name);

    /**
     * 根据日期查找比赛信息
     *
     * @param date        日期
     * @param matchType   比赛类型
     * @param matchNumber 比赛数量
     * @return List<TSplatMatchInfoVO>
     */
    List<TSplatMatchInfoVO> findMatchByDate(Date date, String matchType, Integer matchNumber);

    /**
     * 查找赛程信息
     *
     * @param params 参数
     * @return List<TSplatMatchInfo>
     */
    List<TSplatMatchInfo> findMatch(Map<String, Object> params);
}
