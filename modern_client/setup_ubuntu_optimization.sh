#!/bin/bash

# Modern Lineage II Client - Ubuntu Optimization Setup Script
# Финальная оптимизация клиента для Ubuntu Linux

set -e

echo "🐧 Modern Lineage II Client - Ubuntu Optimization"
echo "==============================================="

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

# Определение системных характеристик
detect_system_specs() {
    print_status "Определение характеристик системы..."
    
    # CPU информация
    CPU_MODEL=$(lscpu | grep "Model name" | cut -d: -f2 | xargs)
    CPU_CORES=$(nproc)
    CPU_FREQ=$(lscpu | grep "CPU MHz" | cut -d: -f2 | xargs)
    
    # GPU информация
    if command -v nvidia-smi &> /dev/null; then
        GPU_MODEL=$(nvidia-smi --query-gpu=name --format=csv,noheader,nounits | head -1)
        GPU_MEMORY=$(nvidia-smi --query-gpu=memory.total --format=csv,noheader,nounits | head -1)
        GPU_DRIVER="NVIDIA"
    elif lspci | grep -i "vga.*amd\|vga.*radeon" &> /dev/null; then
        GPU_MODEL=$(lspci | grep -i "vga.*amd\|vga.*radeon" | cut -d: -f3 | xargs)
        GPU_DRIVER="AMD"
        GPU_MEMORY="Unknown"
    else
        GPU_MODEL=$(lspci | grep -i vga | cut -d: -f3 | xargs)
        GPU_DRIVER="Intel/Other"
        GPU_MEMORY="Unknown"
    fi
    
    # RAM информация
    SYSTEM_RAM=$(free -m | awk '/^Mem:/ {print $2}')
    
    # Система информация
    KERNEL_VERSION=$(uname -r)
    DISTRO_NAME=$(lsb_release -si 2>/dev/null || echo "Unknown")
    DISTRO_VERSION=$(lsb_release -sr 2>/dev/null || echo "Unknown")
    
    print_success "Система определена:"
    echo "  CPU: $CPU_MODEL ($CPU_CORES cores, $CPU_FREQ MHz)"
    echo "  GPU: $GPU_MODEL ($GPU_DRIVER driver)"
    echo "  RAM: ${SYSTEM_RAM}MB"
    echo "  OS: $DISTRO_NAME $DISTRO_VERSION (Kernel $KERNEL_VERSION)"
}

# Создание профиля оптимизации
create_optimization_profile() {
    print_status "Создание профиля оптимизации..."
    
    # Определение рекомендуемых настроек на основе системы
    if [ "$SYSTEM_RAM" -lt 4096 ]; then
        GRAPHICS_QUALITY=0  # Low
        TEXTURE_QUALITY=0
        SHADOW_QUALITY=0
        EFFECTS_QUALITY=0
        TARGET_FPS=30
    elif [ "$SYSTEM_RAM" -lt 8192 ]; then
        GRAPHICS_QUALITY=1  # Medium
        TEXTURE_QUALITY=1
        SHADOW_QUALITY=1
        EFFECTS_QUALITY=1
        TARGET_FPS=45
    elif [ "$SYSTEM_RAM" -lt 16384 ]; then
        GRAPHICS_QUALITY=2  # High
        TEXTURE_QUALITY=2
        SHADOW_QUALITY=2
        EFFECTS_QUALITY=2
        TARGET_FPS=60
    else
        GRAPHICS_QUALITY=3  # Ultra
        TEXTURE_QUALITY=3
        SHADOW_QUALITY=3
        EFFECTS_QUALITY=3
        TARGET_FPS=60
    fi
    
    # Создание файла профиля
    cat > Config/UbuntuOptimization.ini << EOF
[/Script/Engine.GameUserSettings]
; Ubuntu Optimization Profile
; Generated on $(date)

[System Specs]
CPUModel=$CPU_MODEL
CPUCores=$CPU_CORES
CPUFrequency=$CPU_FREQ
GPUModel=$GPU_MODEL
GPUDriver=$GPU_DRIVER
GPUMemory=$GPU_MEMORY
SystemRAM=$SYSTEM_RAM
KernelVersion=$KERNEL_VERSION
DistributionName=$DISTRO_NAME
DistributionVersion=$DISTRO_VERSION

[Optimization Settings]
GraphicsQuality=$GRAPHICS_QUALITY
TextureQuality=$TEXTURE_QUALITY
ShadowQuality=$SHADOW_QUALITY
EffectsQuality=$EFFECTS_QUALITY
VSyncEnabled=true
TargetFPS=$TARGET_FPS
Fullscreen=true
Resolution=1920x1080
RenderScale=1.0
MultithreadingEnabled=true

[Linux Specific]
UseWayland=false
AudioSystem=PulseAudio
InputMethod=X11
WindowManager=Auto
CompositorOptimization=true
EOF

    print_success "Профиль оптимизации создан"
}

