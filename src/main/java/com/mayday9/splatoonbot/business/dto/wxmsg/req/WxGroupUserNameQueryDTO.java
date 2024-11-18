package com.mayday9.splatoonbot.business.dto.wxmsg.req;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.common.dto.WxMsgSendDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/10/9 16:06
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxGroupUserNameQueryDTO extends WxMsgSendDTO {

    @Alias("Gid")
    private String gid;

    @Alias("Wxid")
    private String wxid;

}
