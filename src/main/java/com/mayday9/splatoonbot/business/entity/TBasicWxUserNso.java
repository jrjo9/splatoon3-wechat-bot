package com.mayday9.splatoonbot.business.entity;

import com.mayday9.splatoonbot.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.mayday9.splatoonbot.common.enums.*;

/**
*
* 微信人员NSO表
*
* @author AutoGenerator
* @since 2024-11-11
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_wx_user_nso")
@ApiModel(value="TBasicWxUserNso对象", description="微信人员NSO表")
public class TBasicWxUserNso extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "NSO昵称", required = true)
    @TableField("nso_name")
    private String nsoName;

    @ApiModelProperty(value = "语言", required = true)
    @TableField("lang")
    private String lang;

    @ApiModelProperty(value = "国家", required = true)
    @TableField("country")
    private String country;

    @ApiModelProperty(value = "SESSION TOKEN", required = true)
    @TableField("session_token")
    private String sessionToken;

    @ApiModelProperty(value = "gameServiceToken", required = true)
    @TableField("g_token")
    private String gToken;

    @ApiModelProperty(value = "bullet_token", required = true)
    @TableField("bullet_token")
    private String bulletToken;

    @ApiModelProperty(value = "登陆时间", required = true)
    @TableField("login_time")
    private Date loginTime;

    @ApiModelProperty(value = "获取token时间", required = true)
    @TableField("get_token_time")
    private Date getTokenTime;


}
