package com.mayday9.splatoonbot.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Lianjiannan
 * @since 2024/9/29 11:46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMatchDailyInfoDTO {

    @ApiModelProperty(value = "比赛类型")
    @NotNull(message = "比赛类型不能为空")
    private String matchType;

    @ApiModelProperty(value = "比赛次数")
    private Integer matchNumber = 6;

}
