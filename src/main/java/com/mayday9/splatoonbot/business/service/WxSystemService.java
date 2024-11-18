package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.basic.WxGroupRegisterDTO;

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

}
