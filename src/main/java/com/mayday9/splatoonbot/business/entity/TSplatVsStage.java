package com.mayday9.splatoonbot.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mayday9.splatoonbot.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 喷喷地图数据表
 *
 * @author AutoGenerator
 * @since 2024-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_vs_stage")
@ApiModel(value = "TSplatVsStage对象", description = "喷喷地图数据表")
public class TSplatVsStage extends BaseEntity {

    @ApiModelProperty(value = "地图数据ID", required = true)
    @TableField("vs_stage_id")
    private Integer vsStageId;

    @ApiModelProperty(value = "地图图片", required = true)
    @TableField("original_image")
    private String originalImage;

    @ApiModelProperty(value = "地图名称", required = true)
    @TableField("vs_stage_name")
    private String vsStageName;

    @ApiModelProperty(value = "键值", required = true)
    @TableField("keyword")
    private String keyword;


}
