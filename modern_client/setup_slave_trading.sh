#!/bin/bash

# Modern Lineage II Client - Slave Trading System Setup Script
# Настройка системы работорговли в стиле аниме фентези

set -e

echo "🔗 Modern Lineage II Client - Slave Trading System Setup"
echo "======================================================="

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

# Создание структуры папок для работорговли
create_slave_trading_structure() {
    print_status "Создание структуры папок для работорговли..."
    
    # Основные папки
    mkdir -p Content/Slaves/Characters
    mkdir -p Content/Slaves/Combat
    mkdir -p Content/Slaves/Labor
    mkdir -p Content/Slaves/Decorative
    mkdir -p Content/Slaves/Special
    
    # UI ресурсы
    mkdir -p Content/UI/SlaveMarket
    mkdir -p Content/UI/SlaveManagement
    mkdir -p Content/UI/SlaveIcons
    
    # Анимации
    mkdir -p Content/Animations/Slaves/Combat
    mkdir -p Content/Animations/Slaves/Labor
    mkdir -p Content/Animations/Slaves/Decorative
    
    # Звуки
    mkdir -p Content/Sounds/Slaves/Voices
    mkdir -p Content/Sounds/Slaves/Effects
    
    print_success "Структура папок создана"
}

# Создание конфигурации рабов
create_slave_config() {
    print_status "Создание конфигурации рабов..."
    
    # Создание DataTable для рабов
    cat > Config/SlaveData.csv << 'EOF'
SlaveID,SlaveName,SlaveType,Price,BaseLoyalty,RequiredLevel,Gender,Description,MeshPath,MaterialPaths,Skills
"slave_001","Elven Warrior","Combat",500,50,5,"Female","Skilled elven warrior with combat experience","/Game/Slaves/Combat/ElvenWarrior","/Game/Materials/Slaves/Combat/ElvenWarrior_Material","Combat,Leadership"
"slave_002","Dwarf Blacksmith","Labor",300,60,3,"Male","Expert blacksmith and craftsman","/Game/Slaves/Labor/DwarfBlacksmith","/Game/Materials/Slaves/Labor/DwarfBlacksmith_Material","Crafting,Mining"
"slave_003","Human Dancer","Decorative",200,40,1,"Female","Beautiful dancer and entertainer","/Game/Slaves/Decorative/HumanDancer","/Game/Materials/Slaves/Decorative/HumanDancer_Material","Entertainment,Charm"
"slave_004","Orc Laborer","Labor",150,70,2,"Male","Strong orc for heavy labor","/Game/Slaves/Labor/OrcLaborer","/Game/Materials/Slaves/Labor/OrcLaborer_Material","Labor,Strength"
"slave_005","Dark Elf Assassin","Combat",800,30,10,"Female","Deadly assassin with stealth skills","/Game/Slaves/Combat/DarkElfAssassin","/Game/Materials/Slaves/Combat/DarkElfAssassin_Material","Stealth,Assassination"
"slave_006","Goblin Merchant","Labor",100,80,1,"Male","Cunning goblin trader","/Game/Slaves/Labor/GoblinMerchant","/Game/Materials/Slaves/Labor/GoblinMerchant_Material","Trading,Negotiation"
"slave_007","Angel Healer","Special",1000,90,15,"Female","Divine healer with powerful magic","/Game/Slaves/Special/AngelHealer","/Game/Materials/Slaves/Special/AngelHealer_Material","Healing,Divine Magic"
"slave_008","Demon Warrior","Combat",1200,20,20,"Male","Powerful demon warrior","/Game/Slaves/Combat/DemonWarrior","/Game/Materials/Slaves/Combat/DemonWarrior_Material","Combat,Dark Magic"
EOF

    # Создание конфигурации восстаний
    cat > Config/SlaveRebellionData.csv << 'EOF'
RebellionType,Severity,ConsequenceType,ConsequenceValue,Description
"Escape",1,"GoldLoss",100,"Slave escaped, lost 100 gold"
"Violence",3,"ItemLoss",1,"Slave became violent, lost 1 item"
"Sabotage",5,"ReputationLoss",50,"Slave sabotaged, lost 50 reputation"
"Organized",8,"SlaveDeath",1,"Organized rebellion, slave died"
"Mass",10,"GoldLoss",500,"Mass rebellion, lost 500 gold"
EOF

    print_success "Конфигурация рабов создана"
}

