package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/12/4 15:38
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.COUNT_CHAT, desc = "记录用户聊天数量")
@Component
public class StatisticsWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private GroupStatisticsService groupStatisticsService;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            return;
        }
        groupStatisticsService.generateGroupStatistics(wechatMessage.getTalker(), wechatMessage.getWxid());
    }
}
