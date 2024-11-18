package com.mayday9.splatoonbot.business.dto.splatoon.fest;

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
public class SplatFestSchedulesDTO {

    @Alias(value = "nodes")
    private List<SplatFestSchedulesNodeDTO> nodes;
}
