package com.mayday9.splatoonbot.common.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxMsgResponseDTO;
import com.mayday9.splatoonbot.common.dto.WxMsgSendDTO;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Lianjiannan
 * @since 2024/9/14 10:22
 **/
@Slf4j
@Component
public class WxMsgSendUtil {

    private static String paipaiToken;

    private static String paipaiHost;

    private static String paipaiPort;


    @Value("${paipai.server.token}")
    private String token;

    @Value("${paipai.server.host}")
    private String host;

    @Value("${paipai.server.port}")
    private String port;

    @PostConstruct
    public void init() {
        paipaiToken = token;
        paipaiHost = host;
        paipaiPort = port;
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
        sendDto.setToken(paipaiToken);
        String bodyStr = JSONUtil.toJsonStr(sendDto);
        log.info("发送消息请求体：{}", bodyStr);
        String bodyStrEncode = chinaToUnicode(bodyStr);
        log.info("发送消息请求体转换Unicode：{}", bodyStrEncode);
        String url = "http://" + paipaiHost + ":" + paipaiPort;
        HttpResponse response = HttpUtil.createPost(url)
            .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
            .header("Content-Type", "application/json")
            .body(bodyStrEncode).execute();
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
        sendDto.setToken(paipaiToken);
        String bodyStr = JSONUtil.toJsonStr(sendDto);
        log.info("发送消息请求体：{}", bodyStr);
        String bodyStrEncode = chinaToUnicode(bodyStr);
        log.info("发送消息请求体转换Unicode：{}", bodyStrEncode);
        String url = "http://" + paipaiHost + ":" + paipaiPort;
        HttpResponse response = HttpUtil.createPost(url)
            .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
            .header("Content-Type", "application/json")
            .body(bodyStrEncode).execute();

        byte[] datacopy = response.bodyBytes();
        String data = new String(datacopy, "GBK");

        if (response.isOk()) {
            log.info("发送消息成功，响应结果：{}", data);
//            JSONObject jsonObject = JSONUtil.parseObj();

            WxMsgResponseDTO<JSONObject> wxMsgResponseDTO = JSONUtil.toBean(data, WxMsgResponseDTO.class);
            if (wxMsgResponseDTO.getSuccess()) {
                if (responseClazz == String.class) {
                    return (T) wxMsgResponseDTO.getMessage();
                }
                return JSONUtil.toBean(wxMsgResponseDTO.getMessage(), responseClazz);
            } else {
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "发送消息失败，响应结果：" + data);
            }

        } else {
            throw new Exception("发送消息失败，响应状态码：" + response.getStatus());
        }
    }

    public static void main(String[] args) {
        String message = "我是美味不确定的魔魔胡胡胡萝卜\uD83E\uDD55，请输入以下文字与我对话吧~\nps：括号内为指令说明\n\n-----基础功能-----\n- 。帮助\n- 。激活群组\n- 签到\n- @魔魔胡胡胡萝卜\uD83E\uDD55 [聊天内容] （群聊艾特我并输入聊天内容进行AI聊天）\n\n-----NSO功能-----\n- nso登录\n- 我的NSO信息\n- 最近一场打工\n- 最近一场比赛\n\n-----喷喷功能-----\n格、真格\n工、打工\n涂、涂地\nX赛、x赛\n\n--------------------\n有任何问题请联系wx：LMayday99\n";
        System.out.println(chinaToUnicode(message));
    }

    /**
     * 中文转Unicode
     *
     * @param str 字符串
     * @return String
     */
    public static String chinaToUnicode(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
//            if (isChinese(c) || isJapanese(c)) {
            if (c != '\\' && !isEnglishOrDigitOrSymbolOrNewLine(c)) {
                // 不为数字、字母、英文符号、回车符的需要转换成unicode编码，否则发送微信文本出现乱码
                result.append(String.format("\\u%04X", (int) c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static boolean isAlphaNumeric(char ch) {
        // 正则表达式匹配英文字母或数字
        return String.valueOf(ch).matches("[a-zA-Z0-9]");
    }


    public static boolean isEnglishOrDigitOrSymbolOrNewLine(char ch) {
        // 正则表达式匹配英文字母、数字、常见的英文符号以及回车符
        // 常见的英文符号包括空格、标点等，可以根据需要进行扩展
        String regex = "[a-zA-Z0-9 \\t\n\\r\\f\\v\\'!\"#$%&\\(\\)*+,\\-\\./:;<=>?@\\[\\]\\^_`{|}~]+";
        return String.valueOf(ch).matches(regex);
    }


    /**
     * 判断是否中文
     *
     * @param c 字符
     * @return boolean
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }


    private static boolean isJapanese(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || block == Character.UnicodeBlock.HIRAGANA
            || block == Character.UnicodeBlock.KATAKANA
            || block == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
            // 其他可能包含日文字符的Unicode块可以在这里添加
            ;
    }
}