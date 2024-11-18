package com.mayday9.splatoonbot.business.controller;

import com.mayday9.splatoonbot.business.dto.basic.WxUserSignInDTO;
import com.mayday9.splatoonbot.business.service.SignInService;
import com.mayday9.splatoonbot.business.vo.WxUserSignInVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 签到
 *
 * @author Lianjiannan
 * @since 2024/9/23 18:38
 **/
@RequestMapping("/basic/sign-in")
@RestController
public class SignInController {

    @Resource
    private SignInService signInService;

    /**
     * 微信用户签到
     *
     * @param wxUserSignInDTO 微信用户签到DTO
     * @return WxUserSignInVO
     */
    @PostMapping("/wxUserSignIn")
    public WxUserSignInVO wxUserSignIn(@Validated @RequestBody WxUserSignInDTO wxUserSignInDTO) {
        return signInService.wxUserSignIn(wxUserSignInDTO);
    }

}
