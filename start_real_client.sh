#!/bin/bash

echo "🎮 =========================================="
echo "🎮 Запуск НАСТОЯЩЕГО клиента Lineage 2"
echo "🎮 =========================================="
echo ""

# Проверяем, что серверы запущены
echo "Проверка серверов..."
if ! pgrep -f "L2LoginServer" > /dev/null; then
    echo "❌ Login Server не запущен! Запускаем..."
    ./start_login_server.sh &
    sleep 5
fi

if ! pgrep -f "l2j-server" > /dev/null; then
    echo "❌ Game Server не запущен! Запускаем..."
    ./start_game_server.sh &
    sleep 10
fi

echo "✅ Серверы готовы!"

# Проверяем порты
echo "Проверка портов:"
ss -tlnp | grep -E "(2106|7777)" || echo "⚠️  Серверы еще инициализируются..."

# Настраиваем Wine
echo "Настройка Wine..."
export WINEDEBUG=-all
export WINEDLLOVERRIDES="mshtml,mscoree,iexplore.exe="

# Переходим в папку клиента
cd client/system

# Проверяем, что L2.exe существует
if [ ! -f "L2.exe" ]; then
    echo "❌ L2.exe не найден в client/system/"
    echo "🔍 Ищем клиент в других местах..."
    find /home/ni -name "L2.exe" 2>/dev/null | head -3
    exit 1
fi

echo "✅ Найден настоящий клиент: L2.exe"

# Проверяем DLL файлы
echo "Проверка DLL файлов..."
if [ ! -f "Engine.dll" ]; then
    echo "Создание Engine.dll..."
    ln -sf engine.dll Engine.dll
fi

if [ ! -f "DSETUP.dll" ]; then
    echo "Создание DSETUP.dll..."
    ln -sf dsetup.dll DSETUP.dll
fi

if [ ! -f "Window.dll" ]; then
    echo "Создание Window.dll..."
    ln -sf window.dll Window.dll
fi

# Проверяем конфигурацию
echo "Проверка конфигурации..."
if [ -f "l2.ini" ]; then
    echo "📋 Настройки клиента:"
    grep -E "(LoginServerIP|GameServerIP|LoginServerPort|GameServerPort)" l2.ini
else
    echo "❌ Конфигурация l2.ini не найдена"
fi

# Запускаем НАСТОЯЩИЙ клиент
echo "🚀 Запуск НАСТОЯЩЕГО клиента L2.exe..."
echo "Это не лаунчер, а сам игровой клиент!"
echo ""

wine L2.exe

echo ""
echo "Клиент завершил работу."
