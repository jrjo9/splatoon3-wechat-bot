package com.mayday9.splatoonbot.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayday9.splatoonbot.business.entity.TBasicWxChatStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Mapper 接口
 *
 * @author AutoGenerator
 * @since 2024-12-04
 */
@Mapper
public interface TBasicWxChatStatisticsMapper extends BaseMapper<TBasicWxChatStatistics> {

    /**
     * 查询统计排名
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    List<TBasicWxChatStatistics> findStatisticsRankByDate(@Param("chatDate") Date chatDate, @Param("gid") String gid);

    /**
     * 查询月未说话用户
     *
     * @param chatDate 日期
     * @return List<TBasicWxChatStatistics>
     */
    List<TBasicWxChatStatistics> findMonthTalkUserList(@Param("chatDate") Date chatDate, @Param("gid") String gid);

    TBasicWxChatStatistics findByGroupUserDate(Map<String, Object> param);
}
