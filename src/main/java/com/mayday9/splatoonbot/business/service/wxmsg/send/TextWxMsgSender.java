package com.mayday9.splatoonbot.business.service.wxmsg.send;

import cn.hutool.core.util.StrUtil;
import com.mayday9.splatoonbot.business.dto.wxmsg.req.TextWxMsgSendDTO;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupUserNameQueryService;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:03
 **/
@Component
public class TextWxMsgSender {

    @Resource
    private WxGroupUserNameQueryService wxGroupUserNameQueryService;


    public void sendTextMessage(String text, String groupId, List<String> wxIdList, boolean notifyAll) throws Exception {
        TextWxMsgSendDTO textWxMsgSendDto = new TextWxMsgSendDTO();
        if (!CollectionUtils.isEmpty(wxIdList)) {
            if (notifyAll) {
                // 艾特全体
                textWxMsgSendDto.setAtid("notify@all");
                text = "@全体成员 " + text;
            } else {
                textWxMsgSendDto.setAtid(StrUtil.join(",", wxIdList));
                for (String wxid : wxIdList) {
                    String username = wxGroupUserNameQueryService.queryGroupUserName(groupId, wxid);
                    text = "@" + username + " " + text;
                }
            }
        }
        textWxMsgSendDto.setContent(text);
        textWxMsgSendDto.setType(101);
        textWxMsgSendDto.setWxid(groupId);
        WxMsgSendUtil.sendMessage(textWxMsgSendDto);
    }

}
