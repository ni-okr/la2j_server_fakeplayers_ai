#!/bin/bash
# Скрипт для компиляции системы растительности Modern Lineage II

VEGETATION_CPP_FILE="vegetation_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_VegetationSystem"
LOG_FILE="/tmp/vegetation_system_game.log"
PID_FILE="/tmp/vegetation_system_game.pid"

echo "🌳 Компиляция Modern Lineage II Vegetation System Client..."
echo "🎯 Особенности системы растительности:"
echo "   ✅ 10 типов деревьев из L2 деобфусцированного клиента"
echo "   ✅ Реалистичная трава с анимацией ветра"
echo "   ✅ Красочные цветы с покачиванием"
echo "   ✅ Динамическая растительность на основе высоты рельефа"
echo "   ✅ Погодные эффекты на растительность"

# Проверка наличия исходного файла
if [ ! -f "$VEGETATION_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $VEGETATION_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему растительности..."

# Компиляция C++ файла с использованием g++
g++ "$VEGETATION_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему растительности..."
    
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
    echo "🚀 Запуск Modern Lineage II Vegetation System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🌳 ===== MODERN LINEAGE II VEGETATION SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🌲 СИСТЕМА РАСТИТЕЛЬНОСТИ:"
    echo "   🌳 200 деревьев 10 типов из L2 клиента"
    echo "   🌱 1000 травинок с анимацией ветра"
    echo "   🌸 500 цветов 5 типов с покачиванием"
    echo "   🎨 Динамические цвета в зависимости от времени суток"
    echo "   💨 Ветровые эффекты на всю растительность"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по миру"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь и ветер"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🌳 ТИПЫ ДЕРЕВЬЕВ L2:"
    echo "   🌲 Aden Tree - деревья Адена"
    echo "   🌲 Dion Tree - деревья Диона"
    echo "   🌲 Gludio Tree - деревья Глудио"
    echo "   🌲 Godad Tree - деревья Годада"
    echo "   🌲 Innadrill Tree - деревья Иннадрила"
    echo "   🌲 Oren Tree - деревья Орена"
    echo "   🌲 Primitive Tree - примитивные деревья"
    echo "   🌲 Rionsctgart Tree - деревья Рионсцтгарта"
    echo "   🌲 Rune Tree - рунические деревья"
    echo "   🌲 Speaking Tree - говорящие деревья"
    echo ""
    echo "🌱 ЭФФЕКТЫ РАСТИТЕЛЬНОСТИ:"
    echo "   🌅 Цвета меняются в зависимости от времени суток"
    echo "   💨 Покачивание от ветра с разной интенсивностью"
    echo "   🌧️  Усиленное покачивание во время дождя"
    echo "   🎨 Реалистичные цвета (зеленый днем, темный ночью)"
    echo "   🌸 Разноцветные цветы (красный, желтый, фиолетовый, зеленый, синий)"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система растительности успешно запущена и работает!"
        echo "🌳 Наслаждайтесь живой природой с реалистичной анимацией!"
        echo "🎯 Попробуйте нажать R для включения дождя и посмотрите на покачивание деревьев!"
    else
        echo "❌ Ошибка запуска системы растительности. Проверьте логи: $LOG_FILE"
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

