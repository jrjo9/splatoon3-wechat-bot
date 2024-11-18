package com.mayday9.splatoonbot.business.dto.splatoon.vsstage;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:32
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatVsStagesDTO {

    // 数据节点
    @Alias(value = "nodes")
    private List<SplatVsStagesNodeDTO> nodes;

}
