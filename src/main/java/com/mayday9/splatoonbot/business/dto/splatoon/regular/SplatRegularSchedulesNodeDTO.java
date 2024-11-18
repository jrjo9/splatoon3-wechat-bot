package com.mayday9.splatoonbot.business.dto.splatoon.regular;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:30
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatRegularSchedulesNodeDTO {

    @Alias(value = "startTime")
    private Date startTime;

    @Alias(value = "endTime")
    private Date endTime;

    @Alias(value = "regularMatchSetting")
    private SplatRegularSchedulesMatchSettingDTO regularMatchSetting;


}