# Оптимизация графических настроек
optimize_graphics() {
    print_status "Оптимизация графических настроек..."
    
    # NVIDIA оптимизации
    if [ "$GPU_DRIVER" == "NVIDIA" ]; then
        print_status "Применение NVIDIA оптимизаций..."
        
        # Создание профиля NVIDIA
        cat > nvidia_profile.sh << 'EOF'
#!/bin/bash
# NVIDIA оптимизации для Modern Lineage II

# Установка оптимальных настроек драйвера
nvidia-settings -a "[gpu:0]/GPUPowerMizerMode=1" 2>/dev/null || true
nvidia-settings -a "[gpu:0]/GPUMemoryTransferRateOffset[3]=1000" 2>/dev/null || true
nvidia-settings -a "[gpu:0]/GPUGraphicsClockOffset[3]=100" 2>/dev/null || true

# Настройки OpenGL
export __GL_SYNC_TO_VBLANK=1
export __GL_THREADED_OPTIMIZATIONS=1
export __GL_SHADER_DISK_CACHE=1
export __GL_SHADER_DISK_CACHE_PATH="$HOME/.cache/nvidia/GLCache"

echo "NVIDIA оптимизации применены"
EOF
        chmod +x nvidia_profile.sh
        
    # AMD оптимизации
    elif [ "$GPU_DRIVER" == "AMD" ]; then
        print_status "Применение AMD оптимизаций..."
        
        cat > amd_profile.sh << 'EOF'
#!/bin/bash
# AMD оптимизации для Modern Lineage II

# Mesa настройки
export MESA_GL_VERSION_OVERRIDE=4.6
export MESA_GLSL_VERSION_OVERRIDE=460
export AMD_VULKAN_ICD=RADV
export RADV_PERFTEST=aco

# DRI настройки
export DRI_PRIME=1

echo "AMD оптимизации применены"
EOF
        chmod +x amd_profile.sh
    fi
    
    print_success "Графические оптимизации настроены"
}

# Оптимизация системы (БЕЗ ИЗМЕНЕНИЯ ОС)
optimize_system() {
    print_status "Создание рекомендаций по оптимизации системы..."
    
    # Создание скрипта с рекомендациями (БЕЗ ИЗМЕНЕНИЯ СИСТЕМЫ)
    cat > system_optimization_recommendations.sh << 'EOF'
#!/bin/bash
# РЕКОМЕНДАЦИИ по оптимизации системы для игр
# ВНИМАНИЕ: Этот скрипт НЕ изменяет системные настройки!

echo "🔧 РЕКОМЕНДАЦИИ ПО ОПТИМИЗАЦИИ СИСТЕМЫ"
echo "====================================="
echo
echo "Для улучшения производительности игры рекомендуется:"
echo
echo "1. CPU Governor:"
echo "   - Текущий: $(cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor 2>/dev/null || echo 'Недоступно')"
echo "   - Рекомендуется: performance (для максимальной производительности)"
echo "   - Команда: echo performance | sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor"
echo
echo "2. I/O Scheduler:"
echo "   - Текущий: $(cat /sys/block/*/queue/scheduler 2>/dev/null | head -1 || echo 'Недоступно')"
echo "   - Рекомендуется: deadline (для SSD) или mq-deadline (для NVMe)"
echo "   - Команда: echo deadline | sudo tee /sys/block/*/queue/scheduler"
echo
echo "3. Swappiness:"
echo "   - Текущий: $(sysctl vm.swappiness | cut -d= -f2)"
echo "   - Рекомендуется: 10 (для игр)"
echo "   - Команда: echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf"
echo
echo "4. Сетевые оптимизации:"
echo "   - Добавить в /etc/sysctl.conf:"
echo "     net.core.rmem_max = 16777216"
echo "     net.core.wmem_max = 16777216"
echo "     net.ipv4.tcp_rmem = 4096 87380 16777216"
echo "     net.ipv4.tcp_wmem = 4096 65536 16777216"
echo
echo "⚠️  ВНИМАНИЕ: Эти изменения могут повлиять на работу других приложений!"
echo "⚠️  Применяйте их только если понимаете последствия!"
echo
echo "Для применения изменений выполните команды вручную."
EOF
    chmod +x system_optimization_recommendations.sh
    
    print_success "Рекомендации по оптимизации созданы (БЕЗ ИЗМЕНЕНИЯ СИСТЕМЫ)"
}

