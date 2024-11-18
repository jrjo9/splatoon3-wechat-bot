package com.mayday9.splatoonbot.business.service.wxmsg.receive.splatoon;

import com.mayday9.splatoonbot.business.constants.SplatoonConstant;
import com.mayday9.splatoonbot.business.dto.GetMatchDailyInfoDTO;
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
 * @since 2024/10/8 9:41
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.BANKARA, desc = "真格")
@Component
public class BankaraWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private SplatService splatService;

    @Resource
    private ImageWxMsgSender imageWxMsgSender;

    @Override
    @AuthWxMsg
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        GetMatchDailyInfoDTO getMatchDailyInfoDTO = new GetMatchDailyInfoDTO();
        getMatchDailyInfoDTO.setMatchType(SplatoonConstant.MATCH_TYPE_BANKARA);
        File file = splatService.getMatchDailyInfo(getMatchDailyInfoDTO);
        imageWxMsgSender.sendImageMessage(file.getPath(), wechatMessage.getWxid());
        file.delete();
    }

}
