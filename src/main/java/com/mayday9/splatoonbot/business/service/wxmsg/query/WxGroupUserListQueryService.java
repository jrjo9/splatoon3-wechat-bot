package com.mayday9.splatoonbot.business.service.wxmsg.query;

import com.mayday9.splatoonbot.business.dto.wxmsg.req.WxGroupUserInfoQueryDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxGroupUserInfoDetailRespDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxGroupUserInfoRespDTO;
import com.mayday9.splatoonbot.common.util.WxMsgSendUtil;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/12/4 14:10
 **/
@Component
public class WxGroupUserListQueryService {

    /**
     * 微信_取群昵称
     *
     * @param gid 群ID
     * @return String
     */
    public List<WxGroupUserInfoDetailRespDTO> queryGroupUserList(String gid) {
        WxGroupUserInfoQueryDTO wxGroupUserInfoQueryDTO = new WxGroupUserInfoQueryDTO();
        wxGroupUserInfoQueryDTO.setType(204);
        wxGroupUserInfoQueryDTO.setGid(gid);
        try {
            WxGroupUserInfoRespDTO resp = WxMsgSendUtil.sendMessage(wxGroupUserInfoQueryDTO, WxGroupUserInfoRespDTO.class);
            if (resp != null) {
                return resp.getList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取群成员昵称失败！");
        }
        return new ArrayList<>();
    }
}
