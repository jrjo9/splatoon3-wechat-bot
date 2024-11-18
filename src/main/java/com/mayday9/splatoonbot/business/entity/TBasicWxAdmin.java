package com.mayday9.splatoonbot.business.entity;

import com.mayday9.splatoonbot.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
* 微信人员信息表
*
* @author AutoGenerator
* @since 2024-10-10
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_wx_admin")
@ApiModel(value="TBasicWxAdmin对象", description="微信人员信息表")
public class TBasicWxAdmin extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "用户名称", required = true)
    @TableField("username")
    private String username;


}
