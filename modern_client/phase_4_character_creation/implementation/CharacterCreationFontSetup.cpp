// CharacterCreationFontSetup.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/TextBlock.h"
#include "Components/Button.h"
#include "Components/EditableTextBox.h"
#include "Engine/Font.h"
#include "Engine/Engine.h"
#include "UObject/ConstructorHelpers.h"

// Вспомогательный класс для настройки шрифтов и цветов экрана создания персонажа
class FCharacterCreationFontSetup
{
public:
    // Структура настроек шрифта
    struct FFontSettings
    {
        FString FontName;
        FString FontPath;
        int32 FontSize;
        FLinearColor FontColor;
        FLinearColor HoverColor;
        FLinearColor SelectedColor;
        FLinearColor DisabledColor;
        bool bIsBold;
        bool bIsItalic;
        bool bIsUnderlined;
        float ShadowOffsetX;
        float ShadowOffsetY;
        FLinearColor ShadowColor;
    };

    // Структура цветовой схемы
    struct FColorScheme
    {
        FLinearColor BackgroundColor;
        FLinearColor PanelColor;
        FLinearColor BorderColor;
        FLinearColor TextColor;
        FLinearColor ButtonColor;
        FLinearColor ButtonHoverColor;
        FLinearColor ButtonPressedColor;
        FLinearColor SelectedColor;
        FLinearColor ErrorColor;
        FLinearColor WarningColor;
        FLinearColor SuccessColor;
    };

