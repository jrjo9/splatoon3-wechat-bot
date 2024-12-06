package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.dto.statistics.MonthNoTalkUserDTO;
import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.util.core.StringUtil;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lianjiannan
 * @since 2024/12/4 15:43
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.MONTH_NO_TALK_STATISTICS, desc = "当月未发言人员")
@Component
public class MonthNoTalkWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private GroupStatisticsService groupStatisticsService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            return;
        }
        List<MonthNoTalkUserDTO> monthNoTalkUserDTOList = groupStatisticsService.getMonthNoTalkUserList(wechatMessage.getWxid());
        String content = "";
        if (CollectionUtils.isEmpty(monthNoTalkUserDTOList)) {
            content = "本月没有未发言人员";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("本月未发言人员名单").append("\n")
                .append("==================\n");
            List<String> userNameList = monthNoTalkUserDTOList.stream().map(MonthNoTalkUserDTO::getUserGroupNickName).collect(Collectors.toList());
            sb.append(StringUtil.join(userNameList, "、"));
            content = sb.toString();
        }
        List<String> wxIdList = new ArrayList<>();
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }
}
