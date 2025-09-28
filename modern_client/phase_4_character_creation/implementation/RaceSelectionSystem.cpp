// RaceSelectionSystem.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Components/ScrollBox.h"
#include "Engine/Texture2D.h"
#include "Engine/Engine.h"

// Вспомогательный класс для системы выбора расы
class FRaceSelectionSystem
{
public:
    // Структура данных расы
    struct FRaceData
    {
        FString RaceName;
        FString DisplayName;
        FString Description;
        FString IconPath;
        TArray<FString> AvailableClasses;
        TArray<FString> AvailableGenders;
        FLinearColor RaceColor;
        bool bIsUnlocked;
    };

    // Инициализация системы выбора расы
    static void InitializeRaceSelection(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы выбора расы"));
        
        // Создаем данные о расах
        CreateRaceData();
        
        // Настраиваем панель выбора расы
        SetupRaceSelectionPanel(CurrentWidget);
        
        // Привязываем события
        BindRaceSelectionEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система выбора расы инициализирована"));
    }

    // Создание данных о расах
    static void CreateRaceData()
    {
        // Очищаем предыдущие данные
        RaceDataMap.Empty();
        
        // Human (Человек)
        FRaceData HumanData;
        HumanData.RaceName = TEXT("Human");
        HumanData.DisplayName = TEXT("Человек");
        HumanData.Description = TEXT("Универсальная раса с хорошими характеристиками");
        HumanData.IconPath = TEXT("/Game/UI/CharacterCreation/Races/Human_Icon");
        HumanData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        HumanData.AvailableGenders = {TEXT("Male"), TEXT("Female")};
        HumanData.RaceColor = FLinearColor(0.8f, 0.6f, 0.4f, 1.0f); // Коричневый
        HumanData.bIsUnlocked = true;
        RaceDataMap.Add(TEXT("Human"), HumanData);
        
        // Elf (Эльф)
        FRaceData ElfData;
        ElfData.RaceName = TEXT("Elf");
        ElfData.DisplayName = TEXT("Эльф");
        ElfData.Description = TEXT("Магическая раса с высоким интеллектом");
        ElfData.IconPath = TEXT("/Game/UI/CharacterCreation/Races/Elf_Icon");
        ElfData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        ElfData.AvailableGenders = {TEXT("Male"), TEXT("Female")};
        ElfData.RaceColor = FLinearColor(0.4f, 0.8f, 0.4f, 1.0f); // Зеленый
        ElfData.bIsUnlocked = true;
        RaceDataMap.Add(TEXT("Elf"), ElfData);
        
        // Dark Elf (Темный эльф)
        FRaceData DarkElfData;
        DarkElfData.RaceName = TEXT("DarkElf");
        DarkElfData.DisplayName = TEXT("Темный эльф");
        DarkElfData.Description = TEXT("Темная раса с высоким уроном");
        DarkElfData.IconPath = TEXT("/Game/UI/CharacterCreation/Races/DarkElf_Icon");
        DarkElfData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        DarkElfData.AvailableGenders = {TEXT("Male"), TEXT("Female")};
        DarkElfData.RaceColor = FLinearColor(0.4f, 0.2f, 0.6f, 1.0f); // Фиолетовый
        DarkElfData.bIsUnlocked = true;
        RaceDataMap.Add(TEXT("DarkElf"), DarkElfData);
        
        // Orc (Орк)
        FRaceData OrcData;
        OrcData.RaceName = TEXT("Orc");
        OrcData.DisplayName = TEXT("Орк");
        OrcData.Description = TEXT("Сильная раса с высоким здоровьем");
        OrcData.IconPath = TEXT("/Game/UI/CharacterCreation/Races/Orc_Icon");
        OrcData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        OrcData.AvailableGenders = {TEXT("Male"), TEXT("Female")};
        OrcData.RaceColor = FLinearColor(0.6f, 0.3f, 0.2f, 1.0f); // Красно-коричневый
        OrcData.bIsUnlocked = true;
        RaceDataMap.Add(TEXT("Orc"), OrcData);
        
        // Dwarf (Дворф)
        FRaceData DwarfData;
        DwarfData.RaceName = TEXT("Dwarf");
        DwarfData.DisplayName = TEXT("Дворф");
        DwarfData.Description = TEXT("Мастерская раса с высоким мастерством");
        DwarfData.IconPath = TEXT("/Game/UI/CharacterCreation/Races/Dwarf_Icon");
        DwarfData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        DwarfData.AvailableGenders = {TEXT("Male"), TEXT("Female")};
        DwarfData.RaceColor = FLinearColor(0.5f, 0.5f, 0.3f, 1.0f); // Золотистый
        DwarfData.bIsUnlocked = true;
        RaceDataMap.Add(TEXT("Dwarf"), DwarfData);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d рас"), RaceDataMap.Num());
    }

