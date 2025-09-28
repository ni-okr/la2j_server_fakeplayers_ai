#include "CharacterCreationScreen.h"
#include "Components/ScrollBox.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/EditableTextBox.h"
#include "Components/Image.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"
#include "RaceSelectionSystem.cpp"
#include "GenderSelectionSystem.cpp"
#include "ClassSelectionSystem.cpp"
#include "CharacterCustomizationSystem.cpp"
#include "CharacterValidationSystem.cpp"
#include "CharacterCreationFontSetup.cpp"
#include "CharacterCreationAnimationSystem.cpp"
#include "CharacterCreationVisualEffects.cpp"
#include "CharacterCreationRealtimeValidation.cpp"
#include "CharacterCreationPixelComparison.cpp"
#include "CharacterCreationPerformanceOptimization.cpp"
#include "CharacterCreationAccessibilitySecurity.cpp"

UCharacterCreationScreen::UCharacterCreationScreen(const FObjectInitializer& ObjectInitializer)
    : Super(ObjectInitializer)
{
    // Инициализация переменных
    SelectedRace = TEXT("");
    SelectedGender = TEXT("");
    SelectedClass = TEXT("");
    CharacterName = TEXT("");
}

void UCharacterCreationScreen::NativeConstruct()
{
    Super::NativeConstruct();

    // Инициализация экрана создания персонажа
    InitializeCharacterCreation();

    // Привязка событий к кнопкам
    if (CreateCharacterButton)
    {
        CreateCharacterButton->OnClicked.AddDynamic(this, &UCharacterCreationScreen::OnCreateCharacterButtonClicked);
    }

    if (CancelButton)
    {
        CancelButton->OnClicked.AddDynamic(this, &UCharacterCreationScreen::OnCancelButtonClicked);
    }

    if (NameInputField)
    {
        NameInputField->OnTextChanged.AddDynamic(this, &UCharacterCreationScreen::OnNameInputChanged);
    }

    UE_LOG(LogTemp, Log, TEXT("Экран создания персонажа инициализирован"));
}

void UCharacterCreationScreen::InitializeCharacterCreation()
{
    // Инициализация систем выбора
    FRaceSelectionSystem::InitializeRaceSelection(this);
    FGenderSelectionSystem::InitializeGenderSelection(this);
    FClassSelectionSystem::InitializeClassSelection(this);
    FCharacterCustomizationSystem::InitializeCustomization(this);
    FCharacterValidationSystem::InitializeValidation(this);

    // Инициализация систем точной настройки
    FCharacterCreationFontSetup::InitializeFontSetup(this);
    FCharacterCreationAnimationSystem::InitializeAnimationSystem(this);
    FCharacterCreationVisualEffects::InitializeVisualEffects(this);
    FCharacterCreationRealtimeValidation::InitializeRealtimeValidation(this);

    // Инициализация систем финального тестирования
    FCharacterCreationPixelComparison::InitializePixelComparison(this);
    FCharacterCreationPerformanceOptimization::InitializePerformanceOptimization(this);
    FCharacterCreationAccessibilitySecurity::InitializeAccessibilitySecurity(this);

    // Настройка панелей выбора
    SetupRaceSelection();
    SetupGenderSelection();
    SetupClassSelection();
    SetupCustomization();
    SetupCharacterPreview();

    UE_LOG(LogTemp, Log, TEXT("Инициализация экрана создания персонажа завершена"));
}

void UCharacterCreationScreen::SetupRaceSelection()
{
    if (!RaceSelectionPanel) return;

    // В реальной реализации здесь будет создание кнопок выбора рас
    UE_LOG(LogTemp, Log, TEXT("Настройка выбора расы"));
}

void UCharacterCreationScreen::SetupGenderSelection()
{
    if (!GenderSelectionPanel) return;

    // В реальной реализации здесь будет создание кнопок выбора пола
    UE_LOG(LogTemp, Log, TEXT("Настройка выбора пола"));
}

void UCharacterCreationScreen::SetupClassSelection()
{
    if (!ClassSelectionPanel) return;

    // В реальной реализации здесь будет создание кнопок выбора класса
    UE_LOG(LogTemp, Log, TEXT("Настройка выбора класса"));
}

void UCharacterCreationScreen::SetupCustomization()
{
    if (!CustomizationPanel) return;

    // В реальной реализации здесь будет создание элементов кастомизации
    UE_LOG(LogTemp, Log, TEXT("Настройка кастомизации персонажа"));
}

void UCharacterCreationScreen::SetupCharacterPreview()
{
    if (!CharacterPreviewImage) return;

    // В реальной реализации здесь будет настройка 3D превью
    UE_LOG(LogTemp, Log, TEXT("Настройка предварительного просмотра персонажа"));
}

void UCharacterCreationScreen::OnCreateCharacterButtonClicked()
{
    UE_LOG(LogTemp, Log, TEXT("Нажата кнопка создания персонажа"));
    
    if (ValidateCharacterData())
    {
        CreateCharacter();
    }
    else
    {
        ShowErrorMessage(TEXT("Пожалуйста, заполните все обязательные поля"));
    }
}

