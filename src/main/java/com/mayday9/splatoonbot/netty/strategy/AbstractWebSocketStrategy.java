package com.mayday9.splatoonbot.netty.strategy;

import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.netty.manager.TextMessage;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractWebSocketStrategy implements WebSocketStrategy {

    @Override
    public void handle(ChannelHandlerContext ctx, WebSocketMessage message) {
        try {
            doBusiness(ctx, message);
        } catch (ApiException e) {
            log.warn("WebSocket业务异常: {}", e.getMessage());
            sendTextMessage(ctx, e.getMessage());
        } catch (Exception e) {
            log.error("WebSocket策略处理异常", e);
            sendTextMessage(ctx, "抱歉，服务暂时不可用，请稍后再试。");
        }
    }

    /**
     * 发送文本消息
     */
    protected void sendTextMessage(ChannelHandlerContext ctx, String text) {
        ctx.writeAndFlush(new TextMessage(text));
    }

    /**
     * 业务逻辑，子类实现
     */
    protected abstract void doBusiness(ChannelHandlerContext ctx, WebSocketMessage message) throws Exception;
}
