package com.mayday9.splatoonbot.business.dto.splatoon.salmonrun;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:31
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatSalmonRunSchedulesNodeDTO {

    @Alias(value = "startTime")
    private Date startTime;

    @Alias(value = "endTime")
    private Date endTime;

    @Alias(value = "setting")
    private SplatSalmonRunSchedulesNodeSettingDTO setting;

    @Alias(value = "__splatoon3ink_king_salmonid_guess")
    private String splatoon3inkKingSalmonidGuess;

}
