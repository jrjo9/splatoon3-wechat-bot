package com.mayday9.splatoonbot.business.dto.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lianjiannan
 * @since 2024/12/4 11:26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayStatisticsRankDTO {

    @ApiModelProperty(value = "排名")
    private Integer rank;

    @ApiModelProperty(value = "用户群昵称")
    private String userGroupNickName;

    @ApiModelProperty(value = "聊天数量")
    private Integer chatNumber;

}
