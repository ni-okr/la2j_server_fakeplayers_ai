#!/bin/bash

echo "=== ЗАПУСК СЕРВЕРОВ L2J INTERLUDE ==="

# Останавливаем все процессы Java
echo "Останавливаю все процессы Java..."
killall java 2>/dev/null || true
sleep 2

# Проверяем MySQL
echo "Проверяю MySQL..."
if ! pgrep -f mysqld > /dev/null; then
    echo "MySQL не запущен, запускаю..."
    sudo mysqld_safe --user=mysql --datadir=/var/lib/mysql &
    sleep 5
fi

# Проверяем подключение к базе данных
echo "Проверяю подключение к базе данных..."
mysql -u l2j -pl2jpass -e "SELECT 1;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ Не удается подключиться к базе данных!"
    exit 1
fi
echo "✅ База данных доступна"

# Переходим в директорию сервера
cd /home/ni/Projects/la2bots/l2InterludeServer/L2J_Server/dist

# Запускаем LoginServer
echo "Запускаю LoginServer..."
nohup java -Xms512m -Xmx512m -cp ../build/classes:javolution-5.5.1.jar:mmocore.jar:c3p0-0.9.2-pre1.jar:mysql-connector-java-5.1.18-bin.jar:commons-logging-1.1.jar:bsf.jar:bsh-2.0b4.jar net.sf.l2j.loginserver.L2LoginServer > loginserver.log 2>&1 &
LOGIN_PID=$!
echo "LoginServer PID: $LOGIN_PID"

# Ждем 5 секунд
sleep 5

# Проверяем, что LoginServer запустился
if kill -0 $LOGIN_PID 2>/dev/null; then
    echo "✅ LoginServer запущен"
else
    echo "❌ LoginServer не запустился"
    echo "Логи LoginServer:"
    cat loginserver.log
    exit 1
fi

# Запускаем GameServer
echo "Запускаю GameServer..."
nohup java -Xms512m -Xmx512m -cp ../build/classes:bsf.jar:bsh-2.0b4.jar:commons-logging-1.1.jar:javolution-5.5.1.jar:mmocore.jar:c3p0-0.9.2-pre1.jar:mysql-connector-java-5.1.18-bin.jar:jython.jar net.sf.l2j.gameserver.GameServer > gameserver.log 2>&1 &
GAME_PID=$!
echo "GameServer PID: $GAME_PID"

# Ждем 10 секунд
sleep 10

# Проверяем, что GameServer запустился
if kill -0 $GAME_PID 2>/dev/null; then
    echo "✅ GameServer запущен"
else
    echo "❌ GameServer не запустился"
    echo "Логи GameServer:"
    cat gameserver.log
    exit 1
fi

# Проверяем порты
echo "Проверяю порты..."
if ss -tlnp | grep -q ":2106"; then
    echo "✅ LoginServer слушает порт 2106"
else
    echo "❌ LoginServer НЕ слушает порт 2106"
fi

if ss -tlnp | grep -q ":7777"; then
    echo "✅ GameServer слушает порт 7777"
else
    echo "❌ GameServer НЕ слушает порт 7777"
fi

echo ""
echo "=== СЕРВЕРЫ ЗАПУЩЕНЫ ==="
echo "LoginServer PID: $LOGIN_PID"
echo "GameServer PID: $GAME_PID"
echo ""
echo "Для остановки серверов выполните: kill $LOGIN_PID $GAME_PID"
