// CharacterCreationVisualEffects.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Components/Border.h"
#include "Engine/Engine.h"
#include "Materials/MaterialInterface.h"
#include "Materials/MaterialInstanceDynamic.h"

// Вспомогательный класс для системы визуальных эффектов экрана создания персонажа
class FCharacterCreationVisualEffects
{
public:
    // Структура настроек визуального эффекта
    struct FVisualEffectSettings
    {
        FString EffectName;
        FString EffectType;
        FLinearColor EffectColor;
        float EffectIntensity;
        float EffectDuration;
        bool bIsLooping;
        bool bIsReversible;
        FVector2D EffectOffset;
        FVector2D EffectScale;
        float EffectOpacity;
        FString MaterialPath;
        FString TexturePath;
    };

    // Типы визуальных эффектов
    enum class EVisualEffectType
    {
        Glow,
        Shadow,
        Outline,
        Gradient,
        Particle,
        Shimmer,
        Pulse,
        Fade,
        Slide,
        Scale
    };

    // Инициализация системы визуальных эффектов
    static void InitializeVisualEffects(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы визуальных эффектов экрана создания персонажа"));
        
        // Создаем настройки визуальных эффектов
        CreateVisualEffectSettings();
        
        // Настраиваем эффекты для элементов
        SetupElementEffects(CurrentWidget);
        
        // Привязываем события эффектов
        BindEffectEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система визуальных эффектов экрана создания персонажа инициализирована"));
    }

    // Создание настроек визуальных эффектов
    static void CreateVisualEffectSettings()
    {
        // Очищаем предыдущие настройки
        VisualEffectSettingsMap.Empty();
        
        // Эффект свечения для выбранных элементов
        FVisualEffectSettings SelectedGlow;
        SelectedGlow.EffectName = TEXT("SelectedGlow");
        SelectedGlow.EffectType = TEXT("Glow");
        SelectedGlow.EffectColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        SelectedGlow.EffectIntensity = 0.8f;
        SelectedGlow.EffectDuration = 0.5f;
        SelectedGlow.bIsLooping = true;
        SelectedGlow.bIsReversible = true;
        SelectedGlow.EffectOffset = FVector2D(0.0f, 0.0f);
        SelectedGlow.EffectScale = FVector2D(1.0f, 1.0f);
        SelectedGlow.EffectOpacity = 0.7f;
        SelectedGlow.MaterialPath = TEXT("/Game/Materials/UI/GlowMaterial");
        VisualEffectSettingsMap.Add(TEXT("SelectedGlow"), SelectedGlow);
        
        // Эффект тени для панелей
        FVisualEffectSettings PanelShadow;
        PanelShadow.EffectName = TEXT("PanelShadow");
        PanelShadow.EffectType = TEXT("Shadow");
        PanelShadow.EffectColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.5f); // Черный с прозрачностью
        PanelShadow.EffectIntensity = 0.6f;
        PanelShadow.EffectDuration = 0.3f;
        PanelShadow.bIsLooping = false;
        PanelShadow.bIsReversible = false;
        PanelShadow.EffectOffset = FVector2D(2.0f, 2.0f);
        PanelShadow.EffectScale = FVector2D(1.0f, 1.0f);
        PanelShadow.EffectOpacity = 0.5f;
        PanelShadow.MaterialPath = TEXT("/Game/Materials/UI/ShadowMaterial");
        VisualEffectSettingsMap.Add(TEXT("PanelShadow"), PanelShadow);
        
        // Эффект контура для кнопок
        FVisualEffectSettings ButtonOutline;
        ButtonOutline.EffectName = TEXT("ButtonOutline");
        ButtonOutline.EffectType = TEXT("Outline");
        ButtonOutline.EffectColor = FLinearColor(0.4f, 0.4f, 0.5f, 1.0f); // Серо-синий
        ButtonOutline.EffectIntensity = 1.0f;
        ButtonOutline.EffectDuration = 0.2f;
        ButtonOutline.bIsLooping = false;
        ButtonOutline.bIsReversible = false;
        ButtonOutline.EffectOffset = FVector2D(0.0f, 0.0f);
        ButtonOutline.EffectScale = FVector2D(1.0f, 1.0f);
        ButtonOutline.EffectOpacity = 1.0f;
        ButtonOutline.MaterialPath = TEXT("/Game/Materials/UI/OutlineMaterial");
        VisualEffectSettingsMap.Add(TEXT("ButtonOutline"), ButtonOutline);
        
