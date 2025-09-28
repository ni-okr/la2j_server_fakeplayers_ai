#!/bin/bash

# Остановка Modern Lineage II UE4 Client

echo "🛑 Остановка Modern Lineage II UE4 Client..."

# Поиск и остановка всех процессов ModernLineage2
if pgrep -f "ModernLineage2" > /dev/null; then
    echo "Найден запущенный UE4 клиент, останавливаю..."
    pkill -f "ModernLineage2"
    sleep 2
    
    # Проверка что процесс остановлен
    if pgrep -f "ModernLineage2" > /dev/null; then
        echo "Принудительная остановка..."
        pkill -9 -f "ModernLineage2"
    fi
    
    echo "✅ UE4 клиент остановлен"
else
    echo "❌ UE4 клиент не запущен"
fi

# Удаление PID файла
rm -f /tmp/ue4client.pid

echo "Готово!"
