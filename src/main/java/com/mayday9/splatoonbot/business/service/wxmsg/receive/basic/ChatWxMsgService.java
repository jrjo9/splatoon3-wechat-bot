package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.dto.AiChatMessage;
import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.ai.DeepSeekChatService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.AiChatModelConstant;
import com.mayday9.splatoonbot.common.constant.ChatSessionConstant;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.util.RedisUtil;
import com.mayday9.splatoonbot.common.util.core.DateUtil;
import com.mayday9.splatoonbot.common.util.core.jackson.JsonUtil;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private RedisUtil redisUtil;

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
        // 获取会话ID
        String chatSessionKey;
        if (!wechatMessage.getWxid().contains("@chatroom") && StringUtils.isEmpty(wechatMessage.getTalker())) {
            // 私聊
            chatSessionKey = ChatSessionConstant.CHAT_SESSION_USER;
        } else {
            // 群聊
            chatSessionKey = ChatSessionConstant.CHAT_SESSION_GROUP;
        }
        chatSessionKey += wechatMessage.getWxid();

        // 获取消息上下文
        List<AiChatMessage> aiChatMessageList;
        boolean firstSession = false;
        if (redisUtil.hasKey(chatSessionKey)) {
            List<Object> chatMessageObjectList = redisUtil.range(chatSessionKey, 0, redisUtil.listLength(chatSessionKey));
            aiChatMessageList = chatMessageObjectList.stream().map(chatMessageObject -> JsonUtil.parse(chatMessageObject.toString(), AiChatMessage.class)).collect(Collectors.toList());
        } else {
            aiChatMessageList = new ArrayList<>();
            firstSession = true;
        }


        // 存储聊天记录
        AiChatMessage aiChatMessage = new AiChatMessage();
        aiChatMessage.setChatTime(DateUtil.now());
        aiChatMessage.setTalker(wechatMessage.getTalker());
        aiChatMessage.setRole(ChatMessageRole.USER);
        aiChatMessage.setMessage(wechatMessage.getContent());
        aiChatMessageList.add(aiChatMessage);

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
                content = deepSeekChatService.chatCompletion(aiChatMessageList);
                break;
            default:
                content = "未配置大模型，请联系管理员！";
                break;
        }
        // 存储聊天记录
        AiChatMessage aiChatRespMessage = new AiChatMessage();
        aiChatRespMessage.setChatTime(DateUtil.now());
        aiChatRespMessage.setTalker("胡萝卜");
        aiChatRespMessage.setRole(ChatMessageRole.ASSISTANT);
        aiChatRespMessage.setMessage(content);
        redisUtil.rightPushAll(chatSessionKey, JsonUtil.toJson(aiChatMessage), JsonUtil.toJson(aiChatRespMessage));
        // 设置会话时间15分钟空闲过期
        redisUtil.expire(chatSessionKey, 60 * 15);

        // 发送给微信
        List<String> wxIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(wechatMessage.getTalker())) {
            wxIdList.add(wechatMessage.getTalker());
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }

}
