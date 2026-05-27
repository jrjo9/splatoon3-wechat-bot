package com.mayday9.splatoonbot.common.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.common.dto.PaipaiApiRequest;
import com.mayday9.splatoonbot.common.dto.PaipaiFileMsgData;
import com.mayday9.splatoonbot.common.dto.PaipaiMsgData;
import com.mayday9.splatoonbot.common.dto.PaipaiQueryContactData;
import com.mayday9.splatoonbot.common.dto.PaipaiQueryGroupNickData;
import com.mayday9.splatoonbot.common.dto.PaipaiQueryRoomData;
import com.mayday9.splatoonbot.common.dto.PaipaiQueryWxNickData;
import com.mayday9.splatoonbot.websocket.client.PaipaiWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 派派框架 API 工具类
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Slf4j
@Component
public class PaipaiApiUtil {

    private static PaipaiWebSocketClient paipaiWebSocketClient;

    private static String paipaiHost;

    private static String paipaiPort;

    @Autowired
    private PaipaiWebSocketClient webSocketClient;

    @PostConstruct
    public void init() {
        PaipaiApiUtil.paipaiWebSocketClient = webSocketClient;
        PaipaiApiUtil.paipaiHost = webSocketClient.getHost();
        PaipaiApiUtil.paipaiPort = String.valueOf(webSocketClient.getApiPort());
    }

    /**
     * 发送文本消息
     *
     * @param toId    收信人WxID或收信群ID
     * @param content 消息内容
     * @param atList  艾特ID列表，多个用,分隔
     */
    public static void sendTextMessage(String toId, String content, String atList) throws Exception {
        PaipaiMsgData data = new PaipaiMsgData();
        data.setToId(toId);
        data.setContent(emojiToUnicode(content));
        data.setAtList(atList);
        doPostVoid(101, data);
    }

    /**
     * 发送图片/文件
     *
     * @param toId     收信人WxID或收信群ID
     * @param filePath 文件路径（框架本地文件路径）
     */
    public static void sendImageMessage(String toId, String filePath) throws Exception {
        PaipaiFileMsgData data = new PaipaiFileMsgData();
        data.setToId(toId);
        data.setFile(filePath);
        data.setFileName(filePath.substring(filePath.lastIndexOf("\\") + 1));
        doPostVoid(103, data);
    }

    /**
     * 发送GIF
     *
     * @param toId     收信人WxID或收信群ID
     * @param filePath 文件路径
     */
    public static void sendGifMessage(String toId, String filePath) throws Exception {
        PaipaiFileMsgData data = new PaipaiFileMsgData();
        data.setToId(toId);
        data.setFile(filePath);
        data.setFileName(filePath.substring(filePath.lastIndexOf("\\") + 1));
        doPostVoid(103, data);
    }

    /**
     * 查询联系人信息
     *
     * @param toId 联系人WxID或群ID
     * @return 联系人信息JSON
     */
    public static JSONObject queryContactInfo(String toId) throws Exception {
        PaipaiQueryContactData data = new PaipaiQueryContactData();
        data.setToId(toId);
        return doPost(122, data);
    }

    /**
     * 查询群成员列表
     *
     * @param roomId 群ID
     * @return 群成员列表JSON
     */
    public static JSONObject queryGroupMemberList(String roomId) throws Exception {
        PaipaiQueryRoomData data = new PaipaiQueryRoomData();
        data.setRoomId(roomId);
        return doPost(126, data);
    }

    /**
     * 查询微信昵称
     *
     * @param wxId 联系人WxID
     * @return 昵称
     */
    public static String queryWxNickName(String wxId) throws Exception {
        PaipaiQueryWxNickData data = new PaipaiQueryWxNickData();
        data.setWxId(wxId);
        JSONObject result = doPost(130, data);
        JSONObject dataObj = result.getJSONObject("Data");
        if (dataObj != null) {
            String nick = dataObj.getStr("Nick");
            return nick != null ? nick : "";
        }
        return "";
    }

    /**
     * 查询群昵称
     *
     * @param roomId 群ID
     * @param wxId   成员WxID
     * @return 群内昵称
     */
    public static String queryGroupNickName(String roomId, String wxId) throws Exception {
        PaipaiQueryGroupNickData data = new PaipaiQueryGroupNickData();
        data.setWxId(wxId);
        data.setRoomId(roomId);
        JSONObject result = doPost(131, data);
        JSONObject dataObj = result.getJSONObject("Data");
        if (dataObj != null) {
            String nick = dataObj.getStr("Nick");
            return nick != null ? nick : "";
        }
        return "";
    }

    /**
     * 通用的POST请求
     *
     * @param type API类型
     * @param data 消息数据
     */
    private static void doPostVoid(Integer type, Object data) throws Exception {
        PaipaiApiRequest request = buildRequest(type, data);
        String bodyStr = JSONUtil.toJsonStr(request);
        log.info("发送消息请求体：{}", bodyStr);
        String url = buildUrl();
        HttpResponse response = HttpUtil.createPost(url)
            .header("Content-Type", "application/json;charset=utf-8")
            .body(bodyStr, "utf-8").execute();
        handleResponse(response);
    }

    /**
     * 通用的POST请求，返回响应数据
     *
     * @param type API类型
     * @param data 消息数据
     * @return 响应JSON
     */
    public static JSONObject doPost(Integer type, Object data) throws Exception {
        PaipaiApiRequest request = buildRequest(type, data);
        String bodyStr = JSONUtil.toJsonStr(request);
        log.info("请求体：{}", bodyStr);
        String url = buildUrl();
        HttpResponse response = HttpUtil.createPost(url)
            .header("Content-Type", "application/json;charset=utf-8")
            .body(bodyStr, "utf-8").execute();

        if (response.isOk()) {
            String responseStr = response.body();
            log.info("响应结果：{}", responseStr);
            return JSONUtil.parseObj(responseStr);
        } else {
            throw new Exception("请求失败，响应状态码：" + response.getStatus());
        }
    }

    /**
     * 构建请求对象
     */
    private static PaipaiApiRequest buildRequest(Integer type, Object data) {
        PaipaiApiRequest request = new PaipaiApiRequest();
        request.setToken(paipaiWebSocketClient.getToken());
        request.setCid(paipaiWebSocketClient.getCid());
        request.setType(type);
        request.setData(data);
        return request;
    }

    /**
     * 构建请求URL
     */
    private static String buildUrl() {
        return "http://" + paipaiHost + ":" + paipaiPort;
    }

    /**
     * 处理响应
     */
    private static void handleResponse(HttpResponse response) throws Exception {
        if (response.isOk()) {
            String responseStr = response.body();
            log.info("发送消息成功，响应结果：{}", responseStr);
        } else {
            throw new Exception("发送消息失败，响应状态码：" + response.getStatus());
        }
    }

    /**
     * 将emoji转换为Unicode转义字符串
     */
    private static String emojiToUnicode(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isHighSurrogate(c)) {
                // emoji字符（surrogate pair），转换为Unicode转义
                int codePoint = content.codePointAt(i);
                sb.append(String.format("\\u%04x\\u%04x",
                    (int) (0xD800 + ((codePoint - 0x10000) >> 10)),
                    (int) (0xDC00 + ((codePoint - 0x10000) & 0x3FF))));
                i++; // skip low surrogate
            } else if (c >= 0x80) {
                // 非ASCII字符，转换为Unicode转义
                sb.append(String.format("\\u%04X", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}