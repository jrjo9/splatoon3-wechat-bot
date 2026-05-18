# 新建 WebSocket 业务类

## 架构

```
Client -> WebSocketServer (port 3910) -> WebSocketHandler -> WebSocketStrategyContext -> Strategy -> Business Logic
                                              |
                                         ConnectionManager (sessionId <-> Channel)
                                              |
                                         WebSocketMessageUtil (server push)
```

## 关键类说明

| 类 | 位置 | 职责 |
|----|------|------|
| `WebSocketServerStarter` | `netty/` | Spring Boot 启动入口，`CommandLineRunner` |
| `WebSocketServer` | `netty/server/` | Netty 服务端，监听 WebSocket 端口 |
| `WebSocketChannelInitializer` | `netty/channel/` | Channel 初始化（HTTP编解码 → 心跳 → WebSocket协议 → 业务Handler） |
| `WebSocketHandler` | `netty/handler/` | 消息分发：解析 JSON → 注册连接 → 路由到策略；断连时清 Redis 会话 |
| `WebSocketStrategyContext` | `netty/strategy/` | 收集所有 `WebSocketStrategy` bean，按 `getType()` 路由 |
| `ConnectionManager` | `netty/manager/` | 管理 sessionId ↔ Channel 映射，支持查找/广播/单发 |
| `WebSocketMessageUtil` | `netty/manager/` | 服务端主动推送工具，可注入到任意 Bean 使用 |
| `AbstractWebSocketStrategy` | `netty/strategy/` | 模板基类，提供 `sendTextMessage()` + 统一异常兜底 |
| `WebSocketMessage` | `netty/model/` | 消息模型：method / sessionId / param / heartbeat / timestamp |

## 消息协议

客户端发送 JSON：

```json
{
  "method": "chat",
  "sessionId": "772ff2f9-161e-4e70-8d9e-318c5b37ec1d",
  "param": { "Type": 1, "Content": "你好", "Wxid": "...", "Talker": "..." },
  "heartbeat": false,
  "timestamp": 1778751983000
}
```

- `method` — 对应策略的 `getType()`，用于路由到具体策略
- `sessionId` — 客户端生成的 32 位 UUID，首次消息时注册到 `ConnectionManager`
- `param` — 业务参数，Jackson 解析为 `LinkedHashMap`；若转为 `WechatMessage` 需用 hutool `JSONUtil`（原生支持 `@Alias` 注解），不可用 Jackson `convertValue`
- `heartbeat` — `true` 时直接跳过业务处理

## 新建业务流程

### 1. 创建策略类

继承 `AbstractWebSocketStrategy`，标注 `@WsMsgType` 和 `@Component`：

```java
package com.mayday9.splatoonbot.business.service.ws;

import cn.hutool.json.JSONUtil;
import com.mayday9.splatoonbot.common.dto.WechatMessage;
import com.mayday9.splatoonbot.netty.annotation.WsMsgType;
import com.mayday9.splatoonbot.netty.model.WebSocketMessage;
import com.mayday9.splatoonbot.netty.strategy.AbstractWebSocketStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WsMsgType(type = "yourMethod", desc = "功能描述")
public class YourWebSocketStrategy extends AbstractWebSocketStrategy {

    @Override
    protected void doBusiness(ChannelHandlerContext ctx, WebSocketMessage message) throws Exception {
        // 1. 解析 param（如需 WechatMessage）
        WechatMessage wechatMessage = parseWechatMessage(message);
        String content = wechatMessage.getContent();

        // 2. 业务逻辑
        String response = doSomething(content);

        // 3. 发送响应
        sendTextMessage(ctx, response);
    }

    @Override
    public String getType() {
        return "yourMethod";
    }

    // param -> WechatMessage（hutool 支持 @Alias 映射）
    private WechatMessage parseWechatMessage(WebSocketMessage message) {
        String paramJson = JSONUtil.toJsonStr(message.getParam());
        return JSONUtil.toBean(paramJson, WechatMessage.class);
    }
}
```

### 2. 自动注册

策略类上的 `@Component` 会让 Spring 自动创建 bean，`WebSocketStrategyContext` 在启动时收集所有 `WebSocketStrategy` 实现并按 `getType()` 注册到路由表，无需额外配置。

### 3. 客户端调用

客户端发送 `{"method": "yourMethod", "sessionId": "...", "param": {...}}`，`WebSocketHandler` 解析后根据 `method` 字段路由到对应策略。

## 服务端主动推送

使用 `WebSocketMessageUtil`，可注入到任意 Spring Bean：

```java
@Resource
private WebSocketMessageUtil wsMsgUtil;

// 发给指定会话
wsMsgUtil.sendText(sessionId, "通知内容");

// 批量发送
wsMsgUtil.sendText(Arrays.asList("id1", "id2"), "通知内容");

// 广播所有连接
wsMsgUtil.broadcastText("系统通知");

// 发结构化消息（带 method 路由）
wsMsgUtil.sendMessage(sessionId, "notice", Map.of("title", "...", "time", "..."));

// 广播结构化消息
wsMsgUtil.broadcastMessage("notice", Map.of("title", "...", "time", "..."));

// 查询在线状态
boolean online = wsMsgUtil.isOnline(sessionId);
int count = wsMsgUtil.getOnlineCount();
```

## 注意事项

- `param` 解析为 `WechatMessage` 时必须用 hutool `JSONUtil`，不可用 Jackson `JsonUtil.convertValue`（Jackson 不认识 `@Alias` 注解，会导致字段为 null）
- 断连时 `WebSocketHandler.channelInactive` 会自动清理 Redis 中 `chatSession:user:id:{sessionId}` 的会话记录
- 会话 Redis key 格式：`chatSession:user:id:{sessionId}`（私聊），15 分钟空闲过期
- 基类 `handle()` 已封装 try-catch 异常兜底，`doBusiness` 中如需自定义错误信息需自行捕获
