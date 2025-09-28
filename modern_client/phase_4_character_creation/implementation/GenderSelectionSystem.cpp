// GenderSelectionSystem.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Components/ScrollBox.h"
#include "Engine/Texture2D.h"
#include "Engine/Engine.h"

// Вспомогательный класс для системы выбора пола
class FGenderSelectionSystem
{
public:
    // Структура данных пола
    struct FGenderData
    {
        FString GenderName;
        FString DisplayName;
        FString Description;
        FString IconPath;
        TArray<FString> AvailableClasses;
        FLinearColor GenderColor;
        bool bIsUnlocked;
    };

    // Инициализация системы выбора пола
    static void InitializeGenderSelection(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы выбора пола"));
        
        // Создаем данные о полах
        CreateGenderData();
        
        // Настраиваем панель выбора пола
        SetupGenderSelectionPanel(CurrentWidget);
        
        // Привязываем события
        BindGenderSelectionEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система выбора пола инициализирована"));
    }

    // Создание данных о полах
    static void CreateGenderData()
    {
        // Очищаем предыдущие данные
        GenderDataMap.Empty();
        
        // Male (Мужской)
        FGenderData MaleData;
        MaleData.GenderName = TEXT("Male");
        MaleData.DisplayName = TEXT("Мужской");
        MaleData.Description = TEXT("Мужской персонаж с характерными особенностями");
        MaleData.IconPath = TEXT("/Game/UI/CharacterCreation/Genders/Male_Icon");
        MaleData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        MaleData.GenderColor = FLinearColor(0.2f, 0.4f, 0.8f, 1.0f); // Синий
        MaleData.bIsUnlocked = true;
        GenderDataMap.Add(TEXT("Male"), MaleData);
        
        // Female (Женский)
        FGenderData FemaleData;
        FemaleData.GenderName = TEXT("Female");
        FemaleData.DisplayName = TEXT("Женский");
        FemaleData.Description = TEXT("Женский персонаж с характерными особенностями");
        FemaleData.IconPath = TEXT("/Game/UI/CharacterCreation/Genders/Female_Icon");
        FemaleData.AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        FemaleData.GenderColor = FLinearColor(0.8f, 0.2f, 0.6f, 1.0f); // Розовый
        FemaleData.bIsUnlocked = true;
        GenderDataMap.Add(TEXT("Female"), FemaleData);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d полов"), GenderDataMap.Num());
    }

