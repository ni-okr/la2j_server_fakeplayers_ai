#!/bin/bash

# Modern Lineage II UE4 Client Launcher
# Запуск UE4 клиента

set -e

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
PURPLE='\033[0;35m'
NC='\033[0m'

print_header() {
    echo -e "${PURPLE}🎮 Modern Lineage II UE4 Client Launcher${NC}"
    echo -e "${CYAN}==========================================${NC}"
}

print_loading() {
    echo -e "${BLUE}[LOADING]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

# Проверка зависимостей
check_dependencies() {
    print_loading "Проверка зависимостей UE4"
    
    # Проверка X11
    if ! command -v xdpyinfo &> /dev/null; then
        print_error "X11 не найден. Устанавливаю..."
        sudo apt install -y x11-utils
    else
        print_success "X11 доступен ✓"
    fi
    
    # Проверка OpenGL
    if ! glxinfo | grep -q "OpenGL version"; then
        print_error "OpenGL не найден. Устанавливаю..."
        sudo apt install -y mesa-utils
    else
        print_success "OpenGL доступен ✓"
    fi
}

# Проверка исполняемого файла
check_executable() {
    print_loading "Проверка исполняемого файла UE4 клиента"
    
    if [ ! -f "ModernLineage2" ]; then
        print_error "Исполняемый файл ModernLineage2 не найден"
        print_info "Компилирую UE4 клиент..."
        g++ -std=c++17 -O2 -o ModernLineage2 ue4_client.cpp -lX11 -lGL -lGLU -lpthread
    fi
    
    if [ ! -x "ModernLineage2" ]; then
        print_info "Устанавливаю права на выполнение..."
        chmod +x ModernLineage2
    fi
    
    print_success "Исполняемый файл готов ✓"
}

# Настройка переменных окружения
setup_environment() {
    print_loading "Настройка переменных окружения UE4"
    
    export DISPLAY=${DISPLAY:-:0}
    export MESA_GL_VERSION_OVERRIDE=3.3
    export MESA_GLSL_VERSION_OVERRIDE=330
    
    print_success "Переменные окружения настроены ✓"
}

# Запуск UE4 клиента
launch_ue4_client() {
    print_loading "Запуск Modern Lineage II UE4 Client"
    
    print_info "Запускаю UE4 клиент..."
    print_info "Клиент запускается в фоновом режиме..."
    
    # Запуск в фоновом режиме
    nohup ./ModernLineage2 > /tmp/ue4client.log 2>&1 &
    CLIENT_PID=$!
    
    # Сохранение PID для мониторинга
    echo $CLIENT_PID > /tmp/ue4client.pid
    
    print_success "UE4 клиент запущен (PID: $CLIENT_PID) ✓"
    print_info "Логи клиента: /tmp/ue4client.log"
    print_info "PID файл: /tmp/ue4client.pid"
    
    # Проверка что процесс запустился
    sleep 3
    if ps -p $CLIENT_PID > /dev/null; then
        print_success "UE4 клиент успешно запущен! ✓"
        echo ""
        echo -e "${GREEN}🎮 Modern Lineage II UE4 Client запущен!${NC}"
        echo -e "${CYAN}Для остановки клиента используйте: kill $CLIENT_PID${NC}"
        echo -e "${YELLOW}Или выполните: pkill -f ModernLineage2${NC}"
    else
        print_error "Не удалось запустить UE4 клиент. Проверьте логи: /tmp/ue4client.log"
        exit 1
    fi
}

# Главная функция
main() {
    clear
    print_header
    
    echo -e "${BLUE}🚀 Запуск Modern Lineage II UE4 Client...${NC}"
    echo ""
    
    check_dependencies
    check_executable
    setup_environment
    launch_ue4_client
}

# Обработка аргументов
case "$1" in
    --help|-h)
        echo "Использование: $0 [--help]"
        echo "  --help     Показать эту справку"
        exit 0
        ;;
    *)
        main
        ;;
esac
