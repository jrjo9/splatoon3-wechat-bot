package com.mayday9.splatoonbot.netty.strategy;

import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketStrategyContext {

    private final Map<String, WebSocketStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public WebSocketStrategyContext(Map<String, WebSocketStrategy> strategies) {
        strategies.forEach((key, value) -> {
            strategyMap.put(value.getType(), value);
        });
    }

    /**
     * 处理WebSocket消息
     *
     * @param ctx         通道上下文
     * @param messageType 消息类型
     * @param message     消息内容
     */
    public void handle(ChannelHandlerContext ctx, String messageType, WebSocketMessage message) {
        WebSocketStrategy strategy = strategyMap.get(messageType);
        if (strategy != null) {
            strategy.handle(ctx, message);
        } else {
            log.warn("未找到对应的策略，消息类型: {}", messageType);
            // 可以发送错误响应或默认处理
        }
    }

    /**
     * 获取所有策略
     */
    public Map<String, WebSocketStrategy> getStrategies() {
        return strategyMap;
    }

    /**
     * 添加策略
     */
    public void addStrategy(WebSocketStrategy strategy) {
        strategyMap.put(strategy.getType(), strategy);
    }

    /**
     * 移除策略
     */
    public void removeStrategy(String messageType) {
        strategyMap.remove(messageType);
    }
}