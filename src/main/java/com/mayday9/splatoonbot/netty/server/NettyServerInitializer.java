package com.mayday9.splatoonbot.netty.server;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/9/10 13:48
 **/
@Component
@Slf4j
public class NettyServerInitializer implements ApplicationRunner {

    @Resource
    private PaipaiWechatNettyServer paipaiWechatNettyServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        paipaiWechatNettyServer.start();
    }
}
