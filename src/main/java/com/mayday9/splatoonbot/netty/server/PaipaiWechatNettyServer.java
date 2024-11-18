package com.mayday9.splatoonbot.netty.server;

import com.mayday9.splatoonbot.netty.channel.PaipaiWechatChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/9/10 13:52
 **/
@Component
@Slf4j
public class PaipaiWechatNettyServer {

    @Autowired
    private PaipaiWechatChannel paipaiWechatChannel;

    /**
     * 监听端口
     */
    @Getter
    @Value("${netty.wechat.paipai.server.port:9933}")
    private int port;

    /**
     * 开关
     */
    @Value("${netty.wechat.paipai.server.enabled:true}")
    private boolean serverStartFlag;

    @Async
    public void start() {
        if (!serverStartFlag) {
            log.info("系统将不启动派派微信监听...");
            return;
        }
        log.info("正在启动派派微信监听……");
        //主线程组
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        //工作线程组
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            //引导对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置工作线程组
            bootstrap.group(boss, work)
                //配置为NIO的socket通道
                .channel(NioServerSocketChannel.class)
                // 子处理器，用于处理subGroup
                .childHandler(paipaiWechatChannel);
            //使用了Future来启动线程，并绑定了端口
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("启动派派微信监听成功，正在监听端口:" + port);
            //以异步的方式关闭端口
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动出现异常：" + e);
        } finally {
            //出现异常后，关闭线程组
            work.shutdownGracefully();
            boss.shutdownGracefully();
            log.info("派派微信监听已经关闭");
        }

    }
}
