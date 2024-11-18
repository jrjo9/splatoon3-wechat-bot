package com.mayday9.splatoonbot.business.dto.splatoon.x;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesMatchSettingDTO;
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
public class SplatXSchedulesNodeDTO {

    @Alias(value = "startTime")
    private Date startTime;

    @Alias(value = "endTime")
    private Date endTime;

    @Alias(value = "xMatchSetting")
    private SplatXSchedulesMatchSettingDTO xMatchSetting;
}
