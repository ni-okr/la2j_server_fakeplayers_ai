#!/bin/bash
# Скрипт для компиляции улучшенного Modern Lineage II клиента

ENHANCED_CPP_FILE="enhanced_game.cpp"
EXECUTABLE_NAME="ModernLineage2_Enhanced"
LOG_FILE="/tmp/enhanced_game.log"
PID_FILE="/tmp/enhanced_game.pid"

echo "🎮 Компиляция Modern Lineage II Enhanced Client..."
echo "🎯 Особенности:"
echo "   ✅ Текстуры земли из L2 деобфусцированного клиента"
echo "   ✅ Динамический цикл день/ночь"
echo "   ✅ Множественные типы местности"
echo "   ✅ Улучшенная система неба"

# Проверка наличия исходного файла
if [ ! -f "$ENHANCED_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $ENHANCED_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем улучшенную игру..."

# Компиляция C++ файла с использованием g++
# -lGL -lGLU -lglut: линкуем необходимые OpenGL и GLUT библиотеки
# -lm: математические функции
g++ "$ENHANCED_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю улучшенную игру..."
    
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
    echo "🚀 Запуск Modern Lineage II Enhanced Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🎉 ===== MODERN LINEAGE II ENHANCED CLIENT ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🎯 НОВЫЕ ВОЗМОЖНОСТИ:"
    echo "   🌍 Текстурированная местность (земля, трава, камень, вода)"
    echo "   🌅 Динамический цикл день/ночь с переходами"
    echo "   🎨 Разные типы биомов"
    echo "   ☀️ Реалистичное изменение цвета неба"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение персонажа"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Игра успешно запущена и работает!"
        echo "🎯 Откройте игровое окно для взаимодействия"
    else
        echo "❌ Ошибка запуска игры. Проверьте логи: $LOG_FILE"
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

