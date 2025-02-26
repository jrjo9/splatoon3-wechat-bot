package com.mayday9.splatoonbot.business.service.ai;

import com.mayday9.splatoonbot.common.config.VolcEngineConfig;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2025/2/25 16:53
 **/
@Slf4j
@Component
public class DeepSeekChatService {

    @Resource
    private VolcEngineConfig volcEngineConfig;

    private final static String SYSTEM_ROLE_TEXT = "你的名称是“魔魔胡胡胡萝卜”，喜欢五月天，九九是你的主人，实际上是个假装AI的机器人";

    /**
     * 普通对话
     *
     * @param message 用户消息
     * @return BotChatCompletionResult
     */
    public String chatCompletion(String message) {
        ArkService deepSeekV3Service = volcEngineConfig.buildDeepSeekV3Service();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM)
            .content(SYSTEM_ROLE_TEXT)
            .build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
            .model("bot-20250225174139-kncvb")
            .messages(messages)
            .build();
        BotChatCompletionResult response = deepSeekV3Service.createBotChatCompletion(chatCompletionRequest);
        log.info("AI对话返回内容：" + response.getChoices().get(0).getMessage().stringContent());
        return response.getChoices().get(0).getMessage().stringContent();
    }


}
