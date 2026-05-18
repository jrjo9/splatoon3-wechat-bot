package com.mayday9.splatoonbot.netty.manager;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConnectionManager {

    private final Map<String, Channel> connections = new ConcurrentHashMap<>();

    /**
     * 添加连接
     */
    public void addConnection(Channel channel, String sessionId) {
        connections.put(sessionId, channel);
        log.debug("添加连接，sessionId: {}", sessionId);
    }

    /**
     * 移除连接
     */
    public void removeConnection(Channel channel) {
        String sessionId = findSessionIdByChannel(channel);
        if (sessionId != null) {
            connections.remove(sessionId);
            log.debug("移除连接，sessionId: {}", sessionId);
        }
    }

    /**
     * 根据sessionId获取连接
     */
    public Channel getConnection(String sessionId) {
        return connections.get(sessionId);
    }

    /**
     * 获取所有连接
     */
    public Map<String, Channel> getConnections() {
        return connections;
    }

    /**
     * 发送消息给所有连接
     */
    public void broadcast(String message) {
        connections.values().forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(new TextMessage(message));
            }
        });
    }

    /**
     * 发送消息给指定连接
     */
    public void sendToConnection(String sessionId, String message) {
        Channel channel = connections.get(sessionId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextMessage(message));
        }
    }

    /**
     * 检查连接是否存在
     */
    public boolean containsConnection(String sessionId) {
        return connections.containsKey(sessionId);
    }

    /**
     * 获取连接数量
     */
    public int getConnectionCount() {
        return connections.size();
    }

    /**
     * 清空所有连接
     */
    public void clearAll() {
        connections.clear();
        log.info("清空所有连接");
    }

    /**
     * 根据Channel查找sessionId
     */
    public String findSessionIdByChannel(Channel channel) {
        for (Map.Entry<String, Channel> entry : connections.entrySet()) {
            if (entry.getValue() == channel) {
                return entry.getKey();
            }
        }
        return null;
    }
}