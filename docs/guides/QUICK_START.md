# 🚀 Быстрый запуск L2J Bot System + Modern Client

## 📋 Требования

### Системные требования
- **ОС**: Ubuntu 20.04+ или совместимый Linux
- **Java**: 8+ (для L2J Bot System)
- **База данных**: MariaDB/MySQL
- **GPU**: NVIDIA/AMD с поддержкой OpenGL 4.0+
- **RAM**: 8GB+ (рекомендуется 16GB+)
- **Место**: 20GB+ свободного пространства

### Дополнительные требования
- **Unreal Engine 4.27** (для современного клиента)
- **Wine** (для оригинального клиента, опционально)

## Установка

1. **Настройка базы данных:**
```bash
sudo mysql -e "CREATE DATABASE IF NOT EXISTS l2jdb; CREATE USER IF NOT EXISTS 'l2j'@'localhost' IDENTIFIED BY 'l2j'; GRANT ALL PRIVILEGES ON l2jdb.* TO 'l2j'@'localhost'; FLUSH PRIVILEGES;"
sudo mysql -u l2j -pl2j l2jdb < src/main/resources/sql/l2jdb_create.sql
```

2. **Сборка проекта:**
```bash
mvn clean package -DskipTests
```

## 🚀 Запуск

### 🎮 Современный клиент (Рекомендуется)
```bash
cd modern_client
./production_launch.sh        # Запуск в режиме эксплуатации
./start_optimized.sh         # Запуск с оптимизациями
./demo_client.sh             # Демонстрация систем
```

### 🤖 L2J Bot System
```bash
# Автоматический запуск всего
./scripts/system/quick_start.sh

# Ручной запуск
./scripts/server/start_login_server.sh
./scripts/server/start_game_server.sh
```

### 🎯 Оригинальный клиент (Опционально)
```bash
./scripts/client/start_client.sh
```

## Остановка

```bash
pkill -f 'java.*l2j'
```

## 📁 Структура проекта

```
la2bots/
├── 📁 modern_client/              # Современный клиент UE 4.27
│   ├── 📁 Source/                 # Исходный код C++
│   ├── 📁 Content/                # Игровой контент
│   ├── 📁 Config/                 # Конфигурации
│   └── 📄 *.sh                    # Скрипты запуска
├── 📁 src/                        # L2J Bot System (Java)
│   ├── 📁 main/java/              # Основной код
│   └── 📁 test/java/              # Тесты
├── 📁 client/                     # Оригинальный клиент L2
├── 📁 client_deobfuscation/       # Деобфусцированный код (3.5 ГБ)
├── 📁 docs/                       # Документация
├── 📁 scripts/                    # Скрипты автоматизации
├── 📁 config/                     # Конфигурации серверов
└── 📄 README.md                   # Основная документация
```

## ⚙️ Настройки

### Современный клиент
- **Сервер входа**: 127.0.0.1:2106
- **Игровой сервер**: 127.0.0.1:7777
- **Протокол**: L2J Interlude (версия 746)
- **Шифрование**: Включено

### L2J Bot System
- **База данных**: l2jdb
- **Пользователь**: l2j
- **Пароль**: l2j
- **Порт Login**: 2106
- **Порт Game**: 7777

## 🛠️ Устранение проблем

### Современный клиент
```bash
# Проверка системных требований
cd modern_client
./benchmark.sh

# Тестирование подключения
./test_connection.sh

# Мониторинг производительности
./performance_monitor.sh
```

### L2J Bot System
```bash
# Проверка логов
tail -f logs/gameserver.log
tail -f logs/client_debug.log

# Перезапуск серверов
pkill -f 'java.*l2j'
./scripts/system/quick_start.sh
```

### Оригинальный клиент (Wine)
```bash
# Установка Wine Gecko
winetricks gecko

# Использование PlayOnLinux
# Или запуск в виртуальной машине Windows
```

## 🎯 Следующие шаги

1. **Изучите документацию** - `docs/guides/`
2. **Настройте графику** - `modern_client/setup_graphics.sh`
3. **Настройте системы** - `modern_client/setup_*.sh`
4. **Запустите демо** - `modern_client/demo_client.sh`
