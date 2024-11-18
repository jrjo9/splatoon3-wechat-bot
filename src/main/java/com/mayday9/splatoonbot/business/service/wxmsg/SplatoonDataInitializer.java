package com.mayday9.splatoonbot.business.service.wxmsg;

import com.mayday9.splatoonbot.business.task.SplatDataRefreshTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 应用启动初始化Splatoon数据
 *
 * @author Lianjiannan
 * @since 2024/10/24 13:48
 **/
@Component
@Slf4j
public class SplatoonDataInitializer implements ApplicationRunner {

    @Resource
    private SplatDataRefreshTask splatDataRefreshTask;

    @Override
    public void run(ApplicationArguments args) {
        log.info("--------------------- 应用启动初始化Splatoon数据 ---------------------");
        try {
            splatDataRefreshTask.invokeLangCnDataRefreshOnce();
            splatDataRefreshTask.invokeSplatDataRefreshOnce();
        } catch (Exception e) {
            log.error("初始化Splatoon数据失败!!", e);
        }
    }
}
