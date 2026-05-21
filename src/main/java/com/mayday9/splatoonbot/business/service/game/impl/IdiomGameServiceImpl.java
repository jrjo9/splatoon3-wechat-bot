package com.mayday9.splatoonbot.business.service.game.impl;

import com.mayday9.splatoonbot.business.dto.game.IdiomGameState;
import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;
import com.mayday9.splatoonbot.business.service.game.IdiomAiService;
import com.mayday9.splatoonbot.business.service.game.IdiomGameService;
import com.mayday9.splatoonbot.common.constant.IdiomGameConstant;
import com.mayday9.splatoonbot.common.enums.IdiomGameStatusEnum;
import com.mayday9.splatoonbot.common.util.RedisUtil;
import com.mayday9.splatoonbot.common.util.core.jackson.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 成语接龙核心服务实现
 */
@Slf4j
@Service
public class IdiomGameServiceImpl implements IdiomGameService {

    @Resource
    private IdiomAiService idiomAiService;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public IdiomGameState startGame(String groupId, String playerWxid, String playerName) {
        String key = buildKey(groupId);

        // 检查是否已有游戏在进行
        if (redisUtil.hasKey(key)) {
            Object jsonObj = redisUtil.get(key);
            if (jsonObj != null) {
                String json = jsonObj.toString();
                if (!StringUtils.isEmpty(json)) {
                    IdiomGameState existingState = JsonUtil.parse(json, IdiomGameState.class);
                    if (existingState != null && existingState.getStatus() == IdiomGameStatusEnum.PLAYING) {
                        return existingState;
                    }
                }
            }
        }

        // 生成初始成语
        String idiomResponse = idiomAiService.generateIdiom(playerName);
        String currentIdiom = extractIdiom(idiomResponse);
        String explanation = extractExplanation(idiomResponse);

        // 保存游戏状态
        IdiomGameState state = new IdiomGameState();
        state.setGroupId(groupId);
        state.setPlayerWxid(playerWxid);
        state.setPlayerName(playerName);
        state.setCurrentIdiom(currentIdiom);
        state.setUsedIdioms(new ArrayList<>());
        state.getUsedIdioms().add(currentIdiom);
        state.setConsecutiveCorrect(0);
        state.setStartTime(LocalDateTime.now());
        state.setStatus(IdiomGameStatusEnum.PLAYING);

        redisUtil.set(key, JsonUtil.toJson(state), IdiomGameConstant.GAME_EXPIRE_SECONDS);

        log.info("成语接龙游戏开始，群组：{}，玩家：{}，成语：{}", groupId, playerName, currentIdiom);
        return state;
    }

    @Override
    public IdiomValidationResult processAnswer(String groupId, String playerAnswer) {
        String key = buildKey(groupId);

        // 检查游戏是否存在
        if (!redisUtil.hasKey(key)) {
            return buildErrorResult("游戏未开始，请发送\"成语接龙\"开始新游戏！");
        }

        Object jsonObj = redisUtil.get(key);
        String json = jsonObj != null ? jsonObj.toString() : null;
        if (StringUtils.isEmpty(json)) {
            return buildErrorResult("游戏状态异常，请发送\"成语接龙\"重新开始！");
        }

        IdiomGameState state = JsonUtil.parse(json, IdiomGameState.class);
        if (state == null || state.getStatus() != IdiomGameStatusEnum.PLAYING) {
            return buildErrorResult("游戏已结束，请发送\"成语接龙\"重新开始！");
        }

        // 验证接龙
        IdiomValidationResult result = idiomAiService.validateAndGenerate(state.getCurrentIdiom(), playerAnswer);

        if (result.isValid()) {
            state.setCurrentIdiom(result.getNextIdiom());
            state.setConsecutiveCorrect(state.getConsecutiveCorrect() + 1);
            state.getUsedIdioms().add(result.getNextIdiom());
            // 刷新过期时间
            redisUtil.set(key, JsonUtil.toJson(state), IdiomGameConstant.GAME_EXPIRE_SECONDS);
            log.info("成语接龙正确答案，玩家：{}，答对：{}题", state.getPlayerName(), state.getConsecutiveCorrect());
        }

        return result;
    }

    @Override
    public IdiomGameState quitGame(String groupId) {
        String key = buildKey(groupId);

        if (!redisUtil.hasKey(key)) {
            return null;
        }

        Object jsonObj = redisUtil.get(key);
        String json = jsonObj != null ? jsonObj.toString() : null;
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        IdiomGameState state = JsonUtil.parse(json, IdiomGameState.class);
        redisUtil.deleteKey(key);

        if (state != null) {
            log.info("成语接龙游戏结束，玩家：{}，连续答对：{}题", state.getPlayerName(), state.getConsecutiveCorrect());
        }
        return state;
    }

    @Override
    public IdiomGameState getGameState(String groupId) {
        String key = buildKey(groupId);

        if (!redisUtil.hasKey(key)) {
            return null;
        }

        Object jsonObj = redisUtil.get(key);
        String json = jsonObj != null ? jsonObj.toString() : null;
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        return JsonUtil.parse(json, IdiomGameState.class);
    }

    @Override
    public boolean isPlaying(String groupId) {
        IdiomGameState state = getGameState(groupId);
        return state != null && state.getStatus() == IdiomGameStatusEnum.PLAYING;
    }

    private String buildKey(String groupId) {
        return IdiomGameConstant.GAME_STATE_KEY_PREFIX + groupId;
    }

    private IdiomValidationResult buildErrorResult(String feedback) {
        IdiomValidationResult result = new IdiomValidationResult();
        result.setValid(false);
        result.setFeedback(feedback);
        return result;
    }

    private String extractIdiom(String response) {
        if (StringUtils.isEmpty(response)) {
            return "一心一意";
        }
        int bracketIndex = response.indexOf('[');
        if (bracketIndex > 0) {
            return response.substring(0, bracketIndex).trim();
        }
        String firstLine = response.split("\n")[0].trim();
        if (firstLine.length() > 4) {
            return firstLine.substring(0, 4);
        }
        return firstLine;
    }

    private String extractExplanation(String response) {
        if (StringUtils.isEmpty(response)) {
            return "";
        }
        int startIndex = response.indexOf('[');
        int endIndex = response.indexOf(']');
        if (startIndex >= 0 && endIndex > startIndex) {
            return response.substring(startIndex + 1, endIndex);
        }
        return "";
    }
}