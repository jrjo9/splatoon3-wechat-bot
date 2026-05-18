# 新API接口开发流程 (new-api)

## 项目概述

本项目是一个基于Spring Boot的Splatoon 3 WeChat机器人，使用Netty作为消息接收服务器，采用策略模式处理WeChat消息。支持两种类型的API接口：

1. **REST API接口** - 基于Spring MVC的RESTful API
2. **WeChat消息处理接口** - 基于策略模式的WeChat命令处理

## 架构概览

```
├── Controller层 (REST API)
│   ├── SplatController.java
│   ├── SignInController.java
│   └── 其他控制器...
├── Service层
│   ├── 业务服务接口
│   └── 业务服务实现
├── Netty层 (WeChat消息处理)
│   ├── PaipaiWechatNettyServer.java
│   ├── PaipaiWxMsgStrategy.java
│   └── 消息处理策略
└── 实体层
    └── 业务实体类
```

## 添加新API接口的完整流程

### 1. REST API接口开发流程

#### 步骤1: 创建Controller类
```bash
# 在 controller 目录下创建新的控制器
src/main/java/com/mayday9/splatoonbot/business/controller/
```

**示例代码:**
```java
package com.mayday9.splatoonbot.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/your-api-path")
public class YourController {
    
    // 添加你的API方法
    @PostMapping("/endpoint")
    public ResponseEntity<YourResponse> yourMethod(@RequestBody YourRequest request) {
        // 实现逻辑
        return ResponseEntity.ok(yourResponse);
    }
}
```

#### 步骤2: 创建Service接口
```bash
# 在 service 目录下创建服务接口
src/main/java/com/mayday9/splatoonbot/business/service/
```

**示例代码:**
```java
package com.mayday9.splatoonbot.business.service;

public interface YourService {
    YourResponse processRequest(YourRequest request);
}
```

#### 步骤3: 实现Service
```bash
# 在 service/impl 目录下创建服务实现
src/main/java/com/mayday9/splatoonbot/business/service/impl/
```

**示例代码:**
```java
package com.mayday9.splatoonbot.business.service.impl;

@Service
public class YourServiceImpl implements YourService {
    @Override
    public YourResponse processRequest(YourRequest request) {
        // 实现业务逻辑
        return new YourResponse();
    }
}
```

#### 步骤4: 依赖注入和测试
在Controller中注入Service并测试API。

### 2. WeChat消息处理接口开发流程

#### 步骤1: 添加命令关键词
在 `WxMsgConstant.java` 中添加新的命令关键词：
```java
public class WxMsgConstant {
    // 添加新的命令
    public static final String YOUR_COMMAND = "yourcommand";
}
```

#### 步骤2: 创建消息处理策略
```bash
# 在 service/wxmsg/receive/ 目录下创建新的策略类
src/main/java/com/mayday9/splatoonbot/business/service/wxmsg/receive/
```

**示例代码:**
```java
package com.mayday9.splatoonbot.business.service.wxmsg.receive;

import com.mayday9.splatoonbot.netty.strategy.PaipaiWxMsgStrategy;
import com.mayday9.splatoonbot.netty.annotation.WxMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@WxMsgType(value = WxMsgConstant.YOUR_COMMAND, desc = "Your command description")
@Component
public class YourCommandWxMsgService extends PaipaiWxMsgStrategy {
    
    @Override
    public void doBusiness(WechatMessage wechatMessage) throws Exception {
        // 实现消息处理逻辑
        String content = wechatMessage.getContent();
        String fromUser = wechatMessage.getFromUser();
        
        // 处理消息...
    }
}
```

#### 步骤3: 添加权限验证（可选）
如果需要权限验证，添加 `@AuthWxMsg` 注解：
```java
@AuthWxMsg
public class YourCommandWxMsgService extends PaipaiWxMsgStrategy {
    // ...
}
```

#### 步骤4: 测试和部署
- 启动应用测试WeChat命令
- 部署到生产环境

## 最佳实践

### 1. 代码规范
- 使用 `@RestController` 标记REST控制器
- 使用 `@WxMsgType` 标记WeChat消息处理类
- 遵循分层架构：Controller → Service → DAO
- 使用Lombok简化代码（@Slf4j, @Component等）

### 2. 异常处理
```java
try {
    // 业务逻辑
} catch (Exception e) {
    log.error("Error processing message", e);
    // 返回错误响应或发送错误消息
}
```

### 3. 日志记录
```java
log.info("Processing message from user: {}", wechatMessage.getFromUser());
log.debug("Message content: {}", wechatMessage.getContent());
log.error("Error occurred", e);
```

### 4. 参数校验
```java
@PostMapping("/endpoint")
public ResponseEntity<YourResponse> yourMethod(@Validated @RequestBody YourRequest request) {
    // 参数已校验
}
```

## 常见问题解决

### Q: 如何添加新的WeChat命令？
A: 按照WeChat消息处理流程，创建新的策略类并添加到 `WxMsgConstant.java`。

### Q: 如何处理复杂的业务逻辑？
A: 将业务逻辑拆分到Service层，Controller只负责请求转发和响应处理。

### Q: 如何进行权限验证？
A: 使用 `@AuthWxMsg` 注解进行组权限验证。

### Q: 如何测试API？
A: 使用Postman或curl测试REST API，使用WeChat测试消息处理。

## 开发命令参考

```bash
# 构建项目
mvn clean package

# 运行应用（开发环境）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 运行测试
mvn test

# 生成WAR包
mvn clean package -DskipTests
```

## 注意事项

1. **群组激活**: 大多数命令需要先执行 `。激活群组`
2. **AI聊天**: 仅支持私聊，不支持群聊
3. **数据刷新**: 使用定时任务自动刷新游戏数据
4. **异常处理**: 确保所有异常都有适当的处理

通过遵循以上流程和规范，你可以高效地添加新的API接口到Splatoon 3 WeChat机器人中。