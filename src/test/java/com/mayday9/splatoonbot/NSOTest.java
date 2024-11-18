package com.mayday9.splatoonbot;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.BattleDetailPlayerVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.BattleDetailVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailPlayerVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailWaveVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GetGTokenAndBulletTokenResult;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GetTokenResult;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GraphqlRequestParameter;
import com.mayday9.splatoonbot.common.util.OkHttpClientUtil;
import com.mayday9.splatoonbot.common.util.OkHttpUtil;
import com.mayday9.splatoonbot.common.util.core.DateUtil;
import com.mayday9.splatoonbot.common.util.core.StringUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lianjiannan
 * @since 2024/11/4 9:19
 **/
@Slf4j
public class NSOTest {

    /*
     * 参考资料：
     * https://github.com/imink-app/f-API
     * https://github.com/JoneWang/imink/wiki/imink-API-Documentation
     * https://github.com/samuelthomas2774/nxapi
     * https://raw.githubusercontent.com/JoneWang/imink/master/Resources/Login%20Flow.svg
     * https://github.com/paul-sama/splatoon3-bot
     *
     * */

    // see https://github.com/frozenpandaman/s3s/blob/master/iksm.py
    private static final String NSO_APP_VERSION = "2.10.1";

    // https://github.com/nintendoapis/nintendo-app-versions/blob/main/data/splatnet3-app.json
    private static final String WEB_VIEW_VERSION = "6.0.0-30a1464a";

    private static final String APP_USER_AGENT = "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36";

    private final Map<String, String> translateRidMap = new HashMap<>();

    {
//        NSO_APP_VERSION = this.getNSO_APP_VERSION();

        translateRidMap.put("HomeQuery", "51fc56bbf006caf37728914aa8bc0e2c86a80cf195b4d4027d6822a3623098a8");
        translateRidMap.put("LatestBattleHistoriesQuery", "b24d22fd6cb251c515c2b90044039698aa27bc1fab15801d83014d919cd45780");
        translateRidMap.put("RegularBattleHistoriesQuery", "2fe6ea7a2de1d6a888b7bd3dbeb6acc8e3246f055ca39b80c4531bbcd0727bba");
        translateRidMap.put("BankaraBattleHistoriesQuery", "9863ea4744730743268e2940396e21b891104ed40e2286789f05100b45a0b0fd");
        translateRidMap.put("PrivateBattleHistoriesQuery", "fef94f39b9eeac6b2fac4de43bc0442c16a9f2df95f4d367dd8a79d7c5ed5ce7");
        translateRidMap.put("XBattleHistoriesQuery", "eb5996a12705c2e94813a62e05c0dc419aad2811b8d49d53e5732290105559cb");
        translateRidMap.put("VsHistoryDetailQuery", "f893e1ddcfb8a4fd645fd75ced173f18b2750e5cfba41d2669b9814f6ceaec46");
        translateRidMap.put("CoopHistoryQuery", "0f8c33970a425683bb1bdecca50a0ca4fb3c3641c0b2a1237aedfde9c0cb2b8f");
        translateRidMap.put("CoopHistoryDetailQuery", "42262d241291d7324649e21413b29da88c0314387d8fdf5f6637a2d9d29954ae");
        translateRidMap.put("MyOutfitCommonDataEquipmentsQuery", "45a4c343d973864f7bb9e9efac404182be1d48cf2181619505e9b7cd3b56a6e8");
        translateRidMap.put("FriendsList", "ea1297e9bb8e52404f52d89ac821e1d73b726ceef2fd9cc8d6b38ab253428fb3");
        translateRidMap.put("HistorySummary", "0a62c0152f27c4218cf6c87523377521c2cff76a4ef0373f2da3300079bf0388");
        translateRidMap.put("TotalQuery", "2a9302bdd09a13f8b344642d4ed483b9464f20889ac17401e993dfa5c2bb3607");
        translateRidMap.put("XRankingQuery", "a5331ed228dbf2e904168efe166964e2be2b00460c578eee49fc0bc58b4b899c");
        translateRidMap.put("ScheduleQuery", "9b6b90568f990b2a14f04c25dd6eb53b35cc12ac815db85ececfccee64215edd");
        translateRidMap.put("StageRecordsQuery", "c8b31c491355b4d889306a22bd9003ac68f8ce31b2d5345017cdd30a2c8056f3");
        translateRidMap.put("EventBattleHistoriesQuery", "e47f9aac5599f75c842335ef0ab8f4c640e8bf2afe588a3b1d4b480ee79198ac");
        translateRidMap.put("EventListQuery", "875a827a6e460c3cd6b1921e6a0872d8b95a1fce6d52af79df67734c5cc8b527");
        translateRidMap.put("EventBoardQuery", "ad4097d5fb900b01f12dffcb02228ef6c20ddbfba41f0158bb91e845335c708e");

    }

    @Test
    public void testGetAuthUrl() throws NoSuchAlgorithmException {
        // Visit authorization link in browser
        SecureRandom random = new SecureRandom();

        byte[] bytes = new byte[36];
        random.nextBytes(bytes);
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String auth_state = base64Encoder.encodeToString(bytes);
        log.info("auth_state=" + auth_state);

        byte[] bytes2 = new byte[32];
        random.nextBytes(bytes2);
        String auth_code_verifier = base64Encoder.encodeToString(bytes2).replaceAll("=", "");
        log.info("auth_code_verifier=" + auth_code_verifier);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(auth_code_verifier.getBytes(StandardCharsets.UTF_8));
        String auth_code_challenge = base64Encoder.encodeToString(hash).replaceAll("=", "");
        log.info("auth_code_challenge=" + auth_code_challenge);

        String authUrl = "https://accounts.nintendo.com/connect/1.0.0/authorize?state=" + auth_state +
            "&redirect_uri=npf71b963c1b7b6d119://auth" +
            "&client_id=71b963c1b7b6d119" +
            "&scope=openid%20user%20user.birthday%20user.mii%20user.screenName" +
            "&response_type=session_token_code" +
            "&session_token_code_challenge=" + auth_code_challenge +
            "&session_token_code_challenge_method=S256" +
            "&theme=login_form";


        log.info("authUrl=" + authUrl);
    }


