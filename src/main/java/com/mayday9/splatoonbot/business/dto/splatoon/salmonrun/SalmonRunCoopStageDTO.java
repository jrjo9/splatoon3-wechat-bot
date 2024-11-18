package com.mayday9.splatoonbot.business.dto.splatoon.salmonrun;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsStageImageDTO;
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
public class SalmonRunCoopStageDTO {

    @Alias(value = "name")
    private String name;

    @Alias(value = "thumbnailImage")
    private SplatVsStageImageDTO thumbnailImage;

    @Alias(value = "image")
    private SplatVsStageImageDTO image;

    @Alias(value = "id")
    private String id;

}
