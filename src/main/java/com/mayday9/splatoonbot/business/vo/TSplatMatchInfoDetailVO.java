package com.mayday9.splatoonbot.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lianjiannan
 * @since 2024/9/29 13:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TSplatMatchInfoDetailVO", description = "")
public class TSplatMatchInfoDetailVO {

    @ApiModelProperty(value = "比赛明细ID")
    private Long matchDetailId;

    @ApiModelProperty(value = "地图数据ID")
    private Integer vsStageId;

    @ApiModelProperty(value = "比赛规则")
    private String matchRule;

    @ApiModelProperty(value = "比赛模式")
    private String matchMode;

    @ApiModelProperty(value = "比图名称")
    private String vsStageName;

    @ApiModelProperty(value = "地图中文名称")
    private String cnName;

    @ApiModelProperty(value = "图片名称")
    private String fileTitle;

    @ApiModelProperty(value = "图片URL")
    private String fileUrl;


}
