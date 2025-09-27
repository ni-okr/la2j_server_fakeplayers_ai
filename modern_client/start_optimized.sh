#!/bin/bash

# Modern Lineage II Client - Optimized Launcher for Ubuntu
echo "🚀 Запуск Modern Lineage II Client с оптимизациями..."

# Применение системных оптимизаций
if [ -f "system_optimization.sh" ]; then
    echo "Применение системных оптимизаций..."
    ./system_optimization.sh
fi

# Применение графических оптимизаций
if [ -f "nvidia_profile.sh" ]; then
    echo "Применение NVIDIA оптимизаций..."
    source ./nvidia_profile.sh
elif [ -f "amd_profile.sh" ]; then
    echo "Применение AMD оптимизаций..."
    source ./amd_profile.sh
fi

# Применение аудио оптимизаций
if [ -f "audio_optimization.sh" ]; then
    echo "Применение аудио оптимизаций..."
    ./audio_optimization.sh
fi

# Применение оптимизаций ввода
if [ -f "input_optimization.sh" ]; then
    echo "Применение оптимизаций ввода..."
    ./input_optimization.sh
fi

# Установка переменных окружения для игры
export LD_LIBRARY_PATH="./lib:$LD_LIBRARY_PATH"
export DISPLAY=${DISPLAY:-:0}

# Приоритет процесса
export GAME_PRIORITY="high"

# Запуск игры с оптимизациями
echo "🎮 Запуск игры..."
nice -n -10 ./ModernLineage2 "$@"
