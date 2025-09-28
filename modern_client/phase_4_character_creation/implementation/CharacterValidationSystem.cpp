// CharacterValidationSystem.cpp
#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/TextBlock.h"
#include "Components/EditableTextBox.h"
#include "Engine/Engine.h"
#include "Misc/Regex.h"

// Вспомогательный класс для системы валидации персонажа
class FCharacterValidationSystem
{
public:
    // Структура результата валидации
    struct FValidationResult
    {
        bool bIsValid;
        FString ErrorMessage;
        FString WarningMessage;
        TArray<FString> Suggestions;
    };

    // Структура правил валидации
    struct FValidationRules
    {
        int32 MinNameLength;
        int32 MaxNameLength;
        TArray<FString> AllowedCharacters;
        TArray<FString> ForbiddenWords;
        bool bAllowNumbers;
        bool bAllowSpecialCharacters;
        bool bCaseSensitive;
    };

    // Инициализация системы валидации
    static void InitializeValidation(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        UE_LOG(LogTemp, Log, TEXT("Инициализация системы валидации персонажа"));
        
        // Создаем правила валидации
        CreateValidationRules();
        
        // Настраиваем валидацию полей
        SetupValidationFields(CurrentWidget);
        
        // Привязываем события валидации
        BindValidationEvents(CurrentWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система валидации персонажа инициализирована"));
    }

    // Создание правил валидации
    static void CreateValidationRules()
    {
        // Правила для имени персонажа
        FValidationRules NameRules;
        NameRules.MinNameLength = 3;
        NameRules.MaxNameLength = 16;
        NameRules.AllowedCharacters = {TEXT("a-z"), TEXT("A-Z"), TEXT("0-9"), TEXT("_")};
        NameRules.ForbiddenWords = {TEXT("admin"), TEXT("gm"), TEXT("moderator"), TEXT("test"), TEXT("bot")};
        NameRules.bAllowNumbers = true;
        NameRules.bAllowSpecialCharacters = false;
        NameRules.bCaseSensitive = false;
        ValidationRulesMap.Add(TEXT("Name"), NameRules);
        
        // Правила для расы
        FValidationRules RaceRules;
        RaceRules.MinNameLength = 1;
        RaceRules.MaxNameLength = 20;
        RaceRules.AllowedCharacters = {TEXT("a-z"), TEXT("A-Z")};
        RaceRules.ForbiddenWords = {};
        RaceRules.bAllowNumbers = false;
        RaceRules.bAllowSpecialCharacters = false;
        RaceRules.bCaseSensitive = false;
        ValidationRulesMap.Add(TEXT("Race"), RaceRules);
        
        // Правила для пола
        FValidationRules GenderRules;
        GenderRules.MinNameLength = 1;
        GenderRules.MaxNameLength = 10;
        GenderRules.AllowedCharacters = {TEXT("a-z"), TEXT("A-Z")};
        GenderRules.ForbiddenWords = {};
        GenderRules.bAllowNumbers = false;
        GenderRules.bAllowSpecialCharacters = false;
        GenderRules.bCaseSensitive = false;
        ValidationRulesMap.Add(TEXT("Gender"), GenderRules);
        
        // Правила для класса
        FValidationRules ClassRules;
        ClassRules.MinNameLength = 1;
        ClassRules.MaxNameLength = 20;
        ClassRules.AllowedCharacters = {TEXT("a-z"), TEXT("A-Z")};
        ClassRules.ForbiddenWords = {};
        ClassRules.bAllowNumbers = false;
        ClassRules.bAllowSpecialCharacters = false;
        ClassRules.bCaseSensitive = false;
        ValidationRulesMap.Add(TEXT("Class"), ClassRules);
        
        UE_LOG(LogTemp, Log, TEXT("Создано %d правил валидации"), ValidationRulesMap.Num());
    }

    // Настройка полей валидации
    static void SetupValidationFields(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // Находим поле ввода имени
        UEditableTextBox* NameInputField = Cast<UEditableTextBox>(CurrentWidget->GetWidgetFromName(TEXT("NameInputField")));
        if (NameInputField)
        {
            // Настраиваем валидацию имени
            SetupNameValidation(NameInputField);
        }
        
        UE_LOG(LogTemp, Log, TEXT("Поля валидации настроены"));
    }

    // Настройка валидации имени
    static void SetupNameValidation(UEditableTextBox* NameField)
    {
        if (!NameField) return;
        
        // В реальной реализации здесь будет настройка валидации имени
        // с проверкой в реальном времени
        UE_LOG(LogTemp, Log, TEXT("Валидация имени настроена"));
    }

    // Привязка событий валидации
    static void BindValidationEvents(UUserWidget* CurrentWidget)
    {
        if (!CurrentWidget) return;
        
        // В реальной реализации здесь будет привязка событий валидации
        UE_LOG(LogTemp, Log, TEXT("События валидации привязаны"));
    }

    // Валидация имени персонажа
    static FValidationResult ValidateCharacterName(const FString& CharacterName)
    {
        FValidationResult Result;
        Result.bIsValid = true;
        Result.ErrorMessage = TEXT("");
        Result.WarningMessage = TEXT("");
        Result.Suggestions.Empty();
        
        if (!ValidationRulesMap.Contains(TEXT("Name")))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Правила валидации имени не найдены");
            return Result;
        }
        
        const FValidationRules& NameRules = ValidationRulesMap[TEXT("Name")];
        
        // Проверка длины имени
        if (CharacterName.Len() < NameRules.MinNameLength)
        {
            Result.bIsValid = false;
            Result.ErrorMessage = FString::Printf(TEXT("Имя должно содержать минимум %d символов"), NameRules.MinNameLength);
            return Result;
        }
        
        if (CharacterName.Len() > NameRules.MaxNameLength)
        {
            Result.bIsValid = false;
            Result.ErrorMessage = FString::Printf(TEXT("Имя должно содержать максимум %d символов"), NameRules.MaxNameLength);
            return Result;
        }
        
        // Проверка на пустое имя
        if (CharacterName.TrimStartAndEnd().IsEmpty())
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Имя не может быть пустым");
            return Result;
        }
        
