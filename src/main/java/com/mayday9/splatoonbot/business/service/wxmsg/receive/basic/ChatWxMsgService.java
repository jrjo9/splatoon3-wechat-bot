package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.ai.DeepSeekChatService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.AiChatModelConstant;
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
    private DeepSeekChatService deepSeekChatService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Resource
    private TSysParamService tSysParamService;

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
        // 判断当前调用的模型
        String aiChatModel = AiChatModelConstant.ERNIE_SPEED_8K;
        TSysParam tSysParam = tSysParamService.findByCode("ai_chat_model");
        if (tSysParam != null) {
            aiChatModel = tSysParam.getParamValue();
        }
        String content = "";
        switch (aiChatModel) {
            case AiChatModelConstant.ERNIE_SPEED_8K:
                content = baiduChatService.chatCompletion(message);
                break;
            case AiChatModelConstant.DEEP_SEEK_V3:
                content = deepSeekChatService.chatCompletion(message);
                break;
            default:
                content = "未配置大模型，请联系管理员！";
                break;
        }

        List<String> wxIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(wechatMessage.getTalker())) {
            wxIdList.add(wechatMessage.getTalker());
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }

}
