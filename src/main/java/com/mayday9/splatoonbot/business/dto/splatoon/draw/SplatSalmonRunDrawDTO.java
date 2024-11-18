package com.mayday9.splatoonbot.business.dto.splatoon.draw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 真格绘图DTO
 *
 * @author Lianjiannan
 * @since 2024/9/26 11:07
 **/
@Setter
@Getter
@NoArgsConstructor
public class SplatSalmonRunDrawDTO {

    // 时间点
    private String timeBetween;

    // BOSS名称
    private String bossName;

    // 地图图片
    private String stageImage;

    // 地图名称
    private String stageName;

    // 开放比赛地图详情
    private List<SplatSalmonRunDrawWeaponDTO> weaponDTOList;


}
