#!/bin/bash
# Скрипт для компиляции системы рельефа Modern Lineage II

TERRAIN_CPP_FILE="terrain_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_TerrainSystem"
LOG_FILE="/tmp/terrain_system_game.log"
PID_FILE="/tmp/terrain_system_game.pid"

echo "🏔️ Компиляция Modern Lineage II Terrain System Client..."
echo "🎯 Особенности системы рельефа:"
echo "   ✅ 3D рельеф на основе карт L2 (15_20 до 26_16)"
echo "   ✅ Реалистичные высоты (горы, холмы, равнины)"
echo "   ✅ Динамическое текстурирование по высоте"
echo "   ✅ Нормали для реалистичного освещения"
echo "   ✅ Цикл день/ночь с атмосферным освещением"

# Проверка наличия исходного файла
if [ ! -f "$TERRAIN_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $TERRAIN_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему рельефа..."

# Компиляция C++ файла с использованием g++
g++ "$TERRAIN_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему рельефа..."
    
    # Остановка предыдущих процессов
    if [ -f "$PID_FILE" ]; then
        OLD_PID=$(cat "$PID_FILE")
        if ps -p "$OLD_PID" > /dev/null 2>&1; then
            echo "🛑 Остановка предыдущего процесса (PID: $OLD_PID)..."
            kill "$OLD_PID" 2>/dev/null
            sleep 1
        fi
        rm -f "$PID_FILE"
    fi
    
    # Запуск скомпилированной игры в фоновом режиме
    echo "🚀 Запуск Modern Lineage II Terrain System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🏔️ ===== MODERN LINEAGE II TERRAIN SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🗺️  СИСТЕМА РЕЛЬЕФА:"
    echo "   🏔️  Горы на краях карты (высота 15+ метров)"
    echo "   🌄 Холмы в средней области (высота 5-10 метров)"
    echo "   🌾 Равнины в центре (высота 1-3 метра)"
    echo "   🌊 Водные области (низкие участки)"
    echo "   🎨 6 типов текстур по высоте"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по рельефу"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🗺️  КАРТЫ L2:"
    echo "   📍 Координаты: 15_20 до 26_16"
    echo "   📏 Размер: 100x100 вершин"
    echo "   🎯 Масштаб: 2.0 единицы на тайл"
    echo "   🌍 Покрытие: 200x200 единиц мира"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система рельефа успешно запущена и работает!"
        echo "🏔️ Исследуйте 3D мир с реалистичным рельефом!"
        echo "🎯 Попробуйте подняться на горы или спуститься в долины!"
    else
        echo "❌ Ошибка запуска системы рельефа. Проверьте логи: $LOG_FILE"
        if [ -f "$LOG_FILE" ]; then
            echo "Последние строки лога:"
            tail -10 "$LOG_FILE"
        fi
    fi
    
else
    echo "❌ Ошибка компиляции!"
    echo "💡 Убедитесь что установлены пакеты: freeglut3-dev libglut-dev"
    echo "💡 Для установки: sudo apt-get install freeglut3-dev libglut-dev"
    exit 1
fi

