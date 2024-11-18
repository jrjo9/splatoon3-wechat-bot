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
* 微信群信息表
*
* @author AutoGenerator
* @since 2024-09-25
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_wx_group")
@ApiModel(value="TBasicWxGroup对象", description="微信群信息表")
public class TBasicWxGroup extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "微信群名称", required = true)
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty(value = "是否激活", required = true)
    @TableField("active_flag")
    private FlagEnum activeFlag;


}
