package com.mayday9.splatoonbot.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/11/11 10:45
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxBotTestDTO {

    @ApiModelProperty(value = "开始时间")
    private String text;
}
