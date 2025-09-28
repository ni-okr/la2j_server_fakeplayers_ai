#include "CoreMinimal.h"
#include "Engine/Font.h"
#include "Fonts/SlateFontInfo.h"
#include "Styling/SlateTypes.h"
#include "Components/TextBlock.h"
#include "Components/Button.h"
#include "Components/ScrollBox.h"

/**
 * Настройка шрифтов для экрана выбора персонажей в соответствии с эталонным клиентом
 * Все шрифты настроены точно по спецификации из анализа эталона
 */
class FCharacterSelectionFontSetup
{
public:
    /**
     * Настройка шрифтов для всех элементов экрана выбора персонажей
     * Основано на анализе эталонного клиента
     */
    static void SetupCharacterSelectionFonts(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("CharacterSelectionWidget is null"));
            return;
        }

        // Настройка шрифтов для слотов персонажей
        SetupCharacterSlotFonts(CharacterSelectionWidget);
        
        // Настройка шрифтов для кнопок
        SetupButtonFonts(CharacterSelectionWidget);
        
        // Настройка шрифтов для заголовков
        SetupHeaderFonts(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Шрифты экрана выбора персонажей настроены"));
    }

private:
    /**
     * Настройка шрифтов для слотов персонажей
     * Спецификация из эталона: различные размеры и стили для разных элементов
     */
    static void SetupCharacterSlotFonts(UUserWidget* CharacterSelectionWidget)
    {
        // Шрифт для имени персонажа (Arial Bold 16px, #FFFFFF)
        FSlateFontInfo CharacterNameFont;
        CharacterNameFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        CharacterNameFont.Size = 16;
        CharacterNameFont.TypefaceFontName = TEXT("Arial Bold");
        CharacterNameFont.LetterSpacing = 0;
        CharacterNameFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        
        // Шрифт для уровня персонажа (Arial 12px, #FFD700)
        FSlateFontInfo CharacterLevelFont;
        CharacterLevelFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        CharacterLevelFont.Size = 12;
        CharacterLevelFont.TypefaceFontName = TEXT("Arial");
        CharacterLevelFont.LetterSpacing = 0;
        CharacterLevelFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        
        // Шрифт для класса персонажа (Arial 12px, #C0C0C0)
        FSlateFontInfo CharacterClassFont;
        CharacterClassFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        CharacterClassFont.Size = 12;
        CharacterClassFont.TypefaceFontName = TEXT("Arial");
        CharacterClassFont.LetterSpacing = 0;
        CharacterClassFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("C0C0C0")));
        
        // Шрифт для локации персонажа (Arial 10px, #808080)
        FSlateFontInfo CharacterLocationFont;
        CharacterLocationFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        CharacterLocationFont.Size = 10;
        CharacterLocationFont.TypefaceFontName = TEXT("Arial");
        CharacterLocationFont.LetterSpacing = 0;
        CharacterLocationFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("808080")));
        
        // Применяем шрифты к элементам слотов персонажей
        ApplyCharacterSlotFonts(CharacterSelectionWidget, CharacterNameFont, CharacterLevelFont, CharacterClassFont, CharacterLocationFont);
    }

    /**
     * Настройка шрифтов для кнопок
     * Спецификация из эталона: Arial Bold 14px, #FFFFFF
     */
    static void SetupButtonFonts(UUserWidget* CharacterSelectionWidget)
    {
        // Создаем шрифт для кнопок
        FSlateFontInfo ButtonFont;
        ButtonFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        ButtonFont.Size = 14;
        ButtonFont.TypefaceFontName = TEXT("Arial Bold");
        ButtonFont.LetterSpacing = 0;
        ButtonFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        
        // Применяем шрифт к кнопке "Создать персонажа"
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton)
        {
            UTextBlock* CreateButtonText = CreateCharacterButton->FindWidget<UTextBlock>(TEXT("CreateButtonText"));
            if (CreateButtonText)
            {
                CreateButtonText->SetFont(ButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Создать персонажа' настроен: Arial Bold 14px, #FFFFFF"));
            }
        }
        
        // Применяем шрифт к кнопке "Удалить персонажа"
        UButton* DeleteCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("DeleteCharacterButton"));
        if (DeleteCharacterButton)
        {
            UTextBlock* DeleteButtonText = DeleteCharacterButton->FindWidget<UTextBlock>(TEXT("DeleteButtonText"));
            if (DeleteButtonText)
            {
                DeleteButtonText->SetFont(ButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Удалить персонажа' настроен: Arial Bold 14px, #FFFFFF"));
            }
        }
        
        // Применяем шрифт к кнопке "Войти в игру"
        UButton* EnterGameButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("EnterGameButton"));
        if (EnterGameButton)
        {
            UTextBlock* EnterButtonText = EnterGameButton->FindWidget<UTextBlock>(TEXT("EnterButtonText"));
            if (EnterButtonText)
            {
                EnterButtonText->SetFont(ButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Войти в игру' настроен: Arial Bold 14px, #FFFFFF"));
            }
        }
        
        // Применяем шрифт к кнопке "Назад"
        UButton* BackButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("BackButton"));
        if (BackButton)
        {
            UTextBlock* BackButtonText = BackButton->FindWidget<UTextBlock>(TEXT("BackButtonText"));
            if (BackButtonText)
            {
                // Кнопка "Назад" имеет меньший размер шрифта
                FSlateFontInfo BackButtonFont = ButtonFont;
                BackButtonFont.Size = 12;
                BackButtonText->SetFont(BackButtonFont);
                UE_LOG(LogTemp, Log, TEXT("Шрифт кнопки 'Назад' настроен: Arial Bold 12px, #FFFFFF"));
            }
        }
    }

    /**
     * Настройка шрифтов для заголовков
     * Спецификация из эталона: Arial Bold 18px, #FFD700
     */
    static void SetupHeaderFonts(UUserWidget* CharacterSelectionWidget)
    {
        // Создаем шрифт для заголовков
        FSlateFontInfo HeaderFont;
        HeaderFont.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        HeaderFont.Size = 18;
        HeaderFont.TypefaceFontName = TEXT("Arial Bold");
        HeaderFont.LetterSpacing = 1;
        HeaderFont.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        
        // Применяем шрифт к заголовку "Выбор персонажа"
        UTextBlock* HeaderText = CharacterSelectionWidget->FindWidget<UTextBlock>(TEXT("HeaderText"));
        if (HeaderText)
        {
            HeaderText->SetFont(HeaderFont);
            UE_LOG(LogTemp, Log, TEXT("Шрифт заголовка настроен: Arial Bold 18px, #FFD700"));
        }
    }

    /**
     * Применение шрифтов к элементам слотов персонажей
     */
    static void ApplyCharacterSlotFonts(UUserWidget* CharacterSelectionWidget, 
                                       const FSlateFontInfo& NameFont,
                                       const FSlateFontInfo& LevelFont,
                                       const FSlateFontInfo& ClassFont,
                                       const FSlateFontInfo& LocationFont)
    {
        // Получаем панель списка персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (!CharacterListPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("CharacterListPanel не найден"));
            return;
        }

        // Применяем шрифты к каждому слоту персонажа
        for (int32 i = 0; i < CharacterListPanel->GetChildrenCount(); i++)
        {
            UUserWidget* CharacterSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(i));
            if (CharacterSlot)
            {
                // Имя персонажа
                UTextBlock* CharacterName = CharacterSlot->FindWidget<UTextBlock>(TEXT("CharacterName"));
                if (CharacterName)
                {
                    CharacterName->SetFont(NameFont);
                }
                
                // Уровень персонажа
                UTextBlock* CharacterLevel = CharacterSlot->FindWidget<UTextBlock>(TEXT("CharacterLevel"));
                if (CharacterLevel)
                {
                    CharacterLevel->SetFont(LevelFont);
                }
                
                // Класс персонажа
                UTextBlock* CharacterClass = CharacterSlot->FindWidget<UTextBlock>(TEXT("CharacterClass"));
                if (CharacterClass)
                {
                    CharacterClass->SetFont(ClassFont);
                }
                
                // Локация персонажа
                UTextBlock* CharacterLocation = CharacterSlot->FindWidget<UTextBlock>(TEXT("CharacterLocation"));
                if (CharacterLocation)
                {
                    CharacterLocation->SetFont(LocationFont);
                }
            }
        }
    }
};

