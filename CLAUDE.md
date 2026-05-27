## 项目概述

面向 Splatoon 3 的微信机器人，提供游戏赛程查询、用户统计和 AI 聊天功能。

## 架构

### 消息流程
```
微信 -> 派派WeChat App -> Netty Server (9933) -> 策略模式 -> 业务逻辑 -> Paipai API (7717) -> 微信
```

### 核心组件

1. **WebSocket 客户端** (`PaipaiWebSocketClient`)：连接派派框架 WSS，接收微信消息
2. **策略模式** (`PaipaiWxMsgStrategy`)：每个命令关键词通过 `@WxMsgType` 注解映射到对应策略实现
3. **策略上下文** (`PaipaiWxMsgStrategyContext`)：启动时自动注册所有策略 Bean，按关键词路由消息
4. **定时任务**：`SplatDataRefreshTask` 从 splatoon3.ink API 获取 Splatoon 3 数据

### 目录结构

- `websocket/` - WebSocket 客户端及相关处理器
- `netty/` - 保留内部服务（如 WebSocketServer）
- `business/service/wxmsg/receive/` - 消息处理策略（basic、nso、splatoon、system）

## 构建和运行

### 前置条件
- JDK 8+
- Maven 3.x
- MySQL 8.x
- Redis

### 开发命令
```bash
mvn clean package
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn test
mvn clean package -DskipTests
```

### 配置

- `application.yml` - 主配置（端口 9941）
- `application-dev.yml` - 开发环境（数据库、Redis、API 密钥）
- `application-prod.yml` - 生产环境

## 添加新命令

1. 在 `WxMsgConstant.java` 中添加命令关键词
2. 创建继承自 `PaipaiWxMsgStrategy` 的策略类：

```java

@WxMsgType(value = WxMsgConstant.YOUR_COMMAND, desc = "描述")
@Component
public class YourCommandWxMsgService extends PaipaiWxMsgStrategy {
    @Override
    @AuthWxMsg  // 可选：需要群组激活
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        // 你的逻辑
    }
}
```

3. 启动时自动注册

## 派派框架集成

通过 **WebSocket 客户端** 连接派派框架（WSS://host:8000），接收微信消息事件。

### 流程
1. 连接 WSS → 发送应用注册（Type:1000）→ 接收 CID（Type:999）→ 接收消息（Type:1001）
2. 调用框架 API：通过 POST 请求发送消息

### 组件
- `websocket/client/PaipaiWebSocketClient.java` - WebSocket 客户端
- `websocket/handler/PaipaiWsMsgHandler.java` - 业务消息处理器

## 数据持久化

- **MyBatis-Plus** 用于 ORM
- **Druid** 用于连接池
- **Redis** 用于缓存（聊天记录、AI 上下文）
- 实体类位于 `business/entity/`

## 重要约束

- Netty 通过 `@Async` 异步运行，自定义线程池拒绝策略
- **敏感数据禁止写入 `settings.json`**：数据库密码、API 密钥等敏感信息只能放在 `settings.local.json`（已被 .gitignore 忽略，不会提交到仓库）
- 本地启动服务时，profile.active为dev
- **调试后必须关闭服务**：启动服务测试后，使用 `taskkill //F //PID <PID>` 关闭进程，避免端口占用导致下次启动失败