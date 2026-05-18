package com.mayday9.splatoonbot.business.service.ws;

import com.mayday9.splatoonbot.netty.annotation.WsMsgType;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import com.mayday9.splatoonbot.netty.strategy.AbstractWebSocketStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WsMsgType(type = "connect", desc = "连接成功")
public class ConnectWebSocketStrategy extends AbstractWebSocketStrategy {

    @Override
    protected void doBusiness(ChannelHandlerContext ctx, WebSocketMessage message) throws Exception {
        sendTextMessage(ctx, "连接成功");
    }

    @Override
    public String getType() {
        return "connect";
    }
}
