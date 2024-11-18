package com.mayday9.splatoonbot.business.controller;

import com.mayday9.splatoonbot.business.dto.GetMatchDailyInfoDTO;
import com.mayday9.splatoonbot.business.service.SplatService;
import com.mayday9.splatoonbot.business.task.SplatDataRefreshTask;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/9/19 17:22
 **/
@RequestMapping("/splatoon")
@RestController
public class SplatController {

    @Resource
    private SplatDataRefreshTask splatDataRefreshTask;

    @Resource
    private SplatService splatService;

    /**
     * 刷新喷喷信息
     *
     * @param
     * @return void
     */
    @PostMapping("/invokeSplatDataRefresh")
    public void invokeSplatDataRefresh() throws Exception {
        splatDataRefreshTask.invokeSplatDataRefreshOnce();
    }

    /**
     * 刷新中文信息
     *
     * @param
     * @return void
     */
    @PostMapping("/invokeLangCnDataRefresh")
    public void invokeLangCnDataRefresh() throws Exception {
        splatDataRefreshTask.invokeLangCnDataRefreshOnce();
    }

    /**
     * 获取赛程信息
     *
     * @param getMatchDailyInfoDTO 查询DTO
     * @return void
     */
    @PostMapping("/getMatchDailyInfo")
    public void getMatchDailyInfo(@Validated @RequestBody GetMatchDailyInfoDTO getMatchDailyInfoDTO) throws Exception {
        splatService.getMatchDailyInfo(getMatchDailyInfoDTO);
    }
}
