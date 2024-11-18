package com.mayday9.splatoonbot.business.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.druid.util.StringUtils;
import com.mayday9.splatoonbot.business.dto.WxBotTestDTO;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.enums.WechatMessageTypeEnum;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/11/11 10:34
 **/
@RequestMapping("/wxbot/test/")
// 开发环境可用
@Profile("dev")
@Slf4j
@RestController
public class WxBotTestController {

    @Resource
    private PaipaiWxMsgStrategyContext paipaiWxMsgStrategyContext;

    @PostMapping("/chat")
    public void chat(@Validated @RequestBody WxBotTestDTO wxBotTestDTO) throws Exception {
        // {"Type":1,"Sender":0,"MsgID":28366,"Time":1725955050,"Wxid":"7149208010@chatroom","Talker":"tin0101","Content":"碰上较真的真路人","Path":"","AtList":""}
        // {"Type":1,"Sender":0,"MsgID":28366,"Time":1725955050,"Wxid":"loveuxiao9","Talker":"","Content":"你好","Path":"","AtList":""}
        WechatMessage wechatMessage = new WechatMessage();
        wechatMessage.setType(WechatMessageTypeEnum.TEXT);
        wechatMessage.setSender(0);
        wechatMessage.setMsgID(28366);
        wechatMessage.setTime(DateUtil.current());
        wechatMessage.setWxid("loveuxiao9");
        wechatMessage.setTalker("");
        wechatMessage.setContent(wxBotTestDTO.getText());
        wechatMessage.setPath("");
        wechatMessage.setAtList("");

        String textMsg = null;
        boolean iStartWith = false;
        for (String startWithPattern : WxMsgConstant.startWithPatterns) {
            if (wechatMessage.getContent().startsWith(startWithPattern)) {
                textMsg = startWithPattern;
                iStartWith = true;
                break;
            }
        }
        if (!iStartWith) {
            textMsg = wechatMessage.getContent();
        }

        PaipaiWxMsgStrategy wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(textMsg);
        if (wxMsgStrategy != null) {
            try {
                wxMsgStrategy.doStrategyBusiness(wechatMessage);
                log.debug("执行文本[{}]成功！", textMsg);
            } catch (Exception e) {
                log.error("执行文本[{}]失败！", textMsg, e);
            }
        } else {
            // 判断是否为私聊，私聊的话默认走AI聊天
            if (!wechatMessage.getWxid().contains("@chatroom") && StringUtils.isEmpty(wechatMessage.getTalker())) {
                wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(WxMsgConstant.CHAT);
                if (wxMsgStrategy != null) {
                    try {
                        wxMsgStrategy.doStrategyBusiness(wechatMessage);
                        log.debug("执行文本[{}]成功！", textMsg);
                    } catch (Exception e) {
                        log.error("执行文本[{}]失败！", textMsg, e);
                    }
                } else {
                    log.info("文本[{}]不为功能关键词，略过该文本...", textMsg);
                }
            } else {
                log.info("文本[{}]不为功能关键词，略过该文本...", textMsg);
            }
        }
    }

}