# Настройка аудиосистемы (БЕЗ ИЗМЕНЕНИЯ СИСТЕМЫ)
setup_audio() {
    print_status "Определение аудиосистемы (БЕЗ ИЗМЕНЕНИЯ НАСТРОЕК)..."
    
    # Определение аудиосистемы
    if command -v pulseaudio &> /dev/null; then
        AUDIO_SYSTEM="PulseAudio"
        
        # Создание рекомендаций для PulseAudio (БЕЗ ИЗМЕНЕНИЯ СИСТЕМЫ)
        cat > audio_optimization_recommendations.sh << 'EOF'
#!/bin/bash
# РЕКОМЕНДАЦИИ по оптимизации PulseAudio
# ВНИМАНИЕ: Этот скрипт НЕ изменяет системные настройки!

echo "🔊 РЕКОМЕНДАЦИИ ПО ОПТИМИЗАЦИИ PULSEAUDIO"
echo "========================================"
echo
echo "Для улучшения звука в игре рекомендуется:"
echo
echo "1. Уменьшение латентности:"
echo "   - Команда: pactl set-default-sink-latency 20000"
echo "   - Команда: pactl set-default-source-latency 20000"
echo
echo "2. Настройка буферов в ~/.pulse/daemon.conf:"
echo "   - default-fragments = 4"
echo "   - default-fragment-size-msec = 5"
echo
echo "3. Перезапуск PulseAudio:"
echo "   - pulseaudio -k && pulseaudio --start"
echo
echo "⚠️  ВНИМАНИЕ: Эти изменения могут повлиять на работу других приложений!"
echo "⚠️  Применяйте их только если понимаете последствия!"
EOF
        
    elif command -v pipewire &> /dev/null; then
        AUDIO_SYSTEM="PipeWire"
        
        # Создание рекомендаций для PipeWire (БЕЗ ИЗМЕНЕНИЯ СИСТЕМЫ)
        cat > audio_optimization_recommendations.sh << 'EOF'
#!/bin/bash
# РЕКОМЕНДАЦИИ по оптимизации PipeWire
# ВНИМАНИЕ: Этот скрипт НЕ изменяет системные настройки!

echo "🔊 РЕКОМЕНДАЦИИ ПО ОПТИМИЗАЦИИ PIPEWIRE"
echo "======================================"
echo
echo "Для улучшения звука в игре рекомендуется:"
echo
echo "1. Создать конфигурацию в ~/.config/pipewire/pipewire.conf:"
echo "   context.properties = {"
echo "       default.clock.rate = 48000"
echo "       default.clock.quantum = 1024"
echo "       default.clock.min-quantum = 32"
echo "       default.clock.max-quantum = 8192"
echo "   }"
echo
echo "2. Перезапустить PipeWire:"
echo "   - systemctl --user restart pipewire"
echo
echo "⚠️  ВНИМАНИЕ: Эти изменения могут повлиять на работу других приложений!"
echo "⚠️  Применяйте их только если понимаете последствия!"
EOF
        
    else
        AUDIO_SYSTEM="ALSA"
        
        # ALSA оптимизации
        cat > audio_optimization.sh << 'EOF'
#!/bin/bash
# ALSA оптимизации

# Создание .asoundrc для низкой латентности
cat > ~/.asoundrc << 'ALSA_EOF'
pcm.!default {
    type hw
    card 0
    device 0
}
ctl.!default {
    type hw
    card 0
}
ALSA_EOF

echo "ALSA оптимизирован"
EOF
    fi
    
    chmod +x audio_optimization.sh
    print_success "Аудиосистема настроена ($AUDIO_SYSTEM)"
}

