package com.mayday9.splatoonbot.business.dto.splatoon.regular;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsRuleCommDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsStageCommDTO;
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
public class SplatRegularSchedulesMatchSettingDTO {

    @Alias(value = "__isVsSetting")
    private String isVsSetting;

    @Alias(value = "__typename")
    private String typeName;

    @Alias(value = "vsStages")
    private List<SplatVsStageCommDTO> vsStages;

    @Alias(value = "vsRule")
    private SplatVsRuleCommDTO vsRule;
}
