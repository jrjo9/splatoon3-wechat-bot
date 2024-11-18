package com.mayday9.splatoonbot.netty.channel;

import com.mayday9.splatoonbot.netty.handler.PaipaiWechatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/9/10 13:49
 **/
@Slf4j
@Component
public class PaipaiWechatChannel extends ChannelInitializer<SocketChannel> {

    @Resource
    private PaipaiWechatHandler paipaiWechatHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("初始化 SocketChannel");
        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));//设置log监听器，并且日志级别为debug，方便观察运行流程
        ch.pipeline().addLast(new HttpRequestDecoder());
        // 自定义handler
        ch.pipeline().addLast(paipaiWechatHandler);
    }

}
