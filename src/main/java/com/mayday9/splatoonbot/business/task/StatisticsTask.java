package com.mayday9.splatoonbot.business.task;

import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/12/4 17:20
 **/
@Component
@Slf4j
public class StatisticsTask {

    @Resource
    private GroupStatisticsService groupStatisticsService;

    @Scheduled(cron = "0 55 23 * * ?")
    public void autoStatistics() throws Exception {
//        groupStatisticsService.getTodayStatisticsRank();
    }

}
