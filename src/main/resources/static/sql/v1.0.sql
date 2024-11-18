/*
 Navicat Premium Data Transfer

 Source Server         : 开发数据库
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 192.168.59.210:3306
 Source Schema         : wx-bot

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 12/10/2024 17:51:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_basic_sign_in
-- ----------------------------
DROP TABLE IF EXISTS `t_basic_sign_in`;
CREATE TABLE `t_basic_sign_in`
(
    `id`           bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wxid`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信号',
    `gid`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信群ID',
    `username`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
    `sign_in_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '签到时间',
    `is_delete`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`    bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`    bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`  datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '签到表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_basic_wx_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_basic_wx_admin`;
CREATE TABLE `t_basic_wx_admin`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wxid`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信号',
    `username`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '微信人员信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_basic_wx_group
-- ----------------------------
DROP TABLE IF EXISTS `t_basic_wx_group`;
CREATE TABLE `t_basic_wx_group`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gid`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信号',
    `group_name`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信群名称',
    `active_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT NULL COMMENT '是否激活',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `UNIQ_GID` (`gid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '微信群信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_basic_wx_user
-- ----------------------------
DROP TABLE IF EXISTS `t_basic_wx_user`;
CREATE TABLE `t_basic_wx_user`
(
    `id`                 bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wxid`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信号',
    `username`           varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
    `gid`                varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信群ID',
    `salmon_eggs`        int(10)                                                       NULL DEFAULT NULL COMMENT '鲑鱼蛋数量',
    `sign_in_days_keep`  int(10)                                                       NULL DEFAULT NULL COMMENT '连续签到天数',
    `sign_in_days_total` int(10)                                                       NULL DEFAULT NULL COMMENT '总签到天数',
    `last_sign_in_time`  datetime(0)                                                   NULL DEFAULT NULL COMMENT '签到时间',
    `is_delete`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`          bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`          bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`        datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '微信人员信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_lang_cn
-- ----------------------------
DROP TABLE IF EXISTS `t_lang_cn`;
CREATE TABLE `t_lang_cn`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '对照类型',
    `keyword`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '键值',
    `cn_name`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '中文名称',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '中文语言表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_match_info
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_match_info`;
CREATE TABLE `t_splat_match_info`
(
    `id`          bigint(20)                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `start_time`  datetime(0)                                                  NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`    datetime(0)                                                  NULL DEFAULT NULL COMMENT '结束时间',
    `match_type`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '比赛类型',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                   NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                   NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                  NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷比赛信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_match_info_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_match_info_detail`;
CREATE TABLE `t_splat_match_info_detail`
(
    `id`          bigint(20)                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `match_id`    bigint(20)                                                   NULL DEFAULT NULL COMMENT '比赛ID',
    `vs_stage_id` int(10)                                                      NULL DEFAULT NULL COMMENT '地图数据ID',
    `match_rule`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '比赛规则',
    `match_mode`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '比赛模式',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                   NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                   NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                  NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷比赛信息明细表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_salmon_run_info
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_salmon_run_info`;
CREATE TABLE `t_splat_salmon_run_info`
(
    `id`                    bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `start_time`            datetime(0)                                                   NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`              datetime(0)                                                   NULL DEFAULT NULL COMMENT '结束时间',
    `salmon_run_type`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '鲑鱼跑类型',
    `boss_keyword`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT 'BOSS键值',
    `boss_name`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'BOSS名称',
    `stage_keyword`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '地图键值',
    `stage_name`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图名称',
    `stage_thumbnail_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图缩略图片',
    `stage_image`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图图片',
    `is_delete`             char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`             bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`             bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`           datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷鲑鱼跑打工信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_salmon_run_stage
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_salmon_run_stage`;
CREATE TABLE `t_splat_salmon_run_stage`
(
    `id`                    bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `stage_keyword`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '地图键值',
    `stage_name`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图名称',
    `stage_image`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图图片',
    `stage_thumbnail_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图缩略图片',
    `is_delete`             char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`             bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`             bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`           datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷鲑鱼跑地图数据表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_salmon_run_weapon
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_salmon_run_weapon`;
CREATE TABLE `t_splat_salmon_run_weapon`
(
    `id`                 bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `salmon_run_main_id` bigint(20)                                                    NULL DEFAULT NULL COMMENT '比赛ID',
    `weapon_keyword`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '武器键值',
    `weapon_name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '武器名称',
    `weapon_image`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '武器图片',
    `is_delete`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`          bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`          bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`        datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷鲑鱼跑打工武器信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_vs_stage
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_vs_stage`;
CREATE TABLE `t_splat_vs_stage`
(
    `id`             bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `vs_stage_id`    int(10)                                                       NULL DEFAULT NULL COMMENT '地图ID',
    `original_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图图片URL',
    `vs_stage_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地图名称',
    `keyword`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '键值',
    `is_delete`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`      bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`      bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`    datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷地图数据表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_splat_weapon
-- ----------------------------
DROP TABLE IF EXISTS `t_splat_weapon`;
CREATE TABLE `t_splat_weapon`
(
    `id`           bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `keyword`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '键值',
    `weapon_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '武器名称',
    `weapon_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '武器图片',
    `is_delete`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`    bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`    bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time`  datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '喷喷武器数据表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_sys_file
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_file`;
CREATE TABLE `t_sys_file`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `biz_id`      bigint(20)                                                    NULL DEFAULT NULL COMMENT '业务ID',
    `biz_args`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '业务参数',
    `file_title`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件标题',
    `file_type`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型',
    `sort`        int(10)                                                       NULL DEFAULT NULL COMMENT '排序号',
    `file_url`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件URL',
    `file_size`   bigint(255)                                                   NULL DEFAULT NULL COMMENT '文件大小',
    `width`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '图片宽度',
    `height`      bigint(20)                                                    NULL DEFAULT NULL COMMENT '图片高度',
    `is_delete`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '是否作废(1：是，0：否)',
    `create_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20)                                                    NULL DEFAULT NULL COMMENT '更新人',
    `create_time` datetime(0)                                                   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `IDX_FILE_1` (`sort`) USING BTREE,
    INDEX `IDX_FILE_2` (`file_type`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '附件表'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
