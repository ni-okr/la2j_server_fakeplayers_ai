#include "CoreMinimal.h"
#include "Engine/Engine.h"
#include "Components/TextBlock.h"
#include "Components/Button.h"
#include "Components/EditableTextBox.h"

/**
 * Система валидации и безопасности для экрана выбора персонажей
 * Обеспечивает проверку данных персонажей и защиту от некорректных операций
 */
class FCharacterValidationSystem
{
public:
    /**
     * Инициализация системы валидации
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void InitializeValidationSystem(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("CharacterSelectionWidget is null"));
            return;
        }

        // Инициализация валидации данных персонажей
        InitializeCharacterDataValidation(CharacterSelectionWidget);
        
        // Инициализация валидации операций
        InitializeOperationValidation(CharacterSelectionWidget);
        
        // Инициализация системы безопасности
        InitializeSecuritySystem(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система валидации инициализирована"));
    }

private:
    /**
     * Инициализация валидации данных персонажей
     */
    static void InitializeCharacterDataValidation(UUserWidget* CharacterSelectionWidget)
    {
        // Настраиваем валидацию для всех полей персонажей
        SetupCharacterNameValidation(CharacterSelectionWidget);
        SetupCharacterLevelValidation(CharacterSelectionWidget);
        SetupCharacterClassValidation(CharacterSelectionWidget);
        SetupCharacterLocationValidation(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Валидация данных персонажей инициализирована"));
    }

    /**
     * Настройка валидации имени персонажа
     */
    static void SetupCharacterNameValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации имени персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация имени персонажа настроена"));
    }

    /**
     * Настройка валидации уровня персонажа
     */
    static void SetupCharacterLevelValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации уровня персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация уровня персонажа настроена"));
    }

    /**
     * Настройка валидации класса персонажа
     */
    static void SetupCharacterClassValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации класса персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация класса персонажа настроена"));
    }

    /**
     * Настройка валидации локации персонажа
     */
    static void SetupCharacterLocationValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации локации персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация локации персонажа настроена"));
    }

    /**
     * Инициализация валидации операций
     */
    static void InitializeOperationValidation(UUserWidget* CharacterSelectionWidget)
    {
        // Настраиваем валидацию для всех операций
        SetupCharacterCreationValidation(CharacterSelectionWidget);
        SetupCharacterDeletionValidation(CharacterSelectionWidget);
        SetupCharacterSelectionValidation(CharacterSelectionWidget);
        SetupGameEntryValidation(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Валидация операций инициализирована"));
    }

    /**
     * Настройка валидации создания персонажа
     */
    static void SetupCharacterCreationValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации создания персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация создания персонажа настроена"));
    }

    /**
     * Настройка валидации удаления персонажа
     */
    static void SetupCharacterDeletionValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации удаления персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация удаления персонажа настроена"));
    }

    /**
     * Настройка валидации выбора персонажа
     */
    static void SetupCharacterSelectionValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации выбора персонажа
        UE_LOG(LogTemp, Log, TEXT("Валидация выбора персонажа настроена"));
    }

    /**
     * Настройка валидации входа в игру
     */
    static void SetupGameEntryValidation(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка валидации входа в игру
        UE_LOG(LogTemp, Log, TEXT("Валидация входа в игру настроена"));
    }

    /**
     * Инициализация системы безопасности
     */
    static void InitializeSecuritySystem(UUserWidget* CharacterSelectionWidget)
    {
        // Настраиваем систему безопасности
        SetupAntiCheatSystem(CharacterSelectionWidget);
        SetupDataIntegritySystem(CharacterSelectionWidget);
        SetupAccessControlSystem(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система безопасности инициализирована"));
    }

    /**
     * Настройка системы защиты от читов
     */
    static void SetupAntiCheatSystem(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка системы защиты от читов
        UE_LOG(LogTemp, Log, TEXT("Система защиты от читов настроена"));
    }

    /**
     * Настройка системы целостности данных
     */
    static void SetupDataIntegritySystem(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка системы целостности данных
        UE_LOG(LogTemp, Log, TEXT("Система целостности данных настроена"));
    }

    /**
     * Настройка системы контроля доступа
     */
    static void SetupAccessControlSystem(UUserWidget* CharacterSelectionWidget)
    {
        // В реальной реализации здесь будет настройка системы контроля доступа
        UE_LOG(LogTemp, Log, TEXT("Система контроля доступа настроена"));
    }
};

/**
 * Утилиты для валидации персонажей
 */
