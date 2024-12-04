package com.mayday9.splatoonbot.business.infrastructure.dao;

import com.mayday9.splatoonbot.business.entity.TBasicWxChatStatistics;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-12-04
 */
public interface TBasicWxChatStatisticsDao extends IBaseDao<TBasicWxChatStatistics> {

    /**
     * 新增
     *
     * @param tBasicWxChatStatistics 新增参数
     * @return 成功或失败
     */
    boolean add(TBasicWxChatStatistics tBasicWxChatStatistics);

    /**
     * 编辑
     *
     * @param tBasicWxChatStatistics 编辑参数
     * @return 成功或失败
     */
    boolean edit(TBasicWxChatStatistics tBasicWxChatStatistics);

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
    PageResult<TBasicWxChatStatistics> search(SearchDTO searchDTO);

    /**
     * 名称不重复
     *
     * @param id   主键
     * @param name 名称
     * @return 名称重复数量
     */
    boolean findByIdAndName(Serializable id, String name);

    /**
     * 查询统计排名
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    List<TBasicWxChatStatistics> findStatisticsRankByDate(Date chatDate, String gid);

    /**
     * 查询当月未发言用户
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    List<TBasicWxChatStatistics> findMonthTalkUserList(Date chatDate, String gid);

    /**
     * 查询群组用户日期统计
     *
     * @param param 参数
     * @return TBasicWxChatStatistics
     */
    TBasicWxChatStatistics findByGroupUserDate(Map<String, Object> param);
}
