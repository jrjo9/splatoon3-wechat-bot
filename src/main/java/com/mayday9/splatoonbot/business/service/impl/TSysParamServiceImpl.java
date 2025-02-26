package com.mayday9.splatoonbot.business.service.impl;

import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSysParamDao;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Override
    @CacheEvict(cacheNames = "tSysParam", allEntries = true)
    public void flushCache() {
        log.info("------------ 刷新参数表缓存 --------------");
    }
}