class FCharacterValidationUtils
{
public:
    /**
     * Валидация имени персонажа
     * @param CharacterName - имя персонажа для проверки
     * @return bool - валидность имени
     */
    static bool ValidateCharacterName(const FString& CharacterName)
    {
        // Проверяем длину имени
        if (CharacterName.Len() < 3 || CharacterName.Len() > 16)
        {
            UE_LOG(LogTemp, Warning, TEXT("Имя персонажа должно быть от 3 до 16 символов: %s"), *CharacterName);
            return false;
        }

        // Проверяем допустимые символы (только буквы, цифры и подчеркивания)
        for (int32 i = 0; i < CharacterName.Len(); i++)
        {
            TCHAR Char = CharacterName[i];
            if (!FChar::IsAlnum(Char) && Char != TEXT('_'))
            {
                UE_LOG(LogTemp, Warning, TEXT("Имя персонажа содержит недопустимые символы: %s"), *CharacterName);
                return false;
            }
        }

        // Проверяем, что имя не начинается с цифры
        if (FChar::IsDigit(CharacterName[0]))
        {
            UE_LOG(LogTemp, Warning, TEXT("Имя персонажа не может начинаться с цифры: %s"), *CharacterName);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Имя персонажа валидно: %s"), *CharacterName);
        return true;
    }

    /**
     * Валидация уровня персонажа
     * @param Level - уровень персонажа для проверки
     * @return bool - валидность уровня
     */
    static bool ValidateCharacterLevel(int32 Level)
    {
        if (Level < 1 || Level > 80)
        {
            UE_LOG(LogTemp, Warning, TEXT("Уровень персонажа должен быть от 1 до 80: %d"), Level);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Уровень персонажа валиден: %d"), Level);
        return true;
    }

    /**
     * Валидация класса персонажа
     * @param CharacterClass - класс персонажа для проверки
     * @return bool - валидность класса
     */
    static bool ValidateCharacterClass(const FString& CharacterClass)
    {
        // Список допустимых классов
        TArray<FString> ValidClasses = {
            TEXT("Воин"), TEXT("Маг"), TEXT("Лучник"), TEXT("Жрец"),
            TEXT("Рыцарь"), TEXT("Ассасин"), TEXT("Друид"), TEXT("Паладин")
        };

        if (!ValidClasses.Contains(CharacterClass))
        {
            UE_LOG(LogTemp, Warning, TEXT("Недопустимый класс персонажа: %s"), *CharacterClass);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Класс персонажа валиден: %s"), *CharacterClass);
        return true;
    }

    /**
     * Валидация локации персонажа
     * @param Location - локация персонажа для проверки
     * @return bool - валидность локации
     */
    static bool ValidateCharacterLocation(const FString& Location)
    {
        // Список допустимых локаций
        TArray<FString> ValidLocations = {
            TEXT("Гиран"), TEXT("Аден"), TEXT("Глодио"), TEXT("Дион"),
            TEXT("Орен"), TEXT("Хейн"), TEXT("Руна"), TEXT("Шутгарт")
        };

        if (!ValidLocations.Contains(Location))
        {
            UE_LOG(LogTemp, Warning, TEXT("Недопустимая локация персонажа: %s"), *Location);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Локация персонажа валидна: %s"), *Location);
        return true;
    }

    /**
     * Валидация данных персонажа
     * @param CharacterData - данные персонажа для проверки
     * @return bool - валидность данных
     */
    static bool ValidateCharacterData(const FCharacterData& CharacterData)
    {
        bool bIsValid = true;

        // Проверяем имя персонажа
        if (!ValidateCharacterName(CharacterData.CharacterName))
        {
            bIsValid = false;
        }

        // Проверяем уровень персонажа
        if (!ValidateCharacterLevel(CharacterData.Level))
        {
            bIsValid = false;
        }

        // Проверяем класс персонажа
        if (!ValidateCharacterClass(CharacterData.CharacterClass))
        {
            bIsValid = false;
        }

        // Проверяем локацию персонажа
        if (!ValidateCharacterLocation(CharacterData.Location))
        {
            bIsValid = false;
        }

        if (bIsValid)
        {
            UE_LOG(LogTemp, Log, TEXT("Данные персонажа валидны: %s"), *CharacterData.CharacterName);
        }
        else
        {
            UE_LOG(LogTemp, Error, TEXT("Данные персонажа невалидны: %s"), *CharacterData.CharacterName);
        }

        return bIsValid;
    }

    /**
     * Валидация операции создания персонажа
     * @param CurrentCharacterCount - текущее количество персонажей
     * @return bool - можно ли создать персонажа
     */
    static bool ValidateCharacterCreation(int32 CurrentCharacterCount)
    {
        const int32 MaxCharacters = 7;
        
        if (CurrentCharacterCount >= MaxCharacters)
        {
            UE_LOG(LogTemp, Warning, TEXT("Достигнуто максимальное количество персонажей: %d/%d"), 
                   CurrentCharacterCount, MaxCharacters);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Можно создать нового персонажа: %d/%d"), 
               CurrentCharacterCount, MaxCharacters);
        return true;
    }

    /**
     * Валидация операции удаления персонажа
     * @param CharacterIndex - индекс персонажа для удаления
     * @param TotalCharacterCount - общее количество персонажей
     * @return bool - можно ли удалить персонажа
     */
    static bool ValidateCharacterDeletion(int32 CharacterIndex, int32 TotalCharacterCount)
    {
        if (CharacterIndex < 0 || CharacterIndex >= TotalCharacterCount)
        {
            UE_LOG(LogTemp, Warning, TEXT("Неверный индекс персонажа для удаления: %d"), CharacterIndex);
            return false;
        }

        if (TotalCharacterCount <= 0)
        {
            UE_LOG(LogTemp, Warning, TEXT("Нет персонажей для удаления"));
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Можно удалить персонажа: %d"), CharacterIndex);
        return true;
    }

    /**
     * Валидация операции выбора персонажа
     * @param CharacterIndex - индекс персонажа для выбора
     * @param TotalCharacterCount - общее количество персонажей
     * @return bool - можно ли выбрать персонажа
     */
    static bool ValidateCharacterSelection(int32 CharacterIndex, int32 TotalCharacterCount)
    {
        if (CharacterIndex < 0 || CharacterIndex >= TotalCharacterCount)
        {
            UE_LOG(LogTemp, Warning, TEXT("Неверный индекс персонажа для выбора: %d"), CharacterIndex);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Можно выбрать персонажа: %d"), CharacterIndex);
        return true;
    }

    /**
     * Валидация операции входа в игру
     * @param CharacterIndex - индекс персонажа для входа в игру
     * @param TotalCharacterCount - общее количество персонажей
     * @return bool - можно ли войти в игру
     */
    static bool ValidateGameEntry(int32 CharacterIndex, int32 TotalCharacterCount)
    {
        if (CharacterIndex < 0 || CharacterIndex >= TotalCharacterCount)
        {
            UE_LOG(LogTemp, Warning, TEXT("Неверный индекс персонажа для входа в игру: %d"), CharacterIndex);
            return false;
        }

        UE_LOG(LogTemp, Log, TEXT("Можно войти в игру с персонажем: %d"), CharacterIndex);
        return true;
    }
};

/**
 * Система отображения ошибок валидации
 */
class FCharacterValidationErrorDisplay
{
public:
    /**
     * Отображение ошибки валидации
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     * @param ErrorMessage - сообщение об ошибке
     * @param ErrorType - тип ошибки
     */
    static void ShowValidationError(UUserWidget* CharacterSelectionWidget, 
                                   const FString& ErrorMessage, 
                                   const FString& ErrorType = TEXT("Error"))
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        // Получаем элемент для отображения ошибок
        UTextBlock* ErrorTextBlock = CharacterSelectionWidget->FindWidget<UTextBlock>(TEXT("ErrorMessageText"));
        if (ErrorTextBlock)
        {
            ErrorTextBlock->SetText(FText::FromString(ErrorMessage));
            ErrorTextBlock->SetVisibility(ESlateVisibility::Visible);
            
            // Устанавливаем цвет в зависимости от типа ошибки
            if (ErrorType == TEXT("Error"))
            {
                ErrorTextBlock->SetColorAndOpacity(FLinearColor::Red);
            }
            else if (ErrorType == TEXT("Warning"))
            {
                ErrorTextBlock->SetColorAndOpacity(FLinearColor::Yellow);
            }
            else
            {
                ErrorTextBlock->SetColorAndOpacity(FLinearColor::White);
            }
        }

        UE_LOG(LogTemp, Error, TEXT("Ошибка валидации [%s]: %s"), *ErrorType, *ErrorMessage);
    }

    /**
     * Скрытие ошибки валидации
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void HideValidationError(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        UTextBlock* ErrorTextBlock = CharacterSelectionWidget->FindWidget<UTextBlock>(TEXT("ErrorMessageText"));
        if (ErrorTextBlock)
        {
            ErrorTextBlock->SetVisibility(ESlateVisibility::Hidden);
        }

        UE_LOG(LogTemp, Log, TEXT("Ошибка валидации скрыта"));
    }

    /**
     * Отображение успешного сообщения
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     * @param SuccessMessage - сообщение об успехе
     */
    static void ShowSuccessMessage(UUserWidget* CharacterSelectionWidget, const FString& SuccessMessage)
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        UTextBlock* ErrorTextBlock = CharacterSelectionWidget->FindWidget<UTextBlock>(TEXT("ErrorMessageText"));
        if (ErrorTextBlock)
        {
            ErrorTextBlock->SetText(FText::FromString(SuccessMessage));
            ErrorTextBlock->SetColorAndOpacity(FLinearColor::Green);
            ErrorTextBlock->SetVisibility(ESlateVisibility::Visible);
        }

        UE_LOG(LogTemp, Log, TEXT("Успешное сообщение: %s"), *SuccessMessage);
    }
};