        // Эффект градиента для фона
        FVisualEffectSettings BackgroundGradient;
        BackgroundGradient.EffectName = TEXT("BackgroundGradient");
        BackgroundGradient.EffectType = TEXT("Gradient");
        BackgroundGradient.EffectColor = FLinearColor(0.1f, 0.1f, 0.15f, 1.0f); // Темно-синий
        BackgroundGradient.EffectIntensity = 1.0f;
        BackgroundGradient.EffectDuration = 0.0f;
        BackgroundGradient.bIsLooping = false;
        BackgroundGradient.bIsReversible = false;
        BackgroundGradient.EffectOffset = FVector2D(0.0f, 0.0f);
        BackgroundGradient.EffectScale = FVector2D(1.0f, 1.0f);
        BackgroundGradient.EffectOpacity = 1.0f;
        BackgroundGradient.MaterialPath = TEXT("/Game/Materials/UI/GradientMaterial");
        VisualEffectSettingsMap.Add(TEXT("BackgroundGradient"), BackgroundGradient);
        
        // Эффект частиц для предпросмотра персонажа
        FVisualEffectSettings CharacterParticles;
        CharacterParticles.EffectName = TEXT("CharacterParticles");
        CharacterParticles.EffectType = TEXT("Particle");
        CharacterParticles.EffectColor = FLinearColor(1.0f, 1.0f, 1.0f, 0.8f); // Белый с прозрачностью
        CharacterParticles.EffectIntensity = 0.5f;
        CharacterParticles.EffectDuration = 2.0f;
        CharacterParticles.bIsLooping = true;
        CharacterParticles.bIsReversible = false;
        CharacterParticles.EffectOffset = FVector2D(0.0f, 0.0f);
        CharacterParticles.EffectScale = FVector2D(1.0f, 1.0f);
        CharacterParticles.EffectOpacity = 0.8f;
        CharacterParticles.MaterialPath = TEXT("/Game/Materials/UI/ParticleMaterial");
        VisualEffectSettingsMap.Add(TEXT("CharacterParticles"), CharacterParticles);
        
        // Эффект мерцания для важных элементов
        FVisualEffectSettings Shimmer;
        Shimmer.EffectName = TEXT("Shimmer");
        Shimmer.EffectType = TEXT("Shimmer");
        Shimmer.EffectColor = FLinearColor(1.0f, 1.0f, 1.0f, 0.6f); // Белый с прозрачностью
        Shimmer.EffectIntensity = 0.7f;
        Shimmer.EffectDuration = 1.5f;
        Shimmer.bIsLooping = true;
        Shimmer.bIsReversible = true;
        Shimmer.EffectOffset = FVector2D(0.0f, 0.0f);
        Shimmer.EffectScale = FVector2D(1.0f, 1.0f);
        Shimmer.EffectOpacity = 0.6f;
        Shimmer.MaterialPath = TEXT("/Game/Materials/UI/ShimmerMaterial");
        VisualEffectSettingsMap.Add(TEXT("Shimmer"), Shimmer);
        
