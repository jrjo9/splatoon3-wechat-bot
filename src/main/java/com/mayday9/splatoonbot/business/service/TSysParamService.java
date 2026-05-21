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


    /**
     * 根据参数编码更新参数值
     *
     * @param paramCode  参数编码
     * @param paramValue 参数值
     * @return 是否更新成功
     */
    boolean updateParamValue(String paramCode, String paramValue);
}
