package com.mayday9.splatoonbot.business.dto.splatoon.salmonrun;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:31
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatSalmonRunSchedulesNodeSettingDTO {

    @Alias(value = "__typename")
    private String typeName;

    @Alias(value = "boss")
    private SalmonRunBossDTO boss;

    @Alias(value = "coopStage")
    private SalmonRunCoopStageDTO coopStage;

    @Alias(value = "__isCoopSetting")
    private String isCoopSetting;

    @Alias(value = "weapons")
    private List<SalmonRunWeaponDTO> weapons;
}
