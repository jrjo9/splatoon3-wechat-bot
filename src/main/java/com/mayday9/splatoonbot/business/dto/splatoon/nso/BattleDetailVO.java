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
 * @since 2024/11/8 14:59
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BattleDetailVO {

    // 比赛模式
    private String mode;

    // 比赛名称
    private String matchName;

    // 结果
    private String judgement;

    // 我方玩家
    private List<BattleDetailPlayerVO> myTeamPlayerList;

    // 敌方玩家
    private List<BattleDetailPlayerVO> enemyTeamPlayerList;

    public void generateRankScore() {
        getScore(myTeamPlayerList);
        getScore(enemyTeamPlayerList);
    }

    private void getScore(List<BattleDetailPlayerVO> myTeamPlayerList) {
        for (BattleDetailPlayerVO battleDetailPlayerVO : myTeamPlayerList) {
            // 计算规则
            // （击杀数量 - 死亡数量）* 2 + 涂地点数 * 0.005 + 大招数量 * 0.5 + 协助数量
            double score = 2 * (battleDetailPlayerVO.getKill() - battleDetailPlayerVO.getDeath())
                + battleDetailPlayerVO.getPaint() * 0.005 + battleDetailPlayerVO.getSpecial() * 0.5
                + battleDetailPlayerVO.getAssist();
            BigDecimal bg = new BigDecimal(score);
            double f1 = bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
            battleDetailPlayerVO.setRankScore(f1);
        }
    }

}
