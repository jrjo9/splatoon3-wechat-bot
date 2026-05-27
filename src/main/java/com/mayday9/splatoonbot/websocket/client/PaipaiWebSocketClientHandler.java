package com.mayday9.splatoonbot.websocket.client;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 派派 WebSocket 客户端处理器
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Slf4j
public class PaipaiWebSocketClientHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private final PaipaiWebSocketClient client;

    public PaipaiWebSocketClientHandler(WebSocketClientHandshaker handshaker, PaipaiWebSocketClient client) {
        this.handshaker = handshaker;
        this.client = client;
        log.info("PaipaiWebSocketClientHandler 创建完成");
    }

    public WebSocketClientHandshaker handshaker() {
        return handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded: {}", ctx);
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered: {}", ctx);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive: {}", ctx);
        log.info("开始 WebSocket 握手...");
        handshaker.handshake(ctx.channel()).addListener(f -> {
            if (f.isSuccess()) {
                log.info("握手请求发送成功");
            } else {
                log.error("握手请求发送失败: {}", f.cause());
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive: {}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead: msg type = {}", msg.getClass().getSimpleName());
        Channel ch = ctx.channel();

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            log.info("收到 FullHttpResponse: status={}", response.status());

            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, response);
                    log.info("WebSocket 握手完成!");
                    handshakeFuture.setSuccess();
                    client.onHandshakeComplete();
                } catch (WebSocketHandshakeException e) {
                    log.error("WebSocket 握手失败", e);
                    handshakeFuture.setFailure(e);
                    ch.close();
                }
                return;
            }
        }

        // 握手已完成，处理WebSocket帧
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                String text = textFrame.text();
                log.info("收到 TextWebSocketFrame: {}", text);
                client.handleMessage(text);
            } else if (frame instanceof PingWebSocketFrame) {
                log.info("收到 Ping");
                ch.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            } else if (frame instanceof PongWebSocketFrame) {
                log.info("收到 Pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                log.info("收到关闭帧");
                ch.close();
            } else {
                log.warn("收到未知类型的 WebSocketFrame: {}", frame.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket 异常: {}", cause.getMessage(), cause);
        if (handshakeFuture != null && !handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}