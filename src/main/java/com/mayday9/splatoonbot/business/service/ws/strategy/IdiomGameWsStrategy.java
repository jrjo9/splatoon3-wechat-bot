package com.mayday9.splatoonbot.business.service.ws.strategy;

import com.mayday9.splatoonbot.business.dto.game.IdiomGameState;
import com.mayday9.splatoonbot.business.dto.game.IdiomValidationResult;
import com.mayday9.splatoonbot.business.service.game.IdiomGameService;
import com.mayday9.splatoonbot.business.service.ws.dto.WsBusinessContext;
import com.mayday9.splatoonbot.business.service.ws.router.WsBusinessStrategy;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.netty.manager.TextMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * WebSocket成语接龙业务策略
 */
@Slf4j
@Component
public class IdiomGameWsStrategy implements WsBusinessStrategy {

    @Resource
    private IdiomGameService idiomGameService;

    @Override
    public boolean supports(String content) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        String trimmed = content.trim();
        return trimmed.equals(WxMsgConstant.IDIOM_GAME) ||
               trimmed.equals("退出") ||
               (trimmed.length() == 4 && !trimmed.startsWith(WxMsgConstant.IDIOM_GAME));
    }

    @Override
    public void handle(WsBusinessContext context) throws Exception {
        String trimmed = context.getContent().trim();
        String sessionId = context.getSessionId();
        ChannelHandlerContext ctx = context.getCtx();

        if (trimmed.equals(WxMsgConstant.IDIOM_GAME)) {
            startGame(ctx, sessionId);
        } else if (trimmed.equals("退出")) {
            quitGame(ctx, sessionId);
        } else if (trimmed.length() == 4) {
            processAnswer(ctx, sessionId, trimmed);
        } else {
            sendMessage(ctx, "请发送\"成语接龙\"开始游戏，或发送4字成语接龙。");
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, String text) {
        ctx.writeAndFlush(new TextMessage(text));
    }

    private void startGame(ChannelHandlerContext ctx, String sessionId) throws Exception {
        IdiomGameState state = idiomGameService.startGame(sessionId, sessionId, "玩家");

        if (state.getStatus() == com.mayday9.splatoonbot.common.enums.IdiomGameStatusEnum.PLAYING) {
            String startMsg = String.format("🎮 游戏开始！让我来考考你～\n%s\n请接龙（回复\"退出\"可结束游戏）", state.getCurrentIdiom());
            sendMessage(ctx, startMsg);
        } else {
            sendMessage(ctx, "已有游戏在进行中，请先回复\"退出\"结束当前游戏！当前成语：" + state.getCurrentIdiom());
        }
    }

    private void processAnswer(ChannelHandlerContext ctx, String sessionId, String answer) throws Exception {
        IdiomValidationResult result = idiomGameService.processAnswer(sessionId, answer);

        if (result.isValid()) {
            String correctMsg = String.format("✅ 正确！👏\n%s\n%s\n请继续接龙~", result.getNextIdiom(), result.getExplanation());
            sendMessage(ctx, correctMsg);
        } else {
            IdiomGameState state = idiomGameService.getGameState(sessionId);
            String currentIdiom = state != null ? state.getCurrentIdiom() : "";
            String wrongMsg = String.format("❌ 错误：%s\n请重新接龙，当前成语是：%s", result.getFeedback(), currentIdiom);
            sendMessage(ctx, wrongMsg);
        }
    }

    private void quitGame(ChannelHandlerContext ctx, String sessionId) throws Exception {
        IdiomGameState state = idiomGameService.quitGame(sessionId);

        if (state == null) {
            sendMessage(ctx, "游戏未开始，无需退出！");
            return;
        }

        String endMsg = String.format("🏁 游戏结束！\n玩家：%s\n连续答对：%d 题\n太棒了！🎉", state.getPlayerName(), state.getConsecutiveCorrect());
        sendMessage(ctx, endMsg);
    }
}