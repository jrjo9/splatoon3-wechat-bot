package com.mayday9.splatoonbot.business.dto.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lianjiannan
 * @since 2024/12/5 10:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindMonthTalkUserDTO {

    @ApiModelProperty(value = "微信群ID")
    private String gid;

    @ApiModelProperty(value = "微信号")
    private String wxid;

}
