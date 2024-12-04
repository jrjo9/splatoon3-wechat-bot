package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.dto.statistics.TodayStatisticsRankDTO;
import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/4 15:43
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.TODAY_STATISTICS, desc = "当日统计")
@Component
public class TodayStatisticsWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Resource
    private GroupStatisticsService groupStatisticsService;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            return;
        }
        List<TodayStatisticsRankDTO> todayStatisticsRankDTOList = groupStatisticsService.getTodayStatisticsRank(wechatMessage.getWxid());
        String content;
        if (CollectionUtils.isEmpty(todayStatisticsRankDTOList)) {
            content = "今日还没有人发言~";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("  今 日 龙 王 [emoji=D83D][emoji=DC32]").append("\n")
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
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }
}
