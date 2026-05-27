package com.mayday9.splatoonbot.business.service.wxmsg.send;

import cn.hutool.core.util.StrUtil;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupUserNameQueryService;
import com.mayday9.splatoonbot.common.util.PaipaiApiUtil;
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
        String atList = null;
        if (!CollectionUtils.isEmpty(wxIdList)) {
            if (notifyAll) {
                // 艾特全体
                atList = "notify@all";
                text = "@全体成员 " + text;
            } else {
                atList = StrUtil.join(",", wxIdList);
                for (String wxid : wxIdList) {
                    String username = wxGroupUserNameQueryService.queryGroupUserName(groupId, wxid);
                    if (StrUtil.isEmpty(username)) {
                        // 群昵称为空时使用微信昵称
                        username = wxGroupUserNameQueryService.queryWxNickName(wxid);
                    }
                    if (StrUtil.isNotEmpty(username)) {
                        text = "@" + username + " " + text;
                    }
                }
            }
        }
        PaipaiApiUtil.sendTextMessage(groupId, text, atList);
    }

}