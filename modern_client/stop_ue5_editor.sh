#!/bin/bash
# Остановка Modern Lineage II UE5 Editor

echo "🛑 Остановка Modern Lineage II UE5 Editor..."

if [ -f /tmp/ue5_editor.pid ]; then
    EDITOR_PID=$(cat /tmp/ue5_editor.pid)
    if ps -p $EDITOR_PID > /dev/null; then
        kill $EDITOR_PID
        echo "✅ UE5 Editor остановлен (PID: $EDITOR_PID)"
    else
        echo "❌ UE5 Editor не запущен или PID файл устарел"
    fi
    rm /tmp/ue5_editor.pid
else
    echo "❌ PID файл не найден. Попытка остановить через pkill..."
    pkill -f UnrealEditor
    if [ $? -eq 0 ]; then
        echo "✅ UE5 Editor остановлен через pkill"
    else
        echo "❌ UE5 Editor не запущен"
    fi
fi

echo "Готово!"
