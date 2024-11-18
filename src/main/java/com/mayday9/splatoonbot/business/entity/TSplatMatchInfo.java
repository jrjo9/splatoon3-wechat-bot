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
 * 喷喷比赛信息表
 *
 * @author AutoGenerator
 * @since 2024-09-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_splat_match_info")
@ApiModel(value = "TSplatMatchInfo对象", description = "喷喷比赛信息表")
public class TSplatMatchInfo extends BaseEntity {

    @ApiModelProperty(value = "开始时间", required = true)
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "比赛类型", required = true)
    @TableField("match_type")
    private String matchType;


    @TableField(exist = false)
    private String tempId;
}
