#!/bin/bash

# Modern Lineage II Client - Network Integration Setup Script
# Настройка сетевой интеграции с L2J сервером

set -e

echo "🌐 Modern Lineage II Client - Network Integration Setup"
echo "====================================================="

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Создание структуры папок для сетевой интеграции
create_network_structure() {
    print_status "Создание структуры папок для сетевой интеграции..."
    
    # Основные папки
    mkdir -p Content/Network/Protocols
    mkdir -p Content/Network/Security
    mkdir -p Content/Network/Synchronization
    mkdir -p Content/Network/AntiCheat
    
    # Конфигурации
    mkdir -p Config/Network
    mkdir -p Config/Security
    mkdir -p Config/Protocols
    
    # Логи и данные
    mkdir -p Logs/Network
    mkdir -p Logs/Security
    mkdir -p Data/Network
    
    print_success "Структура папок создана"
}

# Создание конфигурации L2J протокола
create_l2j_protocol_config() {
    print_status "Создание конфигурации L2J протокола..."
    
    # Создание конфигурации протокола
    cat > Config/Network/L2JProtocol.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; L2J Protocol Configuration
ProtocolVersion=746
ServerAddress=127.0.0.1
LoginPort=2106
GamePort=7777
UseEncryption=true
ConnectionTimeout=30.0
HeartbeatInterval=5.0
MaxPacketSize=65536
PacketBufferSize=1048576
CompressionEnabled=true
CompressionLevel=6
EOF

    # Создание таблицы пакетов
    cat > Config/Protocols/PacketDefinitions.csv << 'EOF'
PacketID,PacketName,PacketType,Direction,Size,Encrypted,Description
0x00,"RequestAuthLogin","Login","ClientToServer",32,true,"Authentication login request"
0x01,"LoginOk","Login","ServerToClient",16,true,"Login successful response"
0x02,"LoginFail","Login","ServerToClient",8,true,"Login failed response"
0x03,"RequestCharacterList","Character","ClientToServer",4,true,"Request character list"
0x04,"CharacterList","Character","ServerToClient",256,true,"Character list response"
0x05,"RequestSelectCharacter","Character","ClientToServer",8,true,"Select character request"
0x06,"CharacterSelected","Character","ServerToClient",64,true,"Character selected response"
0x0B,"RequestMoveToLocation","Game","ClientToServer",16,false,"Move to location request"
0x0C,"MoveToLocation","Game","ServerToClient",20,false,"Move to location notification"
0x0D,"RequestAttack","Game","ClientToServer",8,false,"Attack target request"
0x0E,"Attack","Game","ServerToClient",12,false,"Attack notification"
0x38,"Say2","Chat","ClientToServer",256,false,"Chat message"
0x4A,"CreatureSay","Chat","ServerToClient",256,false,"Creature say message"
EOF

    print_success "Конфигурация L2J протокола создана"
}

# Создание конфигурации сетевого менеджера
create_network_manager_config() {
    print_status "Создание конфигурации сетевого менеджера..."
    
    # Создание конфигурации сетевого менеджера
    cat > Config/Network/NetworkManager.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Network Manager Configuration
EnableNetworking=true
ConnectionTimeout=30.0
HeartbeatInterval=5.0
MaxRetryAttempts=3
RetryDelay=2.0
AutoReconnect=true
MaxConnections=10
BufferSize=1048576
SendBufferSize=65536
ReceiveBufferSize=65536
KeepAliveEnabled=true
KeepAliveInterval=30.0
TcpNoDelay=true
EOF

    print_success "Конфигурация сетевого менеджера создана"
}

# Создание конфигурации синхронизации состояния
create_sync_config() {
    print_status "Создание конфигурации синхронизации состояния..."
    
    # Создание конфигурации синхронизации
    cat > Config/Network/StateSynchronization.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; State Synchronization Configuration
SyncInterval=0.1
PositionThreshold=1.0
RotationThreshold=5.0
SyncPosition=true
SyncRotation=true
SyncStats=true
SyncInventory=false
SyncBuffs=true
MaxSnapshotHistory=100
ClientAuthoritative=false
PredictionTime=0.1
InterpolationEnabled=true
ExtrapolationEnabled=false
RollbackEnabled=true
MaxRollbackTime=1.0
EOF

    print_success "Конфигурация синхронизации создана"
}

# Создание конфигурации системы безопасности
create_security_config() {
    print_status "Создание конфигурации системы безопасности..."
    
    # Создание конфигурации античита
    cat > Config/Security/AntiCheat.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Anti-cheat System Configuration
EnableAntiCheat=true
MaxMovementSpeed=300.0
MaxTeleportDistance=50.0
MaxActionsPerSecond=10
CheckInterval=1.0
ViolationThreshold=3
LogViolations=true
MemoryProtection=true
ProcessProtection=true
NetworkProtection=true
AutoBanEnabled=false
AutoKickEnabled=true
AutoWarnEnabled=true
TrustScoreThreshold=0.3
EOF

    # Создание списка нарушений
    cat > Config/Security/ViolationTypes.csv << 'EOF'
ViolationType,Severity,AutoAction,Description
"SpeedHack",8,"Kick","Player moving faster than allowed"
"TeleportHack",9,"TempBan","Player teleporting without valid skill"
"ItemDupe",10,"PermBan","Player duplicating items"
"StatHack",7,"Warn","Player modifying stats illegally"
"PacketManipulation",9,"TempBan","Player sending invalid packets"
"MemoryModification",10,"PermBan","Player modifying game memory"
"ProcessInjection",10,"PermBan","External process injection detected"
"DebuggerDetected",6,"Warn","Debugger attached to process"
"SuspiciousBehavior",4,"Log","Unusual player behavior pattern"
"RapidActions",5,"Warn","Too many actions per second"
EOF

    print_success "Конфигурация системы безопасности создана"
}

