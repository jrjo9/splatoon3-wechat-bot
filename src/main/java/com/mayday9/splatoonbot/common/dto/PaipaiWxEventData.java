package com.mayday9.splatoonbot.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 派派框架 WebSocket 事件消息
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiWxEventData {

    /**
     * 消息类型：1=文本
     */
    private Integer Type;

    /**
     * 0:他人 1:自己
     */
    private Integer IsSelf;

    /**
     * 本地消息ID
     */
    private Integer LocalId;

    /**
     * 服务器消息ID
     */
    private Long MsgSvrID;

    /**
     * 私聊=发送者ID，群聊=群ID
     */
    private String From;

    /**
     * 群聊=实际发送者，私聊=""
     */
    private String Final_From;

    /**
     * 消息内容
     */
    private String Msg;

    /**
     * 文件路径
     */
    private String FilePath;

    /**
     * 艾特微信列表
     */
    private String ATList;
}