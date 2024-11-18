package com.mayday9.splatoonbot.business.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.StringUtils;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.BattleDetailPlayerVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.BattleDetailVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailPlayerVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.CoopDetailWaveVO;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GetGTokenAndBulletTokenResult;
import com.mayday9.splatoonbot.business.dto.splatoon.nso.GraphqlRequestParameter;
import com.mayday9.splatoonbot.business.entity.TBasicWxUserNso;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxUserNsoDao;
import com.mayday9.splatoonbot.business.service.NSOService;
import com.mayday9.splatoonbot.common.util.NSOUtil;
import com.mayday9.splatoonbot.common.util.core.DateUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lianjiannan
 * @since 2024/11/8 16:33
 **/
@Slf4j
@Service
public class NSOServiceImpl implements NSOService {

    @Resource
    private TBasicWxUserNsoDao tBasicWxUserNsoDao;

    private static Map<String, String> authCodeMap = new ConcurrentHashMap<>();

    /**
     * 生成认证URL
     *
     * @param wxid 微信号
     * @return String
     */
    @Override
    public String generateAuthUrl(String wxid) {

        // Visit authorization link in browser
        SecureRandom random = new SecureRandom();

        byte[] bytes = new byte[36];
        random.nextBytes(bytes);
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String auth_state = base64Encoder.encodeToString(bytes);
        log.debug("auth_state=" + auth_state);

        byte[] bytes2 = new byte[32];
        random.nextBytes(bytes2);
        String authCodeVerifier = base64Encoder.encodeToString(bytes2).replaceAll("=", "");
        log.debug("auth_code_verifier=" + authCodeVerifier);

        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "系统错误，请联系管理鱿。");
        }
        byte[] hash = sha256.digest(authCodeVerifier.getBytes(StandardCharsets.UTF_8));
        String auth_code_challenge = base64Encoder.encodeToString(hash).replaceAll("=", "");
        log.debug("auth_code_challenge=" + auth_code_challenge);

        // 临时记录auth_code_verifier，用于认证登陆
        authCodeMap.put(wxid, authCodeVerifier);

        return "https://accounts.nintendo.com/connect/1.0.0/authorize?state=" + auth_state +
            "&redirect_uri=npf71b963c1b7b6d119://auth" +
            "&client_id=71b963c1b7b6d119" +
            "&scope=openid%20user%20user.birthday%20user.mii%20user.screenName" +
            "&response_type=session_token_code" +
            "&session_token_code_challenge=" + auth_code_challenge +
            "&session_token_code_challenge_method=S256" +
            "&theme=login_form";
    }

    /**
     * 登陆
     *
     * @param wxid 微信号
     * @return void
     */
    @Override
    @Transactional
    public void login(String wxid, String text) {
        // 通过sessionTokenCode获取到sessionToken
        String sessionTokenCode = text.substring(text.indexOf("session_token_code=") + 19);
        sessionTokenCode = sessionTokenCode.substring(0, sessionTokenCode.indexOf("&"));
        String authCodeVerifier = authCodeMap.get(wxid);
        if (StringUtils.isEmpty(authCodeVerifier)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登陆失败，请重新生成认证URL，并复制code进行登陆");
        }
        String sessionToken = NSOUtil.getSessionToken(sessionTokenCode, authCodeVerifier);
        if (StringUtils.isEmpty(sessionToken)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "登陆失败，请重新生成认证URL，并复制code进行登陆");
        }
        // 通过sessionToken获取gToken与bulletToken
        GetGTokenAndBulletTokenResult getGTokenAndBulletTokenResult = NSOUtil.getGTokenAndBulletToken(sessionToken);
        // 保存信息
        TBasicWxUserNso tBasicWxUserNso = tBasicWxUserNsoDao.findOneBy("wxid", wxid);
        if (tBasicWxUserNso == null) {
            tBasicWxUserNso = new TBasicWxUserNso();
            tBasicWxUserNso.setWxid(wxid);
        }
        tBasicWxUserNso.setSessionToken(sessionToken);
        tBasicWxUserNso.setNsoName(getGTokenAndBulletTokenResult.getUserNickName());
        tBasicWxUserNso.setLang(getGTokenAndBulletTokenResult.getUserLang());
        tBasicWxUserNso.setCountry(getGTokenAndBulletTokenResult.getUserCountry());
        tBasicWxUserNso.setGToken(getGTokenAndBulletTokenResult.getGToken());
        tBasicWxUserNso.setBulletToken(getGTokenAndBulletTokenResult.getBulletToken());
        Date loginTime = new Date();
        tBasicWxUserNso.setLoginTime(loginTime);
        tBasicWxUserNso.setGetTokenTime(loginTime);
        tBasicWxUserNsoDao.saveOrUpdate(tBasicWxUserNso);
    }

    /**
     * 获取个人总览信息
     *
     * @param wxid 微信号
     * @return String
     */
    @Override
    public String getMeInfo(String wxid) {
        TBasicWxUserNso tBasicWxUserNso = tBasicWxUserNsoDao.findOneBy("wxid", wxid);
        if (tBasicWxUserNso == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请先绑定NSO账号！");
        }
        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(tBasicWxUserNso.getWxid(),
            tBasicWxUserNso.getSessionToken(), tBasicWxUserNso.getGToken(), tBasicWxUserNso.getBulletToken(), "zh-CN", tBasicWxUserNso.getCountry());
        // 总览
        JSONObject summary = this.getSummary(graphqlRequestParameter);
        // 获取所有资源
        JSONObject allRes = this.getAllRes(graphqlRequestParameter);
        // 打工
        JSONObject coopSummary = this.getCoopSummary(graphqlRequestParameter);
        // 组装返回消息
        //昵称：ホホオヒ™HOSEE
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
        sb.append("昵称：").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("name")).append("\n")
            .append("头衔：").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("byname")).append("\n")
            .append("编号：").append(summary.getJSONObject("data").getJSONObject("currentPlayer").getStr("nameId")).append("\n")
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
            .append("[emoji=D83E][emoji=DD47]：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("gold"))
            .append("，[emoji=D83E][emoji=DD48]：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("silver"))
            .append("，[emoji=D83E][emoji=DD49]：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("scale").getInt("bronze"))
            .append("]\n")
            .append("打工次数：").append(coopSummary.getJSONObject("data").getJSONObject("coopResult").getJSONObject("pointCard").getInt("playCount")).append("\n");
        log.info(sb.toString());
        return sb.toString();
    }


    /**
     * 获取最新一场打工信息
     *
     * @param wxid 微信号
     * @return String
     */
    @Override
    public String getLastCoop(String wxid) {
        TBasicWxUserNso tBasicWxUserNso = tBasicWxUserNsoDao.findOneBy("wxid", wxid);
        if (tBasicWxUserNso == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请先绑定NSO账号！");
        }
        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(tBasicWxUserNso.getWxid(),
            tBasicWxUserNso.getSessionToken(), tBasicWxUserNso.getGToken(), tBasicWxUserNso.getBulletToken(), "zh-CN", tBasicWxUserNso.getCountry());
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
            .append("==================\n");
        for (int i = 0; i < coopDetailVO.getWaveList().size(); i++) {
            CoopDetailWaveVO waveVO = coopDetailVO.getWaveList().get(i);
            if (i < 3) {
                String waveResult = waveVO.getTeamDeliverCount() >= waveVO.getDeliverNorm() ? "GJ!" : "NG";
                sb.append("WAVE-").append(waveVO.getWaveNumber()).append("     ").append(waveResult).append("     ").append(waveVO.getTeamDeliverCount()).append("/").append(waveVO.getDeliverNorm()).append("\n");
            } else {
                String waveResult = coopDetailVO.getResultWave() == 0 ? "GJ!" : "NG";
                sb.append("EX-WAVE").append(" ").append(waveResult).append("\n");
            }
        }
        sb.append("==================\n");
        for (CoopDetailPlayerVO playerVO : coopDetailVO.getPlayerList()) {
            sb.append("[emoji=D83E][emoji=DD91]").append(playerVO.getPlayerName()).append("\n")
                .append("[emoji=D83D][emoji=DC1F]：x ").append(playerVO.getDefeatEnemyCount()).append("\n")
                .append("[emoji=D83D][emoji=DFE1]：x ").append(playerVO.getGoldenDeliverCount()).append("(").append(playerVO.getGoldenAssistCount()).append(")\n")
                .append("[emoji=D83D][emoji=DD34]：x ").append(playerVO.getDeliverCount()).append("\n")
                .append("[emoji=D83D][emoji=DE91]：x ").append(playerVO.getRescueCount()).append("\n")
                .append("[emoji=D83D][emoji=DEDF]：x ").append(playerVO.getRescuedCount()).append("\n");
            sb.append("\n");
        }
        sb.append("------------排行榜-----------\n\n");
        coopDetailVO.getPlayerList().sort(Comparator.comparing(CoopDetailPlayerVO::getRankScore, Comparator.reverseOrder()));
        int no = 1;
        for (CoopDetailPlayerVO playerVO : coopDetailVO.getPlayerList()) {
            if (no == 1) {
                sb.append("[emoji=D83E][emoji=DD47]");
            } else if (no == 2) {
                sb.append("[emoji=D83E][emoji=DD48]");
            } else if (no == 3) {
                sb.append("[emoji=D83E][emoji=DD49]");
            } else {
                sb.append("[emoji=D83D][emoji=DE30]");
            }
            sb.append(playerVO.getPlayerName()).append("[emoji=D83E][emoji=DEE7]").append(playerVO.getRankScore());
            if (no == 1) {
                sb.append("[emoji=D83D][emoji=DC51]");
            }
            sb.append("\n");
            no++;
        }
        log.info(sb.toString());
        return sb.toString();
    }

    /**
     * 获取最后一场（真格/涂地）比赛信息
     *
     * @param wxid 微信号
     * @return String
     */
    @Override
    public String getLastMatch(String wxid) {
        TBasicWxUserNso tBasicWxUserNso = tBasicWxUserNsoDao.findOneBy("wxid", wxid);
        if (tBasicWxUserNso == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请先绑定NSO账号！");
        }
        GraphqlRequestParameter graphqlRequestParameter = new GraphqlRequestParameter(tBasicWxUserNso.getWxid(),
            tBasicWxUserNso.getSessionToken(), tBasicWxUserNso.getGToken(), tBasicWxUserNso.getBulletToken(), "zh-CN", tBasicWxUserNso.getCountry());
        // 比赛
        JSONObject recentBattles = this.getRecentBattles(graphqlRequestParameter);
        JSONArray nodes = recentBattles.getJSONObject("data").getJSONObject("latestBattleHistories").getJSONObject("historyGroupsOnlyFirst").getJSONArray("nodes");
        if (nodes.isEmpty()) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "查询不到比赛记录！");
        }
        String firstMatchId = nodes.getJSONObject(0).getJSONObject("historyDetails").getJSONArray("nodes").getJSONObject(0).getStr("id");
        log.debug("first match id is :{}", firstMatchId);
        JSONObject matchDetail = this.getBattleDetail(graphqlRequestParameter, firstMatchId);
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
        sb.append(battleDetailVO.getMatchName()).append("==[  ").append(battleDetailVO.getJudgement()).append("  ]==\n")
            .append("===============\n")
            .append("[emoji=D83C][emoji=DFAE]我方队伍\n");
        for (BattleDetailPlayerVO playerVO : battleDetailVO.getMyTeamPlayerList()) {
            sb.append("[emoji=D83D][emoji=DD30]").append(playerVO.getPlayerName()).append("\n")
                .append(playerVO.getKill()).append("(").append(playerVO.getAssist()).append(")/")
                .append(playerVO.getDeath()).append("/")
                .append(playerVO.getSpecial()).append("    ")
                .append(playerVO.getPaint())
                .append("p\n");
        }
        sb.append("\n[emoji=D83C][emoji=DFAE]敌方队伍\n");
        for (BattleDetailPlayerVO playerVO : battleDetailVO.getEnemyTeamPlayerList()) {
            sb.append("[emoji=D83D][emoji=DD30]").append(playerVO.getPlayerName()).append("\n")
                .append(playerVO.getKill()).append("(").append(playerVO.getAssist()).append(")/")
                .append(playerVO.getDeath()).append("/")
                .append(playerVO.getSpecial()).append("    ")
                .append(playerVO.getPaint())
                .append("p\n");
        }
        sb.append("======排名======\n");
        List<BattleDetailPlayerVO> allPlayerList = new ArrayList<>();
        allPlayerList.addAll(battleDetailVO.getMyTeamPlayerList());
        allPlayerList.addAll(battleDetailVO.getEnemyTeamPlayerList());
        allPlayerList.sort(Comparator.comparing(BattleDetailPlayerVO::getRankScore, Comparator.reverseOrder()));
        int no = 1;
        for (BattleDetailPlayerVO playerVO : allPlayerList) {
            if (no == 1) {
                sb.append("[emoji=D83E][emoji=DD47]");
            } else if (no == 2) {
                sb.append("[emoji=D83E][emoji=DD48]");
            } else if (no == 3) {
                sb.append("[emoji=D83E][emoji=DD49]");
            } else {
                sb.append(" ").append(no).append(". ");
            }
            sb.append(playerVO.getPlayerName()).append("[emoji=D83C][emoji=DF88]").append(playerVO.getRankScore());
            if (no == 1) {
                sb.append("[emoji=D83C][emoji=DFC6]");
            }
            sb.append("\n");
            no++;
        }
        log.info(sb.toString());
        return sb.toString();
    }

    /**
     * 请求graphql，获取任天堂NSO数据
     *
     * @param data                    请求体
     * @param skipCheckToken          是否跳过验证token，多个接口调用只需要第一个接口验证
     * @param graphqlRequestParameter 请求参数
     * @return String
     */
    private String requestGraphql(Map<String, Object> data, Boolean skipCheckToken, GraphqlRequestParameter graphqlRequestParameter) {
        try {
            String apiUrl = NSOUtil.API_URL;
            String graphqlUrl = NSOUtil.GRAPHQL_URL;
            if (!skipCheckToken) {
                // 请求首页，判断token是否过期，过期重新获取
                prefetchChecks(graphqlUrl, apiUrl, graphqlRequestParameter);
            }
            Response response = NSOUtil.postGraphql(data, graphqlRequestParameter, apiUrl, graphqlUrl);
            if (response.code() != 200) {
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "tokens expired.");
            }
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请求失败，请联系管理员。");
        }
    }

    /**
     * 校验token是否过期，通过请求首页判断
     *
     * @param graphqlUrl              请求URL
     * @param apiUrl                  API URL
     * @param graphqlRequestParameter 请求参数
     * @return void
     */
    private void prefetchChecks(String graphqlUrl, String apiUrl, GraphqlRequestParameter graphqlRequestParameter) throws IOException {
        // Queries the SplatNet 3 homepage to check if our gtoken & bulletToken are still valid and regenerates them if not.
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("HomeQuery"), "naCountry", graphqlRequestParameter.getUserCountry());
        Response response = NSOUtil.postGraphql(data, graphqlRequestParameter, apiUrl, graphqlUrl);
        log.info(Objects.requireNonNull(response.body()).string());
        if (response.code() != 200) {
            log.info("tokens expired.");
            // 重新获取token
            GetGTokenAndBulletTokenResult getGTokenAndBulletTokenResult = NSOUtil.getGTokenAndBulletToken(graphqlRequestParameter.getSessionToken());
            graphqlRequestParameter.setGToken(getGTokenAndBulletTokenResult.getGToken());
            graphqlRequestParameter.setBulletToken(getGTokenAndBulletTokenResult.getBulletToken());
            TBasicWxUserNso tBasicWxUserNso = tBasicWxUserNsoDao.findOneBy("wxid", graphqlRequestParameter.getWxid());
            if (tBasicWxUserNso == null) {
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "无登陆信息，请绑定NSO！");
            }
            tBasicWxUserNso.setGToken(getGTokenAndBulletTokenResult.getGToken());
            tBasicWxUserNso.setSessionToken(getGTokenAndBulletTokenResult.getGToken());
            tBasicWxUserNso.setGetTokenTime(new Date());
            tBasicWxUserNsoDao.updateById(tBasicWxUserNso);
        }
    }

    /**
     * 获取总览数据
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getSummary(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("HistorySummary"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
//        log.debug("获取总览数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    /**
     * 获取总览数据
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getAllRes(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("TotalQuery"));
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
//        log.debug("获取资源数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }


    /**
     * 获取打工数据
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getCoopSummary(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("CoopHistoryQuery"));
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
//        log.debug("获取打工数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    /**
     * 获取打工列表
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getCoops(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("CoopHistoryQuery"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
//        log.debug("获取打工数据列表:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    /**
     * 获取打工详情
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getCoopDetail(GraphqlRequestParameter graphqlRequestParameter, String coopDetailId) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("CoopHistoryDetailQuery"), "coopHistoryDetailId", coopDetailId);
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
//        log.debug("获取打工数据详情:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    /**
     * 获取比赛列表
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getRecentBattles(GraphqlRequestParameter graphqlRequestParameter) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("LatestBattleHistoriesQuery"));
        String resp = this.requestGraphql(data, false, graphqlRequestParameter);
//        log.debug("获取比赛列表数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }

    /**
     * 获取比赛详情
     *
     * @param graphqlRequestParameter 请求参数
     * @return JSONObject
     */
    private JSONObject getBattleDetail(GraphqlRequestParameter graphqlRequestParameter, String battleDetailId) {
        Map<String, Object> data = NSOUtil.genGraphqlBody(NSOUtil.getTranslateRid("VsHistoryDetailQuery"), "vsResultId", battleDetailId);
        String resp = this.requestGraphql(data, true, graphqlRequestParameter);
//        log.debug("获取比赛详情数据:{}", resp);
        return JSONUtil.parseObj(resp);
    }
}
