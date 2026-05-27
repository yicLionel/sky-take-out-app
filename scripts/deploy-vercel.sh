#!/bin/sh
set -e

if [ -z "$1" ]; then
  echo "Usage: scripts/deploy-vercel.sh https://api.example.com"
  exit 1
fi

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$BASE_DIR"

./scripts/set-api-url.sh "$1"

cd sky-app
vercel --prod
