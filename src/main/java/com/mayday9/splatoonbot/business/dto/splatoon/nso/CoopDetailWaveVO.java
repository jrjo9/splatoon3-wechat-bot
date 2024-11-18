package com.mayday9.splatoonbot.business.dto.splatoon.nso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/11/8 10:38
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoopDetailWaveVO {

    // 波次
    private Integer waveNumber;

    // 队伍搬蛋数量
    private Integer teamDeliverCount;

    // 本波次所需蛋数
    private Integer deliverNorm;

}
