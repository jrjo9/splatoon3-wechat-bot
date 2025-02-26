CREATE TABLE `t_sys_param`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`    bigint(20)   DEFAULT NULL COMMENT '父主键',
    `param_code`   varchar(30)  DEFAULT NULL COMMENT '系统参数编号',
    `param_name`   varchar(60)  DEFAULT NULL COMMENT '系统参数名称',
    `param_info`   varchar(300) DEFAULT NULL COMMENT '系统参数说明，如说明参数值得格式要求',
    `param_value`  varchar(500) DEFAULT NULL COMMENT '系统参数值',
    `param_group`  char(2)      DEFAULT NULL COMMENT '系统参数分组',
    `sort_no`      int(11)      DEFAULT NULL COMMENT '排序号，用于分组内排序',
    `need_encrypt` char(1)      DEFAULT NULL COMMENT '是否需要加密，0：不需要，1：需要',
    `is_show`      char(1)      DEFAULT NULL COMMENT '是否展示（0：隐藏，1：展示）',
    `is_delete`    char(1)      DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`    bigint(20)   DEFAULT NULL COMMENT '创建人',
    `update_by`    bigint(20)   DEFAULT NULL COMMENT '更新人',
    `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `IDX_PARAM_1` (`parent_id`) USING BTREE,
    KEY `IDX_PARAM_2` (`param_code`, `param_name`) USING BTREE,
    KEY `IDX_PARAM_3` (`sort_no`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='系统参数表';


INSERT INTO `t_sys_param`(`id`, `parent_id`, `param_code`, `param_name`, `param_info`, `param_value`, `param_group`, `sort_no`, `need_encrypt`, `is_show`, `is_delete`, `create_by`, `update_by`,
                          `create_time`, `update_time`)
VALUES (1, 0, 'ai_chat_model', 'AI模型', 'ERNIE-Speed-8K、deepseek-v3', 'deepseek-v3', '0', 1, '0', '1', '0', NULL, NULL, '2025-02-25 16:28:46', NULL);
