package com.mayday9.splatoonbot.common.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxMsgResponseDTO;
import com.mayday9.splatoonbot.common.dto.WxMsgSendDTO;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import com.mayday9.splatoonbot.websocket.client.PaipaiWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:22
 **/
@Slf4j
@Component
public class WxMsgSendUtil {

    private static PaipaiWebSocketClient paipaiWebSocketClient;

    private static String paipaiHost;

    private static String paipaiPort;

    @Autowired
    private PaipaiWebSocketClient webSocketClient;

    @PostConstruct
    public void init() {
        WxMsgSendUtil.paipaiWebSocketClient = webSocketClient;
        WxMsgSendUtil.paipaiHost = webSocketClient.getHost();
        WxMsgSendUtil.paipaiPort = String.valueOf(webSocketClient.getApiPort());
    }

    /**
     * 发送消息
     *
     * @param sendDto 发送DTO
     * @return void
     */
    public static void sendMessage(WxMsgSendDTO sendDto) throws Exception {
        if (sendDto == null) {
            throw new IllegalArgumentException("发送消息失败，入参为空！");
        }
        sendDto.setCid(paipaiWebSocketClient.getCid());
        String bodyStr = JSONUtil.toJsonStr(sendDto);
        log.info("发送消息请求体：{}", bodyStr);
        String url = buildUrl(sendDto.getType());
        HttpResponse response = HttpUtil.createPost(url)
            .header("Content-Type", "application/json")
            .body(bodyStr).execute();
        if (response.isOk()) {
            String responseStr = response.body();
            log.info("发送消息成功，响应结果：{}", responseStr);
        } else {
            throw new Exception("发送消息失败，响应状态码：" + response.getStatus());
        }
    }

    /**
     * 发送消息，返回结果实体
     *
     * @param sendDto       发送DTO
     * @param responseClazz 结果实体类
     * @return T
     */
    public static <T> T sendMessage(WxMsgSendDTO sendDto, Class<T> responseClazz) throws Exception {
        if (sendDto == null) {
            throw new IllegalArgumentException("发送消息失败，入参为空！");
        }
        sendDto.setCid(paipaiWebSocketClient.getCid());
        String bodyStr = JSONUtil.toJsonStr(sendDto);
        log.info("发送消息请求体：{}", bodyStr);
        String url = buildUrl(sendDto.getType());
        HttpResponse response = HttpUtil.createPost(url)
            .header("Content-Type", "application/json")
            .body(bodyStr).execute();

        if (response.isOk()) {
            String responseStr = response.body();
            log.info("发送消息成功，响应结果：{}", responseStr);
            WxMsgResponseDTO<JSONObject> wxMsgResponseDTO = JSONUtil.toBean(responseStr, WxMsgResponseDTO.class);
            if (wxMsgResponseDTO.getSuccess()) {
                if (responseClazz == String.class) {
                    return (T) wxMsgResponseDTO.getMessage();
                }
                return JSONUtil.toBean(wxMsgResponseDTO.getMessage(), responseClazz);
            } else {
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "发送消息失败，响应结果：" + responseStr);
            }
        } else {
            throw new Exception("发送消息失败，响应状态码：" + response.getStatus());
        }
    }

    /**
     * 构建请求URL
     *
     * @param type 消息类型
     * @return String
     */
    private static String buildUrl(Integer type) {
        Integer cid = paipaiWebSocketClient.getCid();
        // 根据type映射到对应的API方法名
        String method = getMethodByType(type);
        return "http://" + paipaiHost + ":" + paipaiPort + "/api/" + cid + "/" + method;
    }

    /**
     * 根据type获取对应的API方法名
     */
    private static String getMethodByType(Integer type) {
        if (type == null) {
            return "SendText";
        }
        switch (type) {
            case 101:
                return "SendText";
            case 102:
                return "SendImage";
            case 103:
                return "SendFile";
            case 104:
                return "SendXml";
            case 105:
                return "SendGif";
            case 201:
                return "GetLoginInfo";
            case 202:
                return "GetContactInfo";
            case 203:
                return "GetContactList";
            case 204:
                return "GetGroupList";
            case 205:
                return "GetGroupMemberList";
            case 206:
                return "GetGroupMemberDetail";
            case 207:
                return "GetGroupName";
            case 208:
                return "GetWxNickName";
            case 209:
                return "GetGroupNickName";
            case 210:
                return "GetGroupUserName";
            default:
                return "SendText";
        }
    }
}