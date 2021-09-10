#!/usr/bin/env sh

# Create (if necessary) and run local Taiga instance

set -x
exec docker-compose -f docker-compose.yml up -d
