package com.mayday9.splatoonbot.business.controller;

import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2025/2/25 16:41
 **/
@RequestMapping("/system/param")
@RestController
public class ParamController {

    @Resource
    private TSysParamService tSysParamService;

    /**
     * 根据编码查询参数
     *
     * @param paramCode 参数编码
     * @return TSysParam
     */
    @GetMapping("/findByCode")
    TSysParam findByCode(@RequestParam("paramCode") String paramCode) {
        return tSysParamService.findByCode(paramCode);
    }

    /**
     * 更新缓存
     *
     */
    @PostMapping("/flush")
    void flush() {
        tSysParamService.flushCache();
    }

}
