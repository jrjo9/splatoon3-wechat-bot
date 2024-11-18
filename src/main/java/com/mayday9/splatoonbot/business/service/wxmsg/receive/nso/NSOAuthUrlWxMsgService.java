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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @author Lianjiannan
 * @since 2024/11/11 16:06
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.NSO_AUTH_URL, desc = "nso登录")
@Component
public class NSOAuthUrlWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private NSOService nsoService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        // 生成URL
        String content;
        try {
            String wxid = wechatMessage.getWxid();
            if(wechatMessage.getWxid().contains("@chatroom")){
                wxid = wechatMessage.getTalker();
            }
            String authUrl = nsoService.generateAuthUrl(wxid);
            content = "在浏览器中打开下面链接（移动端复制链接至其他浏览器），" +
                "登陆后，右键账号后面的红色按钮 (手机端长按复制)，复制链接后发送给机器人 (两分钟内有效！)\n" +
                "------------------------------\n" + authUrl;
        } catch (ApiException e) {
            content = e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            content = "未知错误，请联系管理员。";
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), new ArrayList<>(), false);
    }
}