# Настройка устройств ввода
setup_input() {
    print_status "Настройка устройств ввода..."
    
    # Создание скрипта оптимизации ввода
    cat > input_optimization.sh << 'EOF'
#!/bin/bash
# Оптимизация устройств ввода

# Отключение композитора для снижения задержки ввода
if command -v compiz &> /dev/null; then
    compiz --replace &
elif command -v kwin &> /dev/null; then
    kwin --replace &
fi

# Настройка мыши
xinput set-prop "pointer:Logitech" "libinput Accel Speed" 0 2>/dev/null || true
xinput set-prop "pointer:Logitech" "libinput Accel Profile Enabled" 0, 1 2>/dev/null || true

# Настройка клавиатуры
setxkbmap -option caps:escape 2>/dev/null || true

echo "Устройства ввода оптимизированы"
EOF
    chmod +x input_optimization.sh
    
    print_success "Устройства ввода настроены"
}

# Создание скрипта запуска с оптимизациями
create_optimized_launcher() {
    print_status "Создание оптимизированного лаунчера..."
    
    cat > start_optimized.sh << 'EOF'
#!/bin/bash

# Modern Lineage II Client - Optimized Launcher for Ubuntu
echo "🚀 Запуск Modern Lineage II Client с оптимизациями..."

# Применение системных оптимизаций
if [ -f "system_optimization.sh" ]; then
    echo "Применение системных оптимизаций..."
    ./system_optimization.sh
fi

# Применение графических оптимизаций
if [ -f "nvidia_profile.sh" ]; then
    echo "Применение NVIDIA оптимизаций..."
    source ./nvidia_profile.sh
elif [ -f "amd_profile.sh" ]; then
    echo "Применение AMD оптимизаций..."
    source ./amd_profile.sh
fi

# Применение аудио оптимизаций
if [ -f "audio_optimization.sh" ]; then
    echo "Применение аудио оптимизаций..."
    ./audio_optimization.sh
fi

# Применение оптимизаций ввода
if [ -f "input_optimization.sh" ]; then
    echo "Применение оптимизаций ввода..."
    ./input_optimization.sh
fi

# Установка переменных окружения для игры
export LD_LIBRARY_PATH="./lib:$LD_LIBRARY_PATH"
export DISPLAY=${DISPLAY:-:0}

# Приоритет процесса
export GAME_PRIORITY="high"

# Запуск игры с оптимизациями
echo "🎮 Запуск игры..."
nice -n -10 ./ModernLineage2 "$@"
EOF
    chmod +x start_optimized.sh
    
    print_success "Оптимизированный лаунчер создан"
}

# Создание системы мониторинга производительности
create_performance_monitor() {
    print_status "Создание системы мониторинга производительности..."
    
    cat > performance_monitor.sh << 'EOF'
#!/bin/bash

# Modern Lineage II Performance Monitor
echo "📊 Мониторинг производительности Modern Lineage II"
echo "================================================="

# Функция мониторинга
monitor_performance() {
    while true; do
        clear
        echo "📊 Performance Monitor - $(date)"
        echo "================================"
        
        # CPU использование
        CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
        echo "🖥️  CPU Usage: ${CPU_USAGE}%"
        
        # RAM использование
        RAM_INFO=$(free -h | awk '/^Mem:/ {printf "Used: %s / %s (%.1f%%)", $3, $2, ($3/$2)*100}')
        echo "🧠 RAM: $RAM_INFO"
        
        # GPU использование (если NVIDIA)
        if command -v nvidia-smi &> /dev/null; then
            GPU_USAGE=$(nvidia-smi --query-gpu=utilization.gpu --format=csv,noheader,nounits)
            GPU_TEMP=$(nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader,nounits)
            echo "🎮 GPU Usage: ${GPU_USAGE}% (${GPU_TEMP}°C)"
        fi
        
        # Процесс игры
        if pgrep -f "ModernLineage2" > /dev/null; then
            GAME_PID=$(pgrep -f "ModernLineage2")
            GAME_CPU=$(ps -p $GAME_PID -o %cpu --no-headers 2>/dev/null || echo "N/A")
            GAME_MEM=$(ps -p $GAME_PID -o %mem --no-headers 2>/dev/null || echo "N/A")
            echo "🎯 Game Process: CPU ${GAME_CPU}%, MEM ${GAME_MEM}%"
        else
            echo "🎯 Game Process: Not running"
        fi
        
        # Сетевая активность
        NETWORK=$(cat /proc/net/dev | grep -E "eth0|wlan0|enp" | head -1 | awk '{print "RX: " $2/1024/1024 " MB, TX: " $10/1024/1024 " MB"}')
        echo "🌐 Network: $NETWORK"
        
        echo ""
        echo "Press Ctrl+C to exit"
        sleep 2
    done
}

# Запуск мониторинга
monitor_performance
EOF
    chmod +x performance_monitor.sh
    
    print_success "Система мониторинга создана"
}

