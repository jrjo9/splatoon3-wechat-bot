package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.entity.TSysParam;

/**
 * @author Lianjiannan
 * @since 2025/2/25 16:38
 **/
public interface TSysParamService {

    /**
     * 根据参数查找
     *
     * @param paramCode 参数编码
     * @return TSysParam
     */
    TSysParam findByCode(String paramCode);


    /**
     * 更新缓存
     *
     */
    void flushCache();
}
