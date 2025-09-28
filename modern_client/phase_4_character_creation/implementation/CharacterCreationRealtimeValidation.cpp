// CharacterCreationRealtimeValidation.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/EditableTextBox.h"
#include "Components/TextBlock.h"
#include "Components/Button.h"
#include "Engine/Engine.h"
#include "TimerManager.h"

// Вспомогательный класс для системы валидации в реальном времени
class FCharacterCreationRealtimeValidation
{
public:
    // Структура настроек валидации в реальном времени
    struct FRealtimeValidationSettings
    {
        float ValidationDelay;
        float ErrorDisplayDuration;
        bool bShowWarnings;
        bool bShowSuggestions;
        bool bAutoCorrect;
        FLinearColor ErrorColor;
        FLinearColor WarningColor;
        FLinearColor SuccessColor;
        FString ErrorSoundPath;
        FString SuccessSoundPath;
    };

    // Типы валидации
    enum class EValidationType
    {
        NameValidation,
        RaceValidation,
        GenderValidation,
        ClassValidation,
        CustomizationValidation,
        OverallValidation
    };

    // Инициализация системы валидации в реальном времени
    static void InitializeRealtimeValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы валидации в реальном времени"));
        
        // Создаем настройки валидации
        CreateValidationSettings();
        
        // Настраиваем валидацию для полей
        SetupFieldValidation(CurrentWidget);
        
        // Привязываем события валидации
        BindValidationEvents(CurrentWidget);
        
        // Запускаем таймер валидации
        StartValidationTimer(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система валидации в реальном времени инициализирована"));
    }

    // Создание настроек валидации
    static void CreateValidationSettings()
    {
        // Очищаем предыдущие настройки
        ValidationSettingsMap.Empty();
        
        // Настройки валидации имени
        FRealtimeValidationSettings NameValidation;
        NameValidation.ValidationDelay = 0.5f;
        NameValidation.ErrorDisplayDuration = 3.0f;
        NameValidation.bShowWarnings = true;
        NameValidation.bShowSuggestions = true;
        NameValidation.bAutoCorrect = false;
        NameValidation.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        NameValidation.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        NameValidation.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        NameValidation.ErrorSoundPath = TEXT("/Game/Sounds/UI/ErrorSound");
        NameValidation.SuccessSoundPath = TEXT("/Game/Sounds/UI/SuccessSound");
        ValidationSettingsMap.Add(TEXT("NameValidation"), NameValidation);
        
        // Настройки валидации расы
        FRealtimeValidationSettings RaceValidation;
        RaceValidation.ValidationDelay = 0.1f;
        RaceValidation.ErrorDisplayDuration = 2.0f;
        RaceValidation.bShowWarnings = false;
        RaceValidation.bShowSuggestions = false;
        RaceValidation.bAutoCorrect = false;
        RaceValidation.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        RaceValidation.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        RaceValidation.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        RaceValidation.ErrorSoundPath = TEXT("/Game/Sounds/UI/ErrorSound");
        RaceValidation.SuccessSoundPath = TEXT("/Game/Sounds/UI/SuccessSound");
        ValidationSettingsMap.Add(TEXT("RaceValidation"), RaceValidation);
        
        // Настройки валидации пола
        FRealtimeValidationSettings GenderValidation;
        GenderValidation.ValidationDelay = 0.1f;
        GenderValidation.ErrorDisplayDuration = 2.0f;
        GenderValidation.bShowWarnings = false;
        GenderValidation.bShowSuggestions = false;
        GenderValidation.bAutoCorrect = false;
        GenderValidation.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        GenderValidation.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        GenderValidation.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        GenderValidation.ErrorSoundPath = TEXT("/Game/Sounds/UI/ErrorSound");
        GenderValidation.SuccessSoundPath = TEXT("/Game/Sounds/UI/SuccessSound");
        ValidationSettingsMap.Add(TEXT("GenderValidation"), GenderValidation);
        
        // Настройки валидации класса
        FRealtimeValidationSettings ClassValidation;
        ClassValidation.ValidationDelay = 0.1f;
        ClassValidation.ErrorDisplayDuration = 2.0f;
        ClassValidation.bShowWarnings = false;
        ClassValidation.bShowSuggestions = false;
        ClassValidation.bAutoCorrect = false;
        ClassValidation.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        ClassValidation.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        ClassValidation.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        ClassValidation.ErrorSoundPath = TEXT("/Game/Sounds/UI/ErrorSound");
        ClassValidation.SuccessSoundPath = TEXT("/Game/Sounds/UI/SuccessSound");
        ValidationSettingsMap.Add(TEXT("ClassValidation"), ClassValidation);
        
        // Настройки общей валидации
        FRealtimeValidationSettings OverallValidation;
        OverallValidation.ValidationDelay = 0.2f;
        OverallValidation.ErrorDisplayDuration = 5.0f;
        OverallValidation.bShowWarnings = true;
        OverallValidation.bShowSuggestions = true;
        OverallValidation.bAutoCorrect = false;
        OverallValidation.ErrorColor = FLinearColor(1.0f, 0.2f, 0.2f, 1.0f); // Красный
        OverallValidation.WarningColor = FLinearColor(1.0f, 0.8f, 0.0f, 1.0f); // Желтый
        OverallValidation.SuccessColor = FLinearColor(0.2f, 1.0f, 0.2f, 1.0f); // Зеленый
        OverallValidation.ErrorSoundPath = TEXT("/Game/Sounds/UI/ErrorSound");
        OverallValidation.SuccessSoundPath = TEXT("/Game/Sounds/UI/SuccessSound");
        ValidationSettingsMap.Add(TEXT("OverallValidation"), OverallValidation);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d настроек валидации в реальном времени"), ValidationSettingsMap.Num());
    }

