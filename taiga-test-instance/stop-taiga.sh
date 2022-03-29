#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
docker-compose -f docker-compose.yml stop
