package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.basic.WxGroupRegisterDTO;
import com.mayday9.splatoonbot.business.dto.basic.WxUserSignInDTO;
import com.mayday9.splatoonbot.business.vo.WxUserSignInVO;

/**
 * 签到服务
 *
 * @author Lianjiannan
 * @since 2024/9/24 15:04
 **/

public interface SignInService {


    /**
     * 用户签到
     *
     * @param wxUserSignInDTO 签到DTO
     * @return WxUserSignInVO
     */
    WxUserSignInVO wxUserSignIn(WxUserSignInDTO wxUserSignInDTO);


}
