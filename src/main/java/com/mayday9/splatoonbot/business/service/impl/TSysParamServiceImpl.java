package com.mayday9.splatoonbot.business.service.impl;

import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSysParamDao;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2025/2/25 16:38
 **/
@Slf4j
@Service
public class TSysParamServiceImpl implements TSysParamService {

    @Resource
    private TSysParamDao tSysParamDao;

    @Override
    @Cacheable(cacheNames = "tSysParam", key = "#paramCode")
    public TSysParam findByCode(String paramCode) {
        return tSysParamDao.findOneBy("paramCode", paramCode);
    }

    @PostConstruct
    public void clearCacheOnStartup() {
        flushCache();
        log.info("应用启动，清理参数表缓存");
    }

    @Override
    @CacheEvict(cacheNames = "tSysParam", allEntries = true)
    public void flushCache() {
        log.info("------------ 刷新参数表缓存 --------------");
    }

    @Override
    @CacheEvict(cacheNames = "tSysParam", allEntries = true)
    public boolean updateParamValue(String paramCode, String paramValue) {
        TSysParam tSysParam = findByCode(paramCode);
        if (tSysParam == null) {
            log.warn("未找到参数: {}", paramCode);
            return false;
        }
        tSysParam.setParamValue(paramValue);
        boolean result = tSysParamDao.edit(tSysParam);
        if (result) {
            log.info("更新参数 {} = {}", paramCode, paramValue);
        }
        return result;
    }
}
