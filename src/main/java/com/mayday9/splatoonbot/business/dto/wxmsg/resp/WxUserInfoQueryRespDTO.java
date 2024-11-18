package com.mayday9.splatoonbot.business.dto.wxmsg.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/10/9 16:03
 **/

@Setter
@Getter
@NoArgsConstructor
public class WxUserInfoQueryRespDTO {

    private String wxid;

    private String name;

    private String mark;

    private String account;

    private String headimg;

}
