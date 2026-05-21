package com.mayday9.splatoonbot.business.service.ws;

import com.mayday9.splatoonbot.business.dto.AiChatMessage;
import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.ai.ArkChatService;
import com.mayday9.splatoonbot.business.service.ws.dto.WsBusinessContext;
import com.mayday9.splatoonbot.business.service.ws.router.WsBusinessRouter;
import com.mayday9.splatoonbot.common.constant.AiChatModelConstant;
import com.mayday9.splatoonbot.common.constant.ChatSessionConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.util.RedisUtil;
import com.mayday9.splatoonbot.common.util.core.DateUtil;
import com.mayday9.splatoonbot.common.util.core.jackson.JsonUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
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
@WsMsgType(type = "chat", desc = "AI聊天/业务路由")
public class ChatWebSocketStrategy extends AbstractWebSocketStrategy {

    @Resource
    private BaiduChatService baiduChatService;

    @Resource
    private ArkChatService arkChatService;

    @Resource
    private TSysParamService tSysParamService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WsBusinessRouter wsBusinessRouter;

    @Override
    protected void doBusiness(ChannelHandlerContext ctx, WebSocketMessage message) throws Exception {
        try {
            WechatMessage wechatMessage = parseWechatMessage(message);
            String content = wechatMessage.getContent();
            if (StringUtils.isEmpty(content)) {
                content = "你好！";
            }

            String sessionId = message.getSessionId();
            String talker = ctx.channel().remoteAddress().toString();

            // 构建业务上下文
            WsBusinessContext businessContext = new WsBusinessContext();
            businessContext.setContent(content);
            businessContext.setSessionId(sessionId);
            businessContext.setTalker(talker);
            businessContext.setCtx(ctx);

            // 尝试路由到业务策略
            try {
                wsBusinessRouter.route(businessContext);
                return;
            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                log.debug("未匹配到业务策略，继续普通聊天: {}", e.getMessage());
            }

            // 普通AI聊天
            handleAiChat(ctx, sessionId, talker, content);
        } catch (ApiException e) {
            log.warn("ChatWebSocketStrategy业务异常: {}", e.getMessage());
            sendTextMessage(ctx, e.getMessage());
        } catch (Exception e) {
            log.error("ChatWebSocketStrategy处理异常", e);
            sendTextMessage(ctx, "抱歉，服务暂时不可用，请稍后再试。");
        }
    }

    /**
     * 处理普通AI聊天
     */
    private void handleAiChat(ChannelHandlerContext ctx, String sessionId, String talker, String content) throws Exception {
        // 私聊会话，以sessionId标识
        String chatSessionKey = ChatSessionConstant.CHAT_SESSION_USER + sessionId;

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
            case AiChatModelConstant.ARK:
                response = arkChatService.chatCompletion(aiChatMessageList);
                break;
            case AiChatModelConstant.ERNIE_SPEED_8K:
                response = baiduChatService.chatCompletion(content);
                break;
            default:
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "未配置大模型，请联系管理员！");
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
    }

    private WechatMessage parseWechatMessage(WebSocketMessage message) {
        String paramJson = JsonUtil.toJson(message.getParam());
        return JsonUtil.parse(paramJson, WechatMessage.class);
    }

    @Override
    public String getType() {
        return "chat";
    }
}