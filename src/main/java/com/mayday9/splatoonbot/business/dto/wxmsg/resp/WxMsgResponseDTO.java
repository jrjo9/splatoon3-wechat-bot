package com.mayday9.splatoonbot.business.dto.wxmsg.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/10/9 16:01
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxMsgResponseDTO<T> {

    private String code;

    private Boolean success;

    private T message;

}
