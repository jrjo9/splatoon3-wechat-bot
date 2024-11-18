package com.mayday9.splatoonbot.business.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:38
 **/
@Setter
@Getter
@NoArgsConstructor
public class TSplatMatchInfoDetailAddDTO {

    @ApiModelProperty(value = "地图数据ID")
    private Integer vsStageId;

    @ApiModelProperty(value = "比赛规则")
    private String matchRule;

    @ApiModelProperty(value = "比赛模式")
    private String matchMode;

}
