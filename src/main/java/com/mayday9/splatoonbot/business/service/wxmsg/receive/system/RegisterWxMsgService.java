package com.mayday9.splatoonbot.business.service.wxmsg.receive.system;

import com.mayday9.splatoonbot.business.dto.basic.WxGroupRegisterDTO;
import com.mayday9.splatoonbot.business.service.WxSystemService;
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
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/14 9:32
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.REGISTER, desc = "激活群组")
@Component
public class RegisterWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private WxSystemService wxSystemService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        // 签到
        WxGroupRegisterDTO wxGroupRegisterDTO = new WxGroupRegisterDTO();
        wxGroupRegisterDTO.setGid(wechatMessage.getWxid());
        wxGroupRegisterDTO.setWxid(wechatMessage.getTalker());
        String content;
        try {
            wxSystemService.registerWxGroup(wxGroupRegisterDTO);
            content = "激活成功！";
        } catch (ApiException e) {
            content = e.getMessage();
        }
        List<String> wxIdList = new ArrayList<>();
        wxIdList.add(wechatMessage.getTalker());
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }

}
