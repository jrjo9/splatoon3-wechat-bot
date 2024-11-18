package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.GetMatchDailyInfoDTO;
import com.mayday9.splatoonbot.business.dto.GetSalmonRunDailyInfoDTO;

import java.io.File;

/**
 * @author Lianjiannan
 * @since 2024/9/29 11:44
 **/
public interface SplatService {

    /**
     * 获取赛程信息
     *
     * @param getMatchDailyInfoDTO 数据DTO
     * @return void
     */
    File getMatchDailyInfo(GetMatchDailyInfoDTO getMatchDailyInfoDTO);

    /**
     * 获取鲑鱼跑信息
     *
     * @param getSalmonRunDailyInfoDTO 数据DTO
     * @return File
     */
    File getSalmonRunDailyInfo(GetSalmonRunDailyInfoDTO getSalmonRunDailyInfoDTO);
}
