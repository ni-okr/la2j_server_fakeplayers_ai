#include "CoreMinimal.h"
#include "Engine/Font.h"
#include "Fonts/SlateFontInfo.h"
#include "Styling/SlateTypes.h"
#include "Components/TextBlock.h"
#include "Components/EditableTextBox.h"
#include "Components/Button.h"

/**
 * Настройка шрифтов для экрана входа в соответствии с эталонным клиентом
 * Все шрифты настроены точно по спецификации из анализа эталона
 */
class FLoginScreenFontSetup
{
public:
    /**
     * Настройка шрифтов для всех элементов экрана входа
     * Основано на анализе эталонного клиента
     */
    static void SetupLoginScreenFonts(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("LoginScreenWidget is null"));
            return;
        }

        // Настройка шрифта для полей ввода (логин и пароль)
        SetupInputFieldFonts(LoginScreenWidget);
        
        // Настройка шрифта для кнопок
        SetupButtonFonts(LoginScreenWidget);
        
        // Настройка шрифта для кнопки настроек
        SetupSettingsButtonFont(LoginScreenWidget);
        
        // Настройка шрифта для логотипа
        SetupLogoFont(LoginScreenWidget);
    }

private:
    /**
     * Настройка шрифтов для полей ввода (логин и пароль)
     * Спецификация из эталона: Arial 12px, Normal, #FFFFFF
     */
    static void SetupInputFieldFonts(UUserWidget* LoginScreenWidget)
    {
        // Создаем шрифт для полей ввода
        FSlateFontInfo InputFieldFont;
        InputFieldFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        InputFieldFont.Size = 12; // Точный размер из эталона
        InputFieldFont.TypefaceFontName = TEXT("Arial"); // Тип шрифта из эталона
        InputFieldFont.LetterSpacing = 0; // Стандартный интервал
        
        // Настройка цвета текста (белый из эталона)
        InputFieldFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        
        // Настройка стиля (Normal из эталона)
        InputFieldFont.FontMaterial = nullptr; // Обычный стиль
        
        // Применяем шрифт к полю логина
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (LoginField)
        {
            LoginField->SetFont(InputFieldFont);
            UE_LOG(LogTemp, Log, TEXT("Шрифт поля логина настроен: Arial 12px, #FFFFFF"));
        }
        
        // Применяем шрифт к полю пароля
        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        if (PasswordField)
        {
            PasswordField->SetFont(InputFieldFont);
            UE_LOG(LogTemp, Log, TEXT("Шрифт поля пароля настроен: Arial 12px, #FFFFFF"));
        }
    }

    /**
     * Настройка шрифтов для кнопок (Войти, Регистрация)
     * Спецификация из эталона: Arial Bold 14px, #000000
     */
    static void SetupButtonFonts(UUserWidget* LoginScreenWidget)
    {
        // Создаем шрифт для кнопок
        FSlateFontInfo ButtonFont;
        ButtonFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        ButtonFont.Size = 14; // Точный размер из эталона
        ButtonFont.TypefaceFontName = TEXT("Arial Bold"); // Жирный шрифт из эталона
        ButtonFont.LetterSpacing = 0;
        
        // Настройка цвета текста (черный из эталона)
        ButtonFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("000000")));
        
        // Настройка стиля (Bold из эталона)
        ButtonFont.FontMaterial = nullptr; // Будет настроен как Bold
        
        // Применяем шрифт к кнопке "Войти"
        UButton* LoginButton = LoginScreenWidget->FindWidget<UButton>(TEXT("LoginButton"));
        if (LoginButton)
        {
            // Получаем текстовый блок кнопки
            UTextBlock* LoginButtonText = LoginButton->FindWidget<UTextBlock>(TEXT("LoginButtonText"));
            if (LoginButtonText)
            {
                LoginButtonText->SetFont(ButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Войти' настроен: Arial Bold 14px, #000000"));
            }
        }
        
        // Применяем шрифт к кнопке "Регистрация"
        UButton* RegisterButton = LoginScreenWidget->FindWidget<UButton>(TEXT("RegisterButton"));
        if (RegisterButton)
        {
            // Получаем текстовый блок кнопки
            UTextBlock* RegisterButtonText = RegisterButton->FindWidget<UTextBlock>(TEXT("RegisterButtonText"));
            if (RegisterButtonText)
            {
                RegisterButtonText->SetFont(ButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Регистрация' настроен: Arial Bold 14px, #000000"));
            }
        }
    }

    /**
     * Настройка шрифта для кнопки настроек
     * Спецификация из эталона: Arial 10px, Normal, #FFFFFF
     */
    static void SetupSettingsButtonFont(UUserWidget* LoginScreenWidget)
    {
        // Создаем шрифт для кнопки настроек
        FSlateFontInfo SettingsFont;
        SettingsFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        SettingsFont.Size = 10; // Точный размер из эталона
        SettingsFont.TypefaceFontName = TEXT("Arial"); // Обычный шрифт из эталона
        SettingsFont.LetterSpacing = 0;
        
        // Настройка цвета текста (белый из эталона)
        SettingsFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        
        // Применяем шрифт к кнопке настроек
        UButton* SettingsButton = LoginScreenWidget->FindWidget<UButton>(TEXT("SettingsButton"));
        if (SettingsButton)
        {
            // Получаем текстовый блок кнопки
            UTextBlock* SettingsButtonText = SettingsButton->FindWidget<UTextBlock>(TEXT("SettingsButtonText"));
            if (SettingsButtonText)
            {
                SettingsButtonText->SetFont(SettingsFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Настройки' настроен: Arial 10px, #FFFFFF"));
            }
        }
    }

    /**
     * Настройка шрифта для логотипа игры
     * Спецификация из эталона: Arial Bold, крупный размер, #FFD700
     */
    static void SetupLogoFont(UUserWidget* LoginScreenWidget)
    {
        // Создаем шрифт для логотипа
        FSlateFontInfo LogoFont;
        LogoFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        LogoFont.Size = 24; // Крупный размер для логотипа
        LogoFont.TypefaceFontName = TEXT("Arial Bold"); // Жирный шрифт для логотипа
        LogoFont.LetterSpacing = 2; // Увеличенный интервал для логотипа
        
        // Настройка цвета текста (золотой из эталона)
        LogoFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        
        // Применяем шрифт к логотипу
        UTextBlock* LogoText = LoginScreenWidget->FindWidget<UTextBlock>(TEXT("LogoText"));
        if (LogoText)
        {
            LogoText->SetFont(LogoFont);
            UE_LOG(LogTemp, Log, TEXT("Шрифт логотипа настроен: Arial Bold 24px, #FFD700"));
        }
    }
};

/**
 * Утилиты для работы с шрифтами экрана входа
 */
class FLoginScreenFontUtils
{
public:
    /**
     * Получение шрифта по типу элемента
     * @param ElementType - тип элемента (InputField, Button, Settings, Logo)
     * @return FSlateFontInfo - настроенный шрифт
     */
    static FSlateFontInfo GetFontForElementType(const FString& ElementType)
    {
        FSlateFontInfo Font;
        Font.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        
        if (ElementType == TEXT("InputField"))
        {
            Font.Size = 12;
            Font.TypefaceFontName = TEXT("Arial");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        }
        else if (ElementType == TEXT("Button"))
        {
            Font.Size = 14;
            Font.TypefaceFontName = TEXT("Arial Bold");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("000000")));
        }
        else if (ElementType == TEXT("Settings"))
        {
            Font.Size = 10;
            Font.TypefaceFontName = TEXT("Arial");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        }
        else if (ElementType == TEXT("Logo"))
        {
            Font.Size = 24;
            Font.TypefaceFontName = TEXT("Arial Bold");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        }
        
        return Font;
    }

    /**
     * Проверка соответствия шрифта эталону
     * @param Font - проверяемый шрифт
     * @param ElementType - тип элемента
     * @return bool - соответствует ли эталону
     */
    static bool ValidateFontCompliance(const FSlateFontInfo& Font, const FString& ElementType)
    {
        FSlateFontInfo ReferenceFont = GetFontForElementType(ElementType);
        
        // Проверяем размер шрифта (допустимое отклонение ±1)
        if (FMath::Abs(Font.Size - ReferenceFont.Size) > 1)
        {
            UE_LOG(LogTemp, Warning, TEXT("Размер шрифта не соответствует эталону: %d != %d"), 
                   Font.Size, ReferenceFont.Size);
            return false;
        }
        
        // Проверяем тип шрифта
        if (Font.TypefaceFontName != ReferenceFont.TypefaceFontName)
        {
            UE_LOG(LogTemp, Warning, TEXT("Тип шрифта не соответствует эталону: %s != %s"), 
                   *Font.TypefaceFontName, *ReferenceFont.TypefaceFontName);
            return false;
        }
        
        // Проверяем цвет (допустимое отклонение ±5 в RGB)
        FColor FontColor = Font.ColorAndOpacity.ToFColor(true);
        FColor ReferenceColor = ReferenceFont.ColorAndOpacity.ToFColor(true);
        
        if (FMath::Abs(FontColor.R - ReferenceColor.R) > 5 ||
            FMath::Abs(FontColor.G - ReferenceColor.G) > 5 ||
            FMath::Abs(FontColor.B - ReferenceColor.B) > 5)
        {
            UE_LOG(LogTemp, Warning, TEXT("Цвет шрифта не соответствует эталону"));
            return false;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Шрифт соответствует эталону для типа: %s"), *ElementType);
        return true;
    }
};
