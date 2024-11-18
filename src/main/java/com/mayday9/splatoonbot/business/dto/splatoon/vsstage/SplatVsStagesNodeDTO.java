package com.mayday9.splatoonbot.business.dto.splatoon.vsstage;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsStageImageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:32
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatVsStagesNodeDTO {

    @Alias(value = "vsStageId")
    private Integer vsStageId;

    @Alias(value = "originalImage")
    private SplatVsStageImageDTO image;

    @Alias(value = "name")
    private String name;

    @Alias(value = "status")
    private String status;

    @Alias(value = "id")
    private String id;
}
