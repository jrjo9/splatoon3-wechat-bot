package com.mayday9.splatoonbot.common.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GetGTokenAndBulletTokenResult;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GetTokenResult;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GraphqlRequestParameter;
import com.mayday9.splatoonbot.common.util.core.StringUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * NSO 工具类
 *
 * @author Lianjiannan
 * @since 2024/11/1 16:11
 **/
@Slf4j
public class NSOUtil {

    /*
     * 参考资料：
     * https://github.com/imink-app/f-API
     * https://github.com/JoneWang/imink/wiki/imink-API-Documentation
     * https://github.com/samuelthomas2774/nxapi
     * https://raw.githubusercontent.com/JoneWang/imink/master/Resources/Login%20Flow.svg
     * https://github.com/paul-sama/splatoon3-bot
     *
     * */

    public static final String API_URL = "https://api.lp1.av5ja.srv.nintendo.net";

    public static final String GRAPHQL_URL = "https://api.lp1.av5ja.srv.nintendo.net/api/graphql";

    // see https://github.com/frozenpandaman/s3s/blob/master/iksm.py
    public static final String NSO_APP_VERSION = "2.10.1";

    // https://github.com/nintendoapis/nintendo-app-versions/blob/main/data/splatnet3-app.json
    public static final String WEB_VIEW_VERSION = "6.0.0-30a1464a";

    public static final String APP_USER_AGENT = "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36";

    private static final Map<String, String> translateRidMap = new HashMap<>();

    static {
        // 获取可用的NSO应用版本
//        NSO_APP_VERSION = this.getNsoAppVersion();

        // 各接口的sha256Hash值，列举一部分
        // full list: https://github.com/samuelthomas2774/nxapi/discussions/11#discussioncomment-3614603
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

    public static String getTranslateRid(String key) {
        return translateRidMap.get(key);
    }


    /**
     * 获取可用NSO应用版本号
     *
     * @return String
     */
    public String getNsoAppVersion() {
        String url = "https://api.imink.app/config";
        String response = OkHttpClientUtil.get(url);
        JSONObject responseJsonObject = JSONUtil.parseObj(response);
        return responseJsonObject.getStr("nso_version");
    }

    /**
     * 获取sessionToken
     *
     * @param sessionTokenCode 页面复制的code
     * @return String
     */
    public static String getSessionToken(String sessionTokenCode, String authCodeVerifier) {
        log.debug("start nso login ...");
        String url = "https://accounts.nintendo.com/connect/1.0.0/api/session_token";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", "71b963c1b7b6d119");
        paramMap.put("session_token_code", sessionTokenCode);
        // auth_code_verifier
        paramMap.put("session_token_code_verifier", authCodeVerifier);

        log.debug("start get session token ...");
        HttpResponse response = HttpUtil.createPost(url)
            .header("User-Agent", "OnlineLounge/" + NSOUtil.NSO_APP_VERSION + " NASDKAPI Android")
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
        JSONObject jsonObject = JSONUtil.parseObj(responseString);
        String sessionToken = jsonObject.getStr("session_token");
        log.debug("get session token success! session token is :{}", sessionToken);
        return sessionToken;
    }

    /**
     * 根据sessionToken获取gToken与bulletToken
     *
     * @param sessionToken 请求获取的sessionToken
     * @return GetGTokenAndBulletTokenResult
     */
    public static GetGTokenAndBulletTokenResult getGTokenAndBulletToken(String sessionToken) {
        log.debug("get gToken and bulletToken with sessionToken ...");
        // 获取gToken和BulletToken
        GetTokenResult getTokenResult;
        // 获取token
        try {
            getTokenResult = getToken(sessionToken);
        } catch (Exception e) {
            log.error("获取token失败...", e);
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登录过期，请重新登录");
        }
        // 获取bulletToken
        String bulletToken;
        try {
            bulletToken = getBulletToken(getTokenResult.getWebServiceToken(), getTokenResult.getUserLang(), getTokenResult.getUserCountry());
        } catch (Exception e) {
            log.error("获取获取bulletToken失败...", e);
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登录过期，请重新登录");
        }
        return new GetGTokenAndBulletTokenResult(getTokenResult.getWebServiceToken(), bulletToken, getTokenResult.getUserNickName(), getTokenResult.getUserLang(), getTokenResult.getUserCountry());
    }

    /**
     * 根据sessionToken获取用户信息与gameServiceToken
     *
     * @param sessionToken sessionToken
     * @return GetTokenResult
     */
    private static GetTokenResult getToken(String sessionToken) {
        log.debug("get gToken ...");
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
        JSONObject getTokenRespJSONObject = JSONUtil.parseObj(getTokenResp);
        String accessToken = getTokenRespJSONObject.getStr("access_token");
        String idToken = getTokenRespJSONObject.getStr("id_token");
        if (StringUtil.isEmpty(accessToken) || StringUtil.isEmpty(idToken)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登录过期，请重新登录。");
        }

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

        JSONObject userInfo = JSONUtil.parseObj(getMeResp);
        String userNickName = userInfo.getStr("nickname");
        String userLang = userInfo.getStr("language");
        String userCountry = userInfo.getStr("country");
        String userId = userInfo.getStr("id");
        String birthday = userInfo.getStr("birthday");

        // # 通过imink生成f
        String fGenUrl = "https://api.imink.app/f";
        String iminkStepOneResp = callIminkApi(idToken, 1, fGenUrl, userId, null);
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
        JSONObject webServiceTokenRespJSONObject = JSONUtil.parseObj(webServiceTokenResp);
        Integer webServiceStatus = webServiceTokenRespJSONObject.getInt("status");
        if (webServiceStatus >= 9403 && webServiceStatus <= 9599) {
            String iminkStepTwoRespOnce = callIminkApi(idToken, 2, fGenUrl, userId, coralUserId);
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
            webServiceTokenRespJSONObject = JSONUtil.parseObj(webServiceTokenRespOnce);
            Integer statusOnce = webServiceTokenRespJSONObject.getInt("status");
            if (statusOnce != 0) {
                String errorMessage = webServiceTokenRespJSONObject.getStr("errorMessage");
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "Error from Nintendo (in Game/GetWebServiceToken step):" + errorMessage);
            }
        }
        String webServiceToken = webServiceTokenRespJSONObject.getJSONObject("result").getStr("accessToken");
        log.debug("get gToken success!");
        log.debug("web_service_token:" + webServiceToken);
        log.debug("user_nickname:" + userNickName);
        log.debug("user_lang:" + userLang);
        log.debug("user_country:" + userCountry);
        log.debug("user_info:" + userInfo);

        return new GetTokenResult(webServiceToken, userNickName, userLang, userCountry, getMeResp);
    }

