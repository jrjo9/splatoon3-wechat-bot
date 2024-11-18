package com.mayday9.splatoonbot.business.service.wxmsg;

import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxGroupDao;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.enums.FlagEnum;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/10/10 11:12
 **/
@Aspect
@Component
public class WxAuthAspect {

    @Resource
    private TBasicWxGroupDao tBasicWxGroupDao;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Before("@annotation(com.mayday9.splatoonbot.common.annotation.AuthWxMsg)")
    public void beforeWxMsg(JoinPoint joinPoint) throws Exception {
        if (joinPoint.getArgs()[0] instanceof WechatMessage) {
            WechatMessage wechatMessage = (WechatMessage) joinPoint.getArgs()[0];
            if (wechatMessage.getWxid().contains("@chatroom")) {
                // 判断该微信组是否激活
                TBasicWxGroup wxGroup = tBasicWxGroupDao.findGroupByGid(wechatMessage.getWxid());
                if (wxGroup == null || FlagEnum.NO.equals(wxGroup.getActiveFlag())) {
                    List<String> wxIdList = new ArrayList<>();
                    textWxMsgSender.sendTextMessage("请先激活当前群组！", wechatMessage.getWxid(), wxIdList, false);
                    throw new RuntimeException("请先激活当前群组！");
                }
            }
        }

    }

}
