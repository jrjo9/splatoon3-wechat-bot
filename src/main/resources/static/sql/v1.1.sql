CREATE TABLE `t_basic_wx_user_nso`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wxid`           varchar(50)   DEFAULT NULL COMMENT '微信号',
    `username`       varchar(200)  DEFAULT NULL COMMENT '微信名称',
    `nso_name`       varchar(200)  DEFAULT NULL COMMENT 'NSO昵称',
    `lang`           varchar(10)   DEFAULT NULL COMMENT '语言',
    `country`        varchar(10)   DEFAULT NULL COMMENT '国家',
    `session_token`  varchar(1000)  DEFAULT NULL COMMENT 'SESSION TOKEN',
    `g_token`        varchar(2000) DEFAULT NULL COMMENT 'gameServiceToken',
    `bullet_token`   varchar(500)  DEFAULT NULL COMMENT 'bullet_token',
    `login_time`     datetime      DEFAULT NULL COMMENT '登陆时间',
    `get_token_time` datetime      DEFAULT NULL COMMENT '获取token时间',
    `is_delete`      char(1)       DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`      bigint(20)    DEFAULT NULL COMMENT '创建人',
    `update_by`      bigint(20)    DEFAULT NULL COMMENT '更新人',
    `create_time`    datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='微信人员NSO表';