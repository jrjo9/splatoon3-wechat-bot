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
* 喷喷鲑鱼跑打工武器信息表
*
* @author AutoGenerator
* @since 2024-10-08
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_salmon_run_weapon")
@ApiModel(value="TSplatSalmonRunWeapon对象", description="喷喷鲑鱼跑打工武器信息表")
public class TSplatSalmonRunWeapon extends BaseEntity {

    @ApiModelProperty(value = "比赛ID", required = true)
    @TableField("salmon_run_main_id")
    private Long salmonRunMainId;

    @ApiModelProperty(value = "武器键值", required = true)
    @TableField("weapon_keyword")
    private String weaponKeyword;

    @ApiModelProperty(value = "武器名称", required = true)
    @TableField("weapon_name")
    private String weaponName;

    @ApiModelProperty(value = "武器图片", required = true)
    @TableField("weapon_image")
    private String weaponImage;


}
