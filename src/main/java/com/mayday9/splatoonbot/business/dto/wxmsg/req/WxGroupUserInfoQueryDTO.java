package com.mayday9.splatoonbot.business.dto.wxmsg.req;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.common.dto.WxMsgSendDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/12/4 14:12
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxGroupUserInfoQueryDTO extends WxMsgSendDTO {

    @Alias("Gid")
    private String gid;

}
