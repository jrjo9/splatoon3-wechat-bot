package com.mayday9.splatoonbot.business.service.ws.dto;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * WebSocket业务消息上下文
 */
@Data
public class WsBusinessContext {
    private String content;
    private String sessionId;
    private String talker;
    private ChannelHandlerContext ctx;
}