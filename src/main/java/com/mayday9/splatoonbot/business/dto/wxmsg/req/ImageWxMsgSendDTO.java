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
public class ImageWxMsgSendDTO extends WxMsgSendDTO {

    // C:xxx1.jpg
    @Alias("Content")
    private String content;

    // 想发送给谁，微信群ID或者联系人微信ID
    @Alias("Wxid")
    private String wxid;
}
