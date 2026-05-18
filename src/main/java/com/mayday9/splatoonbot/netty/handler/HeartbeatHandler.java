package com.mayday9.splatoonbot.netty.handler;

import com.mayday9.splatoonbot.netty.config.WebSocketConfig;
import com.mayday9.splatoonbot.netty.manager.ConnectionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartbeatHandler extends IdleStateHandler {

    private final ConnectionManager connectionManager;

    public HeartbeatHandler(WebSocketConfig webSocketConfig, ConnectionManager connectionManager) {
        super(webSocketConfig.getHeartbeatInterval(), webSocketConfig.getHeartbeatInterval(), webSocketConfig.getHeartbeatTimeout(), TimeUnit.SECONDS);
        this.connectionManager = connectionManager;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt.state() == IdleState.READER_IDLE) {
            // 读空闲，客户端没有发送心跳
            log.warn("连接读空闲超时，关闭连接: {}", ctx.channel().remoteAddress());
            ctx.close();
        } else if (evt.state() == IdleState.WRITER_IDLE) {
            // 写空闲，发送心跳包
            log.debug("连接写空闲，发送心跳包: {}", ctx.channel().remoteAddress());
            ctx.writeAndFlush(new PingMessage());
        }
        super.channelIdle(ctx, evt);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("添加心跳处理器: {}", ctx.channel().remoteAddress());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("移除心跳处理器: {}", ctx.channel().remoteAddress());
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("心跳处理器异常", cause);
        ctx.close();
    }
}