# Создание скриптов тестирования
create_test_scripts() {
    print_status "Создание скриптов тестирования сети..."
    
    # Создание скрипта тестирования подключения
    cat > test_connection.sh << 'EOF'
#!/bin/bash

echo "🌐 Тестирование сетевого подключения к L2J серверу"
echo "================================================"

SERVER_IP="127.0.0.1"
LOGIN_PORT="2106"
GAME_PORT="7777"

# Тестирование подключения к серверу входа
echo "Тестирование подключения к серверу входа ($SERVER_IP:$LOGIN_PORT)..."
if timeout 5 bash -c "</dev/tcp/$SERVER_IP/$LOGIN_PORT"; then
    echo "✅ Сервер входа доступен"
else
    echo "❌ Сервер входа недоступен"
fi

# Тестирование подключения к игровому серверу
echo "Тестирование подключения к игровому серверу ($SERVER_IP:$GAME_PORT)..."
if timeout 5 bash -c "</dev/tcp/$SERVER_IP/$GAME_PORT"; then
    echo "✅ Игровой сервер доступен"
else
    echo "❌ Игровой сервер недоступен"
fi

# Тестирование латентности
echo "Тестирование латентности..."
ping -c 4 $SERVER_IP

echo "Тестирование завершено"
EOF
    chmod +x test_connection.sh

    # Создание скрипта мониторинга сети
    cat > monitor_network.sh << 'EOF'
#!/bin/bash

echo "📊 Мониторинг сетевой активности"
echo "================================"

# Мониторинг сетевых соединений
echo "Активные соединения:"
netstat -an | grep -E ":2106|:7777"

# Мониторинг трафика
echo -e "\nСетевой трафик:"
if command -v iftop &> /dev/null; then
    timeout 10 iftop -t -s 10
else
    echo "iftop не установлен, используйте: sudo apt install iftop"
fi

# Мониторинг использования портов
echo -e "\nИспользование портов:"
ss -tuln | grep -E ":2106|:7777"
EOF
    chmod +x monitor_network.sh

    print_success "Скрипты тестирования созданы"
}

# Создание системы логирования
create_logging_system() {
    print_status "Создание системы логирования..."
    
    # Создание конфигурации логирования
    cat > Config/Network/Logging.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Network Logging Configuration
EnableNetworkLogging=true
LogLevel=2
LogToFile=true
LogToConsole=false
MaxLogFileSize=10485760
MaxLogFiles=5
LogRotationEnabled=true
LogPackets=false
LogConnections=true
LogErrors=true
LogWarnings=true
LogSecurity=true
LogPerformance=false
EOF

    # Создание структуры логов
    mkdir -p Logs/Network/{Connections,Packets,Errors,Security,Performance}
    
    print_success "Система логирования создана"
}

# Создание системы мониторинга производительности
create_performance_monitoring() {
    print_status "Создание системы мониторинга производительности..."
    
    # Создание конфигурации мониторинга
    cat > Config/Network/Performance.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Network Performance Monitoring
EnablePerformanceMonitoring=true
MonitoringInterval=5.0
TrackLatency=true
TrackBandwidth=true
TrackPacketLoss=true
TrackConnectionStability=true
MaxLatencyThreshold=200.0
MaxPacketLossThreshold=5.0
MinBandwidthThreshold=1024.0
AlertOnThresholdExceeded=true
SavePerformanceData=true
PerformanceDataRetention=7
EOF

    print_success "Система мониторинга производительности создана"
}

# Основная функция
main() {
    echo "Начинаем настройку сетевой интеграции..."
    echo
    
    create_network_structure
    create_l2j_protocol_config
    create_network_manager_config
    create_sync_config
    create_security_config
    create_test_scripts
    create_logging_system
    create_performance_monitoring
    
    echo
    print_success "🌐 Настройка сетевой интеграции завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Откройте проект в Unreal Engine 4.27"
    echo "2. Скомпилируйте сетевые системы"
    echo "3. Настройте подключение к L2J серверу"
    echo "4. Протестируйте сетевое взаимодействие"
    echo "5. Настройте систему безопасности"
    echo
    echo "Доступные скрипты тестирования:"
    echo "- ./test_connection.sh - тестирование подключения"
    echo "- ./monitor_network.sh - мониторинг сети"
    echo
    echo "Сетевая интеграция готова к использованию!"
}

# Запуск основной функции
main "$@"
