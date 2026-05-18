package com.mayday9.splatoonbot.netty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebSocketMessage {

    /**
     * 业务方法名，用于路由到不同的策略
     */
    @JsonProperty("method")
    private String method;

    /**
     * 会话ID，32位UUID
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * 消息参数
     */
    @JsonProperty("param")
    private Object param;

    /**
     * 是否为心跳消息
     */
    @JsonProperty("heartbeat")
    private boolean heartbeat;

    /**
     * 消息时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    public WebSocketMessage() {
        this.timestamp = System.currentTimeMillis();
    }
}