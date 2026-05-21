package com.mayday9.splatoonbot.business.service.wxmsg.receive.game;

import com.mayday9.splatoonbot.business.dto.game.IdiomGameState;
import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;
import com.mayday9.splatoonbot.business.service.game.IdiomGameService;
import com.mayday9.splatoonbot.business.service.wxmsg.send.TextWxMsgSender;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 成语接龙微信消息服务
 */
@Slf4j
@WxMsgType(value = WxMsgConstant.IDIOM_GAME, desc = "成语接龙游戏")
@Component
public class IdiomGameWxMsgService extends PaipaiWxMsgStrategy {

    @Resource
    private IdiomGameService idiomGameService;

    @Resource
    private TextWxMsgSender textWxMsgSender;

    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        if (!wechatMessage.getWxid().contains("@chatroom")) {
            textWxMsgSender.sendTextMessage("成语接龙仅支持群聊，请在群组中使用！", wechatMessage.getWxid(), null, false);
            return;
        }

        String content = wechatMessage.getContent().trim();
        String groupId = wechatMessage.getWxid();
        String playerWxid = wechatMessage.getTalker();
        String playerName = wechatMessage.getTalker();

        if (StringUtils.isEmpty(playerName)) {
            playerName = "玩家";
        }

        if (WxMsgConstant.IDIOM_GAME.equals(content)) {
            // 开始游戏
            startGame(groupId, playerWxid, playerName);
        } else if ("退出".equals(content)) {
            // 退出游戏
            endGame(groupId, playerWxid);
        } else {
            // 接龙答案
            processAnswer(groupId, playerWxid, content);
        }
    }

    private void startGame(String groupId, String playerWxid, String playerName) throws Exception {
        IdiomGameState state = idiomGameService.startGame(groupId, playerWxid, playerName);

        if (state.getStatus() == com.mayday9.splatoonbot.common.enums.IdiomGameStatusEnum.PLAYING
                && StringUtils.hasLength(state.getCurrentIdiom())) {
            // 有游戏在进行（可能是新游戏或已有游戏）
            String startMsg = String.format("🎮 游戏开始！让我来考考你～\n%s\n请接龙（发送\"退出\"可结束游戏）", state.getCurrentIdiom());
            textWxMsgSender.sendTextMessage(startMsg, groupId, null, false);
        } else {
            // 已有游戏在进行
            textWxMsgSender.sendTextMessage("本群已有一局游戏在进行中，请等待结束！", groupId, null, false);
        }
    }

    private void processAnswer(String groupId, String playerWxid, String content) throws Exception {
        IdiomValidationResult result = idiomGameService.processAnswer(groupId, content);

        if (result.isValid()) {
            String correctMsg = String.format("✅ 正确！👏\n%s\n%s\n请继续接龙~", result.getNextIdiom(), result.getExplanation());
            textWxMsgSender.sendTextMessage(correctMsg, groupId, null, false);
        } else {
            String wrongMsg = String.format("❌ 错误：%s", result.getFeedback());
            textWxMsgSender.sendTextMessage(wrongMsg, groupId, null, false);
        }
    }

    private void endGame(String groupId, String playerWxid) throws Exception {
        IdiomGameState state = idiomGameService.quitGame(groupId);

        if (state == null) {
            textWxMsgSender.sendTextMessage("游戏未开始，无需退出！", groupId, null, false);
            return;
        }

        String endMsg = String.format("🏁 游戏结束！\n玩家：%s\n连续答对：%d 题\n太棒了！🎉", state.getPlayerName(), state.getConsecutiveCorrect());
        textWxMsgSender.sendTextMessage(endMsg, groupId, null, false);
    }
}