    @Test
    public void testLogin() {
        String text = "npf71b963c1b7b6d119://auth#session_token_code=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2MDg1YzI3ODRkNDk4OTI5IiwiZXhwIjoxNzMxMDMyNjE0LCJpYXQiOjE3MzEwMzIwMTQsInR5cCI6InNlc3Npb25fdG9rZW5fY29kZSIsInN0YzpjIjoiZWR4RGVpTnBSbnJvbWwxU0loTlp3RDJTb1h3amd4eHcwNExPMzZleXZ3byIsImp0aSI6IjEwNjY1OTU1ODM3NCIsImlzcyI6Imh0dHBzOi8vYWNjb3VudHMubmludGVuZG8uY29tIiwiYXVkIjoiNzFiOTYzYzFiN2I2ZDExOSIsInN0YzpzY3AiOlswLDgsOSwxNywyM10sInN0YzptIjoiUzI1NiJ9.B9IRbyBzUofXuzFtg6g1qqWoWENHD9cM6iKKldR3u8s&state=3HmLnscjndLfxcWH9EhVvssPe47NQW4tZjeGyLiCtUex7YNt&session_state=972e5d2dfaab80fe0c69db83b888eaef962ed392391536a054c7fc580097dbf8";
        String session_token_code = text.substring(text.indexOf("session_token_code=") + 19);
        session_token_code = session_token_code.substring(0, session_token_code.indexOf("&"));
        log.info("session_token_code=" + session_token_code);
        String url = "https://accounts.nintendo.com/connect/1.0.0/api/session_token";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", "71b963c1b7b6d119");
        paramMap.put("session_token_code", session_token_code);
        // auth_code_verifier
        paramMap.put("session_token_code_verifier", "UiyEhEx7Q0oW66EspMQJ8SO5hzxOhWFhbvj9wRpB3cI");
        HttpResponse response = HttpUtil.createPost(url)
            .header("User-Agent", "OnlineLounge/" + NSO_APP_VERSION + " NASDKAPI Android")
            .header("Accept-Language", "en-US")
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Content-Length", "540")
            .header("Host", "accounts.nintendo.com")
            .header("Connection", "Keep-Alive")
            .header("Accept-Encoding", "gzip")
            .charset(StandardCharsets.UTF_8)
            .timeout(60000)
            .form(paramMap)
            .execute();

        if (!response.isOk()) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "sessionTokenCode失效，请重新打开页面复制code");
        }
        String responseString = response.body();
//        log.info("response:" + responseString);
        JSONObject jsonObject = JSONUtil.parseObj(responseString);
        String sessionToken = jsonObject.getStr("session_token");
        log.info("sessionToken:" + sessionToken);
        this.getGTokenAndBulletToken(sessionToken);
    }

    public GetGTokenAndBulletTokenResult getGTokenAndBulletToken(String sessionToken) {
        // 获取gToken和BulletToken
        GetTokenResult getTokenResult;
        // 获取token
        try {
            getTokenResult = this.getToken(sessionToken);
        } catch (Exception e) {
            log.error("获取token失败...", e);
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登录过期，请重新登录");
        }
        // 获取bulletToken
        String bulletToken;
        try {
            bulletToken = this.getBulletToken(getTokenResult.getWebServiceToken(), getTokenResult.getUserLang(), getTokenResult.getUserCountry());
        } catch (Exception e) {
            log.error("获取获取bulletToken失败...", e);
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登录过期，请重新登录");
        }
        return new GetGTokenAndBulletTokenResult(getTokenResult.getWebServiceToken(), bulletToken, getTokenResult.getUserNickName(), getTokenResult.getUserLang(), getTokenResult.getUserCountry());
    }

    public GetTokenResult getToken(String sessionToken) {
        // 获取服务access token
        String getTokenUrl = "https://accounts.nintendo.com/connect/1.0.0/api/token";
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("client_id", "71b963c1b7b6d119");
        bodyMap.put("session_token", sessionToken);
        bodyMap.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer-session-token");
        String getTokenResp = OkHttpUtil.builder()
            .url(getTokenUrl)
            .addParamMap(bodyMap)
            .addHeader("Host", "accounts.nintendo.com")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("Content-Type", "application/json")
            .addHeader("Content-Length", "436")
            .addHeader("Accept", "application/json")
            .addHeader("Connection", "Keep-Alive")
            .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.1.2)")
            .post(true)
            .sync();
//        log.info("get service token response：" + getTokenResp);
        JSONObject getTokenRespJSONObject = JSONUtil.parseObj(getTokenResp);
        String accessToken = getTokenRespJSONObject.getStr("access_token");
        String idToken = getTokenRespJSONObject.getStr("id_token");

        //  根据access token获取用户信息
        String getMeUrl = "https://api.accounts.nintendo.com/2.0.0/users/me";
        String getMeResp = OkHttpUtil.builder()
            .url(getMeUrl)
            .addParamMap(bodyMap)
            .addHeader("User-Agent", "NASDKAPI; Android")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + accessToken)
            .addHeader("Host", "api.accounts.nintendo.com")
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Accept-Encoding", "gzip")
            .get()
            .sync();

//        log.info("get user info: " + getMeResp);
        JSONObject userInfo = JSONUtil.parseObj(getMeResp);
        String userNickName = userInfo.getStr("nickname");
        String userLang = userInfo.getStr("language");
        String userCountry = userInfo.getStr("country");
        String userId = userInfo.getStr("id");
        String birthday = userInfo.getStr("birthday");

        // # 通过imink生成f
        String fGenUrl = "https://api.imink.app/f";
        String iminkStepOneResp = callIminkApi(idToken, 1, fGenUrl, userId, null);
//        log.info("imink f api step 1 response:" + iminkStepOneResp);
        JSONObject iminkResponseJSONObject = JSONUtil.parseObj(iminkStepOneResp);

        // Login to account
        Map<String, Object> accountLoginBody = new HashMap<>();
        Map<String, Object> accountLoginBodyParam = new HashMap<>();
        accountLoginBodyParam.put("f", iminkResponseJSONObject.getStr("f"));
        accountLoginBodyParam.put("language", userLang);
        accountLoginBodyParam.put("naBirthday", birthday);
        accountLoginBodyParam.put("naCountry", userCountry);
        accountLoginBodyParam.put("naIdToken", idToken);
        accountLoginBodyParam.put("requestId", iminkResponseJSONObject.getStr("request_id"));
        accountLoginBodyParam.put("timestamp", Long.valueOf(iminkResponseJSONObject.getStr("timestamp").substring(0, 10)));
        accountLoginBody.put("parameter", accountLoginBodyParam);
        String accountLoginUrl = "https://api-lp1.znc.srv.nintendo.net/v3/Account/Login";
        String accountLoginResponseStr = OkHttpUtil.builder()
            .url(accountLoginUrl)
            .addHeader("X-Platform", "Android")
            .addHeader("X-ProductVersion", NSO_APP_VERSION)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Content-Length", String.valueOf(990 + iminkResponseJSONObject.getStr("f").length()))
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("User-Agent", "com.nintendo.znca/" + NSO_APP_VERSION + "(Android/7.1.2)")
            .addParamMap(accountLoginBody)
            .post(true)
            .sync();
