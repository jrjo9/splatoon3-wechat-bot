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
* 喷喷鲑鱼跑地图数据表
*
* @author AutoGenerator
* @since 2024-10-10
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_salmon_run_stage")
@ApiModel(value="TSplatSalmonRunStage对象", description="喷喷鲑鱼跑地图数据表")
public class TSplatSalmonRunStage extends BaseEntity {

    @ApiModelProperty(value = "地图键值", required = true)
    @TableField("stage_keyword")
    private String stageKeyword;

    @ApiModelProperty(value = "地图名称", required = true)
    @TableField("stage_name")
    private String stageName;

    @ApiModelProperty(value = "地图图片", required = true)
    @TableField("stage_image")
    private String stageImage;

    @ApiModelProperty(value = "地图缩略图片", required = true)
    @TableField("stage_thumbnail_image")
    private String stageThumbnailImage;


}
