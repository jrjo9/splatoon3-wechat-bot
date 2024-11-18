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
public class BattleDetailPlayerVO {

    private String playerName;

    // 涂地点数
    private Integer paint;

    // 击杀数量
    private Integer kill;

    // 死亡数量
    private Integer death;

    // 助攻数量
    private Integer assist;

    // 大招数量
    private Integer special;

    // 分数
    private Double rankScore;

}
