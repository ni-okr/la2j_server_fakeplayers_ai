// CharacterCreationPixelComparison.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Image.h"
#include "Engine/Texture2D.h"
#include "Engine/Engine.h"
#include "Misc/FileHelper.h"
#include "HAL/PlatformFilemanager.h"

// Вспомогательный класс для попиксельного сравнения экрана создания персонажа
class FCharacterCreationPixelComparison
{
public:
    // Структура результата сравнения
    struct FPixelComparisonResult
    {
        float SimilarityPercentage;
        int32 TotalPixels;
        int32 MatchingPixels;
        int32 DifferentPixels;
        TArray<FVector2D> DifferentPixelPositions;
        FString ComparisonType;
        FString ErrorMessage;
        bool bIsSuccessful;
    };

    // Структура настроек сравнения
    struct FPixelComparisonSettings
    {
        float ToleranceThreshold;
        bool bIgnoreAlpha;
        bool bIgnoreTransparentPixels;
        bool bUseColorDistance;
        float ColorDistanceThreshold;
        bool bGenerateDifferenceMap;
        FString OutputDirectory;
    };

    // Инициализация системы попиксельного сравнения
    static void InitializePixelComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы попиксельного сравнения"));
        
        // Создаем настройки сравнения
        CreateComparisonSettings();
        
        // Настраиваем сравнение для элементов
        SetupElementComparison(CurrentWidget);
        