    /**
     * 获取bulletToken
     *
     * @param webServiceToken webServiceToken
     * @param userLang        账号语言
     * @param userCountry     账号国家
     * @return String
     */
    private static String getBulletToken(String webServiceToken, String userLang, String userCountry) {
        log.debug("get bulletToken ...");
        String apiUrl = "https://api.lp1.av5ja.srv.nintendo.net";
        String resp = OkHttpUtil.builder()
            .url(apiUrl + "/api/bullet_tokens")
            .addHeader("Content-Length", "0")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept-Language", userLang)
            .addHeader("User-Agent", APP_USER_AGENT)
            .addHeader("X-Web-View-Ver", WEB_VIEW_VERSION)
            .addHeader("X-NACOUNTRY", userCountry)
            .addHeader("Accept", "*/*")
            .addHeader("Origin", apiUrl)
            .addHeader("X-Requested-With", "com.nintendo.znca")
            .addHeader("Cookie", "_gtoken=" + webServiceToken)
            .post(true)
            .sync();
        log.info(resp);
        JSONObject jsonObject = JSONUtil.parseObj(resp);
        String bulletToken = jsonObject.getStr("bulletToken");
        log.debug("bullet token : {}", bulletToken);
        return bulletToken;
    }

    /**
     * 请求imink的f接口，由imink生成时间戳，f，与请求ID
     *
     * @param idToken     token
     * @param step        步骤
     * @param fGenUrl     生成URL
     * @param useId       用户ID
     * @param coralUserId coralUserId
     * @return String
     */
    private static String callIminkApi(String idToken, Integer step, String fGenUrl, String useId, Long coralUserId) {
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

    /**
     * 获取Graphql请求体
     *
     * @param sha256Hash 接口sha256Hash值
     * @return Map<String, Object>
     */
    public static Map<String, Object> genGraphqlBody(String sha256Hash) {
        return genGraphqlBody(sha256Hash, null, null);
    }

    /**
     * 获取Graphql请求体
     *
     * @param sha256Hash 接口sha256Hash值
     * @return Map<String, Object>
     */
    public static Map<String, Object> genGraphqlBody(String sha256Hash, String varName, String varValue) {
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

    /**
     * 请求graphql
     *
     * @param data                    请求参数
     * @param graphqlRequestParameter 请求参数
     * @param apiUrl                  api地址
     * @param graphqlUrl              graphql地址
     * @return HttpResponse
     */
    public static Response postGraphql(Map<String, Object> data, GraphqlRequestParameter graphqlRequestParameter, String apiUrl, String graphqlUrl) {
        return OkHttpUtil.builder()
            .url(graphqlUrl)
            .addHeader("Authorization", "Bearer " + graphqlRequestParameter.getBulletToken())
            .addHeader("Accept-Language", graphqlRequestParameter.getUserLang())
            .addHeader("User-Agent", NSOUtil.APP_USER_AGENT)
            .addHeader("X-Web-View-Ver", NSOUtil.WEB_VIEW_VERSION)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "'*/*")
            .addHeader("Origin", apiUrl)
            .addHeader("X-Requested-With", "com.nintendo.znca")
            .addHeader("Referer", apiUrl + "/?lang=" + graphqlRequestParameter.getUserLang() + "&na_country=" + graphqlRequestParameter.getUserCountry() + "&na_lang=" + graphqlRequestParameter.getUserLang())
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Cookie", "_gtoken=" + graphqlRequestParameter.getGToken())
            .addParamMap(data)
            .post(true)
            .syncWithResp();

    }

}
