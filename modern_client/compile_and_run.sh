#!/bin/bash
# Компиляция и запуск простой игры Modern Lineage II

echo "🎮 Компиляция Modern Lineage II Simple Client..."

# Проверка зависимостей
if ! command -v g++ &> /dev/null; then
    echo "❌ g++ не найден. Устанавливаем..."
    sudo apt-get update
    sudo apt-get install -y g++ freeglut3-dev libgl1-mesa-dev libglu1-mesa-dev
fi

# Компиляция
echo "🔨 Компилируем игру..."
g++ -o ModernLineage2_Simple simple_game.cpp -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна!"
    echo "🚀 Запускаем игру..."
    ./ModernLineage2_Simple
else
    echo "❌ Ошибка компиляции!"
    exit 1
fi

