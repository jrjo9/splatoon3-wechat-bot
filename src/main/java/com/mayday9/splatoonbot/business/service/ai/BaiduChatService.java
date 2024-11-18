package com.mayday9.splatoonbot.business.service.ai;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.mayday9.splatoonbot.common.config.QianfanConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/10/12 14:52
 **/
@Slf4j
@Component
public class BaiduChatService {

    @Resource
    private QianfanConfig qianfanConfig;

    public ChatResponse chatCompletion(String message) {
        Qianfan qianfan = qianfanConfig.qianfan();
        ChatResponse response = qianfan.chatCompletion()
            .model("ERNIE-Tiny-8K")
            .addMessage("user", message)
            .execute();
        log.info("AI对话返回内容：" + response.getResult());
        return response;
    }

}
