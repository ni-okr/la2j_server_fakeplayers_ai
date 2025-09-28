#!/bin/bash
# Скрипт для компиляции системы НПС и мобов Modern Lineage II

NPCS_MOBS_CPP_FILE="npcs_mobs_system_game.cpp"
EXECUTABLE_NAME="ModernLineage2_NPCsMobsSystem"
LOG_FILE="/tmp/npcs_mobs_system_game.log"
PID_FILE="/tmp/npcs_mobs_system_game.pid"

echo "👹 Компиляция Modern Lineage II NPCs & Mobs System Client..."
echo "🎯 Особенности системы НПС и мобов:"
echo "   ✅ 5 рас НПС из L2 деобфусцированного клиента"
echo "   ✅ 30 мобов с ИИ поведением"
echo "   ✅ 3 босса с улучшенными характеристиками"
echo "   ✅ Реалистичный ИИ с поиском пути"
echo "   ✅ Полоски здоровья и индикаторы состояния"

# Проверка наличия исходного файла
if [ ! -f "$NPCS_MOBS_CPP_FILE" ]; then
    echo "❌ Ошибка: Файл $NPCS_MOBS_CPP_FILE не найден!"
    exit 1
fi

echo "🔨 Компилируем систему НПС и мобов..."

# Компиляция C++ файла с использованием g++
g++ "$NPCS_MOBS_CPP_FILE" -o "$EXECUTABLE_NAME" -lGL -lGLU -lglut -lm

if [ $? -eq 0 ]; then
    echo "✅ Компиляция успешна! Запускаю систему НПС и мобов..."
    
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
    echo "🚀 Запуск Modern Lineage II NPCs & Mobs System Client..."
    nohup "./$EXECUTABLE_NAME" > "$LOG_FILE" 2>&1 &
    GAME_PID=$!
    echo "$GAME_PID" > "$PID_FILE"
    
    echo ""
    echo "👹 ===== MODERN LINEAGE II NPCS & MOBS SYSTEM ЗАПУЩЕН! ====="
    echo ""
    echo "🎮 PID: $GAME_PID"
    echo "📝 Логи: $LOG_FILE"
    echo ""
    echo "👥 СУЩНОСТИ:"
    echo "   👤 20 НПС (4 на каждую расу)"
    echo "   👹 30 мобов с ИИ поведением"
    echo "   👑 3 босса с улучшенными характеристиками"
    echo "   💀 Система респавна для мертвых существ"
    echo "   🎯 Полоски здоровья для всех существ"
    echo ""
    echo "🎮 УПРАВЛЕНИЕ:"
    echo "   WASD     - Движение по миру"
    echo "   Стрелки  - Поворот камеры"
    echo "   T        - Переключить цикл день/ночь"
    echo "   R        - Переключить дождь и ветер"
    echo "   ЛКМ      - Взаимодействие"
    echo "   ESC      - Выход"
    echo ""
    echo "🏛️  РАСЫ НПС L2:"
    echo "   👤 Human - Люди (Guard, Merchant, Blacksmith, Innkeeper)"
    echo "   🧝 Elf - Эльфы (Elven Archer, Forest Keeper, Nature Priest)"
    echo "   ⚒️  Dwarf - Дворфы (Dwarf Miner, Dwarf Smith, Dwarf Trader)"
    echo "   👹 Orc - Орки (Orc Warrior, Orc Shaman, Orc Hunter)"
    echo "   🧙 Dark Elf - Темные эльфы (Assassin, Mage, Priest)"
    echo ""
    echo "🤖 ИИ ПОВЕДЕНИЕ:"
    echo "   🎯 Мобы ищут игрока в радиусе 50 единиц"
    echo "   ⚔️  Атакуют игрока в радиусе 20 единиц"
    echo "   🚶 Случайное движение когда игрок далеко"
    echo "   🏃 НПС просто стоят или ходят"
    echo "   💀 Система респавна через 10 секунд"
    echo ""
    echo "🎨 ВИЗУАЛЬНЫЕ ЭФФЕКТЫ:"
    echo "   🎭 Анимация ходьбы и атаки"
    echo "   💚 Полоски здоровья для раненых существ"
    echo "   🎨 Разные цвета для разных рас"
    echo "   👑 Золотые боссы больше обычных мобов"
    echo "   📊 Индикаторы состояния в HUD"
    echo ""
    echo "🛑 Для остановки: kill $GAME_PID или pkill -f $EXECUTABLE_NAME"
    echo "📊 Мониторинг: tail -f $LOG_FILE"
    echo ""
    
    # Проверка через 3 секунды что игра запустилась
    sleep 3
    if ps -p "$GAME_PID" > /dev/null 2>&1; then
        echo "✅ Система НПС и мобов успешно запущена и работает!"
        echo "👹 Наслаждайтесь живым миром с умными существами!"
        echo "🎯 Попробуйте подойти к мобам и посмотрите на их поведение!"
    else
        echo "❌ Ошибка запуска системы НПС и мобов. Проверьте логи: $LOG_FILE"
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

