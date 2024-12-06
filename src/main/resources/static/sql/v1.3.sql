CREATE TABLE `t_basic_wx_chat_statistics`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `chat_date`   date        DEFAULT NULL COMMENT '聊天日期',
    `gid`         varchar(50) DEFAULT NULL COMMENT '微信群ID',
    `wxid`        varchar(50) DEFAULT NULL COMMENT '微信号',
    `chat_num`    int(10)     DEFAULT NULL COMMENT '聊天数量',
    `is_delete`   char(1)     DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)  DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)  DEFAULT NULL COMMENT '更新人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='聊天统计表';

ALTER TABLE `t_basic_wx_user`
    ADD COLUMN `nickname` varchar(200) NULL COMMENT '用户群昵称' AFTER `gid`;


ALTER TABLE `t_basic_wx_group`
    ADD COLUMN `auto_statistics_flag` char(1) NULL COMMENT '自动统计开关（1：开启，0：关闭）' AFTER `active_flag`;