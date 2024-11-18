package com.mayday9.splatoonbot.netty.strategy;

import cn.hutool.core.util.StrUtil;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lianjiannan
 * @since 2024/9/12 17:22
 **/
@Service
@Slf4j
public class PaipaiWxMsgStrategyContext {

    private final Map<String, PaipaiWxMsgStrategy> strategyServiceMap = new ConcurrentHashMap<>();

    public PaipaiWxMsgStrategyContext(Map<String, PaipaiWxMsgStrategy> strategyMap) throws Exception {
        for (Map.Entry<String, PaipaiWxMsgStrategy> strategyEntry : strategyMap.entrySet()) {
            // 获取注解
            PaipaiWxMsgStrategy strategy = strategyEntry.getValue();
            Class strategyClazz = strategy.getClass();
            WxMsgType wxMsgType = AnnotationUtils.findAnnotation(strategyClazz, WxMsgType.class);
            if (null == wxMsgType) {
                log.warn("代码配置错误：创建微信消息处理策略实现类{}未配置WxMsgType注解", strategyClazz.getName());
                continue;
            }
            log.info(wxMsgType.value());
            List<String> values = StrUtil.split(wxMsgType.value(), ",");
            if (values != null) {
                for (String value : values) {
                    if (strategyServiceMap.containsKey(value)) {
                        log.error("代码配置错误：一个靶机命令({})只能对应一个命令策略实现{}", value, strategyClazz.getName());
                        throw new Exception("代码配置错误：一个命令(" + value + ")只能对应一个靶机命令策略实现bean");
                    }
                    this.strategyServiceMap.put(value, strategy);
                }
            }

        }
    }

    public PaipaiWxMsgStrategy getResource(String cmdType) {
        return strategyServiceMap.get(cmdType);
    }

}
