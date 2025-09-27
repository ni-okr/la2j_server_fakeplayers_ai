# Быстрый запуск L2J Server с Bot System

## Требования
- Java 8+
- MariaDB/MySQL
- Wine (для клиента)

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

## Запуск

### Автоматический запуск всего:
```bash
./start_all.sh
```

### Ручной запуск:

1. **Login Server:**
```bash
./start_login_server.sh
```

2. **Game Server (в другом терминале):**
```bash
./start_game_server.sh
```

3. **Клиент игры:**
```bash
./start_client.sh
```

## Остановка

```bash
pkill -f 'java.*l2j'
```

## Структура проекта

```
la2bots/
├── client/                 # Клиент игры
│   ├── LineageII.exe      # Основной исполняемый файл
│   ├── system/            # Системные файлы
│   └── ...
├── target/                # Собранные JAR файлы
├── config/                # Конфигурация серверов
├── start_all.sh          # Запуск всего
├── start_login_server.sh # Запуск Login Server
├── start_game_server.sh  # Запуск Game Server
└── start_client.sh       # Запуск клиента
```

## Настройки клиента

Клиент уже настроен на подключение к локальному серверу:
- Login Server: 127.0.0.1:2106
- Game Server: 127.0.0.1:7777

## Проблемы

Если клиент не запускается через Wine, попробуйте:
1. Установить Wine Gecko: `winetricks gecko`
2. Использовать PlayOnLinux
3. Запустить в виртуальной машине Windows
