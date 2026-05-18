package com.mayday9.splatoonbot.netty.channel;

import com.mayday9.splatoonbot.netty.handler.HeartbeatHandler;
import com.mayday9.splatoonbot.netty.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private HeartbeatHandler heartbeatHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加日志处理器
        pipeline.addLast(new LoggingHandler());

        // HTTP编解码器
        pipeline.addLast(new HttpServerCodec());

        // 支持大数据流
        pipeline.addLast(new ChunkedWriteHandler());

        // HTTP消息聚合
        pipeline.addLast(new HttpObjectAggregator(65536));

        // 心跳处理器
        pipeline.addLast(heartbeatHandler);

        // WebSocket协议处理器
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true));

        // WebSocket消息处理器
        pipeline.addLast(webSocketHandler);
    }
}