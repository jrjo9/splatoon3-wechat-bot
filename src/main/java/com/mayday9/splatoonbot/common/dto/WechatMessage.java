package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.common.enums.WechatMessageTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/10 17:24
 **/
@Setter
@Getter
@NoArgsConstructor
public class WechatMessage {

    // {"Type":1,"Sender":0,"MsgID":28366,"Time":1725955050,"Wxid":"7149208010@chatroom","Talker":"tin0101","Content":"碰上较真的真路人","Path":"","AtList":""}
    // {"Type":1,"Sender":0,"MsgID":7623,"Time":1747293582,"Wxid":"loveuxiao9","Talker":"","Content":"你好","Path":"","AtList":""}

    // 类型
    @Alias(value = "Type")
    private WechatMessageTypeEnum type;

    // 发送者类型，0:别人 1:自己
    @Alias(value = "Sender")
    private Integer sender;

    // 信息ID
    @Alias(value = "MsgID")
    private Integer msgID;

    // 接收时间
    @Alias(value = "Time")
    private Long time;

    // 微信ID
    @Alias(value = "Wxid")
    private String wxid;

    // 发送者
    @Alias(value = "Talker")
    private String talker;

    // 发送内容
    @Alias(value = "Content")
    private String content;

    // 图片路径
    @Alias(value = "Path")
    private String path;

    // 艾特微信列表
    @Alias(value = "AtList")
    private String atList;


}
