package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/10/12 15:30
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.CHAT, desc = "聊天")
@Component
public class ChatWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private BaiduChatService baiduChatService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        String message = wechatMessage.getContent();
        if (message.startsWith(WxMsgConstant.CHAT)) {
            message = message.substring(WxMsgConstant.CHAT.length());
        }
        if (StringUtils.isEmpty(message)) {
            message = "你好！";
        }
        String content = baiduChatService.chatCompletion(message).getResult();
        List<String> wxIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(wechatMessage.getTalker())) {
            wxIdList.add(wechatMessage.getTalker());
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }

}
