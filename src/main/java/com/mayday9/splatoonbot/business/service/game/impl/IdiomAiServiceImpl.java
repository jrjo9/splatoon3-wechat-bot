package com.mayday9.splatoonbot.business.service.game.impl;

import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;
import com.mayday9.splatoonbot.business.entity.TSysParam;
import com.mayday9.splatoonbot.business.service.TSysParamService;
import com.mayday9.splatoonbot.business.service.ai.BaiduChatService;
import com.mayday9.splatoonbot.business.service.ai.ArkChatService;
import com.mayday9.splatoonbot.business.service.game.IdiomAiService;
import com.mayday9.splatoonbot.common.constant.AiChatModelConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2026/5/20
 **/
@Slf4j
@Service
public class IdiomAiServiceImpl implements IdiomAiService {

    @Resource
    private ArkChatService arkChatService;

    @Resource
    private BaiduChatService baiduChatService;

    @Resource
    private TSysParamService tSysParamService;

    @Override
    public String generateIdiom(String playerName) {
        String prompt = String.format(
            "你是成语接龙游戏主持人\"魔魔胡胡胡萝卜\"。\n" +
            "请给玩家[%s]出一个适合开始的4字成语。\n" +
            "回复格式：成语 [简短的解释]\n" +
            "例如：狐假虎威 [狐狸借老虎的威势吓唬人]",
            playerName
        );

        String response = chatAi(prompt);
        log.info("生成初始成语，AI响应：{}", response);

        // 解析 AI 返回，提取成语和解释
        return extractIdiomResponse(response);
    }

    @Override
    public IdiomValidationResult validateAndGenerate(String currentIdiom, String playerAnswer) {
        String lastChar = String.valueOf(currentIdiom.charAt(currentIdiom.length() - 1));

        String prompt = String.format(
            "当前成语：%s\n" +
            "玩家答案：%s\n\n" +
            "验证规则：\n" +
            "1. 玩家答案是否以\"%s\"字开头？\n" +
            "2. 玩家答案是否是真实存在的中文成语（4字）？\n\n" +
            "回复格式（只回复以下格式之一）：\n" +
            "正确：OK|下一个成语|解释\n" +
            "错误：ERROR|错误原因",
            currentIdiom, playerAnswer, lastChar
        );

        String response = chatAi(prompt);
        log.info("验证接龙，当前成语：{}，玩家答案：{}，AI响应：{}", currentIdiom, playerAnswer, response);

        return parseValidationResult(response, lastChar);
    }

    private String chatAi(String prompt) {
        String aiChatModel = AiChatModelConstant.ERNIE_SPEED_8K;
        TSysParam tSysParam = tSysParamService.findByCode("ai_chat_model");
        if (tSysParam != null && !StringUtils.isEmpty(tSysParam.getParamValue())) {
            aiChatModel = tSysParam.getParamValue();
        }

        if (AiChatModelConstant.ARK.equals(aiChatModel)) {
            return arkChatService.botChatCompletion(prompt);
        } else {
            return baiduChatService.chatCompletion(prompt);
        }
    }

    private String extractIdiomResponse(String response) {
        if (StringUtils.isEmpty(response)) {
            return "一心一意 [形容专心一意，没有别的想法]";
        }
        // 简单处理：如果AI返回包含"【】"或"[]"，保留格式
        // 否则直接返回
        return response.trim();
    }

    private IdiomValidationResult parseValidationResult(String response, String lastChar) {
        IdiomValidationResult result = new IdiomValidationResult();

        if (StringUtils.isEmpty(response)) {
            result.setValid(false);
            result.setFeedback("AI响应为空，请稍后再试");
            return result;
        }

        response = response.trim();

        if (response.startsWith("OK|")) {
            result.setValid(true);
            String[] parts = response.substring(3).split("\\|", 2);
            if (parts.length >= 1) {
                result.setNextIdiom(parts[0].trim());
            }
            if (parts.length >= 2) {
                result.setExplanation(parts[1].trim());
            }
        } else if (response.startsWith("错误：ERROR|")) {
            result.setValid(false);
            result.setFeedback(response.substring(10).trim());
        } else if (response.startsWith("ERROR|")) {
            result.setValid(false);
            result.setFeedback(response.substring(6).trim());
        } else {
            // 尝试解析非标准格式
            if (response.contains("正确") || response.contains("OK")) {
                result.setValid(true);
                result.setFeedback("验证通过");
            } else {
                result.setValid(false);
                result.setFeedback(response);
            }
        }

        return result;
    }

}