#!/bin/bash

# Modern Lineage II Performance Monitor
echo "📊 Мониторинг производительности Modern Lineage II"
echo "================================================="

# Функция мониторинга
monitor_performance() {
    while true; do
        clear
        echo "📊 Performance Monitor - $(date)"
        echo "================================"
        
        # CPU использование
        CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
        echo "🖥️  CPU Usage: ${CPU_USAGE}%"
        
        # RAM использование
        RAM_INFO=$(free -h | awk '/^Mem:/ {printf "Used: %s / %s (%.1f%%)", $3, $2, ($3/$2)*100}')
        echo "🧠 RAM: $RAM_INFO"
        
        # GPU использование (если NVIDIA)
        if command -v nvidia-smi &> /dev/null; then
            GPU_USAGE=$(nvidia-smi --query-gpu=utilization.gpu --format=csv,noheader,nounits)
            GPU_TEMP=$(nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader,nounits)
            echo "🎮 GPU Usage: ${GPU_USAGE}% (${GPU_TEMP}°C)"
        fi
        
        # Процесс игры
        if pgrep -f "ModernLineage2" > /dev/null; then
            GAME_PID=$(pgrep -f "ModernLineage2")
            GAME_CPU=$(ps -p $GAME_PID -o %cpu --no-headers 2>/dev/null || echo "N/A")
            GAME_MEM=$(ps -p $GAME_PID -o %mem --no-headers 2>/dev/null || echo "N/A")
            echo "🎯 Game Process: CPU ${GAME_CPU}%, MEM ${GAME_MEM}%"
        else
            echo "🎯 Game Process: Not running"
        fi
        
        # Сетевая активность
        NETWORK=$(cat /proc/net/dev | grep -E "eth0|wlan0|enp" | head -1 | awk '{print "RX: " $2/1024/1024 " MB, TX: " $10/1024/1024 " MB"}')
        echo "🌐 Network: $NETWORK"
        
        echo ""
        echo "Press Ctrl+C to exit"
        sleep 2
    done
}

# Запуск мониторинга
monitor_performance
