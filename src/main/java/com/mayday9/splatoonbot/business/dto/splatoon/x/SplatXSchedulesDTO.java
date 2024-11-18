package com.mayday9.splatoonbot.business.dto.splatoon.x;

import cn.hutool.core.annotation.Alias;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesNodeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:31
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatXSchedulesDTO {

    // 数据节点
    @Alias(value = "nodes")
    private List<SplatXSchedulesNodeDTO> nodes;

}