/**
 * Утилиты для работы с шрифтами экрана выбора персонажей
 */
class FCharacterSelectionFontUtils
{
public:
    /**
     * Получение шрифта по типу элемента
     * @param ElementType - тип элемента (CharacterName, CharacterLevel, CharacterClass, CharacterLocation, Button, Header)
     * @return FSlateFontInfo - настроенный шрифт
     */
    static FSlateFontInfo GetFontForElementType(const FString& ElementType)
    {
        FSlateFontInfo Font;
        Font.FontObject = LoadObject<UFont>(nullptr, TEXT("/Engine/EngineFonts/Roboto"));
        
        if (ElementType == TEXT("CharacterName"))
        {
            Font.Size = 16;
            Font.TypefaceFontName = TEXT("Arial Bold");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        }
        else if (ElementType == TEXT("CharacterLevel"))
        {
            Font.Size = 12;
            Font.TypefaceFontName = TEXT("Arial");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        }
        else if (ElementType == TEXT("CharacterClass"))
        {
            Font.Size = 12;
            Font.TypefaceFontName = TEXT("Arial");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("C0C0C0")));
        }
        else if (ElementType == TEXT("CharacterLocation"))
        {
            Font.Size = 10;
            Font.TypefaceFontName = TEXT("Arial");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("808080")));
        }
        else if (ElementType == TEXT("Button"))
        {
            Font.Size = 14;
            Font.TypefaceFontName = TEXT("Arial Bold");
            Font.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFFFFF")));
        }
        else if (ElementType == TEXT("Header"))
        {
            Font.Size = 18;
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
