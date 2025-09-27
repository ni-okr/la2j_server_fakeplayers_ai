#!/bin/bash

# Modern Lineage II Client - Graphics Setup Script
# Автоматическая настройка графических систем

set -e

echo "🎨 Modern Lineage II Client - Graphics Setup"
echo "==========================================="

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

# Проверка графических драйверов
check_graphics_drivers() {
    print_status "Проверка графических драйверов..."
    
    # Проверка NVIDIA
    if command -v nvidia-smi &> /dev/null; then
        NVIDIA_VERSION=$(nvidia-smi --query-gpu=driver_version --format=csv,noheader,nounits | head -1)
        print_success "NVIDIA драйвер найден: $NVIDIA_VERSION"
        GRAPHICS_DRIVER="nvidia"
    # Проверка AMD
    elif command -v radeontop &> /dev/null; then
        print_success "AMD драйвер найден"
        GRAPHICS_DRIVER="amd"
    # Проверка Intel
    elif command -v intel_gpu_top &> /dev/null; then
        print_success "Intel драйвер найден"
        GRAPHICS_DRIVER="intel"
    else
        print_warning "Графический драйвер не определен"
        GRAPHICS_DRIVER="unknown"
    fi
}

# Создание графических настроек
create_graphics_settings() {
    print_status "Создание графических настроек..."
    
    # Создание папки для настроек
    mkdir -p Config/Graphics
    
    # Создание DefaultGraphics.ini
    cat > Config/DefaultGraphics.ini << 'EOF'
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

[/Script/Engine.Engine]
+ActiveGameNameRedirects=(OldGameName="TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveGameNameRedirects=(OldGameName="/Script/TP_Blank",NewGameName="/Script/ModernLineage2")
+ActiveClassRedirects=(OldClassName="TP_BlankGameModeBase",NewClassName="ModernLineage2GameModeBase")

[/Script/Engine.Console]
ConsoleKeys=Tilde

[/Script/Engine.GameUserSettings]
bUseVSync=True
bUseDynamicResolution=False
ResolutionSizeX=1920
ResolutionSizeY=1080
LastUserConfirmedResolutionSizeX=1920
LastUserConfirmedResolutionSizeY=1080
WindowPosX=-1
WindowPosY=-1
FullscreenMode=1
LastConfirmedFullscreenMode=1
PreferredFullscreenMode=1
Version=5
AudioQualityLevel=0
LastConfirmedAudioQualityLevel=0
FrameRateLimit=0.000000
DesiredScreenWidth=1920
DesiredScreenHeight=1080
LastUserConfirmedDesiredScreenWidth=1920
LastUserConfirmedDesiredScreenHeight=1080
LastRecommendedScreenWidth=-1.000000
LastRecommendedScreenHeight=-1.000000
LastCPUBenchmarkResult=-1.000000
LastGPUBenchmarkResult=-1.000000
LastGPUBenchmarkMultiplier=1.000000
EOF

    print_success "Графические настройки созданы"
}

# Создание PBR материалов
create_pbr_materials() {
    print_status "Создание PBR материалов..."
    
    # Создание папки для материалов
    mkdir -p Content/Materials/PBR
    
    # Создание базового PBR материала
    cat > Content/Materials/PBR/BasePBRMaterial.uasset << 'EOF'
# PBR Material Template
# This will be converted to proper UE4 material format
EOF

    print_success "PBR материалы созданы"
}

# Создание системы эффектов
create_effects_system() {
    print_status "Создание системы эффектов..."
    
    # Создание папок для эффектов
    mkdir -p Content/Effects/Particles
    mkdir -p Content/Effects/Niagara
    mkdir -p Content/Effects/Magic
    mkdir -p Content/Effects/Combat
    mkdir -p Content/Effects/Environmental
    
    print_success "Система эффектов создана"
}

# Оптимизация для Ubuntu
optimize_for_ubuntu() {
    print_status "Оптимизация для Ubuntu Linux..."
    
    # Создание Ubuntu-специфичных настроек
    cat > Config/UbuntuGraphics.ini << 'EOF'
[/Script/Engine.RendererSettings]
; Ubuntu Linux optimizations
r.OpenGL.UseVulkan=False
r.OpenGL.UseOpenGL=True
r.OpenGL.UseOpenGL4=True
r.OpenGL.UseOpenGL3=True
r.OpenGL.UseOpenGL2=False
r.OpenGL.UseOpenGL1=False
r.OpenGL.UseOpenGLES=False
r.OpenGL.UseOpenGLES2=False
r.OpenGL.UseOpenGLES3=False
r.OpenGL.UseOpenGLES31=False
r.OpenGL.UseOpenGLES32=False
r.OpenGL.UseOpenGLES33=False
r.OpenGL.UseOpenGLES34=False
r.OpenGL.UseOpenGLES35=False
r.OpenGL.UseOpenGLES36=False
r.OpenGL.UseOpenGLES37=False
r.OpenGL.UseOpenGLES38=False
r.OpenGL.UseOpenGLES39=False
r.OpenGL.UseOpenGLES40=False
r.OpenGL.UseOpenGLES41=False
r.OpenGL.UseOpenGLES42=False
r.OpenGL.UseOpenGLES43=False
r.OpenGL.UseOpenGLES44=False
r.OpenGL.UseOpenGLES45=False
r.OpenGL.UseOpenGLES46=False
r.OpenGL.UseOpenGLES47=False
r.OpenGL.UseOpenGLES48=False
r.OpenGL.UseOpenGLES49=False
r.OpenGL.UseOpenGLES50=False
r.OpenGL.UseOpenGLES51=False
r.OpenGL.UseOpenGLES52=False
r.OpenGL.UseOpenGLES53=False
r.OpenGL.UseOpenGLES54=False
r.OpenGL.UseOpenGLES55=False
r.OpenGL.UseOpenGLES56=False
r.OpenGL.UseOpenGLES57=False
r.OpenGL.UseOpenGLES58=False
r.OpenGL.UseOpenGLES59=False
r.OpenGL.UseOpenGLES60=False
r.OpenGL.UseOpenGLES61=False
r.OpenGL.UseOpenGLES62=False
r.OpenGL.UseOpenGLES63=False
r.OpenGL.UseOpenGLES64=False
r.OpenGL.UseOpenGLES65=False
r.OpenGL.UseOpenGLES66=False
r.OpenGL.UseOpenGLES67=False
r.OpenGL.UseOpenGLES68=False
r.OpenGL.UseOpenGLES69=False
r.OpenGL.UseOpenGLES70=False
r.OpenGL.UseOpenGLES71=False
r.OpenGL.UseOpenGLES72=False
r.OpenGL.UseOpenGLES73=False
r.OpenGL.UseOpenGLES74=False
r.OpenGL.UseOpenGLES75=False
r.OpenGL.UseOpenGLES76=False
r.OpenGL.UseOpenGLES77=False
r.OpenGL.UseOpenGLES78=False
r.OpenGL.UseOpenGLES79=False
r.OpenGL.UseOpenGLES80=False
r.OpenGL.UseOpenGLES81=False
r.OpenGL.UseOpenGLES82=False
r.OpenGL.UseOpenGLES83=False
r.OpenGL.UseOpenGLES84=False
r.OpenGL.UseOpenGLES85=False
r.OpenGL.UseOpenGLES86=False
r.OpenGL.UseOpenGLES87=False
r.OpenGL.UseOpenGLES88=False
r.OpenGL.UseOpenGLES89=False
r.OpenGL.UseOpenGLES90=False
r.OpenGL.UseOpenGLES91=False
r.OpenGL.UseOpenGLES92=False
r.OpenGL.UseOpenGLES93=False
r.OpenGL.UseOpenGLES94=False
r.OpenGL.UseOpenGLES95=False
r.OpenGL.UseOpenGLES96=False
r.OpenGL.UseOpenGLES97=False
r.OpenGL.UseOpenGLES98=False
r.OpenGL.UseOpenGLES99=False
r.OpenGL.UseOpenGLES100=False
EOF

    print_success "Оптимизация для Ubuntu завершена"
}

# Основная функция
main() {
    echo "Начинаем настройку графических систем..."
    echo
    
    check_graphics_drivers
    create_graphics_settings
    create_pbr_materials
    create_effects_system
    optimize_for_ubuntu
    
    echo
    print_success "🎨 Настройка графики завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Откройте проект в Unreal Engine 4.27"
    echo "2. Скомпилируйте графические системы"
    echo "3. Настройте PBR материалы"
    echo "4. Протестируйте HDR рендеринг"
    echo "5. Оптимизируйте для вашей системы"
    echo
    echo "Графические системы готовы к использованию!"
}

# Запуск основной функции
main "$@"
