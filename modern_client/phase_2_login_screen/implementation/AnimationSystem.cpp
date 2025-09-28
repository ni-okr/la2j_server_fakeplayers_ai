#include "CoreMinimal.h"
#include "Components/Button.h"
#include "Components/EditableTextBox.h"
#include "Components/Image.h"
#include "Animation/WidgetAnimation.h"
#include "Animation/UMGSequencePlayer.h"
#include "Tweening/UMGSequencePlayer.h"

/**
 * Система анимаций для экрана входа в соответствии с эталонным клиентом
 * Создает плавные анимации кнопок и полей ввода
 */
class FLoginScreenAnimationSystem
{
public:
    /**
     * Настройка всех анимаций для экрана входа
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupLoginScreenAnimations(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("LoginScreenWidget is null"));
            return;
        }

        // Настройка анимаций кнопок
        SetupButtonAnimations(LoginScreenWidget);
        
        // Настройка анимаций полей ввода
        SetupInputFieldAnimations(LoginScreenWidget);
        
        // Настройка анимации появления экрана
        SetupScreenAppearanceAnimation(LoginScreenWidget);
        
        // Настройка анимации логотипа
        SetupLogoAnimation(LoginScreenWidget);
    }

private:
    /**
     * Настройка анимаций для кнопок (наведение, нажатие, фокус)
     */
    static void SetupButtonAnimations(UUserWidget* LoginScreenWidget)
    {
        // Анимация кнопки "Войти"
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton)
        {
            SetupButtonHoverAnimation(LoginButton, TEXT("LoginButton"));
            SetupButtonClickAnimation(LoginButton, TEXT("LoginButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Войти' настроены"));
        }
        
        // Анимация кнопки "Регистрация"
        UButton* RegisterButton = LoginScreenWidget->FindWidget<UButton>(TEXT("RegisterButton"));
        if (RegisterButton)
        {
            SetupButtonHoverAnimation(RegisterButton, TEXT("RegisterButton"));
            SetupButtonClickAnimation(RegisterButton, TEXT("RegisterButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Регистрация' настроены"));
        }
        
