package com.mayday9.splatoonbot.business.service.wxmsg.receive.splatoon;

import com.mayday9.splatoonbot.business.constants.SplatoonConstant;
import com.mayday9.splatoonbot.business.dto.GetMatchDailyInfoDTO;
import com.mayday9.splatoonbot.business.dto.GetSalmonRunDailyInfoDTO;
import com.mayday9.splatoonbot.business.service.SplatService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.ImageWxMsgSender;
import com.mayday9.splatoonbot.common.annotation.AuthWxMsg;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author Lianjiannan
 * @since 2024/10/8 9:44
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.SALMON_RUN, desc = "鲑鱼跑")
@Component
public class SalmonRunWxMsgService extends PaipaiWxMsgStrategy {
    @Resource
    private SplatService splatService;

    @Resource
    private ImageWxMsgSender imageWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        GetSalmonRunDailyInfoDTO getSalmonRunDailyInfoDTO = new GetSalmonRunDailyInfoDTO();
        File file = splatService.getSalmonRunDailyInfo(getSalmonRunDailyInfoDTO);
        imageWxMsgSender.sendImageMessage(file.getPath(), wechatMessage.getWxid());
        file.delete();
    }
}