//        log.info("accountLoginResponseStr:" + accountLoginResponseStr);
        JSONObject accountLoginResponseJSONObject = JSONUtil.parseObj(accountLoginResponseStr);


        // 状态码9403~9599重试一次接口
        Integer status = accountLoginResponseJSONObject.getInt("status");
        if (status >= 9403 && status <= 9599) {
            String iminkStepOneRespOnce = callIminkApi(idToken, 1, fGenUrl, userId, null);
            JSONObject iminkResponseJSONObjectOnce = JSONUtil.parseObj(iminkStepOneRespOnce);
            accountLoginBodyParam.put("f", iminkResponseJSONObjectOnce.getStr("f"));
            accountLoginBodyParam.put("requestId", iminkResponseJSONObjectOnce.getStr("request_id"));
            accountLoginBodyParam.put("timestamp", Long.valueOf(iminkResponseJSONObjectOnce.getStr("timestamp").substring(0, 10)));
            accountLoginBody.put("parameter", accountLoginBodyParam);
            String accountLoginResponseStrOnce = OkHttpUtil.builder()
                .url(accountLoginUrl)
                .addHeader("X-Platform", "Android")
                .addHeader("X-ProductVersion", NSO_APP_VERSION)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Content-Length", String.valueOf(990 + iminkResponseJSONObjectOnce.getStr("f").length()))
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("User-Agent", "com.nintendo.znca/" + NSO_APP_VERSION + "(Android/7.1.2)")
                .addParamMap(accountLoginBody)
                .post(true)
                .sync();
//            log.info("accountLoginResponseStrOnce:" + accountLoginResponseStrOnce);
            accountLoginResponseJSONObject = JSONUtil.parseObj(accountLoginResponseStrOnce);
            Integer statusOnce = accountLoginResponseJSONObject.getInt("status");
            if (statusOnce != 0) {
                String errorMessage = accountLoginResponseJSONObject.getStr("errorMessage");
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "Error from Nintendo (in Account/Login step):" + errorMessage);
            }
        }
        idToken = accountLoginResponseJSONObject.getJSONObject("result").getJSONObject("webApiServerCredential").getStr("accessToken");
        Long coralUserId = accountLoginResponseJSONObject.getJSONObject("result").getJSONObject("user").getLong("id");


        // 获取web service token
        String iminkStepTwoResp = callIminkApi(idToken, 2, fGenUrl, userId, coralUserId);
//        log.info("imink f api step 2 response:" + iminkStepTwoResp);
        JSONObject iminkStepTwoRespJSONObject = JSONUtil.parseObj(iminkStepTwoResp);

        String webServiceTokenUrl = "https://api-lp1.znc.srv.nintendo.net/v2/Game/GetWebServiceToken";

        Map<String, Object> webServiceTokenBody = new HashMap<>();
        Map<String, Object> webServiceTokenBodyParam = new HashMap<>();
        webServiceTokenBodyParam.put("f", iminkStepTwoRespJSONObject.getStr("f"));
        webServiceTokenBodyParam.put("id", 4834290508791808L);
        webServiceTokenBodyParam.put("registrationToken", idToken);
        webServiceTokenBodyParam.put("requestId", iminkStepTwoRespJSONObject.getStr("request_id"));
        webServiceTokenBodyParam.put("timestamp", Long.valueOf(iminkStepTwoRespJSONObject.getStr("timestamp").substring(0, 10)));
        webServiceTokenBody.put("parameter", webServiceTokenBodyParam);
        String webServiceTokenResp = OkHttpUtil.builder()
            .url(webServiceTokenUrl)
            .addHeader("X-Platform", "Android")
            .addHeader("X-ProductVersion", NSO_APP_VERSION)
            .addHeader("Authorization", "Bearer " + idToken)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Content-Length", "391")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("User-Agent", "com.nintendo.znca/" + NSO_APP_VERSION + "(Android/7.1.2)")
            .addParamMap(webServiceTokenBody)
            .post(true)
            .sync();
