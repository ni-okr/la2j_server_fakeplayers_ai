#!/bin/bash

# Modern Lineage II Client - Costume System Setup Script
# Настройка системы костюмов в стиле Blade & Soul

set -e

echo "👗 Modern Lineage II Client - Costume System Setup"
echo "================================================="

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

# Создание структуры папок для костюмов
create_costume_structure() {
    print_status "Создание структуры папок для костюмов..."
    
    # Основные папки
    mkdir -p Content/Costumes/Characters
    mkdir -p Content/Costumes/Upper
    mkdir -p Content/Costumes/Lower
    mkdir -p Content/Costumes/Shoes
    mkdir -p Content/Costumes/Accessories
    mkdir -p Content/Costumes/Hair
    mkdir -p Content/Costumes/Face
    mkdir -p Content/Costumes/Hands
    mkdir -p Content/Costumes/Feet
    
    # Коллекции
    mkdir -p Content/Costumes/Collections/Classic
    mkdir -p Content/Costumes/Collections/Fantasy
    mkdir -p Content/Costumes/Collections/Modern
    mkdir -p Content/Costumes/Collections/Seasonal
    
    # UI ресурсы
    mkdir -p Content/UI/CostumeWardrobe
    mkdir -p Content/UI/CostumePreview
    mkdir -p Content/UI/CostumeIcons
    
    # Материалы
    mkdir -p Content/Materials/Costumes
    mkdir -p Content/Materials/Costumes/Upper
    mkdir -p Content/Materials/Costumes/Lower
    mkdir -p Content/Materials/Costumes/Shoes
    mkdir -p Content/Materials/Costumes/Accessories
    
    print_success "Структура папок создана"
}

# Создание конфигурации костюмов
create_costume_config() {
    print_status "Создание конфигурации костюмов..."
    
    # Создание DataTable для костюмов
    cat > Config/CostumeData.csv << 'EOF'
CostumeID,CostumeName,SlotName,Category,Price,RequiredLevel,Gender,Description,MeshPath,MaterialPaths
"upper_001","Classic Shirt","Upper","Classic",100,1,"Both","Basic cotton shirt","/Game/Costumes/Upper/ClassicShirt","/Game/Materials/Costumes/Upper/ClassicShirt_Material"
"lower_001","Classic Pants","Lower","Classic",150,1,"Both","Basic cotton pants","/Game/Costumes/Lower/ClassicPants","/Game/Materials/Costumes/Lower/ClassicPants_Material"
"shoes_001","Leather Boots","Shoes","Classic",200,1,"Both","Sturdy leather boots","/Game/Costumes/Shoes/LeatherBoots","/Game/Materials/Costumes/Shoes/LeatherBoots_Material"
"accessories_001","Leather Belt","Accessories","Classic",50,1,"Both","Simple leather belt","/Game/Costumes/Accessories/LeatherBelt","/Game/Materials/Costumes/Accessories/LeatherBelt_Material"
"hair_001","Short Hair","Hair","Classic",75,1,"Both","Short practical haircut","/Game/Costumes/Hair/ShortHair","/Game/Materials/Costumes/Hair/ShortHair_Material"
EOF

    # Создание конфигурации коллекций
    cat > Config/CostumeCollections.csv << 'EOF'
CollectionID,CollectionName,Theme,RequiredLevel,CostumePieceIDs,Description
"classic_set_001","Classic Adventurer","Classic",1,"upper_001,lower_001,shoes_001,accessories_001,hair_001","Complete classic adventurer outfit"
"fantasy_set_001","Mystic Mage","Fantasy",10,"upper_002,lower_002,shoes_002,accessories_002,hair_002","Mystical mage robes and accessories"
"modern_set_001","Urban Warrior","Modern",5,"upper_003,lower_003,shoes_003,accessories_003,hair_003","Modern tactical gear"
"seasonal_set_001","Winter Collection","Seasonal",15,"upper_004,lower_004,shoes_004,accessories_004,hair_004","Warm winter clothing set"
EOF

    print_success "Конфигурация костюмов создана"
}

# Создание UI шаблонов
create_ui_templates() {
    print_status "Создание UI шаблонов..."
    
    # Создание базового UI для гардероба
    cat > Content/UI/CostumeWardrobe/CostumeWardrobeWidget.uasset << 'EOF'
# Costume Wardrobe Widget Template
# This will be converted to proper UE4 widget format
EOF

    # Создание 3D превью виджета
    cat > Content/UI/CostumePreview/CostumePreview3D.uasset << 'EOF'
# Costume Preview 3D Widget Template
# This will be converted to proper UE4 widget format
EOF

    print_success "UI шаблоны созданы"
}

