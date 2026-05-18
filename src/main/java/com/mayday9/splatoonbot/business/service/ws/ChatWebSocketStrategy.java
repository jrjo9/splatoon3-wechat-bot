package com.mayday9.splatoonbot.business.service.ws;

import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.business.dto.AiChatMessage;
import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.ai.DeepSeekChatService;
import com.mayday9.splatoonbot.common.constant.AiChatModelConstant;
import com.mayday9.splatoonbot.common.constant.ChatSessionConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.util.RedisUtil;
import com.mayday9.splatoonbot.common.util.core.DateUtil;
import com.mayday9.splatoonbot.common.util.core.jackson.JsonUtil;
import com.mayday9.splatoonbot.netty.annotation.WsMsgType;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import com.mayday9.splatoonbot.netty.strategy.AbstractWebSocketStrategy;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@WsMsgType(type = "chat", desc = "AI聊天")
public class ChatWebSocketStrategy extends AbstractWebSocketStrategy {

    @Resource
    private BaiduChatService baiduChatService;

    @Resource
    private DeepSeekChatService deepSeekChatService;

    @Resource
    private TSysParamService tSysParamService;

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doBusiness(ChannelHandlerContext ctx, WebSocketMessage message) throws Exception {
        try {
            WechatMessage wechatMessage = parseWechatMessage(message);
            String content = wechatMessage.getContent();
            if (StringUtils.isEmpty(content)) {
                content = "你好！";
            }

            // 私聊会话，以sessionId标识
            String chatSessionKey = ChatSessionConstant.CHAT_SESSION_USER + message.getSessionId();

            // 用户名取客户端IP地址
            String talker = ctx.channel().remoteAddress().toString();

            // 获取历史消息上下文
            List<AiChatMessage> aiChatMessageList;
            if (redisUtil.hasKey(chatSessionKey)) {
                List<Object> chatMessageObjectList = redisUtil.range(chatSessionKey, 0, redisUtil.listLength(chatSessionKey));
                aiChatMessageList = chatMessageObjectList.stream()
                    .map(chatMessageObject -> JsonUtil.parse(chatMessageObject.toString(), AiChatMessage.class))
                    .collect(Collectors.toList());
            } else {
                aiChatMessageList = new ArrayList<>();
            }

            // 存储用户消息
            AiChatMessage aiChatMessage = new AiChatMessage();
            aiChatMessage.setChatTime(DateUtil.now());
            aiChatMessage.setTalker(talker);
            aiChatMessage.setRole(ChatMessageRole.USER);
            aiChatMessage.setMessage(content);
            aiChatMessageList.add(aiChatMessage);

            // 判断当前调用的AI模型
            String aiChatModel = AiChatModelConstant.ERNIE_SPEED_8K;
            TSysParam tSysParam = tSysParamService.findByCode("ai_chat_model");
            if (tSysParam != null) {
                aiChatModel = tSysParam.getParamValue();
            }
            String response;
            switch (aiChatModel) {
                case AiChatModelConstant.ERNIE_SPEED_8K:
                    response = baiduChatService.chatCompletion(content);
                    break;
                case AiChatModelConstant.DEEP_SEEK_V3:
                    response = deepSeekChatService.chatCompletion(aiChatMessageList);
                    break;
                default:
                    response = "未配置大模型，请联系管理员！";
                    break;
            }

            // 存储AI回复
            AiChatMessage aiChatRespMessage = new AiChatMessage();
            aiChatRespMessage.setChatTime(DateUtil.now());
            aiChatRespMessage.setTalker("胡萝卜");
            aiChatRespMessage.setRole(ChatMessageRole.ASSISTANT);
            aiChatRespMessage.setMessage(response);
            redisUtil.rightPushAll(chatSessionKey, JsonUtil.toJson(aiChatMessage), JsonUtil.toJson(aiChatRespMessage));
            // 15分钟空闲过期
            redisUtil.expire(chatSessionKey, 60 * 15);

            // 发送响应
            sendTextMessage(ctx, response);
        } catch (Exception e) {
            log.error("AI聊天处理异常", e);
            sendTextMessage(ctx, "抱歉，AI服务暂时不可用，请稍后再试。");
        }
    }

    private WechatMessage parseWechatMessage(WebSocketMessage message) {
        String paramJson = JSONUtil.toJsonStr(message.getParam());
        return JSONUtil.toBean(paramJson, WechatMessage.class);
    }

    @Override
    public String getType() {
        return "chat";
    }
}
