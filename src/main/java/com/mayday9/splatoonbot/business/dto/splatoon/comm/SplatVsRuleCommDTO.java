package com.mayday9.splatoonbot.business.dto.splatoon.comm;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:45
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatVsRuleCommDTO {


    @Alias(value = "name")
    private String name;

    @Alias(value = "rule")
    private String rule;

    @Alias(value = "id;")
    private String id;

}
