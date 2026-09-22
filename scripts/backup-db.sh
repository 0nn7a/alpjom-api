#!/bin/bash
set -euo pipefail

# ===== 設定 =====
BACKUP_ROOT=/home/ubuntu/apps/alpjom-api/backups
KEEP_COUNT=30          # 只保留最近 30 份備份，超過的從最舊的開始刪
CONTAINER_NAME=alpjom-api-mysql-1
DB_NAME=alpjom
ENV_FILE=/home/ubuntu/apps/alpjom-api/.env

# ===== 前置檢查 1：容器是否正在執行 =====
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "[$(date)] ERROR: 容器 ${CONTAINER_NAME} 沒有在執行，略過本次備份" >&2
  exit 1
fi

# ===== 前置檢查 2：讀取 .env 裡的 DB_PASSWORD（找不到就明確報錯）=====
if ! grep -qE '^DB_PASSWORD=' "$ENV_FILE"; then
  echo "[$(date)] ERROR: 在 $ENV_FILE 找不到 DB_PASSWORD，中止備份" >&2
  exit 1
fi
export $(grep -E '^DB_PASSWORD=' "$ENV_FILE" | xargs)

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
MONTH_DIR="$BACKUP_ROOT/$(date +%Y-%m)"
mkdir -p "$MONTH_DIR"

FILENAME="alpjom_${TIMESTAMP}.sql.gz"
FILEPATH="$MONTH_DIR/$FILENAME"

echo "[$(date)] 開始備份..."

docker exec "$CONTAINER_NAME" mysqldump \
  -u root -p"$DB_PASSWORD" \
  --single-transaction \
  --set-gtid-purged=OFF \
  --default-character-set=utf8mb4 \
  --databases "$DB_NAME" | gzip > "$FILEPATH"

echo "[$(date)] 備份完成：$FILEPATH ($(du -h "$FILEPATH" | cut -f1))"

# ===== 輪替：只保留最近 KEEP_COUNT 份，其餘刪除 =====
echo "[$(date)] 檢查是否需要清理舊備份..."
find "$BACKUP_ROOT" -name "*.sql.gz" -printf '%T@ %p\n' \
  | sort -rn \
  | tail -n +$((KEEP_COUNT + 1)) \
  | cut -d' ' -f2- \
  | while read -r old_file; do
      echo "  刪除舊備份：$old_file"
      rm -f "$old_file"
    done

# 清掉變成空的月份資料夾
find "$BACKUP_ROOT" -type d -empty -delete

echo "[$(date)] 備份流程結束"
echo "----------------------------------------"
