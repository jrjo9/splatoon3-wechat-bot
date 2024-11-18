package com.mayday9.splatoonbot.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lianjiannan
 * @since 2024/9/29 11:46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSalmonRunDailyInfoDTO {

    @ApiModelProperty(value = "打工")
    private Integer matchNumber = 5;

}