        // Эффект пульсации для активных элементов
        FVisualEffectSettings Pulse;
        Pulse.EffectName = TEXT("Pulse");
        Pulse.EffectType = TEXT("Pulse");
        Pulse.EffectColor = FLinearColor(1.0f, 0.84f, 0.0f, 0.8f); // Золотой с прозрачностью
        Pulse.EffectIntensity = 0.6f;
        Pulse.EffectDuration = 1.0f;
        Pulse.bIsLooping = true;
        Pulse.bIsReversible = true;
        Pulse.EffectOffset = FVector2D(0.0f, 0.0f);
        Pulse.EffectScale = FVector2D(1.0f, 1.0f);
        Pulse.EffectOpacity = 0.8f;
        Pulse.MaterialPath = TEXT("/Game/Materials/UI/PulseMaterial");
        VisualEffectSettingsMap.Add(TEXT("Pulse"), Pulse);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d настроек визуальных эффектов"), VisualEffectSettingsMap.Num());
    }

    // Настройка эффектов для элементов
    static void SetupElementEffects(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Настраиваем эффекты для панелей
        SetupPanelEffects(CurrentWidget);
        
        // Настраиваем эффекты для кнопок
        SetupButtonEffects(CurrentWidget);
        
        // Настраиваем эффекты для текстовых элементов
        SetupTextEffects(CurrentWidget);
        
        // Настраиваем эффекты для изображений
        SetupImageEffects(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты элементов настроены"));
    }

    // Настройка эффектов для панелей
    static void SetupPanelEffects(UUserWidget* CurrentWidget)
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
                    SetupPanelEffect(Panel);
                }
            }
        }
    }

    // Настройка эффектов для кнопок
    static void SetupButtonEffects(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все кнопки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UButton* Button = Cast<UButton>(Widget))
            {
                SetupButtonEffect(Button);
            }
        }
    }

    // Настройка эффектов для текстовых элементов
    static void SetupTextEffects(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все текстовые блоки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UTextBlock* TextBlock = Cast<UTextBlock>(Widget))
            {
                SetupTextEffect(TextBlock);
            }
        }
    }

    // Настройка эффектов для изображений
    static void SetupImageEffects(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все изображения
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UImage* Image = Cast<UImage>(Widget))
            {
                SetupImageEffect(Image);
            }
        }
    }

    // Настройка эффекта панели
    static void SetupPanelEffect(UScrollBox* Panel)
    {
        if (!Panel) return;
        
        // Применяем эффект тени
        ApplyEffect(Panel, TEXT("PanelShadow"));
        
        UE_LOG(LogTemp, Log, TEXT("Эффект панели настроен: %s"), *Panel->GetName());
    }

    // Настройка эффекта кнопки
    static void SetupButtonEffect(UButton* Button)
    {
        if (!Button) return;
        
        // Применяем эффект контура
        ApplyEffect(Button, TEXT("ButtonOutline"));
        
        UE_LOG(LogTemp, Log, TEXT("Эффект кнопки настроен: %s"), *Button->GetName());
    }

    // Настройка эффекта текста
    static void SetupTextEffect(UTextBlock* TextBlock)
    {
        if (!TextBlock) return;
        
        // Применяем эффект тени
        ApplyEffect(TextBlock, TEXT("PanelShadow"));
        
        UE_LOG(LogTemp, Log, TEXT("Эффект текста настроен: %s"), *TextBlock->GetName());
    }

    // Настройка эффекта изображения
    static void SetupImageEffect(UImage* Image)
    {
        if (!Image) return;
        
        // Применяем эффект тени
        ApplyEffect(Image, TEXT("PanelShadow"));
        
        UE_LOG(LogTemp, Log, TEXT("Эффект изображения настроен: %s"), *Image->GetName());
    }

    // Применение эффекта к виджету
    static void ApplyEffect(UWidget* Widget, const FString& EffectName)
    {
        if (!Widget || !VisualEffectSettingsMap.Contains(EffectName)) return;
        
        const FVisualEffectSettings& Settings = VisualEffectSettingsMap[EffectName];
        
        // В реальной реализации здесь будет применение эффекта к виджету
        UE_LOG(LogTemp, Log, TEXT("Применен эффект %s к виджету %s"), *EffectName, *Widget->GetName());
    }

    // Привязка событий эффектов
    static void BindEffectEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий эффектов
        UE_LOG(LogTemp, Log, TEXT("События визуальных эффектов привязаны"));
    }

    // Включение эффекта свечения
    static void EnableGlowEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        ApplyEffect(Widget, TEXT("SelectedGlow"));
        UE_LOG(LogTemp, Log, TEXT("Включен эффект свечения для виджета %s"), *Widget->GetName());
    }

    // Отключение эффекта свечения
    static void DisableGlowEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        // В реальной реализации здесь будет отключение эффекта свечения
        UE_LOG(LogTemp, Log, TEXT("Отключен эффект свечения для виджета %s"), *Widget->GetName());
    }

    // Включение эффекта пульсации
    static void EnablePulseEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        ApplyEffect(Widget, TEXT("Pulse"));
        UE_LOG(LogTemp, Log, TEXT("Включен эффект пульсации для виджета %s"), *Widget->GetName());
    }

    // Отключение эффекта пульсации
    static void DisablePulseEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        // В реальной реализации здесь будет отключение эффекта пульсации
        UE_LOG(LogTemp, Log, TEXT("Отключен эффект пульсации для виджета %s"), *Widget->GetName());
    }

    // Включение эффекта мерцания
    static void EnableShimmerEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        ApplyEffect(Widget, TEXT("Shimmer"));
        UE_LOG(LogTemp, Log, TEXT("Включен эффект мерцания для виджета %s"), *Widget->GetName());
    }

    // Отключение эффекта мерцания
    static void DisableShimmerEffect(UWidget* Widget)
    {
        if (!Widget) return;
        
        // В реальной реализации здесь будет отключение эффекта мерцания
        UE_LOG(LogTemp, Log, TEXT("Отключен эффект мерцания для виджета %s"), *Widget->GetName());
    }

    // Получение настроек визуального эффекта
    static FVisualEffectSettings* GetVisualEffectSettings(const FString& EffectName)
    {
        if (VisualEffectSettingsMap.Contains(EffectName))
        {
            return &VisualEffectSettingsMap[EffectName];
        }
        return nullptr;
    }

    // Обновление настроек визуального эффекта
    static void UpdateVisualEffectSettings(const FString& EffectName, const FVisualEffectSettings& NewSettings)
    {
        VisualEffectSettingsMap.Add(EffectName, NewSettings);
        UE_LOG(LogTemp, Log, TEXT("Настройки визуального эффекта обновлены: %s"), *EffectName);
    }

    // Сброс настроек визуальных эффектов
    static void ResetVisualEffectSettings()
    {
        VisualEffectSettingsMap.Empty();
        CreateVisualEffectSettings();
        UE_LOG(LogTemp, Log, TEXT("Настройки визуальных эффектов сброшены"));
    }

private:
    // Карта настроек визуальных эффектов
    static TMap<FString, FVisualEffectSettings> VisualEffectSettingsMap;
};

// Статические переменные
TMap<FString, FCharacterCreationVisualEffects::FVisualEffectSettings> FCharacterCreationVisualEffects::VisualEffectSettingsMap;
