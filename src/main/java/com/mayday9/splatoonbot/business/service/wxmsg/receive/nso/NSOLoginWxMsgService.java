package com.mayday9.splatoonbot.business.service.wxmsg.receive.nso;

import com.mayday9.splatoonbot.business.service.NSOService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author Lianjiannan
 * @since 2024/11/11 16:06
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.NSO_LOGIN, desc = "nso登录")
@Component
public class NSOLoginWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private NSOService nsoService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        String content;
        try {
            String wxid = wechatMessage.getWxid();
            if(wechatMessage.getWxid().contains("@chatroom")){
                wxid = wechatMessage.getTalker();
            }
            nsoService.login(wxid, wechatMessage.getContent());
            content = "登录NSO成功！\n----------------\n指令列表：\n我的NSO信息\n最近一场打工\n最近一场比赛";
        } catch (ApiException e) {
            content = e.getMessage();
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), new ArrayList<>(), false);
    }
}
