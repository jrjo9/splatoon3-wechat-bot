package com.mayday9.splatoonbot.netty;

import com.mayday9.splatoonbot.netty.server.WebSocketServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSocketServerTest {

    @Autowired
    private WebSocketServer webSocketServer;

    @Test
    public void testWebSocketServer() throws Exception {
        // 测试服务器启动
        webSocketServer.start();

        // 等待一段时间
        Thread.sleep(5000);

        // 测试服务器状态
        assert webSocketServer.isRunning();

        // 停止服务器
        webSocketServer.stop();
    }
}