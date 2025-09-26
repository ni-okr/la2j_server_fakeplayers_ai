#!/bin/bash

# Test script for FakePlayer bot system
# This script compiles and tests the bot system

echo "=== FakePlayer Bot System Test ==="

# Set working directory
cd /home/ni/Projects/la2bots/l2InterludeServer/L2J_Server

# Compile the bot system
echo "Compiling bot system..."
javac -cp ".:javolution-5.5.1.jar:mmocore.jar:c3p0-0.9.2-pre1.jar:mysql-connector-java-5.1.18-bin.jar:l2jserver.jar" \
    java/net/sf/l2j/gameserver/fakeplayer/*.java \
    java/net/sf/l2j/gameserver/fakeplayer/ai/*.java \
    java/net/sf/l2j/gameserver/fakeplayer/behaviors/*.java \
    java/net/sf/l2j/gameserver/fakeplayer/behaviors/impl/*.java \
    java/net/sf/l2j/gameserver/fakeplayer/manager/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
else
    echo "✗ Compilation failed"
    exit 1
fi

# Run the test
echo "Running bot system test..."
java -cp ".:javolution-5.5.1.jar:mmocore.jar:c3p0-0.9.2-pre1.jar:mysql-connector-java-5.1.18-bin.jar:l2jserver.jar" \
    net.sf.l2j.gameserver.fakeplayer.TestBotSystem

if [ $? -eq 0 ]; then
    echo "✓ Test completed successfully"
else
    echo "✗ Test failed"
    exit 1
fi

echo "=== Test completed ==="

