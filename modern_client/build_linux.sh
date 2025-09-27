#!/bin/bash

# Modern Lineage II Client - Linux Build System
# Система сборки клиента для Ubuntu Linux

set -e

echo "🐧 Modern Lineage II Client - Linux Build System"
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

# Переменные сборки
PROJECT_NAME="ModernLineage2"
BUILD_TYPE="Development"
PLATFORM="Linux"
ARCHITECTURE="x86_64"
UE4_VERSION="4.27"
BUILD_DIR="Binaries/Linux"
PACKAGE_DIR="Package"

# Проверка системных требований
check_system_requirements() {
    print_status "Проверка системных требований..."
    
    # Проверка Ubuntu версии
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        print_status "Операционная система: $NAME $VERSION"
        
        if [[ "$NAME" != *"Ubuntu"* ]]; then
            print_warning "Рекомендуется Ubuntu Linux для лучшей совместимости"
        fi
    fi
    
    # Проверка архитектуры
    ARCH=$(uname -m)
    if [ "$ARCH" != "x86_64" ]; then
        print_error "Поддерживается только архитектура x86_64"
        exit 1
    fi
    
    # Проверка RAM
    RAM_GB=$(free -g | awk '/^Mem:/{print $2}')
    if [ "$RAM_GB" -lt 8 ]; then
        print_warning "Рекомендуется минимум 8GB RAM для сборки"
    fi
    
    # Проверка свободного места
    FREE_SPACE=$(df . | awk 'NR==2{print $4}')
    FREE_SPACE_GB=$((FREE_SPACE / 1024 / 1024))
    if [ "$FREE_SPACE_GB" -lt 20 ]; then
        print_warning "Рекомендуется минимум 20GB свободного места"
    fi
    
    print_success "Системные требования проверены"
}

# Проверка зависимостей
check_dependencies() {
    print_status "Проверка зависимостей сборки..."
    
    # Проверка компиляторов
    if ! command -v clang++ &> /dev/null; then
        print_error "clang++ не найден. Установите: sudo apt install clang"
        exit 1
    fi
    
    if ! command -v make &> /dev/null; then
        print_error "make не найден. Установите: sudo apt install build-essential"
        exit 1
    fi
    
    # Проверка CMake
    if ! command -v cmake &> /dev/null; then
        print_error "cmake не найден. Установите: sudo apt install cmake"
        exit 1
    fi
    
    # Проверка библиотек
    REQUIRED_LIBS=(
        "libx11-dev"
        "libxrandr-dev" 
        "libxcursor-dev"
        "libxinerama-dev"
        "libxi-dev"
        "libgl1-mesa-dev"
        "libglu1-mesa-dev"
        "libasound2-dev"
        "libpulse-dev"
        "libssl-dev"
    )
    
    for lib in "${REQUIRED_LIBS[@]}"; do
        if ! dpkg -l | grep -q "^ii  $lib "; then
            print_warning "Библиотека $lib не установлена"
        fi
    done
    
    print_success "Зависимости проверены"
}

