package com.mayday9.splatoonbot.business.service.wxmsg.query;

import com.mayday9.splatoonbot.business.dto.wxmsg.req.WxGroupUserNameQueryDTO;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/10/10 10:15
 **/
@Component
public class WxGroupUserNameQueryService {

    /**
     * 微信_取群昵称
     *
     * @param gid 群ID
     * @return String
     */
    public String queryGroupUserName(String gid, String wxid) {
        WxGroupUserNameQueryDTO wxGroupUserNameQueryDTO = new WxGroupUserNameQueryDTO();
        wxGroupUserNameQueryDTO.setType(210);
        wxGroupUserNameQueryDTO.setGid(gid);
        wxGroupUserNameQueryDTO.setWxid(wxid);
        try {
            return WxMsgSendUtil.sendMessage(wxGroupUserNameQueryDTO, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取群成员昵称失败！");
        }
    }

}
