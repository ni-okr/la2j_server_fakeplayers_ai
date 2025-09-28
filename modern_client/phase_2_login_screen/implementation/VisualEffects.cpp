#include "CoreMinimal.h"
#include "Components/Button.h"
#include "Components/EditableTextBox.h"
#include "Components/Image.h"
#include "Components/Border.h"
#include "Materials/MaterialInstanceDynamic.h"
#include "Engine/Texture2D.h"

/**
 * Система визуальных эффектов для экрана входа в соответствии с эталонным клиентом
 * Создает эффекты наведения, фокуса и интерактивности
 */
class FLoginScreenVisualEffects
{
public:
    /**
     * Настройка всех визуальных эффектов для экрана входа
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupLoginScreenEffects(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("LoginScreenWidget is null"));
            return;
        }

        // Настройка эффектов кнопок
        SetupButtonEffects(LoginScreenWidget);
        
        // Настройка эффектов полей ввода
        SetupInputFieldEffects(LoginScreenWidget);
        
        // Настройка эффектов фона
        SetupBackgroundEffects(LoginScreenWidget);
        
        // Настройка эффектов логотипа
        SetupLogoEffects(LoginScreenWidget);
    }

private:
    /**
     * Настройка визуальных эффектов для кнопок
     */
    static void SetupButtonEffects(UUserWidget* LoginScreenWidget)
    {
        // Эффекты кнопки "Войти"
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton)
        {
            SetupButtonHoverEffect(LoginButton, TEXT("LoginButton"));
            SetupButtonFocusEffect(LoginButton, TEXT("LoginButton"));
            SetupButtonGlowEffect(LoginButton, TEXT("LoginButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Войти' настроены"));
        }
        
        // Эффекты кнопки "Регистрация"
        UButton* RegisterButton = LoginScreenWidget->FindWidget<UButton>(TEXT("RegisterButton"));
        if (RegisterButton)
        {
            SetupButtonHoverEffect(RegisterButton, TEXT("RegisterButton"));
            SetupButtonFocusEffect(RegisterButton, TEXT("RegisterButton"));
            SetupButtonGlowEffect(RegisterButton, TEXT("RegisterButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Регистрация' настроены"));
        }
        