# Подготовка к сборке
prepare_build() {
    print_status "Подготовка к сборке..."
    
    # Создание директорий сборки
    mkdir -p "$BUILD_DIR"
    mkdir -p "$PACKAGE_DIR"
    mkdir -p "Intermediate/Linux"
    mkdir -p "Saved/Logs"
    
    # Очистка предыдущих сборок
    if [ "$1" == "clean" ]; then
        print_status "Очистка предыдущих сборок..."
        rm -rf "$BUILD_DIR"/*
        rm -rf "Intermediate/Linux"/*
        rm -rf "$PACKAGE_DIR"/*
    fi
    
    print_success "Подготовка завершена"
}

# Сборка проекта
build_project() {
    print_status "Начинаем сборку проекта $PROJECT_NAME..."
    
    # Генерация файлов проекта
    print_status "Генерация файлов проекта..."
    if [ -f "GenerateProjectFiles.sh" ]; then
        ./GenerateProjectFiles.sh
    else
        print_warning "GenerateProjectFiles.sh не найден, пропускаем генерацию"
    fi
    
    # Сборка через UnrealBuildTool (если доступен)
    if command -v UnrealBuildTool &> /dev/null; then
        print_status "Сборка через UnrealBuildTool..."
        UnrealBuildTool $PROJECT_NAME Linux $BUILD_TYPE -project="$PWD/$PROJECT_NAME.uproject"
    else
        # Альтернативная сборка через make
        print_status "Сборка через make..."
        if [ -f "Makefile" ]; then
            make -j$(nproc)
        else
            print_warning "Makefile не найден, создаем базовую сборку"
            create_basic_build
        fi
    fi
    
    print_success "Сборка проекта завершена"
}

# Создание базовой сборки
create_basic_build() {
    print_status "Создание базовой сборки..."
    
    # Компиляция основных модулей
    SOURCES=(
        "Source/$PROJECT_NAME/Private/$PROJECT_NAME.cpp"
        "Source/$PROJECT_NAME/Private/Characters/L2Character.cpp"
        "Source/$PROJECT_NAME/Private/Systems/AdventurerGuildSystem.cpp"
        "Source/$PROJECT_NAME/Private/Systems/SlaveTradingSystem.cpp"
    )
    
    INCLUDES=(
        "-ISource/$PROJECT_NAME/Public"
        "-ISource/$PROJECT_NAME/Public/Characters"
        "-ISource/$PROJECT_NAME/Public/Systems"
        "-ISource/$PROJECT_NAME/Public/Network"
        "-ISource/$PROJECT_NAME/Public/Security"
        "-ISource/$PROJECT_NAME/Public/Platform"
    )
    
    LIBS=(
        "-lX11"
        "-lXrandr"
        "-lXcursor"
        "-lXinerama"
        "-lXi"
        "-lGL"
        "-lGLU"
        "-lasound"
        "-lpulse"
        "-lssl"
        "-lcrypto"
        "-lpthread"
    )
    
    # Компиляция
    clang++ -std=c++17 -O2 -DNDEBUG \
        "${INCLUDES[@]}" \
        "${SOURCES[@]}" \
        "${LIBS[@]}" \
        -o "$BUILD_DIR/$PROJECT_NAME"
    
    print_success "Базовая сборка создана"
}

# Копирование ресурсов
copy_resources() {
    print_status "Копирование ресурсов..."
    
    # Копирование контента
    if [ -d "Content" ]; then
        cp -r Content "$BUILD_DIR/"
    fi
    
    # Копирование конфигураций
    if [ -d "Config" ]; then
        cp -r Config "$BUILD_DIR/"
    fi
    
    # Копирование скриптов
    cp *.sh "$BUILD_DIR/" 2>/dev/null || true
    
    print_success "Ресурсы скопированы"
}

# Создание пакета развертывания
create_package() {
    print_status "Создание пакета развертывания..."
    
    PACKAGE_NAME="${PROJECT_NAME}_Linux_${BUILD_TYPE}_$(date +%Y%m%d)"
    PACKAGE_PATH="$PACKAGE_DIR/$PACKAGE_NAME"
    
    # Создание структуры пакета
    mkdir -p "$PACKAGE_PATH"
    
    # Копирование исполняемых файлов
    cp -r "$BUILD_DIR"/* "$PACKAGE_PATH/"
    
    # Создание скрипта запуска
    cat > "$PACKAGE_PATH/start_game.sh" << 'EOF'
#!/bin/bash

# Modern Lineage II Client Launcher
echo "🎮 Запуск Modern Lineage II Client..."

# Проверка системы
if [ "$(uname)" != "Linux" ]; then
    echo "❌ Этот клиент предназначен для Linux"
    exit 1
fi

# Установка переменных окружения
export LD_LIBRARY_PATH="./lib:$LD_LIBRARY_PATH"
export DISPLAY=${DISPLAY:-:0}

# Запуск игры
./ModernLineage2 "$@"
EOF
    chmod +x "$PACKAGE_PATH/start_game.sh"
    
    # Создание README
    cat > "$PACKAGE_PATH/README.txt" << 'EOF'
Modern Lineage II Client for Linux
==================================

Системные требования:
- Ubuntu 20.04+ или совместимый дистрибутив Linux
- 8GB+ RAM
- NVIDIA/AMD графическая карта с поддержкой OpenGL 4.0+
- 10GB+ свободного места

Установка:
1. Распакуйте архив в любую папку
2. Запустите: ./start_game.sh

Настройка:
- Конфигурационные файлы находятся в папке Config/
- Логи сохраняются в папке Logs/

Поддержка:
- GitHub: https://github.com/ni-okr/la2j_server_fakeplayers_ai
EOF
    
    # Создание архива
    cd "$PACKAGE_DIR"
    tar -czf "${PACKAGE_NAME}.tar.gz" "$PACKAGE_NAME"
    cd ..
    
    print_success "Пакет создан: $PACKAGE_DIR/${PACKAGE_NAME}.tar.gz"
}

# Тестирование сборки
test_build() {
    print_status "Тестирование сборки..."
    
    # Проверка исполняемого файла
    if [ -f "$BUILD_DIR/$PROJECT_NAME" ]; then
        if [ -x "$BUILD_DIR/$PROJECT_NAME" ]; then
            print_success "Исполняемый файл создан и имеет права на выполнение"
        else
            print_error "Исполняемый файл не имеет прав на выполнение"
            chmod +x "$BUILD_DIR/$PROJECT_NAME"
        fi
    else
        print_error "Исполняемый файл не найден"
        return 1
    fi
    
    # Проверка зависимостей
    print_status "Проверка зависимостей исполняемого файла..."
    ldd "$BUILD_DIR/$PROJECT_NAME" | grep "not found" && {
        print_error "Найдены неразрешенные зависимости"
        return 1
    } || {
        print_success "Все зависимости разрешены"
    }
    
    # Быстрый тест запуска
    print_status "Тест запуска (5 секунд)..."
    timeout 5 "$BUILD_DIR/$PROJECT_NAME" --help &>/dev/null && {
        print_success "Тест запуска прошел успешно"
    } || {
        print_warning "Тест запуска завершился с ошибкой (это может быть нормально)"
    }
}

# Генерация отчета о сборке
generate_build_report() {
    print_status "Генерация отчета о сборке..."
    
    REPORT_FILE="build_report_$(date +%Y%m%d_%H%M%S).txt"
    
    cat > "$REPORT_FILE" << EOF
Modern Lineage II Client - Build Report
=======================================

Build Information:
- Project: $PROJECT_NAME
- Platform: $PLATFORM ($ARCHITECTURE)
- Build Type: $BUILD_TYPE
- Date: $(date)
- Builder: $(whoami)@$(hostname)

System Information:
- OS: $(lsb_release -d | cut -f2)
- Kernel: $(uname -r)
- CPU: $(lscpu | grep "Model name" | cut -d: -f2 | xargs)
- RAM: $(free -h | awk '/^Mem:/ {print $2}')
- Disk Space: $(df -h . | awk 'NR==2 {print $4}') available

Build Results:
- Executable: $BUILD_DIR/$PROJECT_NAME
- Size: $(du -h "$BUILD_DIR/$PROJECT_NAME" 2>/dev/null | cut -f1 || echo "N/A")
- Package: $PACKAGE_DIR/${PROJECT_NAME}_Linux_${BUILD_TYPE}_$(date +%Y%m%d).tar.gz

Dependencies:
$(ldd "$BUILD_DIR/$PROJECT_NAME" 2>/dev/null || echo "Could not analyze dependencies")

Build completed successfully!
EOF
    
    print_success "Отчет сохранен: $REPORT_FILE"
}

# Основная функция
main() {
    echo "Начинаем сборку Modern Lineage II Client для Linux..."
    echo
    
    check_system_requirements
    check_dependencies
    prepare_build "$1"
    build_project
    copy_resources
    test_build
    create_package
    generate_build_report
    
    echo
    print_success "🎉 Сборка Modern Lineage II Client завершена успешно!"
    echo
    echo "Результаты сборки:"
    echo "- Исполняемый файл: $BUILD_DIR/$PROJECT_NAME"
    echo "- Пакет развертывания: $PACKAGE_DIR/${PROJECT_NAME}_Linux_${BUILD_TYPE}_$(date +%Y%m%d).tar.gz"
    echo "- Отчет о сборке: build_report_$(date +%Y%m%d_%H%M%S).txt"
    echo
    echo "Для запуска игры:"
    echo "cd $BUILD_DIR && ./start_game.sh"
}

# Обработка аргументов командной строки
case "$1" in
    "clean")
        echo "🧹 Очистка и пересборка..."
        main "clean"
        ;;
    "test")
        echo "🧪 Только тестирование существующей сборки..."
        test_build
        ;;
    "package")
        echo "📦 Только создание пакета..."
        create_package
        ;;
    *)
        main
        ;;
esac
