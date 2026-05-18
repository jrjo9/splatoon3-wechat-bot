package com.mayday9.splatoonbot.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "websocket")
public class WebSocketConfig {

    /**
     * WebSocket服务端口，默认3910
     */
    private int port = 3910;

    /**
     * WebSocket服务开关，默认开启
     */
    private boolean enabled = true;

    /**
     * 心跳检测间隔（秒），默认60秒
     */
    private int heartbeatInterval = 60;

    /**
     * 心跳超时时间（秒），默认60秒
     */
    private int heartbeatTimeout = 60;

    /**
     * 最大连接数，默认1000
     */
    private int maxConnections = 1000;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
}