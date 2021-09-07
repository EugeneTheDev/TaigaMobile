#!/usr/bin/env sh

# Clear data and restart Taiga instance

set -x
./clear-taiga.sh
./launch-taiga.sh