//        log.info("webServiceTokenResp:" + webServiceTokenResp);
        JSONObject webServiceTokenRespJSONObject = JSONUtil.parseObj(webServiceTokenResp);
        Integer webServiceStatus = webServiceTokenRespJSONObject.getInt("status");
        if (webServiceStatus >= 9403 && webServiceStatus <= 9599) {
            String iminkStepTwoRespOnce = callIminkApi(idToken, 2, fGenUrl, userId, coralUserId);
//            log.info("imink f api step 2 response Once:" + iminkStepTwoRespOnce);
            JSONObject iminkStepTwoRespJSONObjectOnce = JSONUtil.parseObj(iminkStepTwoRespOnce);
            webServiceTokenBodyParam.put("f", iminkStepTwoRespJSONObjectOnce.getStr("f"));
            webServiceTokenBodyParam.put("requestId", iminkStepTwoRespJSONObjectOnce.getStr("request_id"));
            webServiceTokenBodyParam.put("timestamp", Long.valueOf(iminkStepTwoRespJSONObjectOnce.getStr("timestamp").substring(0, 10)));
            webServiceTokenBody.put("parameter", webServiceTokenBodyParam);
            String webServiceTokenRespOnce = OkHttpUtil.builder()
                .url(webServiceTokenUrl)
                .addHeader("X-Platform", "Android")
                .addHeader("X-ProductVersion", NSO_APP_VERSION)
                .addHeader("Authorization", "Bearer " + idToken)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Content-Length", "391")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("User-Agent", "com.nintendo.znca/" + NSO_APP_VERSION + "(Android/7.1.2)")
                .addParamMap(webServiceTokenBody)
                .post(true)
                .sync();
//            log.info("webServiceTokenRespOnce:" + webServiceTokenRespOnce);
            webServiceTokenRespJSONObject = JSONUtil.parseObj(webServiceTokenRespOnce);
            Integer statusOnce = webServiceTokenRespJSONObject.getInt("status");
            if (statusOnce != 0) {
                String errorMessage = webServiceTokenRespJSONObject.getStr("errorMessage");
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "Error from Nintendo (in Game/GetWebServiceToken step):" + errorMessage);
            }
        }
        String webServiceToken = webServiceTokenRespJSONObject.getJSONObject("result").getStr("accessToken");
        log.info("web_service_token:" + webServiceToken);
        log.info("user_nickname:" + userNickName);
        log.info("user_lang:" + userLang);
        log.info("user_country:" + userCountry);
        log.info("user_info:" + userInfo);

        return new GetTokenResult(webServiceToken, userNickName, userLang, userCountry, getMeResp);
    }

    private String getNSO_APP_VERSION() {
        String url = "https://api.imink.app/config";
        String response = OkHttpClientUtil.get(url);
        JSONObject responseJsonObject = JSONUtil.parseObj(response);
        return responseJsonObject.getStr("nso_version");
    }

    private String callIminkApi(String idToken, Integer step, String fGenUrl, String useId, Long coralUserId) {
        Map<String, Object> params = new HashMap<>();
        params.put("token", idToken);
        params.put("hash_method", step);
        params.put("na_id", useId);
        if (!StringUtil.isEmpty(coralUserId)) {
            params.put("coral_user_id", coralUserId);
        }
        return OkHttpUtil.builder()
            .url(fGenUrl)
            .addHeader("User-Agent", "splatoon3-wechat-bot/1.0.0")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("X-znca-Platform", "Android")
            .addHeader("X-znca-Version", NSO_APP_VERSION)
            .addParamMap(params)
            .post(true)
            .sync();
    }


    public String getBulletToken(String webServiceToken, String userLang, String userCountry) {
        String apiUrl = "https://api.lp1.av5ja.srv.nintendo.net";
        HttpResponse response = HttpUtil.createPost(apiUrl + "/api/bullet_tokens")
            .header("Content-Length", "0")
            .header("Content-Type", "application/json")
            .header("Accept-Language", userLang)
            .header("User-Agent", APP_USER_AGENT)
            .header("X-Web-View-Ver", WEB_VIEW_VERSION)
            .header("X-NACOUNTRY", userCountry)
            .header("Accept", "*/*")
            .header("Origin", apiUrl)
            .header("X-Requested-With", "com.nintendo.znca")
            .cookie("_gtoken=" + webServiceToken)
            .timeout(60000)
            .execute();
        log.info(response.body());
        String resp = response.body();

        JSONObject jsonObject = JSONUtil.parseObj(resp);
        String bulletToken = jsonObject.getStr("bulletToken");
        log.info("bullet token : " + bulletToken);
        return bulletToken;
    }


    @Test
    public void getMeInfo() {
        // get_summary
        String gToken = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldC92MS9XZWJTZXJ2aWNlL0NlcnRpZmljYXRlL0xpc3QiLCJraWQiOiJjcjZOaWFXTEd2SWtjcjZuaUU0NzZHcVFlQzQiLCJ0eXAiOiJKV1QifQ.eyJpc0NoaWxkUmVzdHJpY3RlZCI6ZmFsc2UsImF1ZCI6IjY2MzM2NzcyOTE1NTI3NjgiLCJleHAiOjE3MzA5NzgwMTUsImlhdCI6MTczMDk2NzIxNSwiaXNzIjoiYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldCIsImp0aSI6IjFhMGUxOTQ3LTM2ZjgtNDgwMC1iMTBiLTQzMmZhZjE0NDU2OCIsInN1YiI6NTUwNjE4OTMwMDU5NjczNiwibGlua3MiOnsibmV0d29ya1NlcnZpY2VBY2NvdW50Ijp7ImlkIjoiNzdhMWEyZTI1ZGRmOGFjYSJ9fSwidHlwIjoiaWRfdG9rZW4iLCJtZW1iZXJzaGlwIjp7ImFjdGl2ZSI6dHJ1ZX19.MgsUvyoWJ_VFBGr1KT0Q186KA5pwL2ymo63inw_ci6y-KqMKwdup2y845ZpKa3_kNE9iYlgEQVjbpyPt_3fH92W3M-vIMr3JX1nPEkEHMe3Uyu7Q0DZ5UgK3hjXrca282AgHCt6HXP2-qSxvhm7dWS4HzzfJwMNo90MHvLa6dJg3v0uLf53lLD5P7QWvHOpfTjQeM0qpSHmrWIVWXxWUIlJaTJbPATfZKjW7JKFhBTMsfrbJehSmoE8Nz8Hl7dG-QEQeYbY3foivr4_QZ0QUq6ujJV-g2EDLT62fCr9-RxBJy3kR0_WOz8N1TA2hMPES2_nTKKtQIxueG0GuVin0DQ";
        String bulletToken = "9cxEPZPKJUYdpevhjWcKmuD7wDQSUIZqeMIWVvUIXPQxCA8CLf3FYnmqUxPQ-dhqUbn5KfJMxjD_B636SRDsx0YMVBgucMQxDtUDKHcMwOMG8fYy191meRdC-zk=";
        String userLang = "zh-CN";
        String userCountry = "JP";

        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(gToken, bulletToken, userLang, userCountry);
        // 总览
        JSONObject summary = this.getSummary(graphqlRequestParameter);
        // 获取所有资源
        JSONObject allRes = this.getAllRes(graphqlRequestParameter);
        // 打工
        JSONObject coopSummary = this.getCoopSummary(graphqlRequestParameter);
        // 组装返回消息
        // 昵称：ホホオヒ™HOSEE
        //头衔：差點就要變成小鮭魚
        //编号：#2076
        //等级：59
        //技术：B+
        //最高技术：A
        //总胜利数：1353
        //至今为止的涂墨面积：2244642p
        //---------------------------
        //从2022/9/8开始游玩
        //---------------------------
        //最常用的武器：
        //消防栓旋轉槍 改裝
        //秩序射擊槍 複製
        //楓葉射擊槍
        //=======鲑鱼跑======
        //段位：达人+2 80
        //鳞片：[金：12，银：12，铜：983]
        //打工次数：1331
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
            .append("昵称：").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("name")).append("\n")
            .append("头衔：").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("byname")).append("\n")
            .append("编号：#").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("nameId")).append("\n")
            .append("等级：").append(summary.getJSONObject("data").getJSONObject("playHistory").getStr("rank")).append("\n")
            .append("技术：").append(summary.getJSONObject("data").getJSONObject("playHistory").getStr("udemae")).append("\n")
            .append("最高技术：").append(summary.getJSONObject("data").getJSONObject("playHistory").getStr("udemaeMax")).append("\n")
            .append("总胜利数：").append(summary.getJSONObject("data").getJSONObject("playHistory").getStr("winCountTotal")).append("\n")
            .append("至今为止的涂墨面积：").append(summary.getJSONObject("data").getJSONObject("playHistory").getStr("paintPointTotal")).append("p\n")
            .append("---------------------------").append("\n")
            .append("从").append(DateUtil.format(summary.getJSONObject("data").getJSONObject("playHistory").getDate("gameStartTime"), DateUtil.PATTERN_DATE)).append("开始游玩").append("\n")
            .append("---------------------------").append("\n")
            .append("最常用的武器：\n");
        JSONArray jsonArray = summary.getJSONObject("data").getJSONObject("playHistory").getJSONArray("frequentlyUsedWeapons");
        for (int i = 0; i < jsonArray.size(); i++) {
            sb.append(summary.getJSONObject("data").getJSONObject("playHistory").getJSONArray("frequentlyUsedWeapons").getJSONObject(i).getStr("name")).append("\n");
        }
        sb.append("=======鲑鱼跑======").append("\n")
            .append("段位：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("regularGrade").getStr("name")).append("\n")
            .append("鳞片：[")
            .append("\uD83E\uDD47：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("gold"))
            .append("，\uD83E\uDD48：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("silver"))
            .append("，\uD83E\uDD49：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("bronze"))
            .append("]\n")
            .append("打工次数：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("pointCard").getInt("playCount")).append("\n");
        log.info(sb.toString());
    }

    @Test
    public void testGetLastCoop() {
        String gToken = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldC92MS9XZWJTZXJ2aWNlL0NlcnRpZmljYXRlL0xpc3QiLCJraWQiOiJVZUlxUXBfdGRVZzlFMUZ5dm9LSGhhb1I3NHMiLCJ0eXAiOiJKV1QifQ.eyJpc0NoaWxkUmVzdHJpY3RlZCI6ZmFsc2UsImF1ZCI6IjY2MzM2NzcyOTE1NTI3NjgiLCJleHAiOjE3MzEwNjM0NjEsImlhdCI6MTczMTA1MjY2MSwiaXNzIjoiYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldCIsImp0aSI6ImM3ZDM5MGFhLTljMzctNDM0Ny05ODYzLWFhMmRmMTI1NTExYiIsInN1YiI6NTUwNjE4OTMwMDU5NjczNiwibGlua3MiOnsibmV0d29ya1NlcnZpY2VBY2NvdW50Ijp7ImlkIjoiNzdhMWEyZTI1ZGRmOGFjYSJ9fSwidHlwIjoiaWRfdG9rZW4iLCJtZW1iZXJzaGlwIjp7ImFjdGl2ZSI6dHJ1ZX19.SjXhea5FsTyptGUS1z14_4I2QOng1C0L94l2AMy41c4MK3teDACH5X7rDq8RnC_IognqhAdm3wc2zI3URL4F6JyJdpXMGvjYNWpMwDE0P-RRITZz38LNLx8w1ubpH1ogz1lW4IxhkVZVC6FZPpp2xRqxvhQiDuy2ux6d4Mf9Q45h-NaE0mTqmGbPTd6h2NtLFZHT7Fp3DMgsCbQdpe9Vl2xws18u5Ybh99I-Wx-grWjwalvbukunzou8P3sMLHhlOMhN8LZ6tlngyCaEpbDG6MLnuus1dUFEfEQxauKDHUU0VmDvetUwYg12OdVbwhYKKQhbQ8ID4W1CmiysfWm28A";
        String bulletToken = "PLr5k9phS-vITQiLtUiF6CgATNx6u8GNJutKI8lfNAUihnIi7HLlWoUzTlqgI56A1Uf4S8P83PF4jU1vEfMKZud2QO_EjBprH5wxCDqOnq7JPHCajtgcFuCsbq0=";
        String userLang = "zh-CN";
        String userCountry = "JP";

        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(gToken, bulletToken, userLang, userCountry);
        // 打工
        JSONObject coopSummary = this.getCoops(graphqlRequestParameter);
        JSONArray nodes = coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("historyGroups").getJSONArray("nodes");
        if (nodes.isEmpty()) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "查询不到打工记录！");
        }
        String firstCoopId = nodes.getJSONObject(0).getJSONObject("historyDetails").getJSONArray("nodes").getJSONObject(0).getStr("id");
        log.debug("firstCoopId:{}", firstCoopId);
        JSONObject coopDetail = this.getCoopDetail(graphqlRequestParameter, firstCoopId);
        // 麦年海洋发电所（危险度：149%）
        // ---------------------------------
        //WAVE-1 GJ! 33/19
        //WAVE-2 GJ! 42/20
        //WAVE-3 GJ! 34/22
        //EX-WAVE GJ!
        //---------------------------------
        //ホホオヒ™HOSEE
        //巨大鲑鱼：x14
        //搬蛋数量：x33
        //红蛋数量：x1581
        //救鱿数量：x1
        //游泳圈数：x0
        //
        //<:εΞ*★
        //巨大鲑鱼：x7
        //搬蛋数量：x19
        //红蛋数量：x1299
        //救鱿数量：x2
        //游泳圈数：x1
        //
        //<:3=
        //巨大鲑鱼：x0
        //搬蛋数量：x38
        //红蛋数量：x585
        //救鱿数量：x0
        //游泳圈数：x0
        //
        //キT　エ　イш　ノ、
        //巨大鲑鱼：x14
        //搬蛋数量：x29
        //红蛋数量：x1193
        //救鱿数量：x1
        //游泳圈数：x3
        //
        //---------------------------------
        //Rank
        //1. ホホオヒ™HOSEE 56.905
        //2. キT　エ　イш　ノ、 46.465
        //3. <:3= 42.925
        //4. <:εΞ*★ 35.995000000000005
        CoopDetailVO coopDetailVO = new CoopDetailVO();
        coopDetailVO.setStageName(coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getJSONObject("coopStage").getStr("name"));
        coopDetailVO.setDangerRate(coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getDouble("dangerRate"));
        coopDetailVO.setResultWave(coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getInt("resultWave"));
        List<CoopDetailWaveVO> waveVOList = new ArrayList<>();
        JSONArray waveResults = coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getJSONArray("waveResults");
        for (int i = 0; i < waveResults.size(); i++) {
            JSONObject waveResult = waveResults.getJSONObject(i);
            CoopDetailWaveVO waveVO = new CoopDetailWaveVO();
            waveVO.setWaveNumber(waveResult.getInt("waveNumber"));
            waveVO.setTeamDeliverCount(waveResult.getInt("teamDeliverCount"));
            waveVO.setDeliverNorm(waveResult.getInt("deliverNorm"));
            waveVOList.add(waveVO);
        }
        coopDetailVO.setWaveList(waveVOList);
        List<CoopDetailPlayerVO> playerVOList = new ArrayList<>();
        CoopDetailPlayerVO myPlayerVO = new CoopDetailPlayerVO();
        JSONObject myResult = coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getJSONObject("myResult");
        myPlayerVO.setPlayerName(myResult.getJSONObject("player").getStr("name"));
        myPlayerVO.setDefeatEnemyCount(myResult.getInt("defeatEnemyCount"));
        myPlayerVO.setDeliverCount(myResult.getInt("deliverCount"));
        myPlayerVO.setGoldenDeliverCount(myResult.getInt("goldenDeliverCount"));
        myPlayerVO.setGoldenAssistCount(myResult.getInt("goldenAssistCount"));
        myPlayerVO.setRescueCount(myResult.getInt("rescueCount"));
        myPlayerVO.setRescuedCount(myResult.getInt("rescuedCount"));
        playerVOList.add(myPlayerVO);
        JSONArray memberResults = coopDetail.getJSONObject("data").getJSONObject("coopHistoryDetail").getJSONArray("memberResults");
        for (int i = 0; i < memberResults.size(); i++) {
            JSONObject memberResult = memberResults.getJSONObject(i);
            CoopDetailPlayerVO playerVO = new CoopDetailPlayerVO();
            playerVO.setPlayerName(memberResult.getJSONObject("player").getStr("name"));
            playerVO.setDefeatEnemyCount(memberResult.getInt("defeatEnemyCount"));
            playerVO.setDeliverCount(memberResult.getInt("deliverCount"));
            playerVO.setGoldenDeliverCount(memberResult.getInt("goldenDeliverCount"));
            playerVO.setGoldenAssistCount(memberResult.getInt("goldenAssistCount"));
            playerVO.setRescueCount(memberResult.getInt("rescueCount"));
            playerVO.setRescuedCount(memberResult.getInt("rescuedCount"));
            playerVOList.add(playerVO);
        }
        coopDetailVO.setPlayerList(playerVOList);
        // 计算成绩
        coopDetailVO.generateRankScore();
        StringBuilder sb = new StringBuilder();
        sb.append(coopDetailVO.getStageName()).append("（危险度：").append((int) (coopDetailVO.getDangerRate() * 100)).append("%）").append("\n")
            .append("---------------------------------\n");
        for (int i = 0; i < coopDetailVO.getWaveList().size(); i++) {
            CoopDetailWaveVO waveVO = coopDetailVO.getWaveList().get(i);
            if (i < 3) {
                String waveResult = waveVO.getTeamDeliverCount() >= waveVO.getDeliverNorm() ? "GJ!" : "NG";
                sb.append("WAVE-").append(waveVO.getWaveNumber()).append(" ").append(waveResult).append(" ").append(waveVO.getTeamDeliverCount()).append("/").append(waveVO.getDeliverNorm()).append("\n");
            } else {
                String waveResult = coopDetailVO.getResultWave() == 0 ? "GJ!" : "NG";
                sb.append("EX-WAVE").append(" ").append(waveResult).append("\n");
            }
        }
        sb.append("---------------------------------\n");
        for (CoopDetailPlayerVO playerVO : coopDetailVO.getPlayerList()) {
            sb.append(playerVO.getPlayerName()).append("\n")
                .append("巨大鲑鱼：x").append(playerVO.getDefeatEnemyCount()).append("\n")
                .append("搬蛋数量：x").append(playerVO.getGoldenDeliverCount()).append("(").append(playerVO.getGoldenAssistCount()).append(")\n")
                .append("红蛋数量：x").append(playerVO.getDeliverCount()).append("\n")
                .append("救鱿数量：x").append(playerVO.getRescueCount()).append("\n")
                .append("游泳圈数：x").append(playerVO.getRescuedCount()).append("\n");
            sb.append("\n");
        }
        sb.append("---------------------------------\nRank\n");
        coopDetailVO.getPlayerList().sort(Comparator.comparing(CoopDetailPlayerVO::getRankScore, Comparator.reverseOrder()));
        int no = 1;
        for (CoopDetailPlayerVO playerVO : coopDetailVO.getPlayerList()) {
            sb.append(no).append(". ").append(playerVO.getPlayerName()).append(" ").append(playerVO.getRankScore());
            if (no == 1) {
                sb.append("  本局MVP");
            }
            sb.append("\n");
            no++;
        }
        log.info(sb.toString());
    }


    private JSONObject getSummary(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("HistorySummary"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
        log.info("获取总览数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private JSONObject getAllRes(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("TotalQuery"));
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
        log.info("获取资源数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }


    private JSONObject getCoopSummary(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("CoopHistoryQuery"));
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
        log.debug("获取打工数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private JSONObject getCoops(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("CoopHistoryQuery"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
        log.debug("获取打工数据列表:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private JSONObject getCoopDetail(GraphqlRequestParameter graphqlRequestParameter, String coopDetailId) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("CoopHistoryDetailQuery"), "coopHistoryDetailId", coopDetailId);
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
        log.debug("获取打工数据详情:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private JSONObject getRecentBattles(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("LatestBattleHistoriesQuery"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
        log.debug("获取比赛列表数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private JSONObject getBattleDetail(GraphqlRequestParameter graphqlRequestParameter, String battleDetailId) {
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("VsHistoryDetailQuery"), "vsResultId", battleDetailId);
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
        log.debug("获取比赛详情数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    private Map<String, Object> genGraphqlBody(String sha256Hash) {
        return this.genGraphqlBody(sha256Hash, null, null);
    }

    private Map<String, Object> genGraphqlBody(String sha256Hash, String varName, String varValue) {
        // {
        //		"extensions": {
        //			"persistedQuery": {
        //				"sha256Hash": sha256hash,
        //				"version": 1
        //			}
        //		},
        //		"variables": {}
        //	}
        Map<String, Object> graphqlBody = new HashMap<>();
        Map<String, Object> persistedQuery = new HashMap<>();
        persistedQuery.put("sha256Hash", sha256Hash);
        persistedQuery.put("version", 1);
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("persistedQuery", persistedQuery);
        graphqlBody.put("extensions", extensions);

        Map<String, Object> variables = new HashMap<>();
        if (!StringUtil.isEmpty(varName) && !StringUtil.isEmpty(varValue)) {
            variables.put(varName, varValue);
        }
        graphqlBody.put("variables", variables);
        return graphqlBody;
    }

    private String requestGraphql(Map<String, Object> data, Boolean skipCheckToken, GraphqlRequestParameter graphqlRequestParameter) {
        String apiUrl = "https://api.lp1.av5ja.srv.nintendo.net";
        String graphqlUrl = "https://api.lp1.av5ja.srv.nintendo.net/api/graphql";
        if (!skipCheckToken) {
            // 请求首页，判断token是否过期，过期重新获取
            prefetchChecks(graphqlUrl, apiUrl, graphqlRequestParameter);
        }
        HttpResponse response = HttpUtil.createPost(graphqlUrl)
            .header("Authorization", "Bearer " + graphqlRequestParameter.getBulletToken())
            .header("Accept-Language", graphqlRequestParameter.getUserLang())
            .header("User-Agent", APP_USER_AGENT)
            .header("X-Web-View-Ver", WEB_VIEW_VERSION)
            .header("Content-Type", "application/json")
            .header("Accept", "'*/*")
            .header("Origin", apiUrl)
            .header("X-Requested-With", "com.nintendo.znca")
            .header("Referer", apiUrl + "/?lang=" + graphqlRequestParameter.getUserLang() + "&na_country=" + graphqlRequestParameter.getUserCountry() + "&na_lang=" + graphqlRequestParameter.getUserLang())
            .header("Accept-Encoding", "gzip, deflate")
            .cookie("_gtoken=" + graphqlRequestParameter.getGToken())
            .body(JSONUtil.toJsonStr(data))
            .timeout(20000)
            .execute();
        if (response.getStatus() != 200) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "tokens expired.");
        }
        return response.body();
    }

    private GraphqlRequestParameter prefetchChecks(String graphqlUrl, String apiUrl, GraphqlRequestParameter graphqlRequestParameter) {
        // Queries the SplatNet 3 homepage to check if our gtoken & bulletToken are still valid and regenerates them if not.
        Map<String, Object> data = this.genGraphqlBody(this.translateRidMap.get("HomeQuery"), "naCountry", graphqlRequestParameter.getUserCountry());
        HttpResponse response = HttpUtil.createPost(graphqlUrl)
            .header("Authorization", "Bearer " + graphqlRequestParameter.getBulletToken())
            .header("Accept-Language", graphqlRequestParameter.getUserLang())
            .header("User-Agent", APP_USER_AGENT)
            .header("X-Web-View-Ver", WEB_VIEW_VERSION)
            .header("Content-Type", "application/json")
            .header("Accept", "'*/*")
            .header("Origin", apiUrl)
            .header("X-Requested-With", "com.nintendo.znca")
            .header("Referer", apiUrl + "/?lang=" + graphqlRequestParameter.getUserLang() + "&na_country=" + graphqlRequestParameter.getUserCountry() + "&na_lang=" + graphqlRequestParameter.getUserLang())
            .header("Accept-Encoding", "gzip, deflate")
            .cookie("_gtoken=" + graphqlRequestParameter.getGToken())
            .body(JSONUtil.toJsonStr(data))
            .timeout(20000)
            .execute();
        log.info(response.body());
        if (response.getStatus() != 200) {
            log.info("tokens expired.");
            // 重新获取token
            String sessionToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOjE2MjEyNDI2NjcxLCJpc3MiOiJodHRwczovL2FjY291bnRzLm5pbnRlbmRvLmNvbSIsInR5cCI6InNlc3Npb25fdG9rZW4iLCJzdWIiOiI2MDg1YzI3ODRkNDk4OTI5Iiwic3Q6c2NwIjpbMCw4LDksMTcsMjNdLCJpYXQiOjE3MzEwMzIwNDMsImF1ZCI6IjcxYjk2M2MxYjdiNmQxMTkiLCJleHAiOjE3OTQxMDQwNDN9.SQBBp6h9jJgyeUmqnuzYX3VvnUVqQhlYVo9glnWAKe4";
            GetGTokenAndBulletTokenResult getGTokenAndBulletTokenResult = this.getGTokenAndBulletToken(sessionToken);
            graphqlRequestParameter.setGToken(getGTokenAndBulletTokenResult.getGToken());
            graphqlRequestParameter.setBulletToken(getGTokenAndBulletTokenResult.getBulletToken());
        }
        return graphqlRequestParameter;
    }

    @Test
    public void testGetLastMatchInfo() {
        String gToken = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldC92MS9XZWJTZXJ2aWNlL0NlcnRpZmljYXRlL0xpc3QiLCJraWQiOiJVZUlxUXBfdGRVZzlFMUZ5dm9LSGhhb1I3NHMiLCJ0eXAiOiJKV1QifQ.eyJpc0NoaWxkUmVzdHJpY3RlZCI6ZmFsc2UsImF1ZCI6IjY2MzM2NzcyOTE1NTI3NjgiLCJleHAiOjE3MzEwNjM0NjEsImlhdCI6MTczMTA1MjY2MSwiaXNzIjoiYXBpLWxwMS56bmMuc3J2Lm5pbnRlbmRvLm5ldCIsImp0aSI6ImM3ZDM5MGFhLTljMzctNDM0Ny05ODYzLWFhMmRmMTI1NTExYiIsInN1YiI6NTUwNjE4OTMwMDU5NjczNiwibGlua3MiOnsibmV0d29ya1NlcnZpY2VBY2NvdW50Ijp7ImlkIjoiNzdhMWEyZTI1ZGRmOGFjYSJ9fSwidHlwIjoiaWRfdG9rZW4iLCJtZW1iZXJzaGlwIjp7ImFjdGl2ZSI6dHJ1ZX19.SjXhea5FsTyptGUS1z14_4I2QOng1C0L94l2AMy41c4MK3teDACH5X7rDq8RnC_IognqhAdm3wc2zI3URL4F6JyJdpXMGvjYNWpMwDE0P-RRITZz38LNLx8w1ubpH1ogz1lW4IxhkVZVC6FZPpp2xRqxvhQiDuy2ux6d4Mf9Q45h-NaE0mTqmGbPTd6h2NtLFZHT7Fp3DMgsCbQdpe9Vl2xws18u5Ybh99I-Wx-grWjwalvbukunzou8P3sMLHhlOMhN8LZ6tlngyCaEpbDG6MLnuus1dUFEfEQxauKDHUU0VmDvetUwYg12OdVbwhYKKQhbQ8ID4W1CmiysfWm28A";
        String bulletToken = "PLr5k9phS-vITQiLtUiF6CgATNx6u8GNJutKI8lfNAUihnIi7HLlWoUzTlqgI56A1Uf4S8P83PF4jU1vEfMKZud2QO_EjBprH5wxCDqOnq7JPHCajtgcFuCsbq0=";
        String userLang = "zh-CN";
        String userCountry = "JP";

        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(gToken, bulletToken, userLang, userCountry);
        // 比赛
        JSONObject recentBattles = this.getRecentBattles(graphqlRequestParameter);
        JSONArray nodes = recentBattles.getJSONObject("data").getJSONObject("latestBattleHistories").getJSONObject("historyGroupsOnlyFirst").getJSONArray("nodes");
        if (nodes.isEmpty()) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "查询不到比赛记录！");
        }
        String firstMatchId = nodes.getJSONObject(0).getJSONObject("historyDetails").getJSONArray("nodes").getJSONObject(0).getStr("id");
        log.debug("first match id is :{}", firstMatchId);
        JSONObject matchDetail = this.getBattleDetail(graphqlRequestParameter, "VnNIaXN0b3J5RGV0YWlsLXUtYXRwbHYzZjd0aHIzNW5tYWJubW06UkVDRU5UOjIwMjQxMDI3VDE0Mjg0Ml9iNTQwMmIwMC1mMjhjLTQ0MDctYTllZi1hMGQ2ZDc1ZmI1MGE=");
        BattleDetailVO battleDetailVO = new BattleDetailVO();
        battleDetailVO.setMode(matchDetail.getJSONObject("data").getJSONObject("vsHistoryDetail").getJSONObject("vsMode").getStr("mode"));
        battleDetailVO.setMatchName(matchDetail.getJSONObject("data").getJSONObject("vsHistoryDetail").getJSONObject("vsRule").getStr("name"));
        battleDetailVO.setJudgement(matchDetail.getJSONObject("data").getJSONObject("vsHistoryDetail").getStr("judgement"));
        List<BattleDetailPlayerVO> myTeamPlayerList = new ArrayList<>();
        JSONArray myTeamNodes = matchDetail.getJSONObject("data").getJSONObject("vsHistoryDetail").getJSONObject("myTeam").getJSONArray("players");
        for (int i = 0; i < myTeamNodes.size(); i++) {
            JSONObject player = myTeamNodes.getJSONObject(i);
            BattleDetailPlayerVO playerVO = new BattleDetailPlayerVO();
            playerVO.setPaint(player.getInt("paint"));
            playerVO.setKill(player.getJSONObject("result").getInt("kill"));
            playerVO.setDeath(player.getJSONObject("result").getInt("death"));
            playerVO.setAssist(player.getJSONObject("result").getInt("assist"));
            playerVO.setSpecial(player.getJSONObject("result").getInt("special"));
            playerVO.setPlayerName(player.getStr("name"));
            myTeamPlayerList.add(playerVO);
        }
        battleDetailVO.setMyTeamPlayerList(myTeamPlayerList);

        List<BattleDetailPlayerVO> enemyTeamPlayerList = new ArrayList<>();
        JSONArray otherTeams = matchDetail.getJSONObject("data").getJSONObject("vsHistoryDetail").getJSONArray("otherTeams");
        for (int i = 0; i < otherTeams.size(); i++) {
            JSONArray enemyTeamNodes = otherTeams.getJSONObject(i).getJSONArray("players");
            for (int j = 0; j < enemyTeamNodes.size(); j++) {
                JSONObject player = enemyTeamNodes.getJSONObject(j);
                BattleDetailPlayerVO playerVO = new BattleDetailPlayerVO();
                playerVO.setPaint(player.getInt("paint"));
                playerVO.setKill(player.getJSONObject("result").getInt("kill"));
                playerVO.setDeath(player.getJSONObject("result").getInt("death"));
                playerVO.setAssist(player.getJSONObject("result").getInt("assist"));
                playerVO.setSpecial(player.getJSONObject("result").getInt("special"));
                playerVO.setPlayerName(player.getStr("name"));
                enemyTeamPlayerList.add(playerVO);
            }
        }
        battleDetailVO.setEnemyTeamPlayerList(enemyTeamPlayerList);
        battleDetailVO.generateRankScore();
        StringBuilder sb = new StringBuilder();
        sb.append("==").append(battleDetailVO.getMatchName()).append("==\n")
            .append("==[  ").append(battleDetailVO.getJudgement()).append("  ]==\n")
            .append("----------------\n")
            .append("*我方队伍*\n");
        for (BattleDetailPlayerVO playerVO : battleDetailVO.getMyTeamPlayerList()) {
            sb.append(playerVO.getPlayerName()).append("    ")
                .append(playerVO.getKill()).append("(").append(playerVO.getAssist()).append(")/")
                .append(playerVO.getDeath()).append("/")
                .append(playerVO.getSpecial()).append("    ")
                .append(playerVO.getPaint())
                .append("p\n");
        }
        sb.append("\n*敌方队伍*\n");
        for (BattleDetailPlayerVO playerVO : battleDetailVO.getEnemyTeamPlayerList()) {
            sb.append(playerVO.getPlayerName()).append("    ")
                .append(playerVO.getKill()).append("(").append(playerVO.getAssist()).append(")/")
                .append(playerVO.getDeath()).append("/")
                .append(playerVO.getSpecial()).append("    ")
                .append(playerVO.getPaint())
                .append("p\n");
        }
        sb.append("----------------\n排名\n");
        List<BattleDetailPlayerVO> allPlayerList = new ArrayList<>();
        allPlayerList.addAll(battleDetailVO.getMyTeamPlayerList());
        allPlayerList.addAll(battleDetailVO.getEnemyTeamPlayerList());
        allPlayerList.sort(Comparator.comparing(BattleDetailPlayerVO::getRankScore, Comparator.reverseOrder()));
        int no = 1;
        for (BattleDetailPlayerVO playerVO : allPlayerList) {
            sb.append(no).append(". ").append(playerVO.getPlayerName()).append(" ").append(playerVO.getRankScore());
            if (no == 1) {
                sb.append("  本局MVP");
            }
            sb.append("\n");
            no++;
        }
        log.info(sb.toString());

    }

    @Test
    public void getLastRegularMatchInfo() {

    }

    @Test
    public void getLastBankaraMatchInfo() {

    }

}
