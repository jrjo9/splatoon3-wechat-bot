package com.mayday9.splatoonbot.business.dto.splatoon.nso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/11/7 16:43
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GraphqlRequestParameter {

    private String wxid;

    private String sessionToken;

    private String gToken;

    private String bulletToken;

    private String userLang = "zh-CN";

    private String userCountry;


    public GraphqlRequestParameter(String gToken, String bulletToken, String userLang, String userCountry) {
        this.gToken = gToken;
        this.bulletToken = bulletToken;
        this.userLang = userLang;
        this.userCountry = userCountry;
    }
}
