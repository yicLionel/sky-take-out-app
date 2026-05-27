#!/bin/sh
set -e

if [ -z "$1" ]; then
  echo "Usage: scripts/set-api-url.sh https://api.example.com"
  exit 1
fi

cat > sky-app/config.js <<EOF
window.SKY_APP_CONFIG = {
  apiBaseUrl: '$1'
}
EOF

echo "Updated sky-app/config.js -> $1"
