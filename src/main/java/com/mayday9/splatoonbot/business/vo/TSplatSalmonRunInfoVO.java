package com.mayday9.splatoonbot.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 13:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TSplatSalmonRunInfoVO", description = "")
public class TSplatSalmonRunInfoVO {

    @ApiModelProperty(value = "日程ID")
    private Long id;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛类型")
    private String salmonRunType;

    @ApiModelProperty(value = "BOSS名称")
    private String bossName;

    @ApiModelProperty(value = "BOSS中文名称")
    private String bossCnName;

    @ApiModelProperty(value = "地图名称")
    private String stageName;

    @ApiModelProperty(value = "地图中文名称")
    private String stageCnName;

    @ApiModelProperty(value = "地图图片")
    private String stageImageFileUrl;

    @ApiModelProperty(value = "比赛明细")
    private List<TSplatSalmonRunWeaponVO> weaponList;

}
