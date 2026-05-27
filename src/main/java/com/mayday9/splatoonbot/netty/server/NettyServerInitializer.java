package com.mayday9.splatoonbot.netty.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Netty 服务初始化（已废弃，请使用 PaipaiWsClientStarter）
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Component
@Slf4j
@Deprecated
public class NettyServerInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.warn("NettyServerInitializer 已废弃，请删除");
    }
}