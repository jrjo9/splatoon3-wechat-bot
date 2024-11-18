package com.mayday9.splatoonbot.business.dto.wxmsg.req;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.common.dto.WxMsgSendDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:19
 **/
@Setter
@Getter
@NoArgsConstructor
public class TextWxMsgSendDTO extends WxMsgSendDTO {

    // 想发送给谁，微信群ID或者联系人微信ID
    @Alias("Wxid")
    private String wxid;

    // 消息内容，如需要艾特别人，消息内容里需要带上@符
    @Alias("Content")
    private String content;

    // 被艾特人wxid列表，想艾特谁就传谁，多个用,分割（wxid1,wxid2），艾特全体notify@all
    @Alias("Atid")
    private String atid;

}
