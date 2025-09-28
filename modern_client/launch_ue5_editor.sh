#!/bin/bash
# Modern Lineage II UE5 Editor Launcher
# Запуск Unreal Engine 5.6.1 Editor

echo "🎮 Запуск Modern Lineage II UE5 Editor..."

# Проверка наличия Unreal Editor
if [ ! -f "/home/ni/UnrealEditor" ]; then
    echo "❌ Unreal Editor не найден!"
    echo "Создаю символическую ссылку..."
    ln -sf /mnt/windows/UnrealEngine/Engine/Binaries/Linux/UnrealEditor /home/ni/UnrealEditor
fi

# Проверка наличия проекта
if [ ! -f "/home/ni/Projects/la2bots/modern_client/ModernLineage2.uproject" ]; then
    echo "❌ Файл проекта не найден!"
    exit 1
fi

# Остановка предыдущих процессов
echo "🛑 Остановка предыдущих процессов..."
pkill -f UnrealEditor 2>/dev/null || true
sleep 2

# Запуск Unreal Editor
echo "🚀 Запуск Unreal Engine 5.6.1 Editor..."
cd /home/ni/Projects/la2bots/modern_client

# Запуск в фоновом режиме с логированием и русской локализацией
export LANG=ru_RU.UTF-8
export LC_ALL=ru_RU.UTF-8
nohup /home/ni/UnrealEditor /home/ni/Projects/la2bots/modern_client/ModernLineage2.uproject -culture=ru > /tmp/ue5_editor.log 2>&1 &
EDITOR_PID=$!

# Сохранение PID
echo $EDITOR_PID > /tmp/ue5_editor.pid

echo "✅ Unreal Editor запущен (PID: $EDITOR_PID)"
echo "📝 Логи: /tmp/ue5_editor.log"
echo "🛑 Для остановки: kill $EDITOR_PID"
echo "🛑 Или: pkill -f UnrealEditor"

# Ожидание запуска
echo "⏳ Ожидание запуска редактора..."
sleep 5

# Проверка статуса
if ps -p $EDITOR_PID > /dev/null; then
    echo "✅ Редактор успешно запущен!"
    echo "🎯 Откройте графический интерфейс для работы с проектом"
else
    echo "❌ Ошибка запуска редактора"
    echo "📝 Проверьте логи: tail -50 /tmp/ue5_editor.log"
fi
