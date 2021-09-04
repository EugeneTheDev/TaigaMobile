#!/usr/bin/env sh

# Just stop (all data will be preserved)

set -x
exec docker-compose -f docker-compose.yml stop
