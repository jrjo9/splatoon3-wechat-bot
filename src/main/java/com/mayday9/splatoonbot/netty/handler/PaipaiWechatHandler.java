package com.mayday9.splatoonbot.netty.handler;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.StringUtils;
import com.mayday9.splatoonbot.common.constant.WxMsgConstant;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.common.enums.WechatMessageTypeEnum;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategyContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lianjiannan
 * @since 2024/9/10 14:06
 **/
@Component
@ChannelHandler.Sharable
@Slf4j
public class PaipaiWechatHandler extends ChannelInboundHandlerAdapter {


    @Resource
    private PaipaiWxMsgStrategyContext paipaiWxMsgStrategyContext;

    private static final ExecutorService executor;

    static {
        int numberOfCPUCores = Runtime.getRuntime().availableProcessors();
        log.info("numberOfCPUCores:{}", numberOfCPUCores);
        executor = new ThreadPoolExecutor(numberOfCPUCores * 2,
                numberOfCPUCores * 2,
                0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000),
                (r, executor) -> {
                    // 重新塞回队列
                    try {
                        log.debug("线程池拒绝策略..., 准备重新提交");
                        // 等待1秒
                        Thread.sleep(1000);
                        // 重新提交任务到队列
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                }
        );
    }

    /**
     * 设备连接时候触发
     *
     * @param ctx
     * @return void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        log.info("派派监听：触发ACTIVE 连接激活...{}", ch.remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        log.info("派派监听：触发INACTIVE 连接失效...{}", ch.remoteAddress());
        super.channelInactive(ctx);
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel add......");
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel remove......");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (HttpMethod.POST.equals(request.method())) {
                ctx.channel().read();
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            String dataJsonStr = buf.toString(io.netty.util.CharsetUtil.UTF_8);
            System.out.println("Received POST request with body: " + dataJsonStr);
            // 业务
            // 解析JSON串成Java对象
            WechatMessage wechatMessage = JSONUtil.toBean(dataJsonStr, WechatMessage.class);
            if (WechatMessageTypeEnum.TEXT.equals(wechatMessage.getType()) && wechatMessage.getSender() == 0) {
                // 文本消息
                // 根据关键词执行对应的业务操作
                String textMsg = null;
                boolean iStartWith = false;
                for (String startWithPattern : WxMsgConstant.startWithPatterns) {
                    if (wechatMessage.getContent().startsWith(startWithPattern)) {
                        textMsg = startWithPattern;
                        iStartWith = true;
                        break;
                    }
                }
                if (!iStartWith) {
                    textMsg = wechatMessage.getContent();
                }
                String finalTextMsg = textMsg;
                executor.execute(() -> {
                    // 业务处理
                    PaipaiWxMsgStrategy wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(finalTextMsg);
                    if (wxMsgStrategy != null) {
                        try {
                            wxMsgStrategy.doStrategyBusiness(wechatMessage);
                            log.debug("执行文本[{}]成功！", finalTextMsg);
                        } catch (Exception e) {
                            log.error("执行文本[{}]失败！", finalTextMsg, e);
                        }
                    } else {
                        // 判断是否为私聊，私聊的话默认走AI聊天
                        if (!wechatMessage.getWxid().contains("@chatroom") && StringUtils.isEmpty(wechatMessage.getTalker())) {
                            wxMsgStrategy = paipaiWxMsgStrategyContext.getResource(WxMsgConstant.CHAT);
                            if (wxMsgStrategy != null) {
                                try {
                                    wxMsgStrategy.doStrategyBusiness(wechatMessage);
                                    log.debug("执行文本[{}]成功！", finalTextMsg);
                                } catch (Exception e) {
                                    log.error("执行文本[{}]失败！", finalTextMsg, e);
                                }
                            } else {
                                log.info("文本[{}]不为功能关键词，略过该文本...", finalTextMsg);
                            }
                        } else {
                            log.info("文本[{}]不为功能关键词，略过该文本...", finalTextMsg);
                        }
                    }
                });
            }

            buf.release();
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
            ctx.close();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
