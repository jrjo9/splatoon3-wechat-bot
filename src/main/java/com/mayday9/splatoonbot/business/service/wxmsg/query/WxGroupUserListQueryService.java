package com.mayday9.splatoonbot.business.service.wxmsg.query;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxGroupUserInfoDetailRespDTO;
import com.mayday9.splatoonbot.common.util.PaipaiApiUtil;
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
     * 微信_取群成员列表
     *
     * @param gid 群ID
     * @return List<WxGroupUserInfoDetailRespDTO>
     */
    public List<WxGroupUserInfoDetailRespDTO> queryGroupUserList(String gid) {
        try {
            JSONObject result = PaipaiApiUtil.queryGroupMemberList(gid);
            List<WxGroupUserInfoDetailRespDTO> list = new ArrayList<>();
            JSONObject dataObj = result.getJSONObject("Data");
            if (dataObj != null && dataObj.containsKey("List") && dataObj.get("List") != null) {
                JSONArray jsonArray = dataObj.getJSONArray("List");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    WxGroupUserInfoDetailRespDTO dto = new WxGroupUserInfoDetailRespDTO();
                    dto.setWxid(item.getStr("Wxid", ""));
                    dto.setName(item.getStr("Nick", ""));
                    list.add(dto);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取群成员列表失败！");
        }
    }
}