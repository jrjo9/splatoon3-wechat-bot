package com.mayday9.splatoonbot.business.dto.splatoon.salmonrun;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:31
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatCoopGroupingScheduleDTO {

    @Alias(value = "bannerImage")
    private String bannerImage;

    @Alias(value = "regularSchedules")
    private SplatSalmonRunSchedulesDTO regularSchedules;

    @Alias(value = "regularSchedules")
    private SplatBigRunSchedulesDTO bigRunSchedules;

    @Alias(value = "regularSchedules")
    private SplatTeamContestSchedulesDTO teamContestSchedules;

}
