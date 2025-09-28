#!/bin/bash
# Скрипт для компиляции системы биомов Modern Lineage II

BIOMES_CPP_FILE="biomes_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_BiomesSystem"
LOG_FILE="/tmp/biomes_system_game.log"
PID_FILE="/tmp/biomes_system_game.pid"

echo "🌍 Компиляция Modern Lineage II Biomes System Client..."
echo "🎯 Особенности системы биомов:"
echo "   ✅ 6 уникальных биомов с разными характеристиками"
echo "   ✅ Динамическая система температуры"
echo "   ✅ Генерация рельефа в зависимости от биома"
echo "   ✅ Погодные эффекты на биомы"
echo "   ✅ Реалистичные переходы между биомами"

# Проверка наличия исходного файла
if [ ! -f "$BIOMES_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $BIOMES_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему биомов..."

# Компиляция C++ файла с использованием g++
g++ "$BIOMES_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему биомов..."
    
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
    echo "🚀 Запуск Modern Lineage II Biomes System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "🌍 ===== MODERN LINEAGE II BIOMES SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "🌲 БИОМЫ:"
    echo "   🌾 Grassland - Равнины (температура: 20°C)"
    echo "   🌳 Forest - Лес (температура: 15°C)"
    echo "   🏔️  Mountain - Горы (температура: 5°C, снег)"
    echo "   🏜️  Desert - Пустыня (температура: 35°C, песок)"
    echo "   ❄️  Snow - Снег (температура: -10°C)"
    echo "   🌋 Lava - Лава (температура: 100°C)"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по биомам"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь и ветер"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🌡️  СИСТЕМА ТЕМПЕРАТУРЫ:"
    echo "   🌅 День: 25°C (теплее)"
    echo "   🌙 Ночь: 10°C (холоднее)"
    echo "   🌧️  Дождь: -5°C (снижает температуру)"
    echo "   🌡️  Динамическое изменение по времени суток"
    echo ""
    echo "🌍 ХАРАКТЕРИСТИКИ БИОМОВ:"
    echo "   🌾 Равнины: зеленая трава, низкие холмы"
    echo "   🌳 Лес: темно-зеленая растительность, средние холмы"
    echo "   🏔️  Горы: серые скалы, высокие горы"
    echo "   🏜️  Пустыня: песочный песок, дюны"
    echo "   ❄️  Снег: белый снег, заснеженные холмы"
    echo "   🌋 Лава: красная лава, низкие участки"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система биомов успешно запущена и работает!"
        echo "🌍 Исследуйте разнообразные биомы с уникальными характеристиками!"
        echo "🎯 Переходите между биомами и наблюдайте за изменением температуры!"
    else
        echo "❌ Ошибка запуска системы биомов. Проверьте логи: $LOG_FILE"
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

