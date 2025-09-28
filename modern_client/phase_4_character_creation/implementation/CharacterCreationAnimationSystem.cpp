// CharacterCreationAnimationSystem.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Components/ScrollBox.h"
#include "Engine/Engine.h"
#include "Animation/WidgetAnimation.h"
#include "Animation/UMGSequencePlayer.h"

// Вспомогательный класс для системы анимаций экрана создания персонажа
class FCharacterCreationAnimationSystem
{
public:
    // Структура настроек анимации
    struct FAnimationSettings
    {
        FString AnimationName;
        float Duration;
        float Delay;
        ECurveEaseFunction EaseFunction;
        bool bLoop;
        bool bReverse;
        bool bAutoStart;
        FLinearColor StartColor;
        FLinearColor EndColor;
        FVector2D StartPosition;
        FVector2D EndPosition;
        FVector2D StartScale;
        FVector2D EndScale;
        float StartOpacity;
        float EndOpacity;
    };

    // Типы анимаций
    enum class EAnimationType
    {
        FadeIn,
        FadeOut,
        SlideIn,
        SlideOut,
        ScaleIn,
        ScaleOut,
        Glow,
        Pulse,
        Shake,
        Rotate
    };

    // Инициализация системы анимаций
    static void InitializeAnimationSystem(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы анимаций экрана создания персонажа"));
        
        // Создаем настройки анимаций
        CreateAnimationSettings();
        
        // Настраиваем анимации для элементов
        SetupElementAnimations(CurrentWidget);
        
        // Привязываем события анимаций
        BindAnimationEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система анимаций экрана создания персонажа инициализирована"));
    }

    // Создание настроек анимаций
    static void CreateAnimationSettings()
    {
        // Очищаем предыдущие настройки
        AnimationSettingsMap.Empty();
        
        // Анимация появления экрана
        FAnimationSettings ScreenFadeIn;
        ScreenFadeIn.AnimationName = TEXT("ScreenFadeIn");
        ScreenFadeIn.Duration = 1.0f;
        ScreenFadeIn.Delay = 0.0f;
        ScreenFadeIn.EaseFunction = ECurveEaseFunction::EaseInOut;
        ScreenFadeIn.bLoop = false;
        ScreenFadeIn.bReverse = false;
        ScreenFadeIn.bAutoStart = true;
        ScreenFadeIn.StartOpacity = 0.0f;
        ScreenFadeIn.EndOpacity = 1.0f;
        AnimationSettingsMap.Add(TEXT("ScreenFadeIn"), ScreenFadeIn);
        
        // Анимация исчезновения экрана
        FAnimationSettings ScreenFadeOut;
        ScreenFadeOut.AnimationName = TEXT("ScreenFadeOut");
        ScreenFadeOut.Duration = 0.5f;
        ScreenFadeOut.Delay = 0.0f;
        ScreenFadeOut.EaseFunction = ECurveEaseFunction::EaseInOut;
        ScreenFadeOut.bLoop = false;
        ScreenFadeOut.bReverse = false;
        ScreenFadeOut.bAutoStart = false;
        ScreenFadeOut.StartOpacity = 1.0f;
        ScreenFadeOut.EndOpacity = 0.0f;
        AnimationSettingsMap.Add(TEXT("ScreenFadeOut"), ScreenFadeOut);
        
        // Анимация появления панелей
        FAnimationSettings PanelSlideIn;
        PanelSlideIn.AnimationName = TEXT("PanelSlideIn");
        PanelSlideIn.Duration = 0.8f;
        PanelSlideIn.Delay = 0.2f;
        PanelSlideIn.EaseFunction = ECurveEaseFunction::EaseOut;
        PanelSlideIn.bLoop = false;
        PanelSlideIn.bReverse = false;
        PanelSlideIn.bAutoStart = true;
        PanelSlideIn.StartPosition = FVector2D(-200.0f, 0.0f);
        PanelSlideIn.EndPosition = FVector2D(0.0f, 0.0f);
        PanelSlideIn.StartOpacity = 0.0f;
        PanelSlideIn.EndOpacity = 1.0f;
        AnimationSettingsMap.Add(TEXT("PanelSlideIn"), PanelSlideIn);
        
        // Анимация наведения на кнопку
        FAnimationSettings ButtonHover;
        ButtonHover.AnimationName = TEXT("ButtonHover");
        ButtonHover.Duration = 0.3f;
        ButtonHover.Delay = 0.0f;
        ButtonHover.EaseFunction = ECurveEaseFunction::EaseOut;
        ButtonHover.bLoop = false;
        ButtonHover.bReverse = false;
        ButtonHover.bAutoStart = false;
        ButtonHover.StartScale = FVector2D(1.0f, 1.0f);
        ButtonHover.EndScale = FVector2D(1.1f, 1.1f);
        ButtonHover.StartColor = FLinearColor(0.8f, 0.8f, 0.8f, 1.0f);
        ButtonHover.EndColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        AnimationSettingsMap.Add(TEXT("ButtonHover"), ButtonHover);
        
        // Анимация выбора элемента
        FAnimationSettings ElementSelect;
        ElementSelect.AnimationName = TEXT("ElementSelect");
        ElementSelect.Duration = 0.2f;
        ElementSelect.Delay = 0.0f;
        ElementSelect.EaseFunction = ECurveEaseFunction::EaseOut;
        ElementSelect.bLoop = false;
        ElementSelect.bReverse = false;
        ElementSelect.bAutoStart = false;
        ElementSelect.StartScale = FVector2D(1.0f, 1.0f);
        ElementSelect.EndScale = FVector2D(1.05f, 1.05f);
        ElementSelect.StartColor = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f);
        ElementSelect.EndColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        AnimationSettingsMap.Add(TEXT("ElementSelect"), ElementSelect);
        
