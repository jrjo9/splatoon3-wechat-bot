package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.basic.WxGroupRegisterDTO;
import com.mayday9.splatoonbot.common.enums.FlagEnum;

/**
 * @author Lianjiannan
 * @since 2024/10/10 10:00
 **/
public interface WxSystemService {

    /**
     * 注册激活群组
     *
     * @param wxGroupRegisterDTO DTO
     * @return Boolean
     */
    void registerWxGroup(WxGroupRegisterDTO wxGroupRegisterDTO);

    /**
     * 转换群组自动统计开关
     *
     * @param gid 群组ID
     * @return void
     */
    FlagEnum switchWxGroupAutoStatistics(String gid);

}
