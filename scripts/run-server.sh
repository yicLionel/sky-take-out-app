#!/bin/sh
set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$BASE_DIR"

./mvnw -pl sky-server -am -DskipTests package
java -jar sky-server/target/sky-server-0.0.1-SNAPSHOT.jar
