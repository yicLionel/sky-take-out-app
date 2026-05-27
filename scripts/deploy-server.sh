#!/bin/sh
set -e

if [ -z "$1" ]; then
  echo "Usage: scripts/deploy-server.sh user@server"
  exit 1
fi

SERVER="$1"
REMOTE_DIR="${2:-~/sky-take-out}"

tar \
  --exclude .git \
  --exclude '*/target' \
  --exclude sky-app \
  --exclude sky-miniapp \
  -czf /tmp/sky-take-out-server.tar.gz .

ssh "$SERVER" "mkdir -p $REMOTE_DIR"
scp /tmp/sky-take-out-server.tar.gz "$SERVER:$REMOTE_DIR/"
ssh "$SERVER" "cd $REMOTE_DIR && tar -xzf sky-take-out-server.tar.gz && test -f .env || cp .env.production.example .env && docker compose -f docker-compose.prod.yml up -d --build"

echo "Server deployment submitted. Edit $REMOTE_DIR/.env on the server before production use."
