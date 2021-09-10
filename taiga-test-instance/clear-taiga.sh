#!/usr/bin/env sh

# Stop and clear everything (all data will be erased)

set -x
exec docker-compose -f docker-compose.yml down -v
