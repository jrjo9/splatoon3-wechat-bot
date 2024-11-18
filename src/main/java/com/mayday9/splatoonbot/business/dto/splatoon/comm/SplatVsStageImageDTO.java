package com.mayday9.splatoonbot.business.dto.splatoon.comm;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:46
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatVsStageImageDTO {

    @Alias(value = "url")
    private String url;

}
