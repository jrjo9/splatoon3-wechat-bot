package com.mayday9.splatoonbot.business.service.wxmsg.query;

import com.mayday9.splatoonbot.business.dto.wxmsg.req.WxUserInfoQueryDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxUserInfoQueryRespDTO;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.stereotype.Component;

/**
 * @author Lianjiannan
 * @since 2024/10/9 15:08
 **/
@Component
public class WxUserInfoQueryService {

    /**
     * 微信_查询联系人信息
     *
     * @param wxid wxid
     * @return WxUserInfoQueryRespDTO
     */
    public WxUserInfoQueryRespDTO queryUserInfo(String wxid) {
        WxUserInfoQueryDTO wxUserInfoQueryDTO = new WxUserInfoQueryDTO();
        wxUserInfoQueryDTO.setType(201);
        wxUserInfoQueryDTO.setWxid(wxid);
        try {
            return WxMsgSendUtil.sendMessage(wxUserInfoQueryDTO, WxUserInfoQueryRespDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取联系人信息失败");
        }
    }

}