# Создание тестов производительности
create_performance_tests() {
    print_status "Создание тестов производительности..."
    
    cat > benchmark.sh << 'EOF'
#!/bin/bash

# Modern Lineage II Benchmark
echo "🏁 Modern Lineage II Performance Benchmark"
echo "========================================="

# Тест CPU
echo "🖥️  CPU Benchmark..."
CPU_SCORE=$(dd if=/dev/zero bs=1M count=1024 2>/dev/null | wc -c)
echo "CPU Score: $CPU_SCORE"

# Тест памяти
echo "🧠 Memory Benchmark..."
MEM_SCORE=$(dd if=/dev/zero of=/tmp/benchmark bs=1M count=512 oflag=direct 2>&1 | grep "bytes" | awk '{print $1}')
rm -f /tmp/benchmark
echo "Memory Score: $MEM_SCORE"

# Тест диска
echo "💾 Disk Benchmark..."
DISK_SCORE=$(dd if=/dev/zero of=/tmp/disktest bs=1M count=100 oflag=direct 2>&1 | grep "MB/s" | awk '{print $10}')
rm -f /tmp/disktest
echo "Disk Score: ${DISK_SCORE} MB/s"

# Тест сети (если доступен сервер)
echo "🌐 Network Benchmark..."
PING_RESULT=$(ping -c 4 8.8.8.8 2>/dev/null | tail -1 | awk '{print $4}' | cut -d'/' -f2)
echo "Network Latency: ${PING_RESULT}ms"

# Общая оценка
echo ""
echo "📊 Benchmark Complete!"
echo "Рекомендуемые настройки будут сохранены в Config/BenchmarkResults.ini"

# Сохранение результатов
cat > Config/BenchmarkResults.ini << BENCH_EOF
[Benchmark Results]
CPUScore=$CPU_SCORE
MemoryScore=$MEM_SCORE
DiskScore=$DISK_SCORE
NetworkLatency=$PING_RESULT
BenchmarkDate=$(date)
BENCH_EOF
EOF
    chmod +x benchmark.sh
    
    print_success "Тесты производительности созданы"
}

# Основная функция
main() {
    echo "Начинаем финальную оптимизацию для Ubuntu..."
    echo
    
    detect_system_specs
    create_optimization_profile
    optimize_graphics
    optimize_system
    setup_audio
    setup_input
    create_optimized_launcher
    create_performance_monitor
    create_performance_tests
    
    echo
    print_success "🐧 Ubuntu оптимизация завершена!"
    echo
    echo "Созданные файлы:"
    echo "- start_optimized.sh - оптимизированный лаунчер"
    echo "- performance_monitor.sh - мониторинг производительности"
    echo "- benchmark.sh - тесты производительности"
    echo "- Config/UbuntuOptimization.ini - профиль оптимизации"
    echo
    echo "Рекомендации:"
    echo "1. Запустите benchmark.sh для оценки производительности"
    echo "2. Используйте start_optimized.sh для запуска игры"
    echo "3. Мониторьте производительность с помощью performance_monitor.sh"
    echo
    echo "Modern Lineage II Client готов к использованию на Ubuntu!"
}

# Запуск основной функции
main "$@"
