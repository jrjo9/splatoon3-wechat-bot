package com.mayday9.splatoonbot.business.dto.wxmsg.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/4 17:00
 **/
@Setter
@Getter
@NoArgsConstructor
public class WxGroupUserInfoRespDTO {

    private String count;

    private List<WxGroupUserInfoDetailRespDTO> list;

}
