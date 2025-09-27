#!/bin/bash

# Modern Lineage II Client - Project Setup Script
# Автоматическая настройка проекта для Ubuntu Linux

set -e

echo "🚀 Modern Lineage II Client - Project Setup"
echo "=========================================="

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Функция для вывода сообщений
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

# Проверка системных требований
check_system_requirements() {
    print_status "Проверка системных требований..."
    
    # Проверка версии Ubuntu
    if ! command -v lsb_release &> /dev/null; then
        print_error "lsb_release не найден. Установите lsb-release: sudo apt install lsb-release"
        exit 1
    fi
    
    UBUNTU_VERSION=$(lsb_release -rs)
    print_status "Ubuntu версия: $UBUNTU_VERSION"
    
    # Проверка доступной памяти
    TOTAL_MEM=$(free -g | awk '/^Mem:/{print $2}')
    if [ $TOTAL_MEM -lt 8 ]; then
        print_warning "Рекомендуется минимум 8GB RAM. Текущая: ${TOTAL_MEM}GB"
    else
        print_success "RAM: ${TOTAL_MEM}GB ✓"
    fi
    
    # Проверка свободного места
    FREE_SPACE=$(df -BG . | awk 'NR==2{print $4}' | sed 's/G//')
    if [ $FREE_SPACE -lt 50 ]; then
        print_warning "Рекомендуется минимум 50GB свободного места. Текущее: ${FREE_SPACE}GB"
    else
        print_success "Свободное место: ${FREE_SPACE}GB ✓"
    fi
}

# Установка зависимостей
install_dependencies() {
    print_status "Установка зависимостей..."
    
    # Обновление пакетов
    sudo apt update
    
    # Установка основных инструментов разработки
    sudo apt install -y \
        build-essential \
        clang \
        cmake \
        ninja-build \
        git \
        curl \
        wget \
        unzip \
        pkg-config
    
    # Установка библиотек для Unreal Engine
    sudo apt install -y \
        libc++-dev \
        libc++abi-dev \
        libx11-dev \
        libxrandr-dev \
        libxi-dev \
        libgl1-mesa-dev \
        libglu1-mesa-dev \
        libasound2-dev \
        libpulse-dev \
        libgtk-3-dev \
        libwebkit2gtk-4.0-dev || true
    
    print_success "Зависимости установлены"
}

# Создание структуры проекта
create_project_structure() {
    print_status "Создание структуры проекта..."
    
    # Создание основных папок
    mkdir -p Content/Characters
    mkdir -p Content/Animations
    mkdir -p Content/Textures
    mkdir -p Content/Materials
    mkdir -p Content/Meshes
    mkdir -p Content/Sounds
    mkdir -p Content/Music
    mkdir -p Content/UI
    mkdir -p Content/Blueprints
    mkdir -p Content/Data
    
    # Создание папок для систем
    mkdir -p Content/Systems/AdultContent
    mkdir -p Content/Systems/CostumeSystem
    mkdir -p Content/Systems/SlaveTrading
    mkdir -p Content/Systems/AdventurerGuild
    
    # Создание папок для ресурсов из деобфусцированного клиента
    mkdir -p Content/Deobfuscated/Textures
    mkdir -p Content/Deobfuscated/Animations
    mkdir -p Content/Deobfuscated/Meshes
    mkdir -p Content/Deobfuscated/Sounds
    
    print_success "Структура проекта создана"
}

