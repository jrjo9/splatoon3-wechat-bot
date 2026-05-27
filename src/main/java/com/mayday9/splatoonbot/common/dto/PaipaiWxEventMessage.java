package com.mayday9.splatoonbot.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 派派框架 WebSocket 消息封装
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiWxEventMessage {

    /**
     * 消息类型
     * 999: CID分配
     * 1000: 应用注册
     * 1001: 事件消息（群聊/私聊）
     */
    private Integer Type;

    /**
     * CID分配时返回的应用唯一标识
     */
    private Integer CID;

    /**
     * Type=1001时为事件数据
     */
    private Object Data;

    /**
     * 应用Token（注册时使用）
     */
    private String ToKen;

    /**
     * 应用昵称（注册时使用）
     */
    private String Name;

    /**
     * 作者（注册时使用）
     */
    private String Author;

    /**
     * 版本（注册时使用）
     */
    private String Ver;

    /**
     * 应用图标URL（注册时使用）
     */
    private String AppIcon;

    /**
     * 应用描述（注册时使用）
     */
    private String Description;

    /**
     * 创建时间（注册时使用）
     */
    private String Creationtime;

    /**
     * 更新时间（注册时使用）
     */
    private String Updatetime;

    /**
     * 应用路径（注册时使用）
     */
    private String File;

    /**
     * 应用ID（注册时使用）
     */
    private String Appid;

    /**
     * 敏感度标记（注册时使用）
     */
    private Integer Sensitive;
}