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

import java.util.Date;

/**
 * 喷喷鲑鱼跑打工信息表
 *
 * @author AutoGenerator
 * @since 2024-10-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_salmon_run_info")
@ApiModel(value = "TSplatSalmonRunInfo对象", description = "喷喷鲑鱼跑打工信息表")
public class TSplatSalmonRunInfo extends BaseEntity {

    @ApiModelProperty(value = "开始时间", required = true)
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "鲑鱼跑类型", required = true)
    @TableField("salmon_run_type")
    private String salmonRunType;

    @ApiModelProperty(value = "BOSS键值", required = true)
    @TableField("boss_keyword")
    private String bossKeyword;

    @ApiModelProperty(value = "BOSS名称", required = true)
    @TableField("boss_name")
    private String bossName;

    @ApiModelProperty(value = "地图键值", required = true)
    @TableField("stage_keyword")
    private String stageKeyword;

    @ApiModelProperty(value = "地图名称", required = true)
    @TableField("stage_name")
    private String stageName;

    @ApiModelProperty(value = "地图缩略图片", required = true)
    @TableField("stage_thumbnail_image")
    private String stageThumbnailImage;

    @ApiModelProperty(value = "地图图片", required = true)
    @TableField("stage_image")
    private String stageImage;

    @TableField(exist = false)
    private String tempId;


}
