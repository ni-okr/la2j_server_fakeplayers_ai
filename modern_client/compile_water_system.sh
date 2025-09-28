#!/bin/bash
# Скрипт для компиляции системы воды Modern Lineage II

WATER_CPP_FILE="water_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_WaterSystem"
LOG_FILE="/tmp/water_system_game.log"
PID_FILE="/tmp/water_system_game.pid"

echo "🌊 Компиляция Modern Lineage II Water System Client..."
echo "🎯 Особенности системы воды:"
echo "   ✅ Океаны, реки и озера с реалистичной физикой"
echo "   ✅ Система волн с физическим моделированием"
echo "   ✅ Дождь с эффектами брызг и волн"
echo "   ✅ Динамические цвета воды в зависимости от времени суток"
echo "   ✅ Ветровые эффекты на воду и дождь"

# Проверка наличия исходного файла
if [ ! -f "$WATER_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $WATER_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему воды..."

# Компиляция C++ файла с использованием g++
g++ "$WATER_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему воды..."
    
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
    echo "🚀 Запуск Modern Lineage II Water System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🌊 ===== MODERN LINEAGE II WATER SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "💧 СИСТЕМА ВОДЫ:"
    echo "   🌊 1 океан в центре (60x60 единиц)"
    echo "   🏞️  3 реки (8x120 единиц каждая)"
    echo "   🏔️  2 озера (20x20 единиц каждое)"
    echo "   🌊 50 волн с физическим моделированием"
    echo "   🌧️  2000 капель дождя с эффектами брызг"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по миру"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь и ветер"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🌊 ВОДНЫЕ ЭФФЕКТЫ:"
    echo "   🌅 Цвет воды меняется в зависимости от времени суток"
    echo "   🌊 Реалистичные волны с физическим моделированием"
    echo "   🌧️  Дождь создает волны при падении в воду"
    echo "   💨 Ветер влияет на силу волн и направление дождя"
    echo "   🎨 Прозрачность и отражения воды"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система воды успешно запущена и работает!"
        echo "🌊 Наблюдайте за реалистичными водными эффектами!"
        echo "🎯 Попробуйте нажать R для включения дождя и посмотрите на волны!"
    else
        echo "❌ Ошибка запуска системы воды. Проверьте логи: $LOG_FILE"
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

