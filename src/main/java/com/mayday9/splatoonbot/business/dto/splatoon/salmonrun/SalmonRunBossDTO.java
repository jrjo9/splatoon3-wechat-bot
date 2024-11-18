package com.mayday9.splatoonbot.business.dto.splatoon.salmonrun;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/10/8 17:28
 **/
@Setter
@Getter
@NoArgsConstructor
public class SalmonRunBossDTO {

    @Alias(value = "name")
    private String name;

    @Alias(value = "id")
    private String id;
}