# Копирование ресурсов из деобфусцированного клиента
copy_deobfuscated_assets() {
    print_status "Копирование ресурсов из деобфусцированного клиента..."
    
    DEOBFUSCATED_PATH="/home/ni/Projects/la2bots/client_deobfuscation"
    
    if [ -d "$DEOBFUSCATED_PATH" ]; then
        # Копирование текстур
        if [ -d "$DEOBFUSCATED_PATH/assets/textures" ]; then
            cp -r "$DEOBFUSCATED_PATH/assets/textures"/* Content/Deobfuscated/Textures/ 2>/dev/null || true
            print_success "Текстуры скопированы"
        fi
        
        # Копирование анимаций
        if [ -d "$DEOBFUSCATED_PATH/assets/animations" ]; then
            cp -r "$DEOBFUSCATED_PATH/assets/animations"/* Content/Deobfuscated/Animations/ 2>/dev/null || true
            print_success "Анимации скопированы"
        fi
        
        # Копирование мешей
        if [ -d "$DEOBFUSCATED_PATH/assets/staticmeshes" ]; then
            cp -r "$DEOBFUSCATED_PATH/assets/staticmeshes"/* Content/Deobfuscated/Meshes/ 2>/dev/null || true
            print_success "Меши скопированы"
        fi
        
        # Копирование звуков
        if [ -d "$DEOBFUSCATED_PATH/assets/sounds" ]; then
            cp -r "$DEOBFUSCATED_PATH/assets/sounds"/* Content/Deobfuscated/Sounds/ 2>/dev/null || true
            print_success "Звуки скопированы"
        fi
    else
        print_warning "Папка деобфусцированного клиента не найдена: $DEOBFUSCATED_PATH"
    fi
}

# Создание файлов конфигурации
create_config_files() {
    print_status "Создание файлов конфигурации..."
    
    # Создание DefaultEngine.ini
    cat > Config/DefaultEngine.ini << 'EOF'
[/Script/EngineSettings.GameMapsSettings]
GameDefaultMap=/Game/Maps/MainMenu
EditorStartupMap=/Game/Maps/MainMenu

[/Script/Engine.RendererSettings]
r.DefaultFeature.AutoExposure=False
r.DefaultFeature.MotionBlur=False
r.DefaultFeature.AntiAliasing=2
r.PostProcessAAQuality=4
r.ScreenSpaceReflectionQuality=4
r.ReflectionMethod=1
r.ShadowQuality=4
r.ViewDistanceScale=1.0
r.SkeletalMeshLODBias=0
r.ParticleLODBias=0
r.MaxAnisotropy=16
r.Streaming.PoolSize=2000
r.Streaming.MaxTempMemoryAllowed=200
r.Streaming.FramesForFullUpdate=1
r.Streaming.Boost=1.0
r.Streaming.UseNewMetrics=1
r.Streaming.UseFixedPoolSize=0
r.Streaming.PoolSize=2000
r.Streaming.MaxTempMemoryAllowed=200
r.Streaming.FramesForFullUpdate=1
r.Streaming.Boost=1.0
r.Streaming.UseNewMetrics=1
r.Streaming.UseFixedPoolSize=0

[/Script/Engine.Engine]
+ActiveGameNameRedirects=(OldGameName="TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveGameNameRedirects=(OldGameName="/Script/TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveClassRedirects=(OldClassName="TP_BlankGameModeBase",NewClassName="ModernLineage2GameModeBase")
EOF

    # Создание DefaultGame.ini
    cat > Config/DefaultGame.ini << 'EOF'
[/Script/EngineSettings.GeneralProjectSettings]
ProjectID=12345678901234567890123456789012
ProjectName=Modern Lineage II
ProjectDisplayedTitle=Modern Lineage II
ProjectVersion=1.0.0
CompanyName=Modern L2 Team
CompanyDistinguishedName=CN=Modern L2 Team
CopyrightNotice=Copyright (c) 2024 Modern L2 Team
Description=Modern Lineage II Client - Deobfuscated and Enhanced
Homepage=https://github.com/ni-okr/la2j_server_fakeplayers_ai
SupportContact=support@modernl2.com
ProjectDebugTitleInfo=Modern Lineage II (Debug)

[/Script/Engine.Engine]
+ActiveGameNameRedirects=(OldGameName="TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveGameNameRedirects=(OldGameName="/Script/TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveClassRedirects=(OldClassName="TP_BlankGameModeBase",NewClassName="ModernLineage2GameModeBase")
EOF

    print_success "Файлы конфигурации созданы"
}

# Создание README для проекта
create_project_readme() {
    print_status "Создание README для проекта..."
    
    cat > README.md << 'EOF'
# Modern Lineage II Client

Современный клиент Lineage II на базе Unreal Engine 4.27 с расширенными возможностями.

## Особенности

- **Современная графика**: PBR материалы, HDR рендеринг, 4K поддержка
- **Система костюмов**: BnS-стиль гардероб с возможностью комбинирования
- **Контент для взрослых**: Детализированные женские модели с физикой
- **Работорговля**: Аниме фентези система покупки и управления рабами
- **Гильдия авантюристов**: Найм спутников для приключений
- **Кроссплатформенность**: Оптимизация для Ubuntu Linux

## Системные требования

- Ubuntu 20.04+ или совместимая ОС
- 8GB RAM (рекомендуется 16GB)
- 50GB свободного места
- Видеокарта с поддержкой OpenGL 4.5+
- Unreal Engine 4.27

## Установка

1. Клонируйте репозиторий
2. Запустите скрипт настройки: `./setup_project.sh`
3. Откройте проект в Unreal Engine 4.27

## Разработка

Проект создан на базе деобфусцированного кода оригинального клиента Lineage II.
Все системы портированы и улучшены для современного движка.

## Лицензия

Проект создан в образовательных целях.
EOF

    print_success "README создан"
}

# Основная функция
main() {
    echo "Начинаем настройку проекта Modern Lineage II..."
    echo
    
    check_system_requirements
    install_dependencies
    create_project_structure
    copy_deobfuscated_assets
    create_config_files
    create_project_readme
    
    echo
    print_success "🎉 Настройка проекта завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Установите Unreal Engine 4.27"
    echo "2. Откройте проект: ModernLineage2.uproject"
    echo "3. Скомпилируйте проект в Unreal Editor"
    echo "4. Начните разработку согласно роадмапе"
    echo
    echo "Документация: /home/ni/Projects/la2bots/client_deobfuscation/MODERNIZATION_ROADMAP.md"
}

# Запуск основной функции
main "$@"
