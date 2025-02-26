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
* 系统参数表
*
* @author AutoGenerator
* @since 2025-02-25
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_param")
@ApiModel(value="TSysParam对象", description="系统参数表")
public class TSysParam extends BaseEntity {

    @ApiModelProperty(value = "父主键", required = true)
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "系统参数编号", required = true)
    @TableField("param_code")
    private String paramCode;

    @ApiModelProperty(value = "系统参数名称", required = true)
    @TableField("param_name")
    private String paramName;

    @ApiModelProperty(value = "系统参数说明，如说明参数值得格式要求", required = true)
    @TableField("param_info")
    private String paramInfo;

    @ApiModelProperty(value = "系统参数值", required = true)
    @TableField("param_value")
    private String paramValue;

    @ApiModelProperty(value = "系统参数分组", required = true)
    @TableField("param_group")
    private String paramGroup;

    @ApiModelProperty(value = "排序号，用于分组内排序", required = true)
    @TableField("sort_no")
    private Integer sortNo;

    @ApiModelProperty(value = "是否需要加密，0：不需要，1：需要", required = true)
    @TableField("need_encrypt")
    private String needEncrypt;

    @ApiModelProperty(value = "是否展示（0：隐藏，1：展示）", required = true)
    @TableField("is_show")
    private FlagEnum isShow;


}
