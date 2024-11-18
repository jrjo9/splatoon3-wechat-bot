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
* 喷喷比赛信息明细表
*
* @author AutoGenerator
* @since 2024-09-29
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_match_info_detail")
@ApiModel(value="TSplatMatchInfoDetail对象", description="喷喷比赛信息明细表")
public class TSplatMatchInfoDetail extends BaseEntity {

    @ApiModelProperty(value = "比赛ID", required = true)
    @TableField("match_id")
    private Long matchId;

    @ApiModelProperty(value = "地图数据ID", required = true)
    @TableField("vs_stage_id")
    private Integer vsStageId;

    @ApiModelProperty(value = "比赛规则", required = true)
    @TableField("match_rule")
    private String matchRule;

    @ApiModelProperty(value = "比赛模式", required = true)
    @TableField("match_mode")
    private String matchMode;


}
