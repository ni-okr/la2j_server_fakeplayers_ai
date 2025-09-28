// ClassSelectionSystem.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Components/ScrollBox.h"
#include "Engine/Texture2D.h"
#include "Engine/Engine.h"

// Вспомогательный класс для системы выбора класса
class FClassSelectionSystem
{
public:
    // Структура данных класса
    struct FClassData
    {
        FString ClassName;
        FString DisplayName;
        FString Description;
        FString IconPath;
        TArray<FString> RequiredRace;
        TArray<FString> RequiredGender;
        FLinearColor ClassColor;
        TMap<FString, int32> BaseStats;
        TArray<FString> AvailableSkills;
        bool bIsUnlocked;
    };

    // Инициализация системы выбора класса
    static void InitializeClassSelection(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы выбора класса"));
        
        // Создаем данные о классах
        CreateClassData();
        
        // Настраиваем панель выбора класса
        SetupClassSelectionPanel(CurrentWidget);
        
        // Привязываем события
        BindClassSelectionEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система выбора класса инициализирована"));
    }

    // Создание данных о классах
    static void CreateClassData()
    {
        // Очищаем предыдущие данные
        ClassDataMap.Empty();
        
        // Fighter (Воин)
        FClassData FighterData;
        FighterData.ClassName = TEXT("Fighter");
        FighterData.DisplayName = TEXT("Воин");
        FighterData.Description = TEXT("Ближний бой, высокая защита и здоровье");
        FighterData.IconPath = TEXT("/Game/UI/CharacterCreation/Classes/Fighter_Icon");
        FighterData.RequiredRace = {TEXT("Human"), TEXT("Elf"), TEXT("DarkElf"), TEXT("Orc"), TEXT("Dwarf")};
        FighterData.RequiredGender = {TEXT("Male"), TEXT("Female")};
        FighterData.ClassColor = FLinearColor(0.8f, 0.2f, 0.2f, 1.0f); // Красный
        FighterData.BaseStats.Add(TEXT("HP"), 100);
        FighterData.BaseStats.Add(TEXT("MP"), 50);
        FighterData.BaseStats.Add(TEXT("STR"), 15);
        FighterData.BaseStats.Add(TEXT("INT"), 10);
        FighterData.BaseStats.Add(TEXT("DEX"), 12);
        FighterData.AvailableSkills = {TEXT("Power Strike"), TEXT("Defense"), TEXT("Shield Bash")};
        FighterData.bIsUnlocked = true;
        ClassDataMap.Add(TEXT("Fighter"), FighterData);
        
        // Mystic (Мистик)
        FClassData MysticData;
        MysticData.ClassName = TEXT("Mystic");
        MysticData.DisplayName = TEXT("Мистик");
        MysticData.Description = TEXT("Магические атаки, высокий интеллект и мана");
        MysticData.IconPath = TEXT("/Game/UI/CharacterCreation/Classes/Mystic_Icon");
        MysticData.RequiredRace = {TEXT("Human"), TEXT("Elf"), TEXT("DarkElf"), TEXT("Orc"), TEXT("Dwarf")};
        MysticData.RequiredGender = {TEXT("Male"), TEXT("Female")};
        MysticData.ClassColor = FLinearColor(0.2f, 0.2f, 0.8f, 1.0f); // Синий
        MysticData.BaseStats.Add(TEXT("HP"), 70);
        MysticData.BaseStats.Add(TEXT("MP"), 120);
        MysticData.BaseStats.Add(TEXT("STR"), 8);
        MysticData.BaseStats.Add(TEXT("INT"), 18);
        MysticData.BaseStats.Add(TEXT("DEX"), 10);
        MysticData.AvailableSkills = {TEXT("Fireball"), TEXT("Heal"), TEXT("Magic Shield")};
        MysticData.bIsUnlocked = true;
        ClassDataMap.Add(TEXT("Mystic"), MysticData);
        
        // Scout (Разведчик)
        FClassData ScoutData;
        ScoutData.ClassName = TEXT("Scout");
        ScoutData.DisplayName = TEXT("Разведчик");
        ScoutData.Description = TEXT("Дальний бой, высокая ловкость и скорость");
        ScoutData.IconPath = TEXT("/Game/UI/CharacterCreation/Classes/Scout_Icon");
        ScoutData.RequiredRace = {TEXT("Human"), TEXT("Elf"), TEXT("DarkElf"), TEXT("Orc"), TEXT("Dwarf")};
        ScoutData.RequiredGender = {TEXT("Male"), TEXT("Female")};
        ScoutData.ClassColor = FLinearColor(0.2f, 0.8f, 0.2f, 1.0f); // Зеленый
        ScoutData.BaseStats.Add(TEXT("HP"), 80);
        ScoutData.BaseStats.Add(TEXT("MP"), 80);
        ScoutData.BaseStats.Add(TEXT("STR"), 12);
        ScoutData.BaseStats.Add(TEXT("INT"), 12);
        ScoutData.BaseStats.Add(TEXT("DEX"), 16);
        ScoutData.AvailableSkills = {TEXT("Arrow Shot"), TEXT("Stealth"), TEXT("Quick Strike")};
        ScoutData.bIsUnlocked = true;
        ClassDataMap.Add(TEXT("Scout"), ScoutData);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d классов"), ClassDataMap.Num());
    }

