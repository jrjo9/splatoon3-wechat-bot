package com.mayday9.splatoonbot.business.service.wxmsg.send;

import com.mayday9.splatoonbot.common.util.PaipaiApiUtil;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:04
 **/
@Component
public class ImageWxMsgSender {

    public void sendImageMessage(String filePath, String wxid) throws Exception {
        PaipaiApiUtil.sendImageMessage(wxid, filePath);
    }
}