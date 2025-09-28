#!/bin/bash
# Скрипт для компиляции системы строений Modern Lineage II

BUILDINGS_CPP_FILE="buildings_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_BuildingsSystem"
LOG_FILE="/tmp/buildings_system_game.log"
PID_FILE="/tmp/buildings_system_game.pid"

echo "🏰 Компиляция Modern Lineage II Buildings System Client..."
echo "🎯 Особенности системы строений:"
echo "   ✅ 6 городов из L2 деобфусцированного клиента"
echo "   ✅ 10 типов строений в каждом городе"
echo "   ✅ Реалистичные планировки городов и население"
echo "   ✅ Система разрушения строений"
echo "   ✅ Границы городов и визуальные индикаторы"

# Проверка наличия исходного файла
if [ ! -f "$BUILDINGS_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $BUILDINGS_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему строений..."

# Компиляция C++ файла с использованием g++
g++ "$BUILDINGS_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему строений..."
    
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
    echo "🚀 Запуск Modern Lineage II Buildings System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🏰 ===== MODERN LINEAGE II BUILDINGS SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🏛️  ГОРОДА L2:"
    echo "   👑 Aden - Столица (Население: 1000)"
    echo "   🏰 Dion - Город (Население: 800)"
    echo "   🏘️  Giran - Город (Население: 900)"
    echo "   🏔️  Oren - Город (Население: 600)"
    echo "   🏰 Rune - Город (Население: 700)"
    echo "   ⚒️  Schtgart - Город (Население: 400)"
    echo ""
    echo "🏗️  ТИПЫ СТРОЕНИЙ:"
    echo "   🏰 Castle - Замки (8x12x8 единиц)"
    echo "   🏘️  Village - Деревни (4x3x4 единиц)"
    echo "   🗼 Tower - Башни (3x15x3 единиц)"
    echo "   🏰 Fortress - Крепости (6x8x6 единиц)"
    echo "   🏠 House - Дома (3x4x3 единиц)"
    echo "   🏛️  Town Hall - Ратуши (5x6x5 единиц)"
    echo "   🏪 Shop - Магазины (3x4x3 единиц)"
    echo "   ⛪ Temple - Храмы (4x8x4 единиц)"
    echo "   🗼 Lighthouse - Маяки (2x20x2 единиц)"
    echo "   🌉 Bridge - Мосты (8x2x2 единиц)"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по городам"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь и ветер"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🏗️  СИСТЕМА СТРОЕНИЙ:"
    echo "   🎨 Разные цвета для разных типов строений"
    echo "   💥 Система разрушения (случайное разрушение)"
    echo "   🎯 Визуальные эффекты разрушения"
    echo "   📊 Индикаторы состояния в HUD"
    echo "   🏰 Границы городов с желтыми линиями"
    echo ""
    echo "🌍 ХАРАКТЕРИСТИКИ ГОРОДОВ:"
    echo "   👑 Aden - Столица с замками и башнями"
    echo "   🏰 Dion - Город с крепостями и храмами"
    echo "   🏘️  Giran - Торговый город с магазинами"
    echo "   🏔️  Oren - Город в горах с маяками"
    echo "   🏰 Rune - Магический город с храмами"
    echo "   ⚒️  Schtgart - Дворфийский город с домами"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система строений успешно запущена и работает!"
        echo "🏰 Исследуйте города и строения из мира L2!"
        echo "🎯 Посетите разные города и посмотрите на их архитектуру!"
    else
        echo "❌ Ошибка запуска системы строений. Проверьте логи: $LOG_FILE"
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

