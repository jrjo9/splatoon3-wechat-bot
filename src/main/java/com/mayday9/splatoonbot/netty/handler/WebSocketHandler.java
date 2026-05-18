package com.mayday9.splatoonbot.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayday9.splatoonbot.common.constant.ChatSessionConstant;
import com.mayday9.splatoonbot.common.util.RedisUtil;
import com.mayday9.splatoonbot.netty.config.WebSocketConfig;
import com.mayday9.splatoonbot.netty.manager.ConnectionManager;
import com.mayday9.splatoonbot.netty.manager.TextMessage;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import com.mayday9.splatoonbot.netty.strategy.WebSocketStrategyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Autowired
    private WebSocketStrategyContext strategyContext;

    @Autowired
    private RedisUtil redisUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // WebSocket握手完成，等待客户端发送包含sessionId的消息
            log.info("WebSocket连接建立，等待客户端发送sessionId，地址: {}", ctx.channel().remoteAddress());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            // 处理文本消息
            String message = ((TextWebSocketFrame) frame).text();
            log.debug("收到WebSocket消息: {}", message);

            try {
                // 解析WebSocket消息
                WebSocketMessage webSocketMessage = objectMapper.readValue(message, WebSocketMessage.class);

                // 检查是否为心跳消息
                if (webSocketMessage.isHeartbeat()) {
                    log.debug("收到心跳消息，sessionId: {}, 不做业务处理", webSocketMessage.getSessionId());
                    return;
                }

                // 检查连接是否已注册sessionId
                if (!connectionManager.containsConnection(webSocketMessage.getSessionId())) {
                    // 首次收到消息，注册连接
                    connectionManager.addConnection(ctx.channel(), webSocketMessage.getSessionId());
                    log.info("WebSocket连接注册，sessionId: {}, 地址: {}", webSocketMessage.getSessionId(), ctx.channel().remoteAddress());
                }

                // 使用策略模式处理消息
                strategyContext.handle(ctx, webSocketMessage.getMethod(), webSocketMessage);
            } catch (Exception e) {
                log.error("消息解析失败: {}", message, e);
                ctx.writeAndFlush(new TextMessage("消息格式错误"));
            }
        } else {
            log.warn("不支持的WebSocket消息类型: {}", frame.getClass().getName());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 连接断开，清理Redis会话记录
        String sessionId = connectionManager.findSessionIdByChannel(ctx.channel());
        if (sessionId != null) {
            String chatSessionKey = ChatSessionConstant.CHAT_SESSION_USER + sessionId;
            redisUtil.deleteKey(chatSessionKey);
            log.info("WebSocket连接断开，已清空Redis会话: {}", chatSessionKey);
        }
        // 移除连接
        connectionManager.removeConnection(ctx.channel());
        log.info("WebSocket连接断开: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket处理异常", cause);
        ctx.close();
    }
}