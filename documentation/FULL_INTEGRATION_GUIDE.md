# 🎮 Полная интеграция L2J Server с Bot System

## 📋 Обзор системы

Система включает в себя:
- **Login Server** (порт 2106) - аутентификация игроков
- **Game Server** (порт 7777) - игровая логика и боты
- **База данных MariaDB** - хранение данных
- **Клиент Lineage 2** - игровой клиент через Wine

## 🚀 Быстрый запуск

### Автоматический запуск всей системы:
```bash
./launch_full_system.sh
```

### Ручной запуск компонентов:

1. **Только серверы:**
```bash
./start_all.sh
```

2. **Только клиент (если серверы уже запущены):**
```bash
./start_client_advanced.sh
```

3. **Диагностика системы:**
```bash
./diagnose_system.sh
```

## 🔧 Структура проекта

```
la2bots/
├── client/                    # Клиент игры
│   ├── LineageII.exe         # Основной исполняемый файл
│   ├── PATCHW32.dll          # Символическая ссылка на patchw32.dll
│   ├── system/               # Системные файлы
│   │   ├── l2.ini           # Конфигурация клиента
│   │   ├── Engine.dll       # Символическая ссылка на engine.dll
│   │   ├── DSETUP.dll       # Символическая ссылка на dsetup.dll
│   │   └── ...              # Остальные DLL файлы
│   └── ...
├── target/                   # Собранные JAR файлы
│   └── l2j-server-1.0.0.jar
├── config/                   # Конфигурация серверов
├── src/main/resources/sql/   # SQL скрипты для БД
├── launch_full_system.sh     # Полный запуск системы
├── start_all.sh             # Запуск серверов
├── start_client_advanced.sh  # Запуск клиента с интеграцией
├── diagnose_system.sh       # Диагностика системы
└── QUICK_START.md           # Быстрый старт
```

## ⚙️ Настройка

### 1. База данных
```bash
# Создание БД и пользователя
sudo mysql -e "CREATE DATABASE IF NOT EXISTS l2jdb; CREATE USER IF NOT EXISTS 'l2j'@'localhost' IDENTIFIED BY 'l2j'; GRANT ALL PRIVILEGES ON l2jdb.* TO 'l2j'@'localhost'; FLUSH PRIVILEGES;"

# Импорт схемы
sudo mysql -u l2j -pl2j l2jdb < src/main/resources/sql/l2jdb_create.sql
```

### 2. Сборка проекта
```bash
mvn clean package -DskipTests
```

### 3. Настройка Wine
```bash
# Очистка Wine кэша (если есть проблемы)
rm -rf ~/.cache/wine

# Настройка Wine (опционально)
winecfg
```

## 🎯 Конфигурация клиента

Клиент уже настроен на подключение к локальному серверу:

**client/system/l2.ini:**
```ini
[Network]
LoginServerIP=127.0.0.1
LoginServerPort=2106
GameServerIP=127.0.0.1
GameServerPort=7777
```

## 🔍 Диагностика проблем

### Проверка статуса системы:
```bash
./diagnose_system.sh
```

### Ручная проверка компонентов:

1. **Серверы:**
```bash
ps aux | grep java
ss -tlnp | grep -E "(2106|7777)"
```

2. **База данных:**
```bash
systemctl status mariadb
mysql -u l2j -pl2j -e "SELECT 1;"
```

3. **Клиент:**
```bash
ls -la client/LineageII.exe
ls -la client/PATCHW32.dll
ls -la client/system/Engine.dll
```

## 🐛 Решение проблем

### Проблема: Клиент не запускается
**Решение:**
1. Проверьте DLL файлы: `ls -la client/system/Engine.dll`
2. Очистите Wine кэш: `rm -rf ~/.cache/wine`
3. Запустите диагностику: `./diagnose_system.sh`

### Проблема: Серверы не запускаются
**Решение:**
1. Проверьте Java: `java -version`
2. Проверьте БД: `systemctl status mariadb`
3. Проверьте порты: `ss -tlnp | grep -E "(2106|7777)"`

### Проблема: Клиент запускается, но сразу закрывается
**Решение:**
1. Запустите клиент и быстро нажмите "START" в окне обновления
2. Нажмите "OK" в диалоговом окне
3. Убедитесь, что серверы запущены

## 📊 Мониторинг

### Логи серверов:
```bash
# Просмотр логов в реальном времени
tail -f logs/gameserver.log
tail -f logs/loginserver.log
```

### Статистика системы:
```bash
# Использование ресурсов
htop

# Сетевые соединения
ss -tlnp | grep -E "(2106|7777)"

# Процессы Java
ps aux | grep java
```

## 🎮 Использование

1. **Запустите систему:**
   ```bash
   ./launch_full_system.sh
   ```

2. **Дождитесь появления окна клиента**

3. **В окне обновления нажмите "START"**

4. **В диалоговом окне нажмите "OK"**

5. **Клиент должен подключиться к серверу**

## 🔄 Остановка системы

```bash
# Остановка всех серверов
pkill -f 'java.*l2j'

# Остановка клиента
pkill -f 'wine.*LineageII'
```

## 📈 Статус интеграции

- ✅ **Login Server** - работает на порту 2106
- ✅ **Game Server** - работает на порту 7777  
- ✅ **База данных** - подключена и настроена
- ✅ **Клиент** - настроен и готов к запуску
- ✅ **DLL файлы** - все необходимые файлы созданы
- ✅ **Скрипты** - автоматизация запуска готова

## 🎯 Следующие шаги

1. **Тестирование подключения** - убедиться, что клиент подключается к серверу
2. **Настройка ботов** - активировать систему ботов
3. **Мониторинг** - настроить логирование и мониторинг
4. **Оптимизация** - настройка производительности

---

**Система готова к использованию!** 🎉