        // Проверка на запрещенные слова
        FString LowerName = CharacterName.ToLower();
        for (const FString& ForbiddenWord : NameRules.ForbiddenWords)
        {
            if (LowerName.Contains(ForbiddenWord.ToLower()))
            {
                Result.bIsValid = false;
                Result.ErrorMessage = FString::Printf(TEXT("Имя содержит запрещенное слово: %s"), *ForbiddenWord);
                return Result;
            }
        }
        
        // Проверка на разрешенные символы
        if (!ValidateCharacterSet(CharacterName, NameRules))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Имя содержит недопустимые символы");
            Result.Suggestions.Add(TEXT("Используйте только буквы, цифры и подчеркивания"));
            return Result;
        }
        
        // Проверка на уникальность (заглушка)
        if (!CheckNameUniqueness(CharacterName))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Имя уже используется другим персонажем");
            Result.Suggestions.Add(TEXT("Попробуйте другое имя"));
            return Result;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Имя персонажа валидно: %s"), *CharacterName);
        return Result;
    }

    // Валидация выбора расы
    static FValidationResult ValidateRaceSelection(const FString& RaceName)
    {
        FValidationResult Result;
        Result.bIsValid = true;
        Result.ErrorMessage = TEXT("");
        Result.WarningMessage = TEXT("");
        Result.Suggestions.Empty();
        
        if (RaceName.IsEmpty())
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Необходимо выбрать расу персонажа");
            return Result;
        }
        
        // Проверяем, что раса существует и доступна
        TArray<FString> AvailableRaces = {TEXT("Human"), TEXT("Elf"), TEXT("DarkElf"), TEXT("Orc"), TEXT("Dwarf")};
        if (!AvailableRaces.Contains(RaceName))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Выбранная раса недоступна");
            return Result;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Раса персонажа валидна: %s"), *RaceName);
        return Result;
    }

    // Валидация выбора пола
    static FValidationResult ValidateGenderSelection(const FString& GenderName)
    {
        FValidationResult Result;
        Result.bIsValid = true;
        Result.ErrorMessage = TEXT("");
        Result.WarningMessage = TEXT("");
        Result.Suggestions.Empty();
        
        if (GenderName.IsEmpty())
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Необходимо выбрать пол персонажа");
            return Result;
        }
        
        // Проверяем, что пол существует и доступен
        TArray<FString> AvailableGenders = {TEXT("Male"), TEXT("Female")};
        if (!AvailableGenders.Contains(GenderName))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Выбранный пол недоступен");
            return Result;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Пол персонажа валиден: %s"), *GenderName);
        return Result;
    }

    // Валидация выбора класса
    static FValidationResult ValidateClassSelection(const FString& ClassName, const FString& RaceName, const FString& GenderName)
    {
        FValidationResult Result;
        Result.bIsValid = true;
        Result.ErrorMessage = TEXT("");
        Result.WarningMessage = TEXT("");
        Result.Suggestions.Empty();
        
        if (ClassName.IsEmpty())
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Необходимо выбрать класс персонажа");
            return Result;
        }
        
        // Проверяем, что класс существует и доступен
        TArray<FString> AvailableClasses = {TEXT("Fighter"), TEXT("Mystic"), TEXT("Scout")};
        if (!AvailableClasses.Contains(ClassName))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Выбранный класс недоступен");
            return Result;
        }
        
        // Проверяем совместимость класса с расой и полом
        if (!CheckClassCompatibility(ClassName, RaceName, GenderName))
        {
            Result.bIsValid = false;
            Result.ErrorMessage = TEXT("Выбранный класс несовместим с расой и полом");
            Result.Suggestions.Add(TEXT("Выберите другой класс или измените расу/пол"));
            return Result;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Класс персонажа валиден: %s"), *ClassName);
        return Result;
    }

    // Комплексная валидация персонажа
    static FValidationResult ValidateCharacter(const FString& CharacterName, const FString& RaceName, const FString& GenderName, const FString& ClassName)
    {
        FValidationResult Result;
        Result.bIsValid = true;
        Result.ErrorMessage = TEXT("");
        Result.WarningMessage = TEXT("");
        Result.Suggestions.Empty();
        
        // Валидация имени
        FValidationResult NameResult = ValidateCharacterName(CharacterName);
        if (!NameResult.bIsValid)
        {
            return NameResult;
        }
        
        // Валидация расы
        FValidationResult RaceResult = ValidateRaceSelection(RaceName);
        if (!RaceResult.bIsValid)
        {
            return RaceResult;
        }
        
        // Валидация пола
        FValidationResult GenderResult = ValidateGenderSelection(GenderName);
        if (!GenderResult.bIsValid)
        {
            return GenderResult;
        }
        
        // Валидация класса
        FValidationResult ClassResult = ValidateClassSelection(ClassName, RaceName, GenderName);
        if (!ClassResult.bIsValid)
        {
            return ClassResult;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Персонаж валиден: %s (%s %s %s)"), *CharacterName, *RaceName, *GenderName, *ClassName);
        return Result;
    }

    // Проверка набора символов
    static bool ValidateCharacterSet(const FString& Text, const FValidationRules& Rules)
    {
        // В реальной реализации здесь будет проверка набора символов
        // с использованием регулярных выражений
        return true;
    }

    // Проверка уникальности имени
    static bool CheckNameUniqueness(const FString& CharacterName)
    {
        // В реальной реализации здесь будет проверка уникальности имени
        // через запрос к серверу или базе данных
        return true;
    }

    // Проверка совместимости класса
    static bool CheckClassCompatibility(const FString& ClassName, const FString& RaceName, const FString& GenderName)
    {
        // В реальной реализации здесь будет проверка совместимости класса
        // с расой и полом
        return true;
    }

    // Получение правил валидации
    static FValidationRules* GetValidationRules(const FString& RuleName)
    {
        if (ValidationRulesMap.Contains(RuleName))
        {
            return &ValidationRulesMap[RuleName];
        }
        return nullptr;
    }

    // Обновление правил валидации
    static void UpdateValidationRules(const FString& RuleName, const FValidationRules& NewRules)
    {
        ValidationRulesMap.Add(RuleName, NewRules);
        UE_LOG(LogTemp, Log, TEXT("Правила валидации обновлены: %s"), *RuleName);
    }

    // Сброс валидации
    static void ResetValidation()
    {
        ValidationRulesMap.Empty();
        CreateValidationRules();
        UE_LOG(LogTemp, Log, TEXT("Валидация сброшена"));
    }

private:
    // Карта правил валидации
    static TMap<FString, FValidationRules> ValidationRulesMap;
};

// Статические переменные
TMap<FString, FCharacterValidationSystem::FValidationRules> FCharacterValidationSystem::ValidationRulesMap;
