#!/bin/bash

# Modern Lineage II Simple UE4 Client Launcher
# Запуск простого UE4 клиента с графическим интерфейсом

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
    echo -e "${PURPLE}🎮 Modern Lineage II Simple UE4 Client Launcher${NC}"
    echo -e "${CYAN}===============================================${NC}"
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
    print_loading "Проверка зависимостей Simple UE4"
    
    # Проверка X11
    if ! command -v xdpyinfo &> /dev/null; then
        print_error "X11 не найден. Устанавливаю..."
        sudo apt install -y x11-utils
    else
        print_success "X11 доступен ✓"
    fi
}

# Проверка исполняемого файла
check_executable() {
    print_loading "Проверка исполняемого файла Simple UE4 клиента"
    
    if [ ! -f "SimpleUE4Client" ]; then
        print_error "Исполняемый файл SimpleUE4Client не найден"
        print_info "Компилирую Simple UE4 клиент..."
        g++ -std=c++17 -O2 -o SimpleUE4Client simple_ue4_client.cpp -lX11 -lpthread
    fi
    
    if [ ! -x "SimpleUE4Client" ]; then
        print_info "Устанавливаю права на выполнение..."
        chmod +x SimpleUE4Client
    fi
    
    print_success "Исполняемый файл готов ✓"
}

# Настройка переменных окружения
setup_environment() {
    print_loading "Настройка переменных окружения Simple UE4"
    
    export DISPLAY=${DISPLAY:-:0}
    
    print_success "Переменные окружения настроены ✓"
}

# Запуск Simple UE4 клиента
launch_simple_ue4_client() {
    print_loading "Запуск Modern Lineage II Simple UE4 Client"
    
    print_info "Запускаю Simple UE4 клиент..."
    print_info "Окно игры откроется через несколько секунд..."
    
    # Запуск в фоновом режиме
    nohup ./SimpleUE4Client > /tmp/simpleue4client.log 2>&1 &
    CLIENT_PID=$!
    
    # Сохранение PID для мониторинга
    echo $CLIENT_PID > /tmp/simpleue4client.pid
    
    print_success "Simple UE4 клиент запущен (PID: $CLIENT_PID) ✓"
    print_info "Логи клиента: /tmp/simpleue4client.log"
    print_info "PID файл: /tmp/simpleue4client.pid"
    
    # Проверка что процесс запустился
    sleep 3
    if ps -p $CLIENT_PID > /dev/null; then
        print_success "Simple UE4 клиент успешно запущен! ✓"
        echo ""
        echo -e "${GREEN}🎮 Modern Lineage II Simple UE4 Client запущен!${NC}"
        echo -e "${CYAN}Окно игры должно быть видно на экране${NC}"
        echo -e "${YELLOW}Для остановки клиента используйте: kill $CLIENT_PID${NC}"
        echo -e "${YELLOW}Или выполните: pkill -f SimpleUE4Client${NC}"
        echo ""
        echo -e "${BLUE}Управление:${NC}"
        echo -e "${BLUE}• ESC - Выход из игры${NC}"
        echo -e "${BLUE}• ЛКМ - Взаимодействие${NC}"
        echo -e "${BLUE}• Изменение размера окна поддерживается${NC}"
        echo ""
        echo -e "${GREEN}Теперь вы должны видеть графическое окно игры!${NC}"
    else
        print_error "Не удалось запустить Simple UE4 клиент. Проверьте логи: /tmp/simpleue4client.log"
        exit 1
    fi
}

# Главная функция
main() {
    clear
    print_header
    
    echo -e "${BLUE}🚀 Запуск Modern Lineage II Simple UE4 Client...${NC}"
    echo ""
    
    check_dependencies
    check_executable
    setup_environment
    launch_simple_ue4_client
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