        // Эффекты кнопки "Настройки"
        UButton* SettingsButton = LoginScreenWidget->FindWidget<UButton>(TEXT("SettingsButton"));
        if (SettingsButton)
        {
            SetupButtonHoverEffect(SettingsButton, TEXT("SettingsButton"));
            SetupButtonFocusEffect(SettingsButton, TEXT("SettingsButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Настройки' настроены"));
        }
    }

    /**
     * Настройка эффекта наведения для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonHoverEffect(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта наведения
        UMaterialInstanceDynamic* HoverMaterial = CreateButtonHoverMaterial(ButtonName);
        if (HoverMaterial)
        {
            // Настраиваем параметры материала
            HoverMaterial->SetScalarParameterValue(TEXT("GlowIntensity"), 1.2f);
            HoverMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            HoverMaterial->SetScalarParameterValue(TEXT("BorderThickness"), 2.0f);
            
            // Применяем материал к кнопке при наведении
            Button->SetHoveredMaterial(HoverMaterial);
        }

        // Настраиваем эффект тени при наведении
        FButtonStyle ButtonStyle = Button->GetStyle();
        FSlateBrush HoveredBrush = ButtonStyle.Hovered;
        HoveredBrush.DrawAs = ESlateBrushDrawType::Box;
        HoveredBrush.Margin = FMargin(2.0f);
        
        // Создаем эффект тени
        FSlateShadowEffect ShadowEffect;
        ShadowEffect.Color = FLinearColor(0.0f, 0.0f, 0.0f, 0.3f);
        ShadowEffect.Offset = FVector2D(2.0f, 2.0f);
        ShadowEffect.BlurRadius = 4.0f;
        HoveredBrush.OutlineSettings = FSlateBrushOutlineSettings(ShadowEffect);
        
        ButtonStyle.Hovered = HoveredBrush;
        Button->SetStyle(ButtonStyle);
    }

    /**
     * Настройка эффекта фокуса для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonFocusEffect(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта фокуса
        UMaterialInstanceDynamic* FocusMaterial = CreateButtonFocusMaterial(ButtonName);
        if (FocusMaterial)
        {
            // Настраиваем параметры материала
            FocusMaterial->SetScalarParameterValue(TEXT("FocusIntensity"), 1.5f);
            FocusMaterial->SetVectorParameterValue(TEXT("FocusColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00BFFF"))));
            FocusMaterial->SetScalarParameterValue(TEXT("PulseSpeed"), 2.0f);
            
            // Применяем материал к кнопке при фокусе
            Button->SetFocusedMaterial(FocusMaterial);
        }
    }

    /**
     * Настройка эффекта свечения для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonGlowEffect(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта свечения
        UMaterialInstanceDynamic* GlowMaterial = CreateButtonGlowMaterial(ButtonName);
        if (GlowMaterial)
        {
            // Настраиваем параметры материала
            GlowMaterial->SetScalarParameterValue(TEXT("GlowRadius"), 8.0f);
            GlowMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            GlowMaterial->SetScalarParameterValue(TEXT("GlowOpacity"), 0.8f);
            
            // Применяем материал к кнопке
            Button->SetGlowMaterial(GlowMaterial);
        }
    }

    /**
     * Настройка визуальных эффектов для полей ввода
     */
    static void SetupInputFieldEffects(UUserWidget* LoginScreenWidget)
    {
        // Эффекты поля логина
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField)
        {
            SetupInputFieldFocusEffect(LoginField, TEXT("LoginField"));
            SetupInputFieldValidationEffect(LoginField, TEXT("LoginField"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты поля логина настроены"));
        }
        
        // Эффекты поля пароля
        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        if (PasswordField)
        {
            SetupInputFieldFocusEffect(PasswordField, TEXT("PasswordField"));
            SetupInputFieldValidationEffect(PasswordField, TEXT("PasswordField"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты поля пароля настроены"));
        }
    }

    /**
     * Настройка эффекта фокуса для поля ввода
     * @param InputField - поле ввода для настройки
     * @param FieldName - имя поля для логирования
     */
    static void SetupInputFieldFocusEffect(UEditableTextBox* InputField, const FString& FieldName)
    {
        if (!InputField)
        {
            return;
        }

        // Создаем материал для эффекта фокуса
        UMaterialInstanceDynamic* FocusMaterial = CreateInputFieldFocusMaterial(FieldName);
        if (FocusMaterial)
        {
            // Настраиваем параметры материала
            FocusMaterial->SetScalarParameterValue(TEXT("FocusIntensity"), 1.3f);
            FocusMaterial->SetVectorParameterValue(TEXT("FocusColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00BFFF"))));
            FocusMaterial->SetScalarParameterValue(TEXT("BorderThickness"), 2.0f);
            
            // Применяем материал к полю ввода при фокусе
            InputField->SetFocusedMaterial(FocusMaterial);
        }

        // Настраиваем стиль поля ввода
        FEditableTextBoxStyle FieldStyle = InputField->GetStyle();
        
        // Стиль обычного состояния
        FSlateBrush NormalBrush = FieldStyle.NormalBackgroundImage;
        NormalBrush.DrawAs = ESlateBrushDrawType::Box;
        NormalBrush.Margin = FMargin(1.0f);
        NormalBrush.TintColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("000000AA")));
        FieldStyle.NormalBackgroundImage = NormalBrush;
        
        // Стиль состояния фокуса
        FSlateBrush FocusedBrush = FieldStyle.FocusedBackgroundImage;
        FocusedBrush.DrawAs = ESlateBrushDrawType::Box;
        FocusedBrush.Margin = FMargin(2.0f);
        FocusedBrush.TintColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("000000CC")));
        
        // Эффект свечения для фокуса
        FSlateShadowEffect FocusGlow;
        FocusGlow.Color = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00BFFF")));
        FocusGlow.Offset = FVector2D(0.0f, 0.0f);
        FocusGlow.BlurRadius = 4.0f;
        FocusedBrush.OutlineSettings = FSlateBrushOutlineSettings(FocusGlow);
        
        FieldStyle.FocusedBackgroundImage = FocusedBrush;
        InputField->SetStyle(FieldStyle);
    }

    /**
     * Настройка эффекта валидации для поля ввода
     * @param InputField - поле ввода для настройки
     * @param FieldName - имя поля для логирования
     */
    static void SetupInputFieldValidationEffect(UEditableTextBox* InputField, const FString& FieldName)
    {
        if (!InputField)
        {
            return;
        }

        // Создаем материал для эффекта валидации
        UMaterialInstanceDynamic* ValidationMaterial = CreateInputFieldValidationMaterial(FieldName);
        if (ValidationMaterial)
        {
            // Настраиваем параметры материала
            ValidationMaterial->SetScalarParameterValue(TEXT("ValidationIntensity"), 1.0f);
            ValidationMaterial->SetVectorParameterValue(TEXT("ValidColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00FF00"))));
            ValidationMaterial->SetVectorParameterValue(TEXT("InvalidColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FF0000"))));
            
            // Применяем материал к полю ввода
            InputField->SetValidationMaterial(ValidationMaterial);
        }
    }

    /**
     * Настройка визуальных эффектов для фона
     */
    static void SetupBackgroundEffects(UUserWidget* LoginScreenWidget)
    {
        UImage* BackgroundImage = LoginScreenWidget->FindWidget<UImage>(TEXT("BackgroundImage"));
        if (!BackgroundImage)
        {
            return;
        }

        // Создаем материал для эффекта фона
        UMaterialInstanceDynamic* BackgroundMaterial = CreateBackgroundMaterial();
        if (BackgroundMaterial)
        {
            // Настраиваем параметры материала
            BackgroundMaterial->SetScalarParameterValue(TEXT("ParallaxSpeed"), 0.5f);
            BackgroundMaterial->SetVectorParameterValue(TEXT("TintColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("1e1e2e"))));
            BackgroundMaterial->SetScalarParameterValue(TEXT("Contrast"), 1.1f);
            
            // Применяем материал к фону
            BackgroundImage->SetBrushFromMaterial(BackgroundMaterial);
        }

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты фона настроены"));
    }

    /**
     * Настройка визуальных эффектов для логотипа
     */
    static void SetupLogoEffects(UUserWidget* LoginScreenWidget)
    {
        UTextBlock* LogoText = LoginScreenWidget->FindWidget<UTextBlock>(TEXT("LogoText"));
        if (!LogoText)
        {
            return;
        }

        // Создаем материал для эффекта логотипа
        UMaterialInstanceDynamic* LogoMaterial = CreateLogoMaterial();
        if (LogoMaterial)
        {
            // Настраиваем параметры материала
            LogoMaterial->SetScalarParameterValue(TEXT("GlowIntensity"), 1.5f);
            LogoMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            LogoMaterial->SetScalarParameterValue(TEXT("PulseSpeed"), 1.0f);
            LogoMaterial->SetScalarParameterValue(TEXT("ShimmerIntensity"), 0.3f);
            
            // Применяем материал к логотипу
            LogoText->SetFontMaterial(LogoMaterial);
        }

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты логотипа настроены"));
    }

    // Методы создания материалов (заглушки для демонстрации)
    static UMaterialInstanceDynamic* CreateButtonHoverMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateButtonFocusMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateButtonGlowMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateInputFieldFocusMaterial(const FString& FieldName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateInputFieldValidationMaterial(const FString& FieldName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateBackgroundMaterial()
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateLogoMaterial()
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }
};

/**
 * Утилиты для работы с визуальными эффектами экрана входа
 */
class FLoginScreenVisualEffectsUtils
{
public:
    /**
     * Включение/выключение всех эффектов
     * @param LoginScreenWidget - виджет экрана входа
     * @param bEnabled - включить или выключить эффекты
     */
    static void SetEffectsEnabled(UUserWidget* LoginScreenWidget, bool bEnabled)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Включаем/выключаем эффекты для всех элементов
        SetButtonEffectsEnabled(LoginScreenWidget, bEnabled);
        SetInputFieldEffectsEnabled(LoginScreenWidget, bEnabled);
        SetBackgroundEffectsEnabled(LoginScreenWidget, bEnabled);
        SetLogoEffectsEnabled(LoginScreenWidget, bEnabled);

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты %s"), bEnabled ? TEXT("включены") : TEXT("выключены"));
    }

    /**
     * Проверка соответствия эффектов эталону
     * @param LoginScreenWidget - виджет экрана входа
     * @return bool - соответствуют ли эффекты эталону
     */
    static bool ValidateEffectsCompliance(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return false;
        }

        bool bAllEffectsPresent = true;

        // Проверяем эффекты кнопок
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton && !LoginButton->HasHoverEffect())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует эффект наведения для кнопки 'Войти'"));
            bAllEffectsPresent = false;
        }

        // Проверяем эффекты полей ввода
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField && !LoginField->HasFocusEffect())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует эффект фокуса для поля логина"));
            bAllEffectsPresent = false;
        }

        if (bAllEffectsPresent)
        {
            UE_LOG(LogTemp, Log, TEXT("Все визуальные эффекты соответствуют эталону"));
        }

        return bAllEffectsPresent;
    }

