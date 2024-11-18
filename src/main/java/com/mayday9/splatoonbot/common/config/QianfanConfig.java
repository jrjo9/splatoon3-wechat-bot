package com.mayday9.splatoonbot.common.config;

import com.baidubce.qianfan.Qianfan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lianjiannan
 * @since 2024/10/12 15:45
 **/
@Slf4j
@Configuration
public class QianfanConfig {

    @Value("${qianfan.accessKey}")
    private String accessKey;

    @Value("${qianfan.secretKey}")
    private String secretKey;


    @Bean
    public Qianfan qianfan() {
        log.debug("init qianfan");
        return new Qianfan(accessKey, secretKey);
    }

}