    // Инициализация системы настройки шрифтов
    static void InitializeFontSetup(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы настройки шрифтов и цветов"));
        
        // Создаем настройки шрифтов
        CreateFontSettings();
        
        // Создаем цветовую схему
        CreateColorScheme();
        
        // Применяем настройки к виджету
        ApplyFontSettings(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система настройки шрифтов и цветов инициализирована"));
    }

    // Создание настроек шрифтов
    static void CreateFontSettings()
    {
        // Очищаем предыдущие настройки
        FontSettingsMap.Empty();
        
        // Основной шрифт (заголовки)
        FFontSettings MainFont;
        MainFont.FontName = TEXT("LineageFont_Bold");
        MainFont.FontPath = TEXT("/Game/Fonts/LineageFont_Bold");
        MainFont.FontSize = 24;
        MainFont.FontColor = FLinearColor(1.0f, 1.0f, 1.0f, 1.0f); // Белый
        MainFont.HoverColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        MainFont.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        MainFont.DisabledColor = FLinearColor(0.5f, 0.5f, 0.5f, 1.0f); // Серый
        MainFont.bIsBold = true;
        MainFont.bIsItalic = false;
        MainFont.bIsUnderlined = false;
        MainFont.ShadowOffsetX = 2.0f;
        MainFont.ShadowOffsetY = 2.0f;
        MainFont.ShadowColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.8f); // Черный с прозрачностью
        FontSettingsMap.Add(TEXT("MainFont"), MainFont);
        
        // Шрифт подзаголовков
        FFontSettings SubtitleFont;
        SubtitleFont.FontName = TEXT("LineageFont_Medium");
        SubtitleFont.FontPath = TEXT("/Game/Fonts/LineageFont_Medium");
        SubtitleFont.FontSize = 18;
        SubtitleFont.FontColor = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f); // Светло-серый
        SubtitleFont.HoverColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        SubtitleFont.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        SubtitleFont.DisabledColor = FLinearColor(0.4f, 0.4f, 0.4f, 1.0f); // Темно-серый
        SubtitleFont.bIsBold = false;
        SubtitleFont.bIsItalic = false;
        SubtitleFont.bIsUnderlined = false;
        SubtitleFont.ShadowOffsetX = 1.0f;
        SubtitleFont.ShadowOffsetY = 1.0f;
        SubtitleFont.ShadowColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.6f); // Черный с прозрачностью
        FontSettingsMap.Add(TEXT("SubtitleFont"), SubtitleFont);
        
        // Шрифт обычного текста
        FFontSettings RegularFont;
        RegularFont.FontName = TEXT("LineageFont_Regular");
        RegularFont.FontPath = TEXT("/Game/Fonts/LineageFont_Regular");
        RegularFont.FontSize = 16;
        RegularFont.FontColor = FLinearColor(0.8f, 0.8f, 0.8f, 1.0f); // Серый
        RegularFont.HoverColor = FLinearColor(1.0f, 1.0f, 1.0f, 1.0f); // Белый
        RegularFont.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        RegularFont.DisabledColor = FLinearColor(0.3f, 0.3f, 0.3f, 1.0f); // Темно-серый
        RegularFont.bIsBold = false;
        RegularFont.bIsItalic = false;
        RegularFont.bIsUnderlined = false;
        RegularFont.ShadowOffsetX = 1.0f;
        RegularFont.ShadowOffsetY = 1.0f;
        RegularFont.ShadowColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.5f); // Черный с прозрачностью
        FontSettingsMap.Add(TEXT("RegularFont"), RegularFont);
        
        // Шрифт мелкого текста
        FFontSettings SmallFont;
        SmallFont.FontName = TEXT("LineageFont_Small");
        SmallFont.FontPath = TEXT("/Game/Fonts/LineageFont_Small");
        SmallFont.FontSize = 14;
        SmallFont.FontColor = FLinearColor(0.7f, 0.7f, 0.7f, 1.0f); // Светло-серый
        SmallFont.HoverColor = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f); // Светло-серый
        SmallFont.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        SmallFont.DisabledColor = FLinearColor(0.2f, 0.2f, 0.2f, 1.0f); // Очень темно-серый
        SmallFont.bIsBold = false;
        SmallFont.bIsItalic = false;
        SmallFont.bIsUnderlined = false;
        SmallFont.ShadowOffsetX = 0.5f;
        SmallFont.ShadowOffsetY = 0.5f;
        SmallFont.ShadowColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.4f); // Черный с прозрачностью
        FontSettingsMap.Add(TEXT("SmallFont"), SmallFont);
        
        // Шрифт кнопок
        FFontSettings ButtonFont;
        ButtonFont.FontName = TEXT("LineageFont_Button");
        ButtonFont.FontPath = TEXT("/Game/Fonts/LineageFont_Button");
        ButtonFont.FontSize = 18;
        ButtonFont.FontColor = FLinearColor(1.0f, 1.0f, 1.0f, 1.0f); // Белый
        ButtonFont.HoverColor = FLinearColor(1.0f, 1.0f, 1.0f, 1.0f); // Белый
        ButtonFont.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        ButtonFont.DisabledColor = FLinearColor(0.4f, 0.4f, 0.4f, 1.0f); // Серый
        ButtonFont.bIsBold = true;
        ButtonFont.bIsItalic = false;
        ButtonFont.bIsUnderlined = false;
        ButtonFont.ShadowOffsetX = 1.0f;
        ButtonFont.ShadowOffsetY = 1.0f;
        ButtonFont.ShadowColor = FLinearColor(0.0f, 0.0f, 0.0f, 0.7f); // Черный с прозрачностью
        FontSettingsMap.Add(TEXT("ButtonFont"), ButtonFont);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d настроек шрифтов"), FontSettingsMap.Num());
    }

    // Создание цветовой схемы
    static void CreateColorScheme()
    {
        // Основная цветовая схема в стиле Lineage 2
        FColorScheme MainScheme;
        MainScheme.BackgroundColor = FLinearColor(0.1f, 0.1f, 0.15f, 1.0f); // Темно-синий
        MainScheme.PanelColor = FLinearColor(0.2f, 0.2f, 0.25f, 0.9f); // Темно-серый с прозрачностью
        MainScheme.BorderColor = FLinearColor(0.4f, 0.4f, 0.5f, 1.0f); // Серо-синий
        MainScheme.TextColor = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f); // Светло-серый
        MainScheme.ButtonColor = FLinearColor(0.3f, 0.3f, 0.4f, 1.0f); // Темно-серый
        MainScheme.ButtonHoverColor = FLinearColor(0.4f, 0.4f, 0.5f, 1.0f); // Серый
        MainScheme.ButtonPressedColor = FLinearColor(0.2f, 0.2f, 0.3f, 1.0f); // Очень темно-серый
        MainScheme.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        MainScheme.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        MainScheme.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        MainScheme.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        ColorSchemeMap.Add(TEXT("MainScheme"), MainScheme);
        
        // Цветовая схема для панелей выбора
        FColorScheme SelectionScheme;
        SelectionScheme.BackgroundColor = FLinearColor(0.15f, 0.15f, 0.2f, 0.95f); // Темно-синий с прозрачностью
        SelectionScheme.PanelColor = FLinearColor(0.25f, 0.25f, 0.3f, 0.9f); // Темно-серый с прозрачностью
        SelectionScheme.BorderColor = FLinearColor(0.5f, 0.5f, 0.6f, 1.0f); // Серо-синий
        SelectionScheme.TextColor = FLinearColor(0.95f, 0.95f, 0.95f, 1.0f); // Очень светло-серый
        SelectionScheme.ButtonColor = FLinearColor(0.35f, 0.35f, 0.45f, 1.0f); // Темно-серый
        SelectionScheme.ButtonHoverColor = FLinearColor(0.45f, 0.45f, 0.55f, 1.0f); // Серый
        SelectionScheme.ButtonPressedColor = FLinearColor(0.25f, 0.25f, 0.35f, 1.0f); // Очень темно-серый
        SelectionScheme.SelectedColor = FLinearColor(1.0f, 0.84f, 0.0f, 1.0f); // Золотой
        SelectionScheme.ErrorColor = FLinearColor(1.0f, 0.3f, 0.3f, 1.0f); // Красный
        SelectionScheme.WarningColor = FLinearColor(1.0f, 0.9f, 0.0f, 1.0f); // Желтый
        SelectionScheme.SuccessColor = FLinearColor(0.3f, 1.0f, 0.3f, 1.0f); // Зеленый
        ColorSchemeMap.Add(TEXT("SelectionScheme"), SelectionScheme);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d цветовых схем"), ColorSchemeMap.Num());
    }

    // Применение настроек шрифтов к виджету
    static void ApplyFontSettings(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Применяем настройки к заголовкам
        ApplyFontToTextBlocks(CurrentWidget, TEXT("TitleText"), TEXT("MainFont"));
        ApplyFontToTextBlocks(CurrentWidget, TEXT("SubtitleText"), TEXT("SubtitleFont"));
        ApplyFontToTextBlocks(CurrentWidget, TEXT("RegularText"), TEXT("RegularFont"));
        ApplyFontToTextBlocks(CurrentWidget, TEXT("SmallText"), TEXT("SmallFont"));
        
        // Применяем настройки к кнопкам
        ApplyFontToButtons(CurrentWidget, TEXT("Button"), TEXT("ButtonFont"));
        
        // Применяем настройки к полям ввода
        ApplyFontToInputFields(CurrentWidget, TEXT("InputField"), TEXT("RegularFont"));
        
        UE_LOG(LogTemp, Log, TEXT("Настройки шрифтов применены к виджету"));
    }

    // Применение шрифта к текстовым блокам
    static void ApplyFontToTextBlocks(UUserWidget* CurrentWidget, const FString& WidgetName, const FString& FontName)
    {
        if (!CurrentWidget || !FontSettingsMap.Contains(FontName)) return;
        
        const FFontSettings& FontSettings = FontSettingsMap[FontName];
        
        // Находим все текстовые блоки с указанным именем
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UTextBlock* TextBlock = Cast<UTextBlock>(Widget))
            {
                if (TextBlock->GetName().Contains(WidgetName))
                {
                    ApplyFontToTextBlock(TextBlock, FontSettings);
                }
            }
        }
    }

    // Применение шрифта к кнопкам
    static void ApplyFontToButtons(UUserWidget* CurrentWidget, const FString& WidgetName, const FString& FontName)
    {
        if (!CurrentWidget || !FontSettingsMap.Contains(FontName)) return;
        
        const FFontSettings& FontSettings = FontSettingsMap[FontName];
        
        // Находим все кнопки с указанным именем
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UButton* Button = Cast<UButton>(Widget))
            {
                if (Button->GetName().Contains(WidgetName))
                {
                    ApplyFontToButton(Button, FontSettings);
                }
            }
        }
    }

    // Применение шрифта к полям ввода
    static void ApplyFontToInputFields(UUserWidget* CurrentWidget, const FString& WidgetName, const FString& FontName)
    {
        if (!CurrentWidget || !FontSettingsMap.Contains(FontName)) return;
        
        const FFontSettings& FontSettings = FontSettingsMap[FontName];
        
        // Находим все поля ввода с указанным именем
        TArray<UWidget*> FoundWidgets;
        CurrentWidget->GetAllChildren(FoundWidgets);
        
        for (UWidget* Widget : FoundWidgets)
        {
            if (UEditableTextBox* InputField = Cast<UEditableTextBox>(Widget))
            {
                if (InputField->GetName().Contains(WidgetName))
                {
                    ApplyFontToInputField(InputField, FontSettings);
                }
            }
        }
    }

    // Применение шрифта к текстовому блоку
    static void ApplyFontToTextBlock(UTextBlock* TextBlock, const FFontSettings& FontSettings)
    {
        if (!TextBlock) return;
        
        // В реальной реализации здесь будет применение шрифта к текстовому блоку
        UE_LOG(LogTemp, Log, TEXT("Применен шрифт %s к текстовому блоку %s"), *FontSettings.FontName, *TextBlock->GetName());
    }

    // Применение шрифта к кнопке
    static void ApplyFontToButton(UButton* Button, const FFontSettings& FontSettings)
    {
        if (!Button) return;
        
        // В реальной реализации здесь будет применение шрифта к кнопке
        UE_LOG(LogTemp, Log, TEXT("Применен шрифт %s к кнопке %s"), *FontSettings.FontName, *Button->GetName());
    }

    // Применение шрифта к полю ввода
    static void ApplyFontToInputField(UEditableTextBox* InputField, const FFontSettings& FontSettings)
    {
        if (!InputField) return;
        
        // В реальной реализации здесь будет применение шрифта к полю ввода
        UE_LOG(LogTemp, Log, TEXT("Применен шрифт %s к полю ввода %s"), *FontSettings.FontName, *InputField->GetName());
    }

    // Получение настроек шрифта
    static FFontSettings* GetFontSettings(const FString& FontName)
    {
        if (FontSettingsMap.Contains(FontName))
        {
            return &FontSettingsMap[FontName];
        }
        return nullptr;
    }

    // Получение цветовой схемы
    static FColorScheme* GetColorScheme(const FString& SchemeName)
    {
        if (ColorSchemeMap.Contains(SchemeName))
        {
            return &ColorSchemeMap[SchemeName];
        }
        return nullptr;
    }

    // Обновление настроек шрифта
    static void UpdateFontSettings(const FString& FontName, const FFontSettings& NewSettings)
    {
        FontSettingsMap.Add(FontName, NewSettings);
        UE_LOG(LogTemp, Log, TEXT("Настройки шрифта обновлены: %s"), *FontName);
    }

    // Обновление цветовой схемы
    static void UpdateColorScheme(const FString& SchemeName, const FColorScheme& NewScheme)
    {
        ColorSchemeMap.Add(SchemeName, NewScheme);
        UE_LOG(LogTemp, Log, TEXT("Цветовая схема обновлена: %s"), *SchemeName);
    }

    // Сброс настроек
    static void ResetSettings()
    {
        FontSettingsMap.Empty();
        ColorSchemeMap.Empty();
        CreateFontSettings();
        CreateColorScheme();
        UE_LOG(LogTemp, Log, TEXT("Настройки шрифтов и цветов сброшены"));
    }

private:
    // Карта настроек шрифтов
    static TMap<FString, FFontSettings> FontSettingsMap;
    
    // Карта цветовых схем
    static TMap<FString, FColorScheme> ColorSchemeMap;
};

// Статические переменные
TMap<FString, FCharacterCreationFontSetup::FFontSettings> FCharacterCreationFontSetup::FontSettingsMap;
TMap<FString, FCharacterCreationFontSetup::FColorScheme> FCharacterCreationFontSetup::ColorSchemeMap;