private:
    static void SetButtonEffectsEnabled(UUserWidget* LoginScreenWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты кнопок
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton)
        {
            LoginButton->SetEffectsEnabled(bEnabled);
        }

        UButton* RegisterButton = LoginScreenWidget->FindWidget<UButton>(TEXT("RegisterButton"));
        if (RegisterButton)
        {
            RegisterButton->SetEffectsEnabled(bEnabled);
        }

        UButton* SettingsButton = LoginScreenWidget->FindWidget<UButton>(TEXT("SettingsButton"));
        if (SettingsButton)
        {
            SettingsButton->SetEffectsEnabled(bEnabled);
        }
    }

    static void SetInputFieldEffectsEnabled(UUserWidget* LoginScreenWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты полей ввода
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField)
        {
            LoginField->SetEffectsEnabled(bEnabled);
        }

        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        if (PasswordField)
        {
            PasswordField->SetEffectsEnabled(bEnabled);
        }
    }

    static void SetBackgroundEffectsEnabled(UUserWidget* LoginScreenWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты фона
        UImage* BackgroundImage = LoginScreenWidget->FindWidget<UImage>(TEXT("BackgroundImage"));
        if (BackgroundImage)
        {
            BackgroundImage->SetEffectsEnabled(bEnabled);
        }
    }

    static void SetLogoEffectsEnabled(UUserWidget* LoginScreenWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты логотипа
        UTextBlock* LogoText = LoginScreenWidget->FindWidget<UTextBlock>(TEXT("LogoText"));
        if (LogoText)
        {
            LogoText->SetEffectsEnabled(bEnabled);
        }
    }
};
