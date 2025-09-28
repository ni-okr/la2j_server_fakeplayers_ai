#!/bin/bash

# Modern Lineage II UE4 Client GUI Launcher
# Запуск UE4 клиента с графическим интерфейсом

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
    echo -e "${PURPLE}🎮 Modern Lineage II UE4 Client GUI Launcher${NC}"
    echo -e "${CYAN}==============================================${NC}"
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
    print_loading "Проверка зависимостей UE4 GUI"
    
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
    
    # Проверка GLX
    if ! glxinfo | grep -q "GLX"; then
        print_error "GLX не найден"
        exit 1
    else
        print_success "GLX доступен ✓"
    fi
}

# Проверка исполняемого файла
check_executable() {
    print_loading "Проверка исполняемого файла UE4 GUI клиента"
    
    if [ ! -f "ModernLineage2GUI" ]; then
        print_error "Исполняемый файл ModernLineage2GUI не найден"
        print_info "Компилирую UE4 GUI клиент..."
        g++ -std=c++17 -O2 -o ModernLineage2GUI ue4_client_gui.cpp -lX11 -lGL -lGLU -lpthread
    fi
    
    if [ ! -x "ModernLineage2GUI" ]; then
        print_info "Устанавливаю права на выполнение..."
        chmod +x ModernLineage2GUI
    fi
    
    print_success "Исполняемый файл готов ✓"
}

# Настройка переменных окружения
setup_environment() {
    print_loading "Настройка переменных окружения UE4 GUI"
    
    export DISPLAY=${DISPLAY:-:0}
    export MESA_GL_VERSION_OVERRIDE=3.3
    export MESA_GLSL_VERSION_OVERRIDE=330
    
    print_success "Переменные окружения настроены ✓"
}

# Запуск UE4 GUI клиента
launch_ue4_gui_client() {
    print_loading "Запуск Modern Lineage II UE4 GUI Client"
    
    print_info "Запускаю UE4 GUI клиент..."
    print_info "Окно игры откроется через несколько секунд..."
    
    # Запуск в фоновом режиме
    nohup ./ModernLineage2GUI > /tmp/ue4guiclient.log 2>&1 &
    CLIENT_PID=$!
    
    # Сохранение PID для мониторинга
    echo $CLIENT_PID > /tmp/ue4guiclient.pid
    
    print_success "UE4 GUI клиент запущен (PID: $CLIENT_PID) ✓"
    print_info "Логи клиента: /tmp/ue4guiclient.log"
    print_info "PID файл: /tmp/ue4guiclient.pid"
    
    # Проверка что процесс запустился
    sleep 3
    if ps -p $CLIENT_PID > /dev/null; then
        print_success "UE4 GUI клиент успешно запущен! ✓"
        echo ""
        echo -e "${GREEN}🎮 Modern Lineage II UE4 GUI Client запущен!${NC}"
        echo -e "${CYAN}Окно игры должно быть видно на экране${NC}"
        echo -e "${YELLOW}Для остановки клиента используйте: kill $CLIENT_PID${NC}"
        echo -e "${YELLOW}Или выполните: pkill -f ModernLineage2GUI${NC}"
        echo ""
        echo -e "${BLUE}Управление:${NC}"
        echo -e "${BLUE}• ESC - Выход из игры${NC}"
        echo -e "${BLUE}• ЛКМ - Взаимодействие${NC}"
        echo -e "${BLUE}• Изменение размера окна поддерживается${NC}"
    else
        print_error "Не удалось запустить UE4 GUI клиент. Проверьте логи: /tmp/ue4guiclient.log"
        exit 1
    fi
}

# Главная функция
main() {
    clear
    print_header
    
    echo -e "${BLUE}🚀 Запуск Modern Lineage II UE4 GUI Client...${NC}"
    echo ""
    
    check_dependencies
    check_executable
    setup_environment
    launch_ue4_gui_client
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
