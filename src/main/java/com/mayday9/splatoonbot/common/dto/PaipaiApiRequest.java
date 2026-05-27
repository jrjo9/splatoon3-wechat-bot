package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 派派框架 API 请求基类
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiApiRequest {

    // 框架验证码
    @Alias("ToKen")
    private String token;

    // 应用唯一标识
    @Alias("CID")
    private Integer cid;

    // API类型，区分不同的消息类型或操作
    @Alias("Type")
    private Integer type;

    // 消息数据
    @Alias("Data")
    private Object data;

}