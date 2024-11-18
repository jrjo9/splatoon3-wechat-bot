package com.mayday9.splatoonbot.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:38
 **/
@Setter
@Getter
@NoArgsConstructor
public class TSplatSalmonRunInfoAddDTO {

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "鲑鱼跑类型")
    private String salmonRunType;

    @ApiModelProperty(value = "BOSS键值")
    private String bossKeyword;

    @ApiModelProperty(value = "BOSS名称")
    private String bossName;

    @ApiModelProperty(value = "地图键值")
    private String stageKeyword;

    @ApiModelProperty(value = "地图名称")
    private String stageName;

    @ApiModelProperty(value = "地图缩略图片")
    private String stageThumbnailImage;

    @ApiModelProperty(value = "地图图片")
    private String stageImage;

    @ApiModelProperty(value = "武器列表")
    private List<TSplatSalmonRunInfoWeaponAddDTO> weaponList;

}