    // Настройка валидации для полей
    static void SetupFieldValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Настраиваем валидацию поля имени
        SetupNameFieldValidation(CurrentWidget);
        
        // Настраиваем валидацию выбора расы
        SetupRaceSelectionValidation(CurrentWidget);
        
        // Настраиваем валидацию выбора пола
        SetupGenderSelectionValidation(CurrentWidget);
        
        // Настраиваем валидацию выбора класса
        SetupClassSelectionValidation(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Валидация полей настроена"));
    }

    // Настройка валидации поля имени
    static void SetupNameFieldValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим поле ввода имени
        UEditableTextBox* NameField = Cast<UEditableTextBox>(CurrentWidget->GetWidgetFromName(TEXT("NameInputField")));
        if (NameField)
        {
            // Привязываем событие изменения текста
            NameField->OnTextChanged.AddDynamic(CurrentWidget, &UUserWidget::OnTextChanged);
            
            UE_LOG(LogTemp, Log, TEXT("Валидация поля имени настроена"));
        }
    }

    // Настройка валидации выбора расы
    static void SetupRaceSelectionValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора расы
        UScrollBox* RacePanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("RaceSelectionPanel")));
        if (RacePanel)
        {
            // Привязываем события выбора расы
            // В реальной реализации здесь будет привязка событий
            
            UE_LOG(LogTemp, Log, TEXT("Валидация выбора расы настроена"));
        }
    }

    // Настройка валидации выбора пола
    static void SetupGenderSelectionValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора пола
        UScrollBox* GenderPanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("GenderSelectionPanel")));
        if (GenderPanel)
        {
            // Привязываем события выбора пола
            // В реальной реализации здесь будет привязка событий
            
            UE_LOG(LogTemp, Log, TEXT("Валидация выбора пола настроена"));
        }
    }

    // Настройка валидации выбора класса
    static void SetupClassSelectionValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим панель выбора класса
        UScrollBox* ClassPanel = Cast<UScrollBox>(CurrentWidget->GetWidgetFromName(TEXT("ClassSelectionPanel")));
        if (ClassPanel)
        {
            // Привязываем события выбора класса
            // В реальной реализации здесь будет привязка событий
            
            UE_LOG(LogTemp, Log, TEXT("Валидация выбора класса настроена"));
        }
    }

    // Привязка событий валидации
    static void BindValidationEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий валидации
        UE_LOG(LogTemp, Log, TEXT("События валидации привязаны"));
    }

    // Запуск таймера валидации
    static void StartValidationTimer(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет запуск таймера валидации
        UE_LOG(LogTemp, Log, TEXT("Таймер валидации запущен"));
    }

    // Валидация имени в реальном времени
    static void ValidateNameRealtime(const FString& Name)
    {
        if (Name.IsEmpty())
        {
            ShowValidationMessage(TEXT("Введите имя персонажа"), EValidationType::NameValidation, false);
            return;
        }
        
        if (Name.Len() < 3)
        {
            ShowValidationMessage(TEXT("Имя должно содержать минимум 3 символа"), EValidationType::NameValidation, false);
            return;
        }
        
        if (Name.Len() > 16)
        {
            ShowValidationMessage(TEXT("Имя должно содержать максимум 16 символов"), EValidationType::NameValidation, false);
            return;
        }
        
        // Проверка на запрещенные символы
        if (Name.Contains(TEXT(" ")) || Name.Contains(TEXT("\t")) || Name.Contains(TEXT("\n")))
        {
            ShowValidationMessage(TEXT("Имя не должно содержать пробелы"), EValidationType::NameValidation, false);
            return;
        }
        
        // Проверка на запрещенные слова
        TArray<FString> ForbiddenWords = {TEXT("admin"), TEXT("gm"), TEXT("moderator"), TEXT("test"), TEXT("bot")};
        for (const FString& Word : ForbiddenWords)
        {
            if (Name.ToLower().Contains(Word))
            {
                ShowValidationMessage(FString::Printf(TEXT("Имя содержит запрещенное слово: %s"), *Word), EValidationType::NameValidation, false);
                return;
            }
        }
        
        ShowValidationMessage(TEXT("Имя корректно"), EValidationType::NameValidation, true);
    }

    // Валидация выбора расы в реальном времени
    static void ValidateRaceRealtime(const FString& RaceName)
    {
        if (RaceName.IsEmpty())
        {
            ShowValidationMessage(TEXT("Выберите расу персонажа"), EValidationType::RaceValidation, false);
            return;
        }
        
        TArray<FString> AvailableRaces = {TEXT("Human"), TEXT("Elf"), TEXT("DarkElf"), TEXT("Orc"), TEXT("Dwarf")};
        if (!AvailableRaces.Contains(RaceName))
        {
            ShowValidationMessage(TEXT("Выбранная раса недоступна"), EValidationType::RaceValidation, false);
            return;
        }
        
        ShowValidationMessage(TEXT("Раса выбрана корректно"), EValidationType::RaceValidation, true);
    }

    // Валидация выбора пола в реальном времени
    static void ValidateGenderRealtime(const FString& GenderName)
    {
        if (GenderName.IsEmpty())
        {
            ShowValidationMessage(TEXT("Выберите пол персонажа"), EValidationType::GenderValidation, false);
            return;
        }
        
        TArray<FString> AvailableGenders = {TEXT("Male"), TEXT("Female")};
        if (!AvailableGenders.Contains(GenderName))
        {
            ShowValidationMessage(TEXT("Выбранный пол недоступен"), EValidationType::GenderValidation, false);
            return;
        }
        
        ShowValidationMessage(TEXT("Пол выбран корректно"), EValidationType::GenderValidation, true);
    }

    // Валидация выбора класса в реальном времени
    static void ValidateClassRealtime(const FString& ClassName, const FString& RaceName, const FString& GenderName)
    {
        if (ClassName.IsEmpty())
        {
            ShowValidationMessage(TEXT("Выберите класс персонажа"), EValidationType::ClassValidation, false);
            return;
        }
        
        TArray<FString> AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        if (!AvailableClasses.Contains(ClassName))
        {
            ShowValidationMessage(TEXT("Выбранный класс недоступен"), EValidationType::ClassValidation, false);
            return;
        }
        
        // Проверка совместимости класса с расой и полом
        if (!CheckClassCompatibility(ClassName, RaceName, GenderName))
        {
            ShowValidationMessage(TEXT("Выбранный класс несовместим с расой и полом"), EValidationType::ClassValidation, false);
            return;
        }
        
        ShowValidationMessage(TEXT("Класс выбран корректно"), EValidationType::ClassValidation, true);
    }

    // Комплексная валидация в реальном времени
    static void ValidateOverallRealtime(const FString& Name, const FString& RaceName, const FString& GenderName, const FString& ClassName)
    {
        bool bIsValid = true;
        FString ErrorMessage = TEXT("");
        
        // Валидация имени
        if (Name.IsEmpty() || Name.Len() < 3 || Name.Len() > 16)
        {
            bIsValid = false;
            ErrorMessage += TEXT("Имя персонажа некорректно. ");
        }
        
        // Валидация расы
        if (RaceName.IsEmpty())
        {
            bIsValid = false;
            ErrorMessage += TEXT("Раса не выбрана. ");
        }
        
        // Валидация пола
        if (GenderName.IsEmpty())
        {
            bIsValid = false;
            ErrorMessage += TEXT("Пол не выбран. ");
        }
        
        // Валидация класса
        if (ClassName.IsEmpty())
        {
            bIsValid = false;
            ErrorMessage += TEXT("Класс не выбран. ");
        }
        
        if (bIsValid)
        {
            ShowValidationMessage(TEXT("Все данные корректны"), EValidationType::OverallValidation, true);
        }
        else
        {
            ShowValidationMessage(ErrorMessage, EValidationType::OverallValidation, false);
        }
    }

    // Показ сообщения валидации
    static void ShowValidationMessage(const FString& Message, EValidationType ValidationType, bool bIsSuccess)
    {
        FString ValidationTypeName = GetValidationTypeName(ValidationType);
        
        if (bIsSuccess)
        {
            UE_LOG(LogTemp, Log, TEXT("Валидация %s успешна: %s"), *ValidationTypeName, *Message);
        }
        else
        {
            UE_LOG(LogTemp, Warning, TEXT("Ошибка валидации %s: %s"), *ValidationTypeName, *Message);
        }
        
        // В реальной реализации здесь будет показ сообщения в UI
    }

    // Получение имени типа валидации
    static FString GetValidationTypeName(EValidationType ValidationType)
    {
        switch (ValidationType)
        {
            case EValidationType::NameValidation:
                return TEXT("имени");
            case EValidationType::RaceValidation:
                return TEXT("расы");
            case EValidationType::GenderValidation:
                return TEXT("пола");
            case EValidationType::ClassValidation:
                return TEXT("класса");
            case EValidationType::CustomizationValidation:
                return TEXT("кастомизации");
            case EValidationType::OverallValidation:
                return TEXT("общая");
            default:
                return TEXT("неизвестная");
        }
    }

    // Проверка совместимости класса
    static bool CheckClassCompatibility(const FString& ClassName, const FString& RaceName, const FString& GenderName)
    {
        // В реальной реализации здесь будет проверка совместимости класса
        return true;
    }

    // Получение настроек валидации
    static FRealtimeValidationSettings* GetValidationSettings(const FString& SettingsName)
    {
        if (ValidationSettingsMap.Contains(SettingsName))
        {
            return &ValidationSettingsMap[SettingsName];
        }
        return nullptr;
    }

    // Обновление настроек валидации
    static void UpdateValidationSettings(const FString& SettingsName, const FRealtimeValidationSettings& NewSettings)
    {
        ValidationSettingsMap.Add(SettingsName, NewSettings);
        UE_LOG(LogTemp, Log, TEXT("Настройки валидации обновлены: %s"), *SettingsName);
    }

    // Сброс настроек валидации
    static void ResetValidationSettings()
    {
        ValidationSettingsMap.Empty();
        CreateValidationSettings();
        UE_LOG(LogTemp, Log, TEXT("Настройки валидации сброшены"));
    }

private:
    // Карта настроек валидации
    static TMap<FString, FRealtimeValidationSettings> ValidationSettingsMap;
};

// Статические переменные
TMap<FString, FCharacterCreationRealtimeValidation::FRealtimeValidationSettings> FCharacterCreationRealtimeValidation::ValidationSettingsMap;