# Создание UI шаблонов
create_ui_templates() {
    print_status "Создание UI шаблонов..."
    
    # Создание UI для невольничьего рынка
    cat > Content/UI/SlaveMarket/SlaveMarketWidget.uasset << 'EOF'
# Slave Market Widget Template
# This will be converted to proper UE4 widget format
EOF

    # Создание UI для управления рабами
    cat > Content/UI/SlaveManagement/SlaveManagementWidget.uasset << 'EOF'
# Slave Management Widget Template
# This will be converted to proper UE4 widget format
EOF

    print_success "UI шаблоны созданы"
}

# Создание системы лояльности
create_loyalty_system() {
    print_status "Создание системы лояльности..."
    
    # Создание конфигурации лояльности
    cat > Config/SlaveLoyalty.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Slave loyalty system configuration
LoyaltyDecayTime=3600.0
LoyaltyDecayAmount=1
RebellionThreshold=20
MaxLoyalty=100
MinLoyalty=0
LoyaltyGainFromFeeding=5
LoyaltyGainFromTraining=3
LoyaltyGainFromReward=10
LoyaltyLossFromPunishment=15
LoyaltyLossFromNeglect=2
EOF

    print_success "Система лояльности создана"
}

# Создание системы восстаний
create_rebellion_system() {
    print_status "Создание системы восстаний..."
    
    # Создание конфигурации восстаний
    cat > Config/SlaveRebellion.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Slave rebellion system configuration
RebellionCheckInterval=60.0
MinLoyaltyForRebellion=20
BaseRebellionChance=0.01
LoyaltyDecayRate=0.1
SuppressionSuccessRate=0.7
NegotiationSuccessRate=0.5
EscapeChance=0.3
ViolenceChance=0.4
SabotageChance=0.2
OrganizedRebellionChance=0.1
EOF

    print_success "Система восстаний создана"
}

# Создание системы экономики
create_economy_system() {
    print_status "Создание системы экономики работорговли..."
    
    # Создание конфигурации экономики
    cat > Config/SlaveEconomy.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Slave trading economy configuration
BaseSlavePrice=100
PriceMultiplierByType=1.5
PriceMultiplierByLevel=1.2
PriceMultiplierByLoyalty=1.1
MarketFluctuationRate=0.1
SupplyDemandRatio=1.0
SeasonalPriceModifier=1.0
RarityMultiplier=2.0
SkillBonusMultiplier=1.3
EOF

    print_success "Система экономики создана"
}

# Создание системы квестов
create_quest_system() {
    print_status "Создание системы квестов работорговли..."
    
    # Создание папок для квестов
    mkdir -p Content/Quests/SlaveTrading
    mkdir -p Content/Quests/SlaveRescue
    mkdir -p Content/Quests/SlaveTraining
    
    # Создание конфигурации квестов
    cat > Config/SlaveQuests.csv << 'EOF'
QuestID,QuestName,QuestType,RequiredLevel,Reward,Description
"slave_quest_001","Rescue the Captured","Rescue",5,200,"Rescue a group of captured slaves"
"slave_quest_002","Train the Newcomer","Training",3,100,"Train a new slave to increase loyalty"
"slave_quest_003","Suppress the Rebellion","Suppression",10,500,"Suppress a slave rebellion"
"slave_quest_004","Find the Runaway","Search",7,300,"Find and capture a runaway slave"
"slave_quest_005","Negotiate Peace","Diplomacy",15,800,"Negotiate peace with rebellious slaves"
EOF

    print_success "Система квестов создана"
}

# Основная функция
main() {
    echo "Начинаем настройку системы работорговли..."
    echo
    
    create_slave_trading_structure
    create_slave_config
    create_ui_templates
    create_loyalty_system
    create_rebellion_system
    create_economy_system
    create_quest_system
    
    echo
    print_success "🔗 Настройка системы работорговли завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Откройте проект в Unreal Engine 4.27"
    echo "2. Скомпилируйте систему работорговли"
    echo "3. Создайте UI виджеты для рынка и управления"
    echo "4. Настройте систему лояльности и восстаний"
    echo "5. Протестируйте экономику работорговли"
    echo
    echo "Система работорговли готова к использованию!"
}

# Запуск основной функции
main "$@"
