package com.mayday9.splatoonbot.business.service.ws.router;

import com.mayday9.splatoonbot.business.service.ws.dto.WsBusinessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * WebSocket业务路由
 */
@Slf4j
@Component
public class WsBusinessRouter {

    @Resource
    private List<WsBusinessStrategy> strategies;

    /**
     * 路由到支持的策略
     */
    public void route(WsBusinessContext context) throws Exception {
        if (StringUtils.isEmpty(context.getContent())) {
            return;
        }

        String content = context.getContent().trim();

        for (WsBusinessStrategy strategy : strategies) {
            if (strategy.supports(content)) {
                strategy.handle(context);
                return;
            }
        }
        throw new IllegalStateException("未匹配到支持的策略");
    }
}