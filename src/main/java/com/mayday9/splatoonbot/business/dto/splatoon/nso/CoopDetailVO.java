package com.mayday9.splatoonbot.business.dto.splatoon.nso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/11/8 10:38
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoopDetailVO {

    // 地图名称
    private String stageName;

    // 危险度
    private Double dangerRate;

    // 最终波次
    private Integer resultWave;

    // 波次信息
    private List<CoopDetailWaveVO> waveList;

    // 玩家信息
    private List<CoopDetailPlayerVO> playerList;

    public void generateRankScore() {
        // 计算规则
        // 搬蛋数量 + 0.5 * 协助搬蛋数量 + 0.005 * 红蛋数量 + 击败大鲑鱼数量 + 2 * (救援次数 - 死亡次数)
        for (CoopDetailPlayerVO coopDetailPlayerVO : playerList) {
            double score = coopDetailPlayerVO.getGoldenDeliverCount() + 0.5 * coopDetailPlayerVO.getGoldenAssistCount()
                + 0.005 * coopDetailPlayerVO.getDeliverCount() + coopDetailPlayerVO.getDefeatEnemyCount()
                + 2 * (coopDetailPlayerVO.getRescueCount() - coopDetailPlayerVO.getRescuedCount());
            BigDecimal bg = new BigDecimal(score);
            double f1 = bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
            coopDetailPlayerVO.setRankScore(f1);
        }
    }
}
