package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.dto.basic.WxUserSignInDTO;
import com.mayday9.splatoonbot.business.service.SignInService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.business.vo.WxUserSignInVO;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
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
@WxMsgType(value = WxMsgConstant.SIGN_IN, desc = "签到")
@Component
public class SignInWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private SignInService signInService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            return;
        }
        // 签到
        WxUserSignInDTO wxUserSignInDTO = new WxUserSignInDTO();
        wxUserSignInDTO.setGid(wechatMessage.getWxid());
        wxUserSignInDTO.setWxid(wechatMessage.getTalker());
        String content;
        try {
            WxUserSignInVO wxUserSignInVO = signInService.wxUserSignIn(wxUserSignInDTO);
            content = wxUserSignInVO.toString();
        } catch (ApiException e) {
            content = e.getMessage();
        }
        List<String> wxIdList = new ArrayList<>();
        wxIdList.add(wechatMessage.getTalker());
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }

}
