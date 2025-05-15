package com.mayday9.splatoonbot.business.dto;

import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2025/5/15 15:37
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage implements Serializable {

    private Date chatTime;

    private String talker;

    private ChatMessageRole role;

    private String message;

}
