#!/bin/bash

echo "📊 Мониторинг сетевой активности"
echo "================================"

# Мониторинг сетевых соединений
echo "Активные соединения:"
netstat -an | grep -E ":2106|:7777"

# Мониторинг трафика
echo -e "\nСетевой трафик:"
if command -v iftop &> /dev/null; then
    timeout 10 iftop -t -s 10
else
    echo "iftop не установлен, используйте: sudo apt install iftop"
fi

# Мониторинг использования портов
echo -e "\nИспользование портов:"
ss -tuln | grep -E ":2106|:7777"