    // Настройка панели выбора класса
    static void SetupClassSelectionPanel(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора класса
        UScrollBox* ClassSelectionPanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("ClassSelectionPanel")));
        if (!ClassSelectionPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("Панель выбора класса не найдена"));
            return;
        }
        
        // Очищаем панель
        ClassSelectionPanel->ClearChildren();
        
        // Создаем кнопки для каждого класса
        for (const auto& ClassPair : ClassDataMap)
        {
            const FClassData& ClassData = ClassPair.Value;
            if (ClassData.bIsUnlocked)
            {
                CreateClassButton(ClassSelectionPanel, ClassData);
            }
        }
        
        UE_LOG(LogTemp, Log, TEXT("Панель выбора класса настроена"));
    }

    // Создание кнопки класса
    static void CreateClassButton(UScrollBox* ParentPanel, const FClassData& ClassData)
    {
        if (!ParentPanel) return;
        
        // Создаем контейнер для кнопки
        UUserWidget* ClassButtonWidget = CreateWidget<UUserWidget>(ParentPanel, UUserWidget::StaticClass());
        if (!ClassButtonWidget) return;
        
        // Настраиваем кнопку (в реальной реализации здесь будет создание UMG виджета)
        UE_LOG(LogTemp, Log, TEXT("Создана кнопка для класса: %s"), *ClassData.DisplayName);
        
        // Добавляем в панель
        ParentPanel->AddChild(ClassButtonWidget);
    }

    // Привязка событий выбора класса
    static void BindClassSelectionEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий к кнопкам
        UE_LOG(LogTemp, Log, TEXT("События выбора класса привязаны"));
    }

    // Обработка выбора класса
    static void OnClassSelected(const FString& ClassName)
    {
        if (!ClassDataMap.Contains(ClassName))
        {
            UE_LOG(LogTemp, Warning, TEXT("Неизвестный класс: %s"), *ClassName);
            return;
        }
        
        const FClassData& SelectedClass = ClassDataMap[ClassName];
        SelectedClassName = ClassName;
        
        UE_LOG(LogTemp, Log, TEXT("Выбран класс: %s (%s)"), *SelectedClass.DisplayName, *ClassName);
        
        // Уведомляем другие системы о выборе класса
        NotifyClassSelectionChanged(SelectedClass);
    }

    // Уведомление о изменении выбора класса
    static void NotifyClassSelectionChanged(const FClassData& SelectedClass)
    {
        // В реальной реализации здесь будет уведомление других систем
        UE_LOG(LogTemp, Log, TEXT("Уведомление: выбран класс %s"), *SelectedClass.DisplayName);
        
        // Обновляем опции кастомизации
        UpdateCustomizationOptions(SelectedClass.ClassName);
        
        // Обновляем предварительный просмотр
        UpdateCharacterPreview(SelectedClass);
    }

    // Обновление опций кастомизации
    static void UpdateCustomizationOptions(const FString& ClassName)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление опций кастомизации для класса: %s"), *ClassName);
        
        // В реальной реализации здесь будет обновление опций кастомизации
        // в зависимости от выбранного класса
        if (ClassName == TEXT("Fighter"))
        {
            UE_LOG(LogTemp, Log, TEXT("Загружены опции кастомизации для воина"));
        }
        else if (ClassName == TEXT("Mystic"))
        {
            UE_LOG(LogTemp, Log, TEXT("Загружены опции кастомизации для мистика"));
        }
        else if (ClassName == TEXT("Scout"))
        {
            UE_LOG(LogTemp, Log, TEXT("Загружены опции кастомизации для разведчика"));
        }
    }

    // Обновление предварительного просмотра
    static void UpdateCharacterPreview(const FClassData& SelectedClass)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление предварительного просмотра для класса: %s"), *SelectedClass.DisplayName);
        
        // В реальной реализации здесь будет обновление 3D модели персонажа
        // в зависимости от выбранного класса
    }

    // Фильтрация классов по расе и полу
    static void FilterClassesByRaceAndGender(const FString& RaceName, const FString& GenderName)
    {
        UE_LOG(LogTemp, Log, TEXT("Фильтрация классов для расы: %s, пол: %s"), *RaceName, *GenderName);
        
        TArray<FString> AvailableClasses;
        
        for (const auto& ClassPair : ClassDataMap)
        {
            const FClassData& ClassData = ClassPair.Value;
            
            // Проверяем требования к расе
            bool bRaceMatch = ClassData.RequiredRace.Contains(RaceName) || ClassData.RequiredRace.Contains(TEXT("All"));
            
            // Проверяем требования к полу
            bool bGenderMatch = ClassData.RequiredGender.Contains(GenderName) || ClassData.RequiredGender.Contains(TEXT("All"));
            
            if (bRaceMatch && bGenderMatch && ClassData.bIsUnlocked)
            {
                AvailableClasses.Add(ClassData.ClassName);
                UE_LOG(LogTemp, Log, TEXT("Доступный класс: %s"), *ClassData.DisplayName);
            }
        }
        
        // Обновляем панель выбора класса
        UpdateClassSelectionPanel(AvailableClasses);
    }

    // Обновление панели выбора класса
    static void UpdateClassSelectionPanel(const TArray<FString>& AvailableClasses)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление панели выбора класса с %d доступными классами"), AvailableClasses.Num());
        
        // В реальной реализации здесь будет обновление панели выбора класса
        // с показом только доступных классов
    }

    // Получение данных о классе
    static FClassData* GetClassData(const FString& ClassName)
    {
        if (ClassDataMap.Contains(ClassName))
        {
            return &ClassDataMap[ClassName];
        }
        return nullptr;
    }

    // Получение всех доступных классов
    static TArray<FClassData> GetAllAvailableClasses()
    {
        TArray<FClassData> AvailableClasses;
        for (const auto& ClassPair : ClassDataMap)
        {
            if (ClassPair.Value.bIsUnlocked)
            {
                AvailableClasses.Add(ClassPair.Value);
            }
        }
        return AvailableClasses;
    }

    // Проверка доступности класса
    static bool IsClassAvailable(const FString& ClassName)
    {
        if (ClassDataMap.Contains(ClassName))
        {
            return ClassDataMap[ClassName].bIsUnlocked;
        }
        return false;
    }

    // Разблокировка класса
    static void UnlockClass(const FString& ClassName)
    {
        if (ClassDataMap.Contains(ClassName))
        {
            ClassDataMap[ClassName].bIsUnlocked = true;
            UE_LOG(LogTemp, Log, TEXT("Класс разблокирован: %s"), *ClassName);
        }
    }

    // Блокировка класса
    static void LockClass(const FString& ClassName)
    {
        if (ClassDataMap.Contains(ClassName))
        {
            ClassDataMap[ClassName].bIsUnlocked = false;
            UE_LOG(LogTemp, Log, TEXT("Класс заблокирован: %s"), *ClassName);
        }
    }

    // Получение выбранного класса
    static FString GetSelectedClass()
    {
        return SelectedClassName;
    }

    // Сброс выбора класса
    static void ResetClassSelection()
    {
        SelectedClassName = TEXT("");
        UE_LOG(LogTemp, Log, TEXT("Выбор класса сброшен"));
    }

private:
    // Карта данных о классах
    static TMap<FString, FClassData> ClassDataMap;
    
    // Выбранный класс
    static FString SelectedClassName;
};

// Статические переменные
TMap<FString, FClassSelectionSystem::FClassData> FClassSelectionSystem::ClassDataMap;
FString FClassSelectionSystem::SelectedClassName;
