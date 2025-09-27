#!/bin/bash

# Modern Lineage II Client - Adventurer Guild System Setup Script
# Настройка гильдии авантюристов в стиле аниме фентези

set -e

echo "🏰 Modern Lineage II Client - Adventurer Guild System Setup"
echo "========================================================="

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

# Создание структуры папок для гильдии авантюристов
create_guild_structure() {
    print_status "Создание структуры папок для гильдии авантюристов..."
    
    # Основные папки
    mkdir -p Content/Companions/Warriors
    mkdir -p Content/Companions/Mages
    mkdir -p Content/Companions/Archers
    mkdir -p Content/Companions/Clerics
    mkdir -p Content/Companions/Rogues
    mkdir -p Content/Companions/Special
    
    # UI ресурсы
    mkdir -p Content/UI/AdventurerGuild
    mkdir -p Content/UI/CompanionRecruitment
    mkdir -p Content/UI/CompanionManagement
    mkdir -p Content/UI/GroupQuests
    
    # Квесты
    mkdir -p Content/Quests/GroupQuests/Dungeons
    mkdir -p Content/Quests/GroupQuests/Bosses
    mkdir -p Content/Quests/GroupQuests/Exploration
    mkdir -p Content/Quests/GroupQuests/Escort
    mkdir -p Content/Quests/GroupQuests/Defense
    
    # Анимации
    mkdir -p Content/Animations/Companions/Combat
    mkdir -p Content/Animations/Companions/Idle
    mkdir -p Content/Animations/Companions/Social
    
    # Звуки
    mkdir -p Content/Sounds/Companions/Voices
    mkdir -p Content/Sounds/Companions/Combat
    mkdir -p Content/Sounds/Companions/Social
    
    print_success "Структура папок создана"
}

# Создание конфигурации компаньонов
create_companion_config() {
    print_status "Создание конфигурации компаньонов..."
    
    # Создание DataTable для компаньонов
    cat > Config/CompanionData.csv << 'EOF'
CompanionID,CompanionName,Class,Level,HireCost,RequiredReputation,BaseRelationship,PersonalityType,Description,MeshPath,MaterialPaths,Skills
"comp_001","Aria the Warrior","Warrior",5,300,50,60,"Brave","Skilled warrior with strong combat abilities","/Game/Companions/Warriors/Aria","/Game/Materials/Companions/Warriors/Aria_Material","Combat,Leadership,Shield"
"comp_002","Luna the Mage","Mage",7,500,75,50,"Mysterious","Powerful mage with arcane knowledge","/Game/Companions/Mages/Luna","/Game/Materials/Companions/Mages/Luna_Material","Magic,Healing,Enchanting"
"comp_003","Kai the Archer","Archer",4,250,25,70,"Independent","Expert archer with keen eyesight","/Game/Companions/Archers/Kai","/Game/Materials/Companions/Archers/Kai_Material","Archery,Scouting,Survival"
"comp_004","Sister Maria","Cleric",6,400,60,80,"Loyal","Devoted cleric with healing powers","/Game/Companions/Clerics/Maria","/Game/Materials/Companions/Clerics/Maria_Material","Healing,Blessing,Divine Magic"
"comp_005","Shadow the Rogue","Rogue",8,600,100,40,"Cautious","Stealthy rogue with thieving skills","/Game/Companions/Rogues/Shadow","/Game/Materials/Companions/Rogues/Shadow_Material","Stealth,Lockpicking,Poison"
"comp_006","Dragon Knight","Special",15,1500,200,30,"Brave","Legendary dragon knight","/Game/Companions/Special/DragonKnight","/Game/Materials/Companions/Special/DragonKnight_Material","Dragon Magic,Combat,Leadership"
"comp_007","Elven Ranger","Archer",10,800,150,55,"Independent","Elven ranger with nature magic","/Game/Companions/Archers/ElvenRanger","/Game/Materials/Companions/Archers/ElvenRanger_Material","Archery,Nature Magic,Scouting"
"comp_008","Dark Sorcerer","Mage",12,1000,180,35,"Mysterious","Dark sorcerer with forbidden knowledge","/Game/Companions/Mages/DarkSorcerer","/Game/Materials/Companions/Mages/DarkSorcerer_Material","Dark Magic,Curses,Summoning"
EOF

    # Создание конфигурации групповых квестов
    cat > Config/GroupQuestData.csv << 'EOF'
QuestID,QuestName,QuestType,RequiredLevel,RequiredCompanions,Difficulty,Duration,RequiredClasses,Rewards,ExperienceReward,GoldReward,ReputationReward
"gq_001","Dragon's Lair","Dungeon",10,2,5,60,"Warrior,Mage","Dragon Scale,Magic Sword",500,200,50
"gq_002","Goblin King","Boss",8,1,3,30,"Any","Goblin Crown,Gold",300,150,30
"gq_003","Lost Temple","Exploration",12,3,7,90,"Rogue,Cleric,Mage","Ancient Artifacts",800,400,80
"gq_004","Merchant Escort","Escort",6,2,2,45,"Warrior,Archer","Merchant's Gratitude",200,100,20
"gq_005","Village Defense","Defense",15,4,8,120,"Warrior,Archer,Cleric,Mage","Hero's Medal",1000,500,100
"gq_006","Demon Lord","Boss",20,5,10,180,"All Classes","Demon Lord's Heart",2000,1000,200
"gq_007","Crystal Caverns","Dungeon",14,3,6,75,"Mage,Cleric,Warrior","Magic Crystals",600,300,60
"gq_008","Bandit Hideout","Exploration",9,2,4,50,"Rogue,Archer","Stolen Goods",350,175,35
EOF

    print_success "Конфигурация компаньонов создана"
}

