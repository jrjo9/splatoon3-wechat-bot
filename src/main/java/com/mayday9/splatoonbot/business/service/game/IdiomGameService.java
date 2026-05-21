package com.mayday9.splatoonbot.business.service.game;

import com.mayday9.splatoonbot.business.dto.game.IdiomGameState;
import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;

/**
 * 成语接龙核心服务
 */
public interface IdiomGameService {

    /**
     * 开始游戏
     *
     * @param groupId    群组ID
     * @param playerWxid 玩家ID
     * @param playerName 玩家名称
     * @return 游戏状态
     */
    IdiomGameState startGame(String groupId, String playerWxid, String playerName);

    /**
     * 处理接龙答案
     *
     * @param groupId     群组ID
     * @param playerAnswer 玩家答案
     * @return 验证结果
     */
    IdiomValidationResult processAnswer(String groupId, String playerAnswer);

    /**
     * 退出游戏
     *
     * @param groupId 群组ID
     * @return 游戏状态（退出前的状态）
     */
    IdiomGameState quitGame(String groupId);

    /**
     * 获取游戏状态
     *
     * @param groupId 群组ID
     * @return 游戏状态
     */
    IdiomGameState getGameState(String groupId);

    /**
     * 判断是否有游戏在进行
     *
     * @param groupId 群组ID
     * @return true if game is playing
     */
    boolean isPlaying(String groupId);
}