package com.mayday9.splatoonbot.business.dto.splatoon.bankara;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:30
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatBankaraSchedulesDTO {

    @Alias(value = "nodes")
    private List<SplatBankaraSchedulesNodeDTO> nodes;
}
