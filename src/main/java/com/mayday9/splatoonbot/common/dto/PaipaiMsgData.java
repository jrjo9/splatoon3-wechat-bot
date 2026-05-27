package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 发送消息数据
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiMsgData {

    // 收信人WxID或收信群ID
    @Alias("ToID")
    private String toId;

    // 消息正文内容
    @Alias("Content")
    private String content;

    // 艾特ID，多个wxid用,分隔
    @Alias("AtList")
    private String atList;

    // 消息类型：1文字 2图片/文件 5GIF
    @Alias("Type")
    private String msgType;

    // 本地消息ID
    @Alias("LocalId")
    private Integer localId;

    // 是否艾特所有人
    @Alias("ATALL")
    private Boolean atAll;

}