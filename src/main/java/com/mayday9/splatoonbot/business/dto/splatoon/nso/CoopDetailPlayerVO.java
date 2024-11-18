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
public class CoopDetailPlayerVO {

    private String playerName;

    // 巨大鲑鱼
    private Integer defeatEnemyCount;

    // 红鲑鱼蛋
    private Integer deliverCount;

    // 金鲑鱼蛋
    private Integer goldenDeliverCount;

    // 金鲑鱼蛋（协助数量）
    private Integer goldenAssistCount;

    // 救援次数
    private Integer rescueCount;

    // 死亡次数
    private Integer rescuedCount;

    // 分数
    private Double rankScore;

}