    // Настройка панели выбора пола
    static void SetupGenderSelectionPanel(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора пола
        UScrollBox* GenderSelectionPanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("GenderSelectionPanel")));
        if (!GenderSelectionPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("Панель выбора пола не найдена"));
            return;
        }
        
        // Очищаем панель
        GenderSelectionPanel->ClearChildren();
        
        // Создаем кнопки для каждого пола
        for (const auto& GenderPair : GenderDataMap)
        {
            const FGenderData& GenderData = GenderPair.Value;
            if (GenderData.bIsUnlocked)
            {
                CreateGenderButton(GenderSelectionPanel, GenderData);
            }
        }
        
        UE_LOG(LogTemp, Log, TEXT("Панель выбора пола настроена"));
    }

    // Создание кнопки пола
    static void CreateGenderButton(UScrollBox* ParentPanel, const FGenderData& GenderData)
    {
        if (!ParentPanel) return;
        
        // Создаем контейнер для кнопки
        UUserWidget* GenderButtonWidget = CreateWidget<UUserWidget>(ParentPanel, UUserWidget::StaticClass());
        if (!GenderButtonWidget) return;
        
        // Настраиваем кнопку (в реальной реализации здесь будет создание UMG виджета)
        UE_LOG(LogTemp, Log, TEXT("Создана кнопка для пола: %s"), *GenderData.DisplayName);
        
        // Добавляем в панель
        ParentPanel->AddChild(GenderButtonWidget);
    }

    // Привязка событий выбора пола
    static void BindGenderSelectionEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий к кнопкам
        UE_LOG(LogTemp, Log, TEXT("События выбора пола привязаны"));
    }

    // Обработка выбора пола
    static void OnGenderSelected(const FString& GenderName)
    {
        if (!GenderDataMap.Contains(GenderName))
        {
            UE_LOG(LogTemp, Warning, TEXT("Неизвестный пол: %s"), *GenderName);
            return;
        }
        
        const FGenderData& SelectedGender = GenderDataMap[GenderName];
        SelectedGenderName = GenderName;
        
        UE_LOG(LogTemp, Log, TEXT("Выбран пол: %s (%s)"), *SelectedGender.DisplayName, *GenderName);
        
        // Уведомляем другие системы о выборе пола
        NotifyGenderSelectionChanged(SelectedGender);
    }

    // Уведомление о изменении выбора пола
    static void NotifyGenderSelectionChanged(const FGenderData& SelectedGender)
    {
        // В реальной реализации здесь будет уведомление других систем
        UE_LOG(LogTemp, Log, TEXT("Уведомление: выбран пол %s"), *SelectedGender.DisplayName);
        
        // Обновляем доступные классы
        UpdateAvailableClasses(SelectedGender.AvailableClasses);
        
        // Обновляем кастомизацию
        UpdateCustomizationOptions(SelectedGender.GenderName);
    }

    // Обновление доступных классов
    static void UpdateAvailableClasses(const TArray<FString>& AvailableClasses)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление доступных классов для выбранного пола"));
        for (const FString& ClassName : AvailableClasses)
        {
            UE_LOG(LogTemp, Log, TEXT("Доступный класс: %s"), *ClassName);
        }
    }

    // Обновление опций кастомизации
    static void UpdateCustomizationOptions(const FString& GenderName)
    {
        UE_LOG(LogTemp, Log, TEXT("Обновление опций кастомизации для пола: %s"), *GenderName);
        
        // В реальной реализации здесь будет обновление опций кастомизации
        // в зависимости от выбранного пола
        if (GenderName == TEXT("Male"))
        {
            UE_LOG(LogTemp, Log, TEXT("Загружены мужские опции кастомизации"));
        }
        else if (GenderName == TEXT("Female"))
        {
            UE_LOG(LogTemp, Log, TEXT("Загружены женские опции кастомизации"));
        }
    }

    // Получение данных о поле
    static FGenderData* GetGenderData(const FString& GenderName)
    {
        if (GenderDataMap.Contains(GenderName))
        {
            return &GenderDataMap[GenderName];
        }
        return nullptr;
    }

    // Получение всех доступных полов
    static TArray<FGenderData> GetAllAvailableGenders()
    {
        TArray<FGenderData> AvailableGenders;
        for (const auto& GenderPair : GenderDataMap)
        {
            if (GenderPair.Value.bIsUnlocked)
            {
                AvailableGenders.Add(GenderPair.Value);
            }
        }
        return AvailableGenders;
    }

    // Проверка доступности пола
    static bool IsGenderAvailable(const FString& GenderName)
    {
        if (GenderDataMap.Contains(GenderName))
        {
            return GenderDataMap[GenderName].bIsUnlocked;
        }
        return false;
    }

    // Разблокировка пола
    static void UnlockGender(const FString& GenderName)
    {
        if (GenderDataMap.Contains(GenderName))
        {
            GenderDataMap[GenderName].bIsUnlocked = true;
            UE_LOG(LogTemp, Log, TEXT("Пол разблокирован: %s"), *GenderName);
        }
    }

    // Блокировка пола
    static void LockGender(const FString& GenderName)
    {
        if (GenderDataMap.Contains(GenderName))
        {
            GenderDataMap[GenderName].bIsUnlocked = false;
            UE_LOG(LogTemp, Log, TEXT("Пол заблокирован: %s"), *GenderName);
        }
    }

    // Получение выбранного пола
    static FString GetSelectedGender()
    {
        return SelectedGenderName;
    }

    // Сброс выбора пола
    static void ResetGenderSelection()
    {
        SelectedGenderName = TEXT("");
        UE_LOG(LogTemp, Log, TEXT("Выбор пола сброшен"));
    }

private:
    // Карта данных о полах
    static TMap<FString, FGenderData> GenderDataMap;
    
    // Выбранный пол
    static FString SelectedGenderName;
};

// Статические переменные
TMap<FString, FGenderSelectionSystem::FGenderData> FGenderSelectionSystem::GenderDataMap;
FString FGenderSelectionSystem::SelectedGenderName;
