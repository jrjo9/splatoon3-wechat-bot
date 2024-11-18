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
* 喷喷武器数据表
*
* @author AutoGenerator
* @since 2024-10-08
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_weapon")
@ApiModel(value="TSplatWeapon对象", description="喷喷武器数据表")
public class TSplatWeapon extends BaseEntity {

    @ApiModelProperty(value = "键值", required = true)
    @TableField("keyword")
    private String keyword;

    @ApiModelProperty(value = "武器名称", required = true)
    @TableField("weapon_name")
    private String weaponName;

    @ApiModelProperty(value = "武器图片", required = true)
    @TableField("weapon_image")
    private String weaponImage;


}
