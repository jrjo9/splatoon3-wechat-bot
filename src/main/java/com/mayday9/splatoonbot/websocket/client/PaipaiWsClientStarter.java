package com.mayday9.splatoonbot.websocket.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 派派 WebSocket 客户端启动器
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Slf4j
@Component
public class PaipaiWsClientStarter implements ApplicationRunner {

    @Autowired
    private PaipaiWebSocketClient paipaiWebSocketClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("正在启动派派 WebSocket 客户端...");
        paipaiWebSocketClient.start();
    }
}