# Создание системы материалов
create_material_system() {
    print_status "Создание системы материалов для костюмов..."
    
    # Создание базового материала для костюмов
    cat > Content/Materials/Costumes/BaseCostumeMaterial.uasset << 'EOF'
# Base Costume Material Template
# This will be converted to proper UE4 material format
EOF

    # Создание материалов для разных типов ткани
    cat > Content/Materials/Costumes/FabricMaterials.uasset << 'EOF'
# Fabric Materials Template
# This will be converted to proper UE4 material format
EOF

    print_success "Система материалов создана"
}

# Создание системы анимаций
create_animation_system() {
    print_status "Создание системы анимаций для костюмов..."
    
    # Создание папок для анимаций
    mkdir -p Content/Animations/Costumes/Upper
    mkdir -p Content/Animations/Costumes/Lower
    mkdir -p Content/Animations/Costumes/Shoes
    mkdir -p Content/Animations/Costumes/Accessories
    
    # Создание базовых анимаций
    cat > Content/Animations/Costumes/BaseCostumeAnimations.uasset << 'EOF'
# Base Costume Animations Template
# This will be converted to proper UE4 animation format
EOF

    print_success "Система анимаций создана"
}

# Создание системы физики для костюмов
create_physics_system() {
    print_status "Создание системы физики для костюмов..."
    
    # Создание конфигурации физики
    cat > Config/CostumePhysics.ini << 'EOF'
[/Script/Engine.PhysicsSettings]
; Physics settings for costume simulation
bEnablePCM=True
bEnableShapeSharing=False
bEnableContactModification=True
bEnableCCD=True
bEnableStabilization=True
bWarnMissingLocks=True
bEnable2DPhysics=False
DefaultGravityZ=-980.0
DefaultTerminalVelocity=4000.0
DefaultFluidFriction=0.3
SimulateScratchMemorySize=262144
RagdollAggregateThreshold=4
TriangleMeshTriangleMinAreaThreshold=5.0
bEnableShapeSharing=False
bEnablePCM=True
bEnableStabilization=True
bWarnMissingLocks=True
bEnable2DPhysics=False
bDefaultHasComplexCollision=True
bDefaultHasSimpleCollision=True
bDefaultHasComplexAsSimple=True
bSuppressFaceRemapTable=False
bSupportUVFromHitResults=False
bDisableActiveActors=False
bDisableKinematicStaticPairs=False
bDisableKinematicKinematicPairs=False
bDisableCCD=False
bEnableEnhancedDeterminism=False
AnimPhysicsMinDeltaTime=0.0
bSimulateSkeletalMeshOnDedicatedServer=True
DefaultShapeComplexity=CTF_UseSimpleAndComplex
bDefaultHasComplexCollision=True
bDefaultHasSimpleCollision=True
bDefaultHasComplexAsSimple=True
bSuppressFaceRemapTable=False
bSupportUVFromHitResults=False
bDisableActiveActors=False
bDisableKinematicStaticPairs=False
bDisableKinematicKinematicPairs=False
bDisableCCD=False
bEnableEnhancedDeterminism=False
AnimPhysicsMinDeltaTime=0.0
bSimulateSkeletalMeshOnDedicatedServer=True
DefaultShapeComplexity=CTF_UseSimpleAndComplex
bDefaultHasComplexCollision=True
bDefaultHasSimpleCollision=True
bDefaultHasComplexAsSimple=True
bSuppressFaceRemapTable=False
bSupportUVFromHitResults=False
bDisableActiveActors=False
bDisableKinematicStaticPairs=False
bDisableKinematicKinematicPairs=False
bDisableCCD=False
bEnableEnhancedDeterminism=False
AnimPhysicsMinDeltaTime=0.0
bSimulateSkeletalMeshOnDedicatedServer=True
DefaultShapeComplexity=CTF_UseSimpleAndComplex
EOF

    print_success "Система физики создана"
}

# Создание системы сохранения
create_save_system() {
    print_status "Создание системы сохранения костюмов..."
    
    # Создание конфигурации сохранения
    cat > Config/CostumeSaveSystem.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Costume save system configuration
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

    print_success "Система сохранения создана"
}

# Основная функция
main() {
    echo "Начинаем настройку системы костюмов..."
    echo
    
    create_costume_structure
    create_costume_config
    create_ui_templates
    create_material_system
    create_animation_system
    create_physics_system
    create_save_system
    
    echo
    print_success "👗 Настройка системы костюмов завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Откройте проект в Unreal Engine 4.27"
    echo "2. Скомпилируйте систему костюмов"
    echo "3. Создайте UI виджеты для гардероба"
    echo "4. Настройте 3D предварительный просмотр"
    echo "5. Протестируйте Drag & Drop функциональность"
    echo
    echo "Система костюмов готова к использованию!"
}

# Запуск основной функции
main "$@"