void UCharacterCreationScreen::OnCancelButtonClicked()
{
    UE_LOG(LogTemp, Log, TEXT("Нажата кнопка отмены создания персонажа"));
    CancelCharacterCreation();
}

void UCharacterCreationScreen::OnNameInputChanged(const FText& Text)
{
    CharacterName = Text.ToString();
    UE_LOG(LogTemp, Log, TEXT("Имя персонажа изменено: %s"), *CharacterName);
}

void UCharacterCreationScreen::OnRaceSelected(FString RaceName)
{
    SelectedRace = RaceName;
    UE_LOG(LogTemp, Log, TEXT("Выбрана раса: %s"), *RaceName);
    
    // Используем систему выбора расы
    FRaceSelectionSystem::OnRaceSelected(RaceName);
    
    // Обновляем доступные классы и кастомизацию
    UpdateAvailableClasses();
    UpdateCustomizationOptions();
    UpdateCharacterPreview();
}

void UCharacterCreationScreen::OnGenderSelected(FString GenderName)
{
    SelectedGender = GenderName;
    UE_LOG(LogTemp, Log, TEXT("Выбран пол: %s"), *GenderName);
    
    // Используем систему выбора пола
    FGenderSelectionSystem::OnGenderSelected(GenderName);
    
    // Обновляем кастомизацию
    UpdateCustomizationOptions();
    UpdateCharacterPreview();
}

void UCharacterCreationScreen::OnClassSelected(FString ClassName)
{
    SelectedClass = ClassName;
    UE_LOG(LogTemp, Log, TEXT("Выбран класс: %s"), *ClassName);
    
    // Используем систему выбора класса
    FClassSelectionSystem::OnClassSelected(ClassName);
    
    // Обновляем кастомизацию
    UpdateCustomizationOptions();
    UpdateCharacterPreview();
}

void UCharacterCreationScreen::OnCustomizationChanged(FString OptionName, FString Value)
{
    CustomizationOptions.Add(OptionName, Value);
    UE_LOG(LogTemp, Log, TEXT("Изменена кастомизация %s: %s"), *OptionName, *Value);
    
    // Используем систему кастомизации
    FCharacterCustomizationSystem::OnCustomizationChanged(OptionName, Value);
    
    UpdateCharacterPreview();
}

void UCharacterCreationScreen::UpdateCharacterPreview()
{
    if (!CharacterPreviewImage) return;

    // В реальной реализации здесь будет обновление 3D модели персонажа
    UE_LOG(LogTemp, Log, TEXT("Обновлен предварительный просмотр персонажа"));
}

bool UCharacterCreationScreen::ValidateCharacterData()
{
    // Используем систему валидации
    FCharacterValidationSystem::FValidationResult ValidationResult = 
        FCharacterValidationSystem::ValidateCharacter(CharacterName, SelectedRace, SelectedGender, SelectedClass);
    
    if (!ValidationResult.bIsValid)
    {
        ShowErrorMessage(ValidationResult.ErrorMessage);
        return false;
    }
    
    if (!ValidationResult.WarningMessage.IsEmpty())
    {
        UE_LOG(LogTemp, Warning, TEXT("Предупреждение валидации: %s"), *ValidationResult.WarningMessage);
    }
    
    HideErrorMessage();
    return true;
}

void UCharacterCreationScreen::CreateCharacter()
{
    UE_LOG(LogTemp, Log, TEXT("Создание персонажа: %s (%s %s %s)"), 
           *CharacterName, *SelectedRace, *SelectedGender, *SelectedClass);

    // В реальной реализации здесь будет отправка данных на сервер
    // и переход к экрану выбора персонажей

    UE_LOG(LogTemp, Log, TEXT("Персонаж успешно создан"));
}

void UCharacterCreationScreen::CancelCharacterCreation()
{
    UE_LOG(LogTemp, Log, TEXT("Отмена создания персонажа"));
    
    // В реальной реализации здесь будет переход к экрану выбора персонажей
}

void UCharacterCreationScreen::UpdateAvailableClasses()
{
    if (!ClassSelectionPanel) return;

    // В реальной реализации здесь будет обновление доступных классов
    // в зависимости от выбранной расы и пола
    UE_LOG(LogTemp, Log, TEXT("Обновлены доступные классы для расы: %s"), *SelectedRace);
}

void UCharacterCreationScreen::UpdateCustomizationOptions()
{
    if (!CustomizationPanel) return;

    // В реальной реализации здесь будет обновление опций кастомизации
    // в зависимости от выбранной расы, пола и класса
    UE_LOG(LogTemp, Log, TEXT("Обновлены опции кастомизации"));
}

void UCharacterCreationScreen::ShowErrorMessage(const FString& Message)
{
    if (ErrorMessageText)
    {
        ErrorMessageText->SetText(FText::FromString(Message));
        ErrorMessageText->SetVisibility(ESlateVisibility::Visible);
    }
    
    UE_LOG(LogTemp, Warning, TEXT("Ошибка создания персонажа: %s"), *Message);
}

void UCharacterCreationScreen::HideErrorMessage()
{
    if (ErrorMessageText)
    {
        ErrorMessageText->SetVisibility(ESlateVisibility::Hidden);
    }
}
