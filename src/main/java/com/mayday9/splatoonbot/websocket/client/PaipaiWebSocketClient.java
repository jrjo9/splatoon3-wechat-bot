package com.mayday9.splatoonbot.websocket.client;

import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.common.dto.PaipaiWxEventMessage;
import com.mayday9.splatoonbot.websocket.handler.PaipaiWsMsgHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 派派框架 WebSocket 客户端
 *
 * @author Lianjiannan
 * @since 2024/9/10
 **/
@Slf4j
@Component
public class PaipaiWebSocketClient {

    @Value("${paipai.server.host:127.0.0.1}")
    private String host;

    @Value("${paipai.server.wsPort:8000}")
    private int wsPort;

    @Value("${paipai.server.apiPort:7717}")
    private int apiPort;

    @Value("${paipai.server.token:}")
    private String token;

    @Value("${paipai.server.appInfo.name:splatoon3-wechat-bot}")
    private String appName;

    @Value("${paipai.server.appInfo.author:Lianjiannan}")
    private String appAuthor;

    @Value("${paipai.server.appInfo.version:1.0.0}")
    private String appVersion;

    @Value("${paipai.server.appInfo.appIcon:}")
    private String appIcon;

    @Value("${paipai.server.appInfo.description:}")
    private String appDescription;

    @Value("${paipai.server.appInfo.file:}")
    private String appFile;

    @Value("${paipai.server.appInfo.appid:}")
    private String appId;

    @Value("${paipai.server.appInfo.sensitive:0}")
    private Integer appSensitive;

    @Value("${paipai.server.enabled:true}")
    private boolean enabled;

    @Autowired
    private PaipaiWsMsgHandler paipaiWsMsgHandler;

    private Channel channel;
    private EventLoopGroup group;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private PaipaiWebSocketClientHandler handler;
    private volatile boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    /**
     * CID (应用唯一标识)，从框架获取
     */
    private Integer cid;

    public Integer getCid() {
        return cid;
    }

    public String getHost() {
        return host;
    }

    public int getApiPort() {
        return apiPort;
    }

    public String getToken() {
        return token;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    /**
     * 启动 WebSocket 客户端连接
     */
    public void start() {
        if (!enabled) {
            log.info("派派 WebSocket 客户端未启用");
            return;
        }

        log.info("正在连接派派框架 WSS://{}:{}", host, wsPort);

        group = new NioEventLoopGroup();

        try {
            URI uri = new URI("wss://" + host + ":" + wsPort);
            boolean isSsl = "wss".equalsIgnoreCase(uri.getScheme());

            handler = new PaipaiWebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                ),
                this
            );

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("initChannel: {}", ch);
                        ChannelPipeline pipeline = ch.pipeline();
                        if (isSsl) {
                            SslContext sslContext = SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                            pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
                        }
                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast("ws-handler", handler);
                    }
                });

            log.info("开始连接...");
            ChannelFuture future = bootstrap.connect(host, wsPort);
            future.addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    log.info("TCP 连接成功! channel: {}", f.channel());
                    channel = f.channel();
                } else {
                    log.error("TCP 连接失败: {}", f.cause());
                    f.channel().close();
                }
            });

            // 等待连接完成
            if (!future.await(30, TimeUnit.SECONDS)) {
                log.error("连接超时!");
                return;
            }

            if (!future.isSuccess()) {
                log.error("连接失败: {}", future.cause());
                return;
            }

            channel = future.channel();
            log.info("连接成功, channel: {}", channel);

            // 等待握手完成
            log.info("等待握手完成...");
            if (!handler.handshakeFuture().await(30, TimeUnit.SECONDS)) {
                log.error("握手超时!");
                // 检查握手状态
                log.info("握手状态 - isHandshakeComplete: {}", handler.handshaker().isHandshakeComplete());
                return;
            }

            log.info("握手完成!");
            connected = true;

        } catch (Exception e) {
            log.error("派派 WebSocket 连接失败", e);
        }
    }

    /**
     * 发送应用注册信息
     */
    public void sendAppRegister() {
        PaipaiWxEventMessage registerMsg = new PaipaiWxEventMessage();
        registerMsg.setType(1000);
        registerMsg.setToKen(token);
        registerMsg.setName(appName);
        registerMsg.setAuthor(appAuthor);
        registerMsg.setVer(appVersion);
        registerMsg.setAppIcon(appIcon);
        registerMsg.setDescription(appDescription);
        registerMsg.setCreationtime("2024-09-21");
        registerMsg.setUpdatetime("2024-09-23");
        registerMsg.setFile(appFile);
        registerMsg.setAppid(appId);
        registerMsg.setSensitive(appSensitive);

        String json = JSONUtil.toJsonStr(registerMsg);
        sendMessage(json);
        log.info("已发送应用注册信息");
    }

    /**
     * 发送 WebSocket 消息
     */
    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            log.info("发送 WebSocket 消息: {}", message);
        } else {
            log.warn("WebSocket 通道未激活，无法发送消息");
        }
    }

    /**
     * 启动心跳
     */
    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (channel != null && channel.isActive()) {
                log.debug("发送心跳...");
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 处理接收到的消息
     */
    public void handleMessage(String message) {
        log.info("收到 WebSocket 消息: {}", message);
        paipaiWsMsgHandler.handleMessage(message, this);
    }

    /**
     * 握手完成后调用，发送注册信息
     */
    public void onHandshakeComplete() {
        log.info("握手完成，发送应用注册信息");
        sendAppRegister();
        startHeartbeat();
    }

    /**
     * 停止客户端
     */
    @PreDestroy
    public void stop() {
        log.info("停止派派 WebSocket 客户端");
        connected = false;
        scheduler.shutdown();
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}