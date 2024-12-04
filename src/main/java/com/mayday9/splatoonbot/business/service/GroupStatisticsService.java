package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.statistics.MonthNoTalkUserDTO;
import com.mayday9.splatoonbot.business.dto.statistics.TodayStatisticsRankDTO;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/4 10:20
 **/
public interface GroupStatisticsService {

    void generateGroupStatistics(String wxid, String gid);

    List<TodayStatisticsRankDTO> getTodayStatisticsRank(String gid);

    List<MonthNoTalkUserDTO> getMonthNoTalkUserList(String gid);
}
