#!/bin/bash

# MIDAS Core Application Runner
# This script checks for port conflicts and starts the application

PORT=8080
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Checking if port $PORT is available..."

# Check if port is in use
if lsof -i :$PORT > /dev/null 2>&1; then
    echo "⚠️  Port $PORT is already in use!"
    echo "Processes using port $PORT:"
    lsof -i :$PORT
    echo ""
    read -p "Do you want to kill the process(es) using port $PORT? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        PIDS=$(lsof -ti :$PORT)
        echo "Killing processes: $PIDS"
        kill -9 $PIDS 2>/dev/null
        sleep 2
        echo "✅ Port $PORT is now free"
    else
        echo "❌ Cannot start application. Port $PORT is in use."
        exit 1
    fi
else
    echo "✅ Port $PORT is available"
fi

echo ""
echo "Starting MIDAS Core Application..."
echo "Press Ctrl+C to stop the application"
echo ""

cd "$PROJECT_DIR"
./mvnw spring-boot:run

