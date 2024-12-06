package com.mayday9.splatoonbot.business.task;

import com.mayday9.splatoonbot.business.dto.statistics.TodayStatisticsRankDTO;
import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxGroupDao;
import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.enums.FlagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/4 17:20
 **/
@Component
@Slf4j
public class StatisticsTask {

    @Resource
    private GroupStatisticsService groupStatisticsService;

    @Resource
    private TBasicWxGroupDao tBasicWxGroupDao;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Scheduled(cron = "0 55 23 * * ?")
    public void autoStatistics() throws Exception {
        // 查找需要推送的群组
        List<TBasicWxGroup> groupList = tBasicWxGroupDao.findBy("autoStatisticsFlag", FlagEnum.YES);
        if (CollectionUtils.isEmpty(groupList)) {
            log.info("未有群组开启自动推送功能，无需推送今日龙王...");
            return;
        }
        for (TBasicWxGroup group : groupList) {
            List<TodayStatisticsRankDTO> todayStatisticsRankDTOList = groupStatisticsService.getTodayStatisticsRank(group.getGid());
            String content;
            if (CollectionUtils.isEmpty(todayStatisticsRankDTOList)) {
                content = "今日还没有人发言~";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("[emoji=D83D][emoji=DCE2]该睡觉咯，亲爱的鱿鱿们！[emoji=D83D][emoji=DCA4]").append("\n");
                sb.append("[emoji=D83D][emoji=DCE2]以下播报【今日龙王[emoji=D83D][emoji=DC32]】排行榜").append("\n")
                    .append("==================\n");
                for (TodayStatisticsRankDTO todayStatisticsRankDTO : todayStatisticsRankDTOList) {
                    if (todayStatisticsRankDTO.getRank() == 1) {
                        sb.append("[emoji=D83E][emoji=DD47]");
                    } else if (todayStatisticsRankDTO.getRank() == 2) {
                        sb.append("[emoji=D83E][emoji=DD48]");
                    } else if (todayStatisticsRankDTO.getRank() == 3) {
                        sb.append("[emoji=D83E][emoji=DD49]");
                    } else {
                        sb.append(todayStatisticsRankDTO.getRank()).append(". ");
                    }
                    sb.append(todayStatisticsRankDTO.getUserGroupNickName()).append("    ")
                        .append(todayStatisticsRankDTO.getChatNumber()).append("条\n");
                }
                content = sb.toString();
            }
            List<String> wxIdList = new ArrayList<>();
            textWxMsgSender.sendTextMessage(content, group.getGid(), wxIdList, false);
        }
    }

}
