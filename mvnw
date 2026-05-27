#!/bin/sh
set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MVN_VERSION=${MVN_VERSION:-3.9.11}
MVN_DIR="$BASE_DIR/.mvn/apache-maven-$MVN_VERSION"
MVN_ARCHIVE="$BASE_DIR/.mvn/apache-maven-$MVN_VERSION-bin.tar.gz"
MVN_URL="https://archive.apache.org/dist/maven/maven-3/$MVN_VERSION/binaries/apache-maven-$MVN_VERSION-bin.tar.gz"

if [ ! -x "$MVN_DIR/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  if [ ! -f "$MVN_ARCHIVE" ]; then
    echo "Downloading Apache Maven $MVN_VERSION..."
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$MVN_URL" -o "$MVN_ARCHIVE"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "$MVN_URL" -O "$MVN_ARCHIVE"
    else
      echo "curl or wget is required to download Maven." >&2
      exit 1
    fi
  fi
  tar -xzf "$MVN_ARCHIVE" -C "$BASE_DIR/.mvn"
fi

exec "$MVN_DIR/bin/mvn" "$@"
