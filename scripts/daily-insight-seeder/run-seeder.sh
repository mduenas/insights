#!/bin/bash
# Runs the daily insight seeder locally.
# Scheduled via launchd — see com.markduenas.insights.seeder.plist

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOG_DIR"

cd "$SCRIPT_DIR"
/opt/homebrew/bin/node index.js \
  --key-file ../../composeApp/insights-b91e8-3190503df694.json \
  >> "$LOG_DIR/seeder.log" 2>&1
