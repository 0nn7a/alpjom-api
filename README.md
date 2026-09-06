# alpJom API

> alpJom 益智小遊戲網站的 REST API 服務

[前端展示頁](https://0nn7a.github.io/alpjom-web/) · [API 服務健檢](https://alpjom.duckdns.org/actuator/health) · [前端原始碼](https://github.com/0nn7a/alpjom-web)

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
| 身分驗證  | Spring Security Filter Chain + JWT Access Token / Refresh Token（支援持久化與撤銷） |
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

## 部署

線上服務：**https://alpjom.duckdns.org**

| 項目 | 內容 |
| --- | --- |
| 主機 | Oracle Cloud Infrastructure，Ampere A1（ARM64）／ Ubuntu 24.04 |
| 容器化 | Docker Compose 管理 Nginx、Spring Boot、MySQL 三個服務 |
| 對外 | Nginx 反向代理，Let's Encrypt 憑證（webroot 驗證，自動續約並重啟容器套用） |
| 網域 | DuckDNS ＋ OCI 保留公用 IP |

```
網際網路
    │ HTTPS :443
    ▼
Nginx（反向代理、TLS 終結）
    │ HTTP :8080（僅 Docker 內部網路）
    ▼
Spring Boot 容器
    │ :3306（僅 Docker 內部網路）
    ▼
MySQL 容器
```

- **Multi-stage build**：最終映像檔不含 Maven 與原始碼
- **最小暴露面**：後端與資料庫皆無對外連接埠，外部僅能經由 Nginx 存取 443
- **兩層防火牆**：OCI 安全清單（雲端層）＋ iptables（作業系統層）
- **啟動順序控制**：MySQL healthcheck 通過後才啟動後端
- **維運端點**：Actuator 僅開放 `/actuator/health`，其餘於 Nginx 與應用層雙重阻擋
- **憑證自動化**：certbot webroot 模式續約，deploy hook 自動重啟 Nginx 容器套用新憑證
- **資料庫備份策略**：cron 排程每日自動備份、壓縮並輪替，保留最近 30 份版本

健康檢查：

```bash
curl https://alpjom.duckdns.org/actuator/health
# {"status":"UP"}
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
│   ├── config/      # Spring Security、CORS、Jackson、R2 設定
│   ├── security/    # JWT 驗證 Filter、身分驗證失敗處理
│   └── init/        # Wordle 單字庫初始化
└── resources/
    ├── wordle/      # 答案與可猜測單字表
    └── application.yml
```

## 開發備註

本專案為個人作品，已完成雲端部署並持續運行中。原始碼公開，但不提供資料庫 schema 與 Cloudflare R2 憑證及自架流程。

應用程式使用 MySQL 儲存帳號、遊戲紀錄與互動資料；使用 Cloudflare R2 儲存使用者頭像。敏感設定透過環境變數注入，不會提交至版本控制。

## 相關專案

- [alpJom Web](https://github.com/0nn7a/alpjom-web)
- [前端展示頁](https://0nn7a.github.io/alpjom-web/)

## 作者

[@0nn7a](https://github.com/0nn7a)
