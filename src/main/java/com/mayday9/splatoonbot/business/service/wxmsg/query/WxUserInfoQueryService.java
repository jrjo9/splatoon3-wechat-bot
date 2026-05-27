package com.mayday9.splatoonbot.business.service.wxmsg.query;

import cn.hutool.json.JSONObject;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxUserInfoQueryRespDTO;
import com.mayday9.splatoonbot.common.util.PaipaiApiUtil;
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
        try {
            JSONObject result = PaipaiApiUtil.queryContactInfo(wxid);
            WxUserInfoQueryRespDTO respDTO = new WxUserInfoQueryRespDTO();
            JSONObject dataObj = result.getJSONObject("Data");
            respDTO.setWxid(dataObj != null ? dataObj.getStr("Wxid", "") : "");
            respDTO.setName(dataObj != null ? dataObj.getStr("Nick", "") : "");
            respDTO.setMark(dataObj != null ? dataObj.getStr("Remark", "") : "");
            respDTO.setAccount(dataObj != null ? dataObj.getStr("Alias", "") : "");
            respDTO.setHeadimg(dataObj != null ? dataObj.getStr("HeadImg", "") : "");
            return respDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取联系人信息失败");
        }
    }

}