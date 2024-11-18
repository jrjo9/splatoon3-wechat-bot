package com.mayday9.splatoonbot.business.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Lianjiannan
 * @since 2024/9/24 15:14
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxUserSignInDTO {

    @ApiModelProperty(value = "组ID", required = true)
    @NotNull(message = "组不能为空")
    private String gid;

    @ApiModelProperty(value = "微信号", required = true)
    @NotNull(message = "微信号不能为空")
    private String wxid;

}
