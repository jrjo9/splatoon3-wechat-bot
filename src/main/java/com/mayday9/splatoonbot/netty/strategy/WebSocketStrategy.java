package com.mayday9.splatoonbot.netty.strategy;

import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;

public interface WebSocketStrategy {

    /**
     * 处理WebSocket消息
     *
     * @param ctx     通道上下文
     * @param message 消息内容
     */
    void handle(ChannelHandlerContext ctx, WebSocketMessage message);

    /**
     * 获取策略类型
     *
     * @return 策略类型
     */
    String getType();
}