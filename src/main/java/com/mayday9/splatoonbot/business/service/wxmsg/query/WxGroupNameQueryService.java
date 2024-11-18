package com.mayday9.splatoonbot.business.service.wxmsg.query;

import com.mayday9.splatoonbot.business.dto.wxmsg.req.WxGroupNameQueryDTO;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/10/10 10:15
 **/
@Component
public class WxGroupNameQueryService {

    /**
     * 微信_取群昵称
     *
     * @param gid 群ID
     * @return String
     */
    public String queryGroupName(String gid) {
        WxGroupNameQueryDTO groupNameQueryDTO = new WxGroupNameQueryDTO();
        groupNameQueryDTO.setType(208);
        groupNameQueryDTO.setGid(gid);
        try {
            return WxMsgSendUtil.sendMessage(groupNameQueryDTO, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取群昵称失败！");
        }
    }

}
