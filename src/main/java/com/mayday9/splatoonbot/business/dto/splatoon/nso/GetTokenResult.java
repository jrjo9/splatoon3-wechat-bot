package com.mayday9.splatoonbot.business.dto.splatoon.nso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/11/7 14:10
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetTokenResult {

    private String webServiceToken;

    private String userNickName;

    private String userLang;

    private String userCountry;

    private String userInfo;

}
