package com.mayday9.splatoonbot.business.dto.splatoon;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.bankara.SplatBankaraSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.fest.SplatFestSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SplatCoopGroupingScheduleDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.vsstage.SplatVsStagesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.x.SplatXSchedulesDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:29
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatDataDTO {

    // 涂地比赛
    @Alias(value = "regularSchedules")
    private SplatRegularSchedulesDTO regularSchedules;

    // 真格比赛
    @Alias(value = "bankaraSchedules")
    private SplatBankaraSchedulesDTO bankaraSchedules;

    // X赛
    @Alias(value = "xSchedules")
    private SplatXSchedulesDTO xSchedules;

    @Alias(value = "eventSchedules")
    private SplatEventSchedulesDTO eventSchedules;

    @Alias(value = "festSchedules")
    private SplatFestSchedulesDTO festSchedules;

    @Alias(value = "coopGroupingSchedule")
    private SplatCoopGroupingScheduleDTO coopGroupingSchedule;

    @Alias(value = "currentFest")
    private SplatCurrentFestDTO currentFest;

    @Alias(value = "currentPlayer")
    private SplatCurrentPlayerDTO currentPlayer;

    // 地图数据
    @Alias(value = "vsStages")
    private SplatVsStagesDTO vsStages;

}
