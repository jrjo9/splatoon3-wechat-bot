package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:16
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxMsgSendDTO {

    // 框架验证码
    @Alias("Token")
    private String token;

    // 请求类型，固定
    @Alias("Type")
    private Integer type;


}
