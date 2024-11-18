package com.mayday9.splatoonbot.business.service.wxmsg.receive.basic;

import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/11/15 14:26
 **/
@Slf4j
@WxMsgType(value = WxMsgConstant.HELP, desc = "帮助")
@Component
public class HelpWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = classLoader.getResourceAsStream("static/help.txt");
        if (inputStream != null) {
            // 创建一个BufferedReader读取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            // 按行读取内容
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } else {
            throw new UnsupportedOperationException("资源文件未找到");
        }
        String content = stringBuilder.toString();
        if (StringUtils.isEmpty(content)) {
            return;
        }
        List<String> wxIdList = new ArrayList<>();
        if (!StringUtils.isEmpty(wechatMessage.getTalker())) {
            wxIdList.add(wechatMessage.getTalker());
        }
        textWxMsgSender.sendTextMessage(content, wechatMessage.getWxid(), wxIdList, false);
    }
}