    // Настройка панели выбора расы
    static void SetupRaceSelectionPanel(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора расы
        UScrollBox* RaceSelectionPanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("RaceSelectionPanel")));
        if (!RaceSelectionPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("Панель выбора расы не найдена"));
            return;
        }
        
        // Очищаем панель
        RaceSelectionPanel->ClearChildren();
        
        // Создаем кнопки для каждой расы
        for (const auto& RacePair : RaceDataMap)
        {
            const FRaceData& RaceData = RacePair.Value;
            if (RaceData.bIsUnlocked)
            {
                CreateRaceButton(RaceSelectionPanel, RaceData);
            }
        }
        
        UE_LOG(LogTemp, Log, TEXT("Панель выбора расы настроена"));
    }

    // Создание кнопки расы
    static void CreateRaceButton(UScrollBox* ParentPanel, const FRaceData& RaceData)
    {
        if (!ParentPanel) return;
        
        // Создаем контейнер для кнопки
        UUserWidget* RaceButtonWidget = CreateWidget<UUserWidget>(ParentPanel, UUserWidget::StaticClass());
        if (!RaceButtonWidget) return;
        
        // Настраиваем кнопку (в реальной реализации здесь будет создание UMG виджета)
        UE_LOG(LogTemp, Log, TEXT("Создана кнопка для расы: %s"), *RaceData.DisplayName);
        
        // Добавляем в панель
        ParentPanel->AddChild(RaceButtonWidget);
    }

    // Привязка событий выбора расы
    static void BindRaceSelectionEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий к кнопкам
        UE_LOG(LogTemp, Log, TEXT("События выбора расы привязаны"));
    }

    // Обработка выбора расы
    static void OnRaceSelected(const FString& RaceName)
    {
        if (!RaceDataMap.Contains(RaceName))
        {
            UE_LOG(LogTemp, Warning, TEXT("Неизвестная раса: %s"), *RaceName);
            return;
        }
        
        const FRaceData& SelectedRace = RaceDataMap[RaceName];
        SelectedRaceName = RaceName;
        
        UE_LOG(LogTemp, Log, TEXT("Выбрана раса: %s (%s)"), *SelectedRace.DisplayName, *RaceName);
        
        // Уведомляем другие системы о выборе расы
        NotifyRaceSelectionChanged(SelectedRace);
    }

    // Уведомление о изменении выбора расы
    static void NotifyRaceSelectionChanged(const FRaceData& SelectedRace)
    {
        // В реальной реализации здесь будет уведомление других систем
        UE_LOG(LogTemp, Log, TEXT("Уведомление: выбрана раса %s"), *SelectedRace.DisplayName);
        
        // Обновляем доступные классы
        UpdateAvailableClasses(SelectedRace.AvailableClasses);
        
        // Обновляем доступные полы
        UpdateAvailableGenders(SelectedRace.AvailableGenders);
    }

    // Обновление доступных классов
    static void UpdateAvailableClasses(const TArray<FString>& AvailableClasses)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление доступных классов для выбранной расы"));
        for (const FString& ClassName : AvailableClasses)
        {
            UE_LOG(LogTemp, Log, TEXT("Доступный класс: %s"), *ClassName);
        }
    }

    // Обновление доступных полов
    static void UpdateAvailableGenders(const TArray<FString>& AvailableGenders)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление доступных полов для выбранной расы"));
        for (const FString& GenderName : AvailableGenders)
        {
            UE_LOG(LogTemp, Log, TEXT("Доступный пол: %s"), *GenderName);
        }
    }

    // Получение данных о расе
    static FRaceData* GetRaceData(const FString& RaceName)
    {
        if (RaceDataMap.Contains(RaceName))
        {
            return &RaceDataMap[RaceName];
        }
        return nullptr;
    }

    // Получение всех доступных рас
    static TArray<FRaceData> GetAllAvailableRaces()
    {
        TArray<FRaceData> AvailableRaces;
        for (const auto& RacePair : RaceDataMap)
        {
            if (RacePair.Value.bIsUnlocked)
            {
                AvailableRaces.Add(RacePair.Value);
            }
        }
        return AvailableRaces;
    }

    // Проверка доступности расы
    static bool IsRaceAvailable(const FString& RaceName)
    {
        if (RaceDataMap.Contains(RaceName))
        {
            return RaceDataMap[RaceName].bIsUnlocked;
        }
        return false;
    }

    // Разблокировка расы
    static void UnlockRace(const FString& RaceName)
    {
        if (RaceDataMap.Contains(RaceName))
        {
            RaceDataMap[RaceName].bIsUnlocked = true;
            UE_LOG(LogTemp, Log, TEXT("Раса разблокирована: %s"), *RaceName);
        }
    }

    // Блокировка расы
    static void LockRace(const FString& RaceName)
    {
        if (RaceDataMap.Contains(RaceName))
        {
            RaceDataMap[RaceName].bIsUnlocked = false;
            UE_LOG(LogTemp, Log, TEXT("Раса заблокирована: %s"), *RaceName);
        }
    }

private:
    // Карта данных о расах
    static TMap<FString, FRaceData> RaceDataMap;
    
    // Выбранная раса
    static FString SelectedRaceName;
};

// Статические переменные
TMap<FString, FRaceSelectionSystem::FRaceData> FRaceSelectionSystem::RaceDataMap;
FString FRaceSelectionSystem::SelectedRaceName;
