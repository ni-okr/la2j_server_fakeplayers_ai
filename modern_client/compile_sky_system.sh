#!/bin/bash
# Скрипт для компиляции системы неба Modern Lineage II

SKY_CPP_FILE="sky_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_SkySystem"
LOG_FILE="/tmp/sky_system_game.log"
PID_FILE="/tmp/sky_system_game.pid"

echo "🌅 Компиляция Modern Lineage II Sky System Client..."
echo "🎯 Особенности системы неба:"
echo "   ✅ Динамический цикл день/ночь с реалистичными переходами"
echo "   ✅ Система облаков (кучевые, слоистые, дождевые)"
echo "   ✅ Система дождя с частицами"
echo "   ✅ Погодные эффекты в стиле Ведьмака 3"
echo "   ✅ Атмосферное освещение и цвета неба"

# Проверка наличия исходного файла
if [ ! -f "$SKY_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $SKY_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему неба..."

# Компиляция C++ файла с использованием g++
# -lGL -lGLU -lglut: линкуем необходимые OpenGL и GLUT библиотеки
# -lm: математические функции
g++ "$SKY_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему неба..."
    
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
    echo "🚀 Запуск Modern Lineage II Sky System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🌅 ===== MODERN LINEAGE II SKY SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🌤️  СИСТЕМА НЕБА:"
    echo "   🌅 Динамический цикл день/ночь (автоматический)"
    echo "   ☁️  50 облаков разных типов"
    echo "   🌧️  Система дождя с 1000 частицами"
    echo "   🌈 Реалистичные цвета неба"
    echo "   ⚡ Случайные погодные изменения"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение персонажа"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🌤️  ПОГОДНЫЕ СОСТОЯНИЯ:"
    echo "   ☀️  Ясная погода - голубое небо"
    echo "   🌧️  Дождливая погода - серое небо + дождь"
    echo "   🌅 Рассвет/закат - оранжевые тона"
    echo "   🌙 Ночь - темное небо"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система неба успешно запущена и работает!"
        echo "🌅 Наблюдайте за динамическим небом и погодными эффектами!"
        echo "🎯 Попробуйте нажать R для включения дождя!"
    else
        echo "❌ Ошибка запуска системы неба. Проверьте логи: $LOG_FILE"
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

