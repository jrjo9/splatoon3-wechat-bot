package com.mayday9.splatoonbot.business.service.wxmsg.send;

import com.mayday9.splatoonbot.business.dto.wxmsg.req.ImageWxMsgSendDTO;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:04
 **/
@Component
public class ImageWxMsgSender {

    public void sendImageMessage(String filePath, String wxid) throws Exception {
        ImageWxMsgSendDTO imageWxMsgSendDTO = new ImageWxMsgSendDTO();
        imageWxMsgSendDTO.setType(105);
        imageWxMsgSendDTO.setContent(filePath);
        imageWxMsgSendDTO.setWxid(wxid);
        WxMsgSendUtil.sendMessage(imageWxMsgSendDTO);
    }
}