# Создание UI шаблонов
create_ui_templates() {
    print_status "Создание UI шаблонов..."
    
    # Создание UI для гильдии авантюристов
    cat > Content/UI/AdventurerGuild/AdventurerGuildWidget.uasset << 'EOF'
# Adventurer Guild Widget Template
# This will be converted to proper UE4 widget format
EOF

    # Создание UI для найма компаньонов
    cat > Content/UI/CompanionRecruitment/CompanionRecruitmentWidget.uasset << 'EOF'
# Companion Recruitment Widget Template
# This will be converted to proper UE4 widget format
EOF

    # Создание UI для управления компаньонами
    cat > Content/UI/CompanionManagement/CompanionManagementWidget.uasset << 'EOF'
# Companion Management Widget Template
# This will be converted to proper UE4 widget format
EOF

    # Создание UI для групповых квестов
    cat > Content/UI/GroupQuests/GroupQuestWidget.uasset << 'EOF'
# Group Quest Widget Template
# This will be converted to proper UE4 widget format
EOF

    print_success "UI шаблоны созданы"
}

# Создание системы отношений
create_relationship_system() {
    print_status "Создание системы отношений..."
    
    # Создание конфигурации отношений
    cat > Config/CompanionRelationships.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Companion relationship system configuration
RelationshipDecayTime=120.0
RelationshipDecayAmount=1
MaxRelationship=100
MinRelationship=0
GiftRelationshipGain=5
TrainingRelationshipGain=3
MissionRelationshipGain=10
ConversationRelationshipGain=2
NeglectRelationshipLoss=2
EOF

    print_success "Система отношений создана"
}

# Создание системы групповых квестов
create_group_quest_system() {
    print_status "Создание системы групповых квестов..."
    
    # Создание конфигурации групповых квестов
    cat > Config/GroupQuestSystem.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Group quest system configuration
MaxActiveQuests=3
QuestCheckInterval=60.0
QuestCompletionTime=300.0
QuestFailureTime=600.0
ExperienceMultiplier=1.5
GoldMultiplier=1.2
ReputationMultiplier=1.3
EOF

    print_success "Система групповых квестов создана"
}

# Создание системы репутации
create_reputation_system() {
    print_status "Создание системы репутации гильдии..."
    
    # Создание конфигурации репутации
    cat > Config/GuildReputation.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Guild reputation system configuration
BaseReputation=0
MaxReputation=1000
QuestReputationGain=10
MissionReputationGain=5
CompanionHireReputationCost=50
CompanionDismissReputationLoss=10
ReputationDecayTime=1440.0
ReputationDecayAmount=1
EOF

    print_success "Система репутации создана"
}

# Создание системы ИИ компаньонов
create_companion_ai_system() {
    print_status "Создание системы ИИ компаньонов..."
    
    # Создание папок для ИИ
    mkdir -p Content/AI/CompanionBehaviors
    mkdir -p Content/AI/CombatAI
    mkdir -p Content/AI/SocialAI
    mkdir -p Content/AI/QuestAI
    
    # Создание конфигурации ИИ
    cat > Config/CompanionAI.ini << 'EOF'
[/Script/Engine.GameUserSettings]
; Companion AI system configuration
CombatAIAggressiveness=0.7
SocialAIFriendliness=0.5
QuestAIDiligence=0.8
AIUpdateInterval=1.0
AIReactionTime=0.5
AIMemoryDuration=300.0
EOF

    print_success "Система ИИ компаньонов создана"
}

# Основная функция
main() {
    echo "Начинаем настройку гильдии авантюристов..."
    echo
    
    create_guild_structure
    create_companion_config
    create_ui_templates
    create_relationship_system
    create_group_quest_system
    create_reputation_system
    create_companion_ai_system
    
    echo
    print_success "🏰 Настройка гильдии авантюристов завершена!"
    echo
    echo "Следующие шаги:"
    echo "1. Откройте проект в Unreal Engine 4.27"
    echo "2. Скомпилируйте систему гильдии авантюристов"
    echo "3. Создайте UI виджеты для найма и управления"
    echo "4. Настройте систему отношений и групповых квестов"
    echo "5. Протестируйте ИИ компаньонов"
    echo
    echo "Гильдия авантюристов готова к использованию!"
}

# Запуск основной функции
main "$@"
