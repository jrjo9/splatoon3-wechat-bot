package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.service.WxSystemService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.enums.FlagEnum;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/5 15:28
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.AUTO_STATISTICS_SWITCH, desc = "每日龙王群推送")
@Component
public class AutoStatisticsSwitchWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private WxSystemService wxSystemService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            return;
        }
        FlagEnum switchFlag = wxSystemService.switchWxGroupAutoStatistics(wechatMessage.getWxid());
        String content;
        if (FlagEnum.YES.equals(switchFlag)) {
            content = "每日龙王群推送已开启！";
        } else {
            content = "每日龙王群推送已关闭！";
        }
        List<String> wxIdList = new ArrayList<>();
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }
}