        // Анимация кнопки "Настройки"
        UButton* SettingsButton = LoginScreenWidget->FindWidget<UButton>(TEXT("SettingsButton"));
        if (SettingsButton)
        {
            SetupButtonHoverAnimation(SettingsButton, TEXT("SettingsButton"));
            SetupButtonClickAnimation(SettingsButton, TEXT("SettingsButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Настройки' настроены"));
        }
    }

    /**
     * Настройка анимации наведения на кнопку
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonHoverAnimation(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем анимацию наведения (увеличение масштаба на 5%)
        FWidgetAnimationData HoverAnimation;
        HoverAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Hover"), *ButtonName));
        HoverAnimation.Duration = 0.2f; // Быстрая анимация 200мс
        HoverAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (100% масштаб)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        StartFrame.ColorAndOpacity = FLinearColor::White;
        HoverAnimation.KeyFrames.Add(StartFrame);
        
        // Конечное состояние (105% масштаб, легкое свечение)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.2f;
        EndFrame.Transform.Scale = FVector2D(1.05f, 1.05f);
        EndFrame.ColorAndOpacity = FLinearColor(1.1f, 1.1f, 1.1f, 1.0f); // Легкое свечение
        HoverAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к кнопке
        Button->SetHoveredAnimation(HoverAnimation);
    }

    /**
     * Настройка анимации нажатия кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonClickAnimation(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем анимацию нажатия (уменьшение масштаба на 3%)
        FWidgetAnimationData ClickAnimation;
        ClickAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Click"), *ButtonName));
        ClickAnimation.Duration = 0.1f; // Очень быстрая анимация 100мс
        ClickAnimation.EasingType = EWidgetAnimationEasing::EaseInOut;
        
        // Начальное состояние (100% масштаб)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        StartFrame.ColorAndOpacity = FLinearColor::White;
        ClickAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние нажатия (97% масштаб, затемнение)
        FWidgetAnimationKeyFrame PressFrame;
        PressFrame.Time = 0.05f;
        PressFrame.Transform.Scale = FVector2D(0.97f, 0.97f);
        PressFrame.ColorAndOpacity = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f);
        ClickAnimation.KeyFrames.Add(PressFrame);
        
        // Возврат к исходному состоянию
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.1f;
        EndFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        EndFrame.ColorAndOpacity = FLinearColor::White;
        ClickAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к кнопке
        Button->SetPressedAnimation(ClickAnimation);
    }

    /**
     * Настройка анимаций для полей ввода (фокус, ввод текста)
     */
    static void SetupInputFieldAnimations(UUserWidget* LoginScreenWidget)
    {
        // Анимация поля логина
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField)
        {
            SetupInputFieldFocusAnimation(LoginField, TEXT("LoginField"));
            SetupInputFieldTextAnimation(LoginField, TEXT("LoginField"));
            UE_LOG(LogTemp, Log, TEXT("Анимации поля логина настроены"));
        }
        
        // Анимация поля пароля
        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        if (PasswordField)
        {
            SetupInputFieldFocusAnimation(PasswordField, TEXT("PasswordField"));
            SetupInputFieldTextAnimation(PasswordField, TEXT("PasswordField"));
            UE_LOG(LogTemp, Log, TEXT("Анимации поля пароля настроены"));
        }
    }

    /**
     * Настройка анимации фокуса для поля ввода
     * @param InputField - поле ввода для настройки
     * @param FieldName - имя поля для логирования
     */
    static void SetupInputFieldFocusAnimation(UEditableTextBox* InputField, const FString& FieldName)
    {
        if (!InputField)
        {
            return;
        }

        // Создаем анимацию фокуса (подсветка рамки)
        FWidgetAnimationData FocusAnimation;
        FocusAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Focus"), *FieldName));
        FocusAnimation.Duration = 0.3f; // Плавная анимация 300мс
        FocusAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (обычная рамка)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.BorderColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        StartFrame.BorderThickness = 1.0f;
        FocusAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние фокуса (яркая рамка, увеличенная толщина)
        FWidgetAnimationKeyFrame FocusFrame;
        FocusFrame.Time = 0.3f;
        FocusFrame.BorderColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00BFFF"))); // Голубая рамка
        FocusFrame.BorderThickness = 2.0f;
        FocusAnimation.KeyFrames.Add(FocusFrame);
        
        // Применяем анимацию к полю ввода
        InputField->SetFocusAnimation(FocusAnimation);
    }

    /**
     * Настройка анимации ввода текста для поля
     * @param InputField - поле ввода для настройки
     * @param FieldName - имя поля для логирования
     */
    static void SetupInputFieldTextAnimation(UEditableTextBox* InputField, const FString& FieldName)
    {
        if (!InputField)
        {
            return;
        }

        // Создаем анимацию ввода текста (легкое мерцание)
        FWidgetAnimationData TextAnimation;
        TextAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Text"), *FieldName));
        TextAnimation.Duration = 0.15f; // Быстрая анимация 150мс
        TextAnimation.EasingType = EWidgetAnimationEasing::EaseInOut;
        
        // Начальное состояние
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.TextColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        TextAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние ввода (легкое свечение)
        FWidgetAnimationKeyFrame InputFrame;
        InputFrame.Time = 0.075f;
        InputFrame.TextColor = FLinearColor(1.2f, 1.2f, 1.2f, 1.0f); // Легкое свечение
        TextAnimation.KeyFrames.Add(InputFrame);
        
        // Возврат к исходному состоянию
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.15f;
        EndFrame.TextColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        TextAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к полю ввода
        InputField->SetTextChangedAnimation(TextAnimation);
    }

    /**
     * Настройка анимации появления экрана
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupScreenAppearanceAnimation(UUserWidget* LoginScreenWidget)
    {
        // Создаем анимацию появления экрана (fade in + scale)
        FWidgetAnimationData ScreenAnimation;
        ScreenAnimation.AnimationName = TEXT("ScreenAppearance");
        ScreenAnimation.Duration = 0.8f; // Плавная анимация 800мс
        ScreenAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (прозрачный, уменьшенный)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(0.9f, 0.9f);
        StartFrame.ColorAndOpacity = FLinearColor(1.0f, 1.0f, 1.0f, 0.0f); // Полностью прозрачный
        ScreenAnimation.KeyFrames.Add(StartFrame);
        
        // Промежуточное состояние (полупрозрачный, нормальный размер)
        FWidgetAnimationKeyFrame MidFrame;
        MidFrame.Time = 0.4f;
        MidFrame.Transform.Scale = FVector2D(1.02f, 1.02f); // Легкое превышение
        MidFrame.ColorAndOpacity = FLinearColor(1.0f, 1.0f, 1.0f, 0.7f);
        ScreenAnimation.KeyFrames.Add(MidFrame);
        
        // Конечное состояние (полностью видимый, нормальный размер)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.8f;
        EndFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        EndFrame.ColorAndOpacity = FLinearColor::White;
        ScreenAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к экрану
        LoginScreenWidget->SetAppearanceAnimation(ScreenAnimation);
        UE_LOG(LogTemp, Log, TEXT("Анимация появления экрана настроена"));
    }

    /**
     * Настройка анимации логотипа (пульсация)
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupLogoAnimation(UUserWidget* LoginScreenWidget)
    {
        UTextBlock* LogoText = LoginScreenWidget->FindWidget<UTextBlock>(TEXT("LogoText"));
        if (!LogoText)
        {
            return;
        }

        // Создаем анимацию пульсации логотипа
        FWidgetAnimationData LogoAnimation;
        LogoAnimation.AnimationName = TEXT("LogoPulse");
        LogoAnimation.Duration = 2.0f; // Медленная анимация 2 секунды
        LogoAnimation.EasingType = EWidgetAnimationEasing::EaseInOut;
        LogoAnimation.bLoop = true; // Зацикленная анимация
        
        // Начальное состояние (100% масштаб)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        StartFrame.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        LogoAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние увеличения (105% масштаб, ярче)
        FWidgetAnimationKeyFrame ScaleFrame;
        ScaleFrame.Time = 1.0f;
        ScaleFrame.Transform.Scale = FVector2D(1.05f, 1.05f);
        ScaleFrame.ColorAndOpacity = FLinearColor(1.1f, 0.9f, 0.0f, 1.0f); // Более яркий золотой
        LogoAnimation.KeyFrames.Add(ScaleFrame);
        
        // Возврат к исходному состоянию
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 2.0f;
        EndFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        EndFrame.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        LogoAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к логотипу
        LogoText->SetAnimation(LogoAnimation);
        UE_LOG(LogTemp, Log, TEXT("Анимация логотипа настроена"));
    }
};

/**
 * Утилиты для работы с анимациями экрана входа
 */
