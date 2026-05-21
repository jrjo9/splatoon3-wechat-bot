package com.mayday9.splatoonbot.business.service.ws.router;

import com.mayday9.splatoonbot.business.service.ws.dto.WsBusinessContext;

/**
 * WebSocket业务策略接口
 */
public interface WsBusinessStrategy {

    /**
     * 判断是否支持处理该消息
     */
    boolean supports(String content);

    /**
     * 处理业务
     */
    void handle(WsBusinessContext context) throws Exception;
}