        // Анимация пульсации
        FAnimationSettings Pulse;
        Pulse.AnimationName = TEXT("Pulse");
        Pulse.Duration = 1.0f;
        Pulse.Delay = 0.0f;
        Pulse.EaseFunction = ECurveEaseFunction::EaseInOut;
        Pulse.bLoop = true;
        Pulse.bReverse = true;
        Pulse.bAutoStart = false;
        Pulse.StartScale = FVector2D(1.0f, 1.0f);
        Pulse.EndScale = FVector2D(1.1f, 1.1f);
        Pulse.StartOpacity = 0.8f;
        Pulse.EndOpacity = 1.0f;
        AnimationSettingsMap.Add(TEXT("Pulse"), Pulse);
        
        // Анимация тряски (для ошибок)
        FAnimationSettings Shake;
        Shake.AnimationName = TEXT("Shake");
        Shake.Duration = 0.5f;
        Shake.Delay = 0.0f;
        Shake.EaseFunction = ECurveEaseFunction::EaseInOut;
        Shake.bLoop = false;
        Shake.bReverse = false;
        Shake.bAutoStart = false;
        Shake.StartPosition = FVector2D(0.0f, 0.0f);
        Shake.EndPosition = FVector2D(10.0f, 0.0f);
        AnimationSettingsMap.Add(TEXT("Shake"), Shake);
        
