# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A WeChat bot for Splatoon 3 that provides game schedule information, user statistics, and AI chat functionality. The bot integrates with:
- **派派WeChat (Paipai WeChat)** framework for WeChat message interception
- **splatoon3.ink** API for game schedule data
- **DeepSeek/VolcEngine** for AI chat functionality
- **Nintendo Switch Online (NSO)** API for user game statistics

## Architecture

### Message Flow
```
WeChat -> 派派WeChat App -> Netty Server (port 9933) -> Strategy Pattern -> Business Logic
                                                          -> Paipai API (port 7717) -> WeChat
```

### Core Components

1. **Netty Server** (`PaipaiWechatNettyServer`): Listens on port 9933 for WeChat messages from 派派
2. **Strategy Pattern** (`PaipaiWxMsgStrategy`): Each command keyword maps to a strategy implementation via `@WxMsgType` annotation
3. **Strategy Context** (`PaipaiWxMsgStrategyContext`): Auto-registers all strategy beans and routes messages by keyword
4. **Scheduled Tasks**: `SplatDataRefreshTask` fetches Splatoon 3 data from splatoon3.ink API

### Directory Structure
- `src/main/java/com/mayday9/splatoonbot/netty/` - Netty server, channel, handler, strategy
- `src/main/java/com/mayday9/splatoonbot/business/` - Business logic layer
  - `service/wxmsg/receive/` - Message handling strategies (basic, nso, splatoon, system)
  - `service/wxmsg/send/` - WeChat message senders
  - `service/ai/` - AI chat services (Baidu Qianfan, DeepSeek)
  - `task/` - Scheduled tasks for data refresh
  - `infrastructure/mapper/` - MyBatis mappers
- `src/main/resources/mapper/` - MyBatis XML mappers

## Build and Run

### Prerequisites
- JDK 8+
- Maven 3.x
- MySQL 8.x
- Redis

### Development Commands
```bash
# Build the project
mvn clean package

# Run the application (dev profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Generate WAR for deployment
mvn clean package -DskipTests
```

### Configuration
- `application.yml` - Main configuration (port 9941)
- `application-dev.yml` - Development settings (database, Redis, API keys)
- `application-prod.yml` - Production settings

## Adding New Commands

To add a new WeChat command:

1. Add command keyword to `WxMsgConstant.java`
2. Create a strategy class extending `PaipaiWxMsgStrategy`:
   ```java
   @WxMsgType(value = WxMsgConstant.YOUR_COMMAND, desc = "Description")
   @Component
   public class YourCommandWxMsgService extends PaipaiWxMsgStrategy {
       @Override
       @AuthWxMsg  // Optional: requires group activation
       public void doBusiness(WechatMessage wechatMessage) throws Exception {
           // Your logic here
       }
   }
   ```
3. The strategy is auto-registered by `PaipaiWxMsgStrategyContext` on startup

## Command Keywords

| Keyword | Description |
|---------|-------------|
| `祭典` | Splatfest schedules |
| `工` / `打工` | Salmon Run schedules |
| `格` / `真格` | Anarchy Battle schedules |
| `X赛` | X Battle schedules |
| `涂` / `涂地` | Regular Battle schedules |
| `签到` | Daily check-in |
| `。激活群组` | Activate group (required before other commands) |
| `@魔魔胡胡胡萝卜[emoji=D83E][emoji=DD55] ` | AI chat (private message only) |
| `nso登录` | NSO authentication URL |
| `npf` | NSO login with session token |
| `。龙王` | Today's chat statistics |
| `。当月未发言人员` | Inactive users this month |

## Paipai API Integration

The bot uses 派派WeChat HTTP API for sending messages:
- API endpoint: `http://{host}:{port}/`
- Authentication via `paipai.server.token`
- Message senders in `service/wxmsg/send/` support different message types (text, image, file, card, etc.)

## Data Persistence

- **MyBatis-Plus** for ORM
- **Druid** for connection pooling
- **Redis** for caching (chat history, AI context)
- Entity classes in `business/entity/`
- All database operations go through mapper interfaces

## Important Notes

- Groups must be activated with `。激活群组` before using most commands
- AI chat only works in private messages (not group chats)
- All group messages are tracked for statistics regardless of command usage
- Netty runs async via `@Async` annotation
- Thread pool handles concurrent message processing with custom rejection policy
