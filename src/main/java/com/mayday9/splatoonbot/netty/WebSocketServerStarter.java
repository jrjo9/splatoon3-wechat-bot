package com.mayday9.splatoonbot.netty;

import com.mayday9.splatoonbot.netty.server.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketServerStarter implements CommandLineRunner {

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> {
            try {
                webSocketServer.start();
            } catch (Exception e) {
                log.error("WebSocket服务启动失败", e);
            }
        }).start();
    }
}