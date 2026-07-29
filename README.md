# alpJom API

> alpJom 益智小遊戲網站的 REST API 服務

[前端展示頁](https://0nn7a.github.io/alpjom-web/) · [前端原始碼](https://github.com/0nn7a/alpjom-web)

此服務為 alpJom 提供帳號驗證、遊戲流程、個人資料、戰績分享與社群互動等 API。

## 功能

- 使用者註冊、登入、登出與 JWT Token 換發
- 每日 Wordle 謎題與練習模式
- EASY、NORMAL、HARD 三種遊戲難度
- 進行中遊戲續玩與完成紀錄查詢
- 公開戰績分享
- 戰績按讚與留言
- 個人檔案、頭像、追蹤／粉絲關係
- 遊玩完成熱力圖與近期戰績
- 首次啟動時自動匯入內建 Wordle 單字庫

## 技術

| 類別 | 使用技術 |
| --- | --- |
| 語言與框架 | Java 21、Spring Boot 4 |
| 資料庫 | MySQL |
| 資料存取 | MyBatis-Plus、PageHelper |
| 身分驗證 | JWT Access Token + Refresh Token（支援持久化與撤銷） |
| 物件儲存 | Cloudflare R2、AWS S3 SDK |
| 建置工具 | Maven Wrapper |

## 系統設計

```text
Vue 3 前端
    │
    │ REST API / JWT
    ▼
Spring Boot API
    ├── MySQL：使用者、遊戲、留言、按讚與追蹤資料
    └── Cloudflare R2：使用者頭像檔案
```

## API 範圍

| 類別 | 主要端點 |
| --- | --- |
| 認證 | `/auth/register`、`/auth/login`、`/auth/logout`、`/auth/refresh` |
| Wordle 遊戲 | `/wordle/start`、`/wordle/guess`、`/wordle/game/{recordId}` |
| 分享與互動 | `/wordle/share/{shareToken}`、`/wordle/like/{shareToken}`、`/wordle/comment` |
| 個人檔案 | `/profile`、`/profile/avatar`、`/profile/follow/*` |
| 遊戲統計 | `/game/count/finished`、`/game/record/finished/{username}` |

需要登入的受保護 API 使用下列 Headers：

```http
Authorization: Bearer <access-token>
X-Refresh-Token: <refresh-token>
```

## 專案結構

```text
src/main/
├── java/com/ternura/
│   ├── controller/  # REST API 端點
│   ├── service/     # 商業邏輯
│   ├── mapper/      # MyBatis 資料存取
│   ├── model/       # Entity、DTO、VO、Enum
│   ├── config/      # CORS、JWT、Jackson、R2 設定
│   └── init/        # Wordle 單字庫初始化
└── resources/
    ├── wordle/      # 答案與可猜測單字表
    └── application.yml
```

## 開發備註

本專案目前作為個人作品部署使用，未提供資料庫 schema、Cloudflare R2 憑證與完整自架流程。

應用程式使用 MySQL 儲存帳號、遊戲紀錄與互動資料；使用 Cloudflare R2 儲存使用者頭像。敏感設定透過環境變數注入，不會提交至版本控制。

## 相關專案

- [alpJom Web](https://github.com/0nn7a/alpjom-web)
- [前端展示頁](https://0nn7a.github.io/alpjom-web/)

## 作者

[@0nn7a](https://github.com/0nn7a)
