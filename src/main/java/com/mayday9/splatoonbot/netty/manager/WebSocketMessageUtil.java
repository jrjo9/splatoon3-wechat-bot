package com.mayday9.splatoonbot.netty.manager;

import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class WebSocketMessageUtil {

    @Resource
    private ConnectionManager connectionManager;

    /**
     * 发送文本消息给指定会话
     *
     * @return true成功 false会话不存在或已断开
     */
    public boolean sendText(String sessionId, String text) {
        Channel channel = connectionManager.getConnection(sessionId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextMessage(text));
            return true;
        }
        log.warn("发送失败，会话不存在或已断开: {}", sessionId);
        return false;
    }

    /**
     * 批量发送文本消息
     */
    public void sendText(List<String> sessionIds, String text) {
        for (String sessionId : sessionIds) {
            sendText(sessionId, text);
        }
    }

    /**
     * 广播文本消息给所有连接
     */
    public void broadcastText(String text) {
        connectionManager.broadcast(text);
    }

    /**
     * 发送结构化消息给指定会话
     *
     * @param sessionId 会话ID
     * @param method    方法名（客户端路由用）
     * @param param     消息参数
     * @return true成功 false失败
     */
    public boolean sendMessage(String sessionId, String method, Object param) {
        WebSocketMessage message = buildMessage(method, param);
        return sendText(sessionId, JSONUtil.toJsonStr(message));
    }

    /**
     * 批量发送结构化消息
     */
    public void sendMessage(List<String> sessionIds, String method, Object param) {
        WebSocketMessage message = buildMessage(method, param);
        String json = JSONUtil.toJsonStr(message);
        for (String sessionId : sessionIds) {
            sendText(sessionId, json);
        }
    }

    /**
     * 广播结构化消息给所有连接
     */
    public void broadcastMessage(String method, Object param) {
        WebSocketMessage message = buildMessage(method, param);
        broadcastText(JSONUtil.toJsonStr(message));
    }

    /**
     * 会话是否在线
     */
    public boolean isOnline(String sessionId) {
        Channel channel = connectionManager.getConnection(sessionId);
        return channel != null && channel.isActive();
    }

    /**
     * 获取在线连接数
     */
    public int getOnlineCount() {
        return connectionManager.getConnectionCount();
    }

    private WebSocketMessage buildMessage(String method, Object param) {
        WebSocketMessage message = new WebSocketMessage();
        message.setMethod(method);
        message.setParam(param);
        message.setHeartbeat(false);
        return message;
    }
}
