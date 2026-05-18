package com.mayday9.splatoonbot.common.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Lianjiannan
 * @since 2025/2/25 16:55
 **/
@Slf4j
@Configuration
public class VolcEngineConfig {

    @Value("${volcEngine.apiKey}")
    private String apiKey;

    @Bean
    public ArkService buildService() {
        log.debug("init volcengine deepseek service...");
        return ArkService.builder().dispatcher(new Dispatcher())
            .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
            .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
                .apiKey(apiKey)
            .build();
    }
}