        // Анимация свечения
        FAnimationSettings Glow;
        Glow.AnimationName = TEXT("Glow");
        Glow.Duration = 2.0f;
        Glow.Delay = 0.0f;
        Glow.EaseFunction = ECurveEaseFunction::EaseInOut;
        Glow.bLoop = true;
        Glow.bReverse = true;
        Glow.bAutoStart = false;
        Glow.StartColor = FLinearColor(1.0f, 0.84f, 0.0f, 0.5f); // Золотой с прозрачностью
        Glow.EndColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        AnimationSettingsMap.Add(TEXT("Glow"), Glow);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d настроек анимаций"), AnimationSettingsMap.Num());
    }

    // Настройка анимаций для элементов
    static void SetupElementAnimations(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Настраиваем анимации для панелей
        SetupPanelAnimations(CurrentWidget);
        
        // Настраиваем анимации для кнопок
        SetupButtonAnimations(CurrentWidget);
        
        // Настраиваем анимации для текстовых элементов
        SetupTextAnimations(CurrentWidget);
        
        // Настраиваем анимации для полей ввода
        SetupInputAnimations(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Анимации элементов настроены"));
    }

    // Настройка анимаций для панелей
    static void SetupPanelAnimations(UUserWidget* CurrentWidget)
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
                    SetupPanelAnimation(Panel);
                }
            }
        }
    }

    // Настройка анимаций для кнопок
    static void SetupButtonAnimations(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все кнопки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UButton* Button = Cast<UButton>(Widget))
            {
                SetupButtonAnimation(Button);
            }
        }
    }

    // Настройка анимаций для текстовых элементов
    static void SetupTextAnimations(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все текстовые блоки
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UTextBlock* TextBlock = Cast<UTextBlock>(Widget))
            {
                SetupTextAnimation(TextBlock);
            }
        }
    }

    // Настройка анимаций для полей ввода
    static void SetupInputAnimations(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим все поля ввода
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UEditableTextBox* InputField = Cast<UEditableTextBox>(Widget))
            {
                SetupInputAnimation(InputField);
            }
        }
    }

    // Настройка анимации панели
    static void SetupPanelAnimation(UScrollBox* Panel)
    {
        if (!Panel) return;
        
        // В реальной реализации здесь будет настройка анимации панели
        UE_LOG(LogTemp, Log, TEXT("Анимация панели настроена: %s"), *Panel->GetName());
    }

    // Настройка анимации кнопки
    static void SetupButtonAnimation(UButton* Button)
    {
        if (!Button) return;
        
        // В реальной реализации здесь будет настройка анимации кнопки
        UE_LOG(LogTemp, Log, TEXT("Анимация кнопки настроена: %s"), *Button->GetName());
    }

    // Настройка анимации текста
    static void SetupTextAnimation(UTextBlock* TextBlock)
    {
        if (!TextBlock) return;
        
        // В реальной реализации здесь будет настройка анимации текста
        UE_LOG(LogTemp, Log, TEXT("Анимация текста настроена: %s"), *TextBlock->GetName());
    }

    // Настройка анимации поля ввода
    static void SetupInputAnimation(UEditableTextBox* InputField)
    {
        if (!InputField) return;
        
        // В реальной реализации здесь будет настройка анимации поля ввода
        UE_LOG(LogTemp, Log, TEXT("Анимация поля ввода настроена: %s"), *InputField->GetName());
    }

    // Привязка событий анимаций
    static void BindAnimationEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий анимаций
        UE_LOG(LogTemp, Log, TEXT("События анимаций привязаны"));
    }

    // Запуск анимации
    static void PlayAnimation(UWidget* Widget, const FString& AnimationName)
    {
        if (!Widget || !AnimationSettingsMap.Contains(AnimationName)) return;
        
        const FAnimationSettings& Settings = AnimationSettingsMap[AnimationName];
        
        // В реальной реализации здесь будет запуск анимации
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация %s для виджета %s"), *AnimationName, *Widget->GetName());
    }

    // Остановка анимации
    static void StopAnimation(UWidget* Widget, const FString& AnimationName)
    {
        if (!Widget || !AnimationSettingsMap.Contains(AnimationName)) return;
        
        // В реальной реализации здесь будет остановка анимации
        UE_LOG(LogTemp, Log, TEXT("Остановлена анимация %s для виджета %s"), *AnimationName, *Widget->GetName());
    }

    // Анимация появления экрана
    static void PlayScreenFadeIn(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        PlayAnimation(CurrentWidget, TEXT("ScreenFadeIn"));
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация появления экрана"));
    }

    // Анимация исчезновения экрана
    static void PlayScreenFadeOut(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        PlayAnimation(CurrentWidget, TEXT("ScreenFadeOut"));
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация исчезновения экрана"));
    }

    // Анимация наведения на кнопку
    static void PlayButtonHoverAnimation(UButton* Button)
    {
        if (!Button) return;
        
        PlayAnimation(Button, TEXT("ButtonHover"));
    }

    // Анимация выбора элемента
    static void PlayElementSelectAnimation(UWidget* Widget)
    {
        if (!Widget) return;
        
        PlayAnimation(Widget, TEXT("ElementSelect"));
    }

    // Анимация пульсации
    static void PlayPulseAnimation(UWidget* Widget)
    {
        if (!Widget) return;
        
        PlayAnimation(Widget, TEXT("Pulse"));
    }

    // Анимация тряски (для ошибок)
    static void PlayShakeAnimation(UWidget* Widget)
    {
        if (!Widget) return;
        
        PlayAnimation(Widget, TEXT("Shake"));
    }

    // Анимация свечения
    static void PlayGlowAnimation(UWidget* Widget)
    {
        if (!Widget) return;
        
        PlayAnimation(Widget, TEXT("Glow"));
    }

    // Получение настроек анимации
    static FAnimationSettings* GetAnimationSettings(const FString& AnimationName)
    {
        if (AnimationSettingsMap.Contains(AnimationName))
        {
            return &AnimationSettingsMap[AnimationName];
        }
        return nullptr;
    }

    // Обновление настроек анимации
    static void UpdateAnimationSettings(const FString& AnimationName, const FAnimationSettings& NewSettings)
    {
        AnimationSettingsMap.Add(AnimationName, NewSettings);
        UE_LOG(LogTemp, Log, TEXT("Настройки анимации обновлены: %s"), *AnimationName);
    }

    // Сброс настроек анимаций
    static void ResetAnimationSettings()
    {
        AnimationSettingsMap.Empty();
        CreateAnimationSettings();
        UE_LOG(LogTemp, Log, TEXT("Настройки анимаций сброшены"));
    }

private:
    // Карта настроек анимаций
    static TMap<FString, FAnimationSettings> AnimationSettingsMap;
};

// Статические переменные
TMap<FString, FCharacterCreationAnimationSystem::FAnimationSettings> FCharacterCreationAnimationSystem::AnimationSettingsMap;