class FLoginScreenAnimationUtils
{
public:
    /**
     * Запуск анимации появления экрана
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void PlayScreenAppearanceAnimation(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Запускаем анимацию появления
        LoginScreenWidget->PlayAppearanceAnimation();
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация появления экрана входа"));
    }

    /**
     * Остановка всех анимаций экрана
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void StopAllAnimations(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Останавливаем все анимации
        LoginScreenWidget->StopAllAnimations();
        UE_LOG(LogTemp, Log, TEXT("Все анимации экрана входа остановлены"));
    }

    /**
     * Проверка соответствия анимаций эталону
     * @param LoginScreenWidget - виджет экрана входа
     * @return bool - соответствуют ли анимации эталону
     */
    static bool ValidateAnimationCompliance(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return false;
        }

        // Проверяем наличие всех необходимых анимаций
        bool bAllAnimationsPresent = true;
        
        // Проверяем анимации кнопок
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton && !LoginButton->HasHoverAnimation())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует анимация наведения для кнопки 'Войти'"));
            bAllAnimationsPresent = false;
        }
        
        // Проверяем анимации полей ввода
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField && !LoginField->HasFocusAnimation())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует анимация фокуса для поля логина"));
            bAllAnimationsPresent = false;
        }
        
        if (bAllAnimationsPresent)
        {
            UE_LOG(LogTemp, Log, TEXT("Все анимации соответствуют эталону"));
        }
        
        return bAllAnimationsPresent;
    }
};
