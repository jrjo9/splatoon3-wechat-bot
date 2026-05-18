package com.mayday9.splatoonbot.netty.server;

import com.mayday9.splatoonbot.netty.channel.WebSocketChannelInitializer;
import com.mayday9.splatoonbot.netty.config.WebSocketConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class WebSocketServer {

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    /**
     * 启动WebSocket服务器
     */
    public void start() {
        if (!webSocketConfig.isEnabled()) {
            log.info("WebSocket服务未启用，跳过启动");
            return;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(webSocketChannelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            InetSocketAddress address = new InetSocketAddress(webSocketConfig.getPort());
            channelFuture = b.bind(address).sync();

            log.info("WebSocket服务器启动成功，监听端口: {}", webSocketConfig.getPort());

            // 等待服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("WebSocket服务器启动失败", e);
            throw new RuntimeException("WebSocket服务器启动失败", e);
        } finally {
            stop();
        }
    }

    /**
     * 停止WebSocket服务器
     */
    @PreDestroy
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("WebSocket服务器已停止");
    }

    /**
     * 检查服务器是否正在运行
     */
    public boolean isRunning() {
        return channelFuture != null && channelFuture.channel().isActive();
    }
}