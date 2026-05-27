package com.mayday9.splatoonbot.websocket.handler;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.StringUtils;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.PaipaiWxEventData;
import com.mayday9.splatoonbot.common.dto.PaipaiWxEventMessage;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategyContext;
import com.mayday9.splatoonbot.websocket.client.PaipaiWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 派派 WebSocket 消息处理器
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Slf4j
@Component
public class PaipaiWsMsgHandler {

    @Autowired
    private PaipaiWxMsgStrategyContext paipaiWxMsgStrategyContext;

    private static final ExecutorService executor;

    static {
        int numberOfCPUCores = Runtime.getRuntime().availableProcessors();
        log.info("numberOfCPUCores:{}", numberOfCPUCores);
        executor = new ThreadPoolExecutor(numberOfCPUCores * 2,
            numberOfCPUCores * 2,
            0,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000),
            (r, executor) -> {
                try {
                    log.debug("线程池拒绝策略..., 准备重新提交");
                    Thread.sleep(1000);
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        );
    }

    /**
     * 处理 WebSocket 消息
     */
    public void handleMessage(String message, PaipaiWebSocketClient client) {
        try {
            PaipaiWxEventMessage eventMessage = JSONUtil.toBean(message, PaipaiWxEventMessage.class);
            Integer type = eventMessage.getType();

            if (type == 999) {
                // CID 分配
                handleCidAssign(eventMessage, client);
            } else if (type == 1001) {
                // 事件消息
                handleEventMessage(eventMessage);
            } else {
                log.info("收到未知类型消息: Type={}", type);
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息失败: {}", message, e);
        }
    }

    /**
     * 处理 CID 分配
     */
    private void handleCidAssign(PaipaiWxEventMessage eventMessage, PaipaiWebSocketClient client) {
        // {"Type":999,"CID":1}
        Integer cid = eventMessage.getCID();
        client.setCid(cid);
        log.info("收到 CID 分配: {}", cid);
    }

    /**
     * 处理事件消息
     */
    private void handleEventMessage(PaipaiWxEventMessage eventMessage) {
        PaipaiWxEventData eventData = JSONUtil.toBean(JSONUtil.toJsonStr(eventMessage.getData()), PaipaiWxEventData.class);

        // 过滤自己发送的消息，防止死循环
        if (eventData.getIsSelf() != null && eventData.getIsSelf() == 1) {
            log.debug("跳过自己发送的消息");
            return;
        }

        // 转换为 WechatMessage
        WechatMessage wechatMessage = convertToWechatMessage(eventData);
        if (wechatMessage == null) {
            return;
        }

        // 判断是否为群聊
        boolean isChatroom = wechatMessage.getWxid().contains("@chatroom");

        executor.execute(() -> {
            // 业务处理
            String textMsg = wechatMessage.getContent();
            boolean matched = false;

            // 尝试匹配关键词策略
            for (String startWithPattern : WxMsgConstant.startWithPatterns) {
                if (textMsg.startsWith(startWithPattern)) {
                    PaipaiWxMsgStrategy wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(startWithPattern);
                    if (wxMsgStrategy != null) {
                        try {
                            wxMsgStrategy.doStrategyBusiness(wechatMessage);
                            log.debug("执行文本[{}]成功！", startWithPattern);
                            matched = true;
                        } catch (Exception e) {
                            log.error("执行文本[{}]失败！", startWithPattern, e);
                        }
                    }
                    break;
                }
            }

            // 尝试精确匹配
            if (!matched) {
                PaipaiWxMsgStrategy wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(textMsg);
                if (wxMsgStrategy != null) {
                    try {
                        wxMsgStrategy.doStrategyBusiness(wechatMessage);
                        log.debug("执行文本[{}]成功！", textMsg);
                        matched = true;
                    } catch (Exception e) {
                        log.error("执行文本[{}]失败！", textMsg, e);
                    }
                }
            }

            // 私聊默认走AI聊天
            if (!matched && !isChatroom && StringUtils.isEmpty(wechatMessage.getTalker())) {
                PaipaiWxMsgStrategy chatStrategy = paipaiWxMsgStrategyContext.getResource(WxMsgConstant.CHAT);
                if (chatStrategy != null) {
                    try {
                        chatStrategy.doStrategyBusiness(wechatMessage);
                        log.debug("执行私聊AI[{}]成功！", textMsg);
                    } catch (Exception e) {
                        log.error("执行私聊AI[{}]失败！", textMsg, e);
                    }
                } else {
                    log.info("文本[{}]不为功能关键词，略过该文本...", textMsg);
                    wechatMessage.setSkipped(true);
                }
            } else if (!matched) {
                log.info("文本[{}]不为功能关键词，略过该文本...", textMsg);
                wechatMessage.setSkipped(true);
            }

            // 记录聊天次数（仅群聊，所有群聊消息都统计，不管是否匹配到业务策略）
            if (isChatroom) {
                PaipaiWxMsgStrategy countStrategy = paipaiWxMsgStrategyContext.getResource(WxMsgConstant.COUNT_CHAT);
                if (countStrategy != null) {
                    try {
                        countStrategy.doStrategyBusiness(wechatMessage);
                    } catch (Exception e) {
                        log.error("记录聊天数量失败！", e);
                    }
                }
            }
        });
    }

    /**
     * 将 PaipaiWxEventData 转换为 WechatMessage
     */
    private WechatMessage convertToWechatMessage(PaipaiWxEventData eventData) {
        if (eventData.getType() != 1) {
            // 目前只处理文本消息
            log.debug("暂不支持的消息类型: {}", eventData.getType());
            return null;
        }

        WechatMessage wechatMessage = new WechatMessage();
        wechatMessage.setType(com.mayday9.splatoonbot.common.enums.WechatMessageTypeEnum.TEXT);
        wechatMessage.setSender(eventData.getIsSelf());
        wechatMessage.setMsgID(eventData.getLocalId());
        wechatMessage.setWxid(eventData.getFrom());
        wechatMessage.setTalker(eventData.getFinal_From());
        wechatMessage.setContent(eventData.getMsg());
        wechatMessage.setPath(eventData.getFilePath());
        wechatMessage.setAtList(eventData.getATList());
        wechatMessage.setTime(System.currentTimeMillis() / 1000);

        return wechatMessage;
    }
}