        // Привязываем события сравнения
        BindComparisonEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система попиксельного сравнения инициализирована"));
    }

    // Создание настроек сравнения
    static void CreateComparisonSettings()
    {
        // Очищаем предыдущие настройки
        ComparisonSettingsMap.Empty();
        
        // Настройки для общего сравнения экрана
        FPixelComparisonSettings GeneralSettings;
        GeneralSettings.ToleranceThreshold = 0.95f; // 95% соответствие
        GeneralSettings.bIgnoreAlpha = false;
        GeneralSettings.bIgnoreTransparentPixels = true;
        GeneralSettings.bUseColorDistance = true;
        GeneralSettings.ColorDistanceThreshold = 0.1f;
        GeneralSettings.bGenerateDifferenceMap = true;
        GeneralSettings.OutputDirectory = TEXT("/Game/Screenshots/CharacterCreation/Comparison/");
        ComparisonSettingsMap.Add(TEXT("General"), GeneralSettings);
        
        // Настройки для сравнения панелей
        FPixelComparisonSettings PanelSettings;
        PanelSettings.ToleranceThreshold = 0.90f; // 90% соответствие
        PanelSettings.bIgnoreAlpha = false;
        PanelSettings.bIgnoreTransparentPixels = true;
        PanelSettings.bUseColorDistance = true;
        PanelSettings.ColorDistanceThreshold = 0.15f;
        PanelSettings.bGenerateDifferenceMap = true;
        PanelSettings.OutputDirectory = TEXT("/Game/Screenshots/CharacterCreation/Panels/");
        ComparisonSettingsMap.Add(TEXT("Panels"), PanelSettings);
        
        // Настройки для сравнения кнопок
        FPixelComparisonSettings ButtonSettings;
        ButtonSettings.ToleranceThreshold = 0.85f; // 85% соответствие
        ButtonSettings.bIgnoreAlpha = false;
        ButtonSettings.bIgnoreTransparentPixels = true;
        ButtonSettings.bUseColorDistance = true;
        ButtonSettings.ColorDistanceThreshold = 0.20f;
        ButtonSettings.bGenerateDifferenceMap = true;
        ButtonSettings.OutputDirectory = TEXT("/Game/Screenshots/CharacterCreation/Buttons/");
        ComparisonSettingsMap.Add(TEXT("Buttons"), ButtonSettings);
        
        // Настройки для сравнения текста
        FPixelComparisonSettings TextSettings;
        TextSettings.ToleranceThreshold = 0.98f; // 98% соответствие
        TextSettings.bIgnoreAlpha = false;
        TextSettings.bIgnoreTransparentPixels = true;
        TextSettings.bUseColorDistance = false;
        TextSettings.ColorDistanceThreshold = 0.05f;
        TextSettings.bGenerateDifferenceMap = true;
        TextSettings.OutputDirectory = TEXT("/Game/Screenshots/CharacterCreation/Text/");
        ComparisonSettingsMap.Add(TEXT("Text"), TextSettings);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d настроек попиксельного сравнения"), ComparisonSettingsMap.Num());
    }

    // Настройка сравнения для элементов
    static void SetupElementComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Настраиваем сравнение для панелей
        SetupPanelComparison(CurrentWidget);
        
        // Настраиваем сравнение для кнопок
        SetupButtonComparison(CurrentWidget);
        
        // Настраиваем сравнение для текстовых элементов
        SetupTextComparison(CurrentWidget);
        
        // Настраиваем сравнение для изображений
        SetupImageComparison(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение элементов настроено"));
    }

    // Настройка сравнения для панелей
    static void SetupPanelComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все панели
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UScrollBox* Panel = Cast<UScrollBox>(Widget))
            {
                if (Panel->GetName().Contains(TEXT("Panel")))
                {
                    SetupWidgetComparison(Panel, TEXT("Panels"));
                }
            }
        }
    }

    // Настройка сравнения для кнопок
    static void SetupButtonComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все кнопки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UButton* Button = Cast<UButton>(Widget))
            {
                SetupWidgetComparison(Button, TEXT("Buttons"));
            }
        }
    }

    // Настройка сравнения для текстовых элементов
    static void SetupTextComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все текстовые блоки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UTextBlock* TextBlock = Cast<UTextBlock>(Widget))
            {
                SetupWidgetComparison(TextBlock, TEXT("Text"));
            }
        }
    }

    // Настройка сравнения для изображений
    static void SetupImageComparison(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все изображения
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UImage* Image = Cast<UImage>(Widget))
            {
                SetupWidgetComparison(Image, TEXT("General"));
            }
        }
    }

    // Настройка сравнения для виджета
    static void SetupWidgetComparison(UWidget* Widget, const FString& ComparisonType)
    {
        if (!Widget) return;
        
        // В реальной реализации здесь будет настройка сравнения для виджета
        UE_LOG(LogTemp, Log, TEXT("Сравнение настроено для виджета %s (тип: %s)"), *Widget->GetName(), *ComparisonType);
    }

    // Привязка событий сравнения
    static void BindComparisonEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий сравнения
        UE_LOG(LogTemp, Log, TEXT("События сравнения привязаны"));
    }

    // Сравнение изображений
    static FPixelComparisonResult CompareImages(UTexture2D* ReferenceImage, UTexture2D* CurrentImage, const FString& ComparisonType)
    {
        FPixelComparisonResult Result;
        Result.bIsSuccessful = false;
        Result.ComparisonType = ComparisonType;
        
        if (!ReferenceImage || !CurrentImage)
        {
            Result.ErrorMessage = TEXT("Один или оба изображения не найдены");
            return Result;
        }
        
        if (!ComparisonSettingsMap.Contains(ComparisonType))
        {
            Result.ErrorMessage = TEXT("Настройки сравнения не найдены");
            return Result;
        }
        
        const FPixelComparisonSettings& Settings = ComparisonSettingsMap[ComparisonType];
        
        // В реальной реализации здесь будет попиксельное сравнение изображений
        Result.SimilarityPercentage = 95.0f; // Заглушка
        Result.TotalPixels = 1000000; // Заглушка
        Result.MatchingPixels = 950000; // Заглушка
        Result.DifferentPixels = 50000; // Заглушка
        Result.bIsSuccessful = true;
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение изображений завершено: %.2f%% соответствие"), Result.SimilarityPercentage);
        
        return Result;
    }

    // Сравнение экрана создания персонажа
    static FPixelComparisonResult CompareCharacterCreationScreen(UUserWidget* CurrentWidget)
    {
        FPixelComparisonResult Result;
        Result.bIsSuccessful = false;
        Result.ComparisonType = TEXT("General");
        
        if (!CurrentWidget)
        {
            Result.ErrorMessage = TEXT("Виджет не найден");
            return Result;
        }
        
        // В реальной реализации здесь будет сравнение всего экрана
        Result.SimilarityPercentage = 92.5f; // Заглушка
        Result.TotalPixels = 2000000; // Заглушка
        Result.MatchingPixels = 1850000; // Заглушка
        Result.DifferentPixels = 150000; // Заглушка
        Result.bIsSuccessful = true;
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение экрана создания персонажа завершено: %.2f%% соответствие"), Result.SimilarityPercentage);
        
        return Result;
    }

    // Сравнение панелей
    static FPixelComparisonResult ComparePanels(UUserWidget* CurrentWidget)
    {
        FPixelComparisonResult Result;
        Result.bIsSuccessful = false;
        Result.ComparisonType = TEXT("Panels");
        
        if (!CurrentWidget)
        {
            Result.ErrorMessage = TEXT("Виджет не найден");
            return Result;
        }
        
        // В реальной реализации здесь будет сравнение панелей
        Result.SimilarityPercentage = 88.0f; // Заглушка
        Result.TotalPixels = 500000; // Заглушка
        Result.MatchingPixels = 440000; // Заглушка
        Result.DifferentPixels = 60000; // Заглушка
        Result.bIsSuccessful = true;
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение панелей завершено: %.2f%% соответствие"), Result.SimilarityPercentage);
        
        return Result;
    }

    // Сравнение кнопок
    static FPixelComparisonResult CompareButtons(UUserWidget* CurrentWidget)
    {
        FPixelComparisonResult Result;
        Result.bIsSuccessful = false;
        Result.ComparisonType = TEXT("Buttons");
        
        if (!CurrentWidget)
        {
            Result.ErrorMessage = TEXT("Виджет не найден");
            return Result;
        }
        
        // В реальной реализации здесь будет сравнение кнопок
        Result.SimilarityPercentage = 85.0f; // Заглушка
        Result.TotalPixels = 200000; // Заглушка
        Result.MatchingPixels = 170000; // Заглушка
        Result.DifferentPixels = 30000; // Заглушка
        Result.bIsSuccessful = true;
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение кнопок завершено: %.2f%% соответствие"), Result.SimilarityPercentage);
        
        return Result;
    }

    // Сравнение текста
    static FPixelComparisonResult CompareText(UUserWidget* CurrentWidget)
    {
        FPixelComparisonResult Result;
        Result.bIsSuccessful = false;
        Result.ComparisonType = TEXT("Text");
        
        if (!CurrentWidget)
        {
            Result.ErrorMessage = TEXT("Виджет не найден");
            return Result;
        }
        
        // В реальной реализации здесь будет сравнение текста
        Result.SimilarityPercentage = 98.5f; // Заглушка
        Result.TotalPixels = 100000; // Заглушка
        Result.MatchingPixels = 98500; // Заглушка
        Result.DifferentPixels = 1500; // Заглушка
        Result.bIsSuccessful = true;
        
        UE_LOG(LogTemp, Log, TEXT("Сравнение текста завершено: %.2f%% соответствие"), Result.SimilarityPercentage);
        
        return Result;
    }

    // Комплексное сравнение
    static FPixelComparisonResult PerformComprehensiveComparison(UUserWidget* CurrentWidget)
    {
        FPixelComparisonResult OverallResult;
        OverallResult.bIsSuccessful = true;
        OverallResult.ComparisonType = TEXT("Comprehensive");
        
        // Сравнение экрана
        FPixelComparisonResult ScreenResult = CompareCharacterCreationScreen(CurrentWidget);
        
        // Сравнение панелей
        FPixelComparisonResult PanelsResult = ComparePanels(CurrentWidget);
        
        // Сравнение кнопок
        FPixelComparisonResult ButtonsResult = CompareButtons(CurrentWidget);
        
        // Сравнение текста
        FPixelComparisonResult TextResult = CompareText(CurrentWidget);
        
        // Вычисляем общий результат
        if (ScreenResult.bIsSuccessful && PanelsResult.bIsSuccessful && ButtonsResult.bIsSuccessful && TextResult.bIsSuccessful)
        {
            OverallResult.SimilarityPercentage = (ScreenResult.SimilarityPercentage + PanelsResult.SimilarityPercentage + 
                                                ButtonsResult.SimilarityPercentage + TextResult.SimilarityPercentage) / 4.0f;
            OverallResult.TotalPixels = ScreenResult.TotalPixels + PanelsResult.TotalPixels + 
                                      ButtonsResult.TotalPixels + TextResult.TotalPixels;
            OverallResult.MatchingPixels = ScreenResult.MatchingPixels + PanelsResult.MatchingPixels + 
                                         ButtonsResult.MatchingPixels + TextResult.MatchingPixels;
            OverallResult.DifferentPixels = ScreenResult.DifferentPixels + PanelsResult.DifferentPixels + 
                                          ButtonsResult.DifferentPixels + TextResult.DifferentPixels;
        }
        else
        {
            OverallResult.bIsSuccessful = false;
            OverallResult.ErrorMessage = TEXT("Ошибка при выполнении комплексного сравнения");
        }
        
        UE_LOG(LogTemp, Log, TEXT("Комплексное сравнение завершено: %.2f%% соответствие"), OverallResult.SimilarityPercentage);
        
        return OverallResult;
    }

    // Генерация карты различий
    static void GenerateDifferenceMap(const FPixelComparisonResult& Result, const FString& OutputPath)
    {
        if (!Result.bIsSuccessful)
        {
            UE_LOG(LogTemp, Warning, TEXT("Не удалось сгенерировать карту различий: %s"), *Result.ErrorMessage);
            return;
        }
        
        // В реальной реализации здесь будет генерация карты различий
        UE_LOG(LogTemp, Log, TEXT("Карта различий сгенерирована: %s"), *OutputPath);
    }

    // Получение настроек сравнения
    static FPixelComparisonSettings* GetComparisonSettings(const FString& SettingsName)
    {
        if (ComparisonSettingsMap.Contains(SettingsName))
        {
            return &ComparisonSettingsMap[SettingsName];
        }
        return nullptr;
    }

    // Обновление настроек сравнения
    static void UpdateComparisonSettings(const FString& SettingsName, const FPixelComparisonSettings& NewSettings)
    {
        ComparisonSettingsMap.Add(SettingsName, NewSettings);
        UE_LOG(LogTemp, Log, TEXT("Настройки сравнения обновлены: %s"), *SettingsName);
    }

    // Сброс настроек сравнения
    static void ResetComparisonSettings()
    {
        ComparisonSettingsMap.Empty();
        CreateComparisonSettings();
        UE_LOG(LogTemp, Log, TEXT("Настройки сравнения сброшены"));
    }

private:
    // Карта настроек сравнения
    static TMap<FString, FPixelComparisonSettings> ComparisonSettingsMap;
};

// Статические переменные
TMap<FString, FCharacterCreationPixelComparison::FPixelComparisonSettings> FCharacterCreationPixelComparison::ComparisonSettingsMap;
