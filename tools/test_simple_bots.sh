#!/bin/bash

# Test script for simple bot system

echo "=== Тест системы простых ботов ==="

# Set classpath
CLASSPATH="build/classes"
CLASSPATH="$CLASSPATH:libs/mysql-connector-java-5.1.49.jar"
CLASSPATH="$CLASSPATH:libs/c3p0-0.9.1.2.jar"
CLASSPATH="$CLASSPATH:libs/commons-logging-1.1.1.jar"
CLASSPATH="$CLASSPATH:libs/commons-dbcp-1.4.jar"
CLASSPATH="$CLASSPATH:libs/commons-pool-1.6.jar"
CLASSPATH="$CLASSPATH:libs/javolution-5.5.1.jar"
CLASSPATH="$CLASSPATH:libs/primrose-2.0.jar"
CLASSPATH="$CLASSPATH:libs/slf4j-api-1.7.5.jar"
CLASSPATH="$CLASSPATH:libs/slf4j-nop-1.7.5.jar"

echo "Собираю проект..."
ant compile

if [ $? -eq 0 ]; then
    echo "✓ Проект собран успешно"
    
    echo "Запускаю тест ботов..."
    java -cp "$CLASSPATH" net.sf.l2j.gameserver.fakeplayer.TestSimpleBots
    
    if [ $? -eq 0 ]; then
        echo "✓ Тест ботов завершен успешно"
    else
        echo "✗ Ошибка в тесте ботов"
        exit 1
    fi
else
    echo "✗ Ошибка сборки проекта"
    exit 1
fi
