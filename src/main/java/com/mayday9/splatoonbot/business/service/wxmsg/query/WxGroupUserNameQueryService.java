package com.mayday9.splatoonbot.business.service.wxmsg.query;

import com.mayday9.splatoonbot.common.util.PaipaiApiUtil;
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
     * 查询群昵称
     *
     * @param gid  群ID
     * @param wxid 成员WxID
     * @return String
     */
    public String queryGroupUserName(String gid, String wxid) {
        try {
            return PaipaiApiUtil.queryGroupNickName(gid, wxid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取群成员昵称失败！");
        }
    }

    /**
     * 查询微信昵称
     *
     * @param wxid 成员WxID
     * @return String
     */
    public String queryWxNickName(String wxid) {
        try {
            return PaipaiApiUtil.queryWxNickName(wxid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取微信昵称失败！");
        }
    }

}