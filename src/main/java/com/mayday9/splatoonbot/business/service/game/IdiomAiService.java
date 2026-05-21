package com.mayday9.splatoonbot.business.service.game;

import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;

/**
 * @author Lianjiannan
 * @since 2026/5/20
 **/
public interface IdiomAiService {

    /**
     * 生成初始成语
     *
     * @param playerName 玩家名称
     * @return 格式：成语 [解释]
     */
    String generateIdiom(String playerName);

    /**
     * 验证接龙并生成下一个成语
     *
     * @param currentIdiom 当前成语
     * @param playerAnswer 玩家答案
     * @return 验证结果
     */
    IdiomValidationResult validateAndGenerate(String currentIdiom, String playerAnswer);

}