#include "CoreMinimal.h"
#include "Components/EditableTextBox.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"

/**
 * Система валидации для экрана входа в соответствии с эталонным клиентом
 * Обеспечивает валидацию полей ввода в реальном времени
 */
class FLoginScreenValidationSystem
{
public:
    /**
     * Настройка системы валидации для экрана входа
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupValidationSystem(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("LoginScreenWidget is null"));
            return;
        }

        // Настройка валидации поля логина
        SetupLoginFieldValidation(LoginScreenWidget);
        
        // Настройка валидации поля пароля
        SetupPasswordFieldValidation(LoginScreenWidget);
        
        // Настройка визуальных индикаторов валидации
        SetupValidationIndicators(LoginScreenWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Система валидации экрана входа настроена"));
    }

private:
    /**
     * Настройка валидации поля логина
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupLoginFieldValidation(UUserWidget* LoginScreenWidget)
    {
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        if (!LoginField)
        {
            UE_LOG(LogTemp, Warning, TEXT("Поле логина не найдено"));
            return;
        }

        // Настройка валидации в реальном времени
        LoginField->OnTextChanged.AddDynamic(LoginScreenWidget, &UUserWidget::OnLoginTextChanged);
        
        // Настройка валидации при потере фокуса
        LoginField->OnTextCommitted.AddDynamic(LoginScreenWidget, &UUserWidget::OnLoginTextCommitted);
        
        // Установка максимальной длины (16 символов из эталона)
        LoginField->SetMaxLength(16);
        
        // Настройка placeholder текста
        LoginField->SetHintText(FText::FromString(TEXT("Логин")));
        
        UE_LOG(LogTemp, Log, TEXT("Валидация поля логина настроена"));
    }

    /**
     * Настройка валидации поля пароля
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupPasswordFieldValidation(UUserWidget* LoginScreenWidget)
    {
        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        if (!PasswordField)
        {
            UE_LOG(LogTemp, Warning, TEXT("Поле пароля не найдено"));
            return;
        }

        // Настройка валидации в реальном времени
        PasswordField->OnTextChanged.AddDynamic(LoginScreenWidget, &UUserWidget::OnPasswordTextChanged);
        
        // Настройка валидации при потере фокуса
        PasswordField->OnTextCommitted.AddDynamic(LoginScreenWidget, &UUserWidget::OnPasswordTextCommitted);
        
        // Установка максимальной длины (16 символов из эталона)
        PasswordField->SetMaxLength(16);
        
        // Настройка placeholder текста
        PasswordField->SetHintText(FText::FromString(TEXT("Пароль")));
        
        // Включение режима пароля (скрытие символов)
        PasswordField->SetIsPassword(true);
        
        UE_LOG(LogTemp, Log, TEXT("Валидация поля пароля настроена"));
    }

    /**
     * Настройка визуальных индикаторов валидации
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void SetupValidationIndicators(UUserWidget* LoginScreenWidget)
    {
        // Создаем индикаторы валидации для полей
        CreateValidationIndicator(LoginScreenWidget, TEXT("LoginValidationIndicator"), FVector2D(620, 300));
        CreateValidationIndicator(LoginScreenWidget, TEXT("PasswordValidationIndicator"), FVector2D(620, 340));
        
        UE_LOG(LogTemp, Log, TEXT("Визуальные индикаторы валидации настроены"));
    }

    /**
     * Создание индикатора валидации
     * @param LoginScreenWidget - виджет экрана входа
     * @param IndicatorName - имя индикатора
     * @param Position - позиция индикатора
     */
    static void CreateValidationIndicator(UUserWidget* LoginScreenWidget, const FString& IndicatorName, const FVector2D& Position)
    {
        // Создаем изображение для индикатора
        UImage* Indicator = NewObject<UImage>(LoginScreenWidget);
        if (Indicator)
        {
            Indicator->SetName(FName(*IndicatorName));
            Indicator->SetPositionInViewport(Position);
            Indicator->SetDesiredSizeScale(FVector2D(16, 16));
            
            // Начальное состояние - скрытый
            Indicator->SetVisibility(ESlateVisibility::Hidden);
            
            // Добавляем к экрану
            LoginScreenWidget->AddToViewport();
        }
    }
};

/**
 * Утилиты для валидации полей ввода
 */
class FLoginScreenValidationUtils
{
public:
    /**
     * Валидация логина
     * @param Login - логин для проверки
     * @return bool - валиден ли логин
     */
    static bool ValidateLogin(const FString& Login)
    {
        // Проверка длины (3-16 символов)
        if (Login.Len() < 3 || Login.Len() > 16)
        {
            UE_LOG(LogTemp, Warning, TEXT("Логин должен содержать от 3 до 16 символов"));
            return false;
        }
        
        // Проверка на допустимые символы (буквы, цифры, подчеркивания)
        for (int32 i = 0; i < Login.Len(); i++)
        {
            TCHAR Char = Login[i];
            if (!FChar::IsAlnum(Char) && Char != TEXT('_'))
            {
                UE_LOG(LogTemp, Warning, TEXT("Логин может содержать только буквы, цифры и подчеркивания"));
                return false;
            }
        }
        
        // Проверка на запрещенные слова
        TArray<FString> ForbiddenWords = {TEXT("admin"), TEXT("root"), TEXT("user"), TEXT("test")};
        for (const FString& Word : ForbiddenWords)
        {
            if (Login.ToLower().Contains(Word))
            {
                UE_LOG(LogTemp, Warning, TEXT("Логин содержит запрещенное слово: %s"), *Word);
                return false;
            }
        }
        
        UE_LOG(LogTemp, Log, TEXT("Логин валиден: %s"), *Login);
        return true;
    }

    /**
     * Валидация пароля
     * @param Password - пароль для проверки
     * @return bool - валиден ли пароль
     */
    static bool ValidatePassword(const FString& Password)
    {
        // Проверка длины (6-16 символов)
        if (Password.Len() < 6 || Password.Len() > 16)
        {
            UE_LOG(LogTemp, Warning, TEXT("Пароль должен содержать от 6 до 16 символов"));
            return false;
        }
        
        // Проверка на наличие хотя бы одной буквы
        bool bHasLetter = false;
        for (int32 i = 0; i < Password.Len(); i++)
        {
            if (FChar::IsAlpha(Password[i]))
            {
                bHasLetter = true;
                break;
            }
        }
        
        if (!bHasLetter)
        {
            UE_LOG(LogTemp, Warning, TEXT("Пароль должен содержать хотя бы одну букву"));
            return false;
        }
        
        // Проверка на наличие хотя бы одной цифры
        bool bHasDigit = false;
        for (int32 i = 0; i < Password.Len(); i++)
        {
            if (FChar::IsDigit(Password[i]))
            {
                bHasDigit = true;
                break;
            }
        }
        
        if (!bHasDigit)
        {
            UE_LOG(LogTemp, Warning, TEXT("Пароль должен содержать хотя бы одну цифру"));
            return false;
        }
        
        UE_LOG(LogTemp, Log, TEXT("Пароль валиден"));
        return true;
    }

    /**
     * Получение сообщения об ошибке валидации
     * @param Login - логин для проверки
     * @param Password - пароль для проверки
     * @return FString - сообщение об ошибке
     */
    static FString GetValidationErrorMessage(const FString& Login, const FString& Password)
    {
        if (Login.IsEmpty())
        {
            return TEXT("Введите логин");
        }
        
        if (Password.IsEmpty())
        {
            return TEXT("Введите пароль");
        }
        
        if (!ValidateLogin(Login))
        {
            if (Login.Len() < 3)
            {
                return TEXT("Логин слишком короткий (минимум 3 символа)");
            }
            else if (Login.Len() > 16)
            {
                return TEXT("Логин слишком длинный (максимум 16 символов)");
            }
            else
            {
                return TEXT("Логин содержит недопустимые символы");
            }
        }
        
        if (!ValidatePassword(Password))
        {
            if (Password.Len() < 6)
            {
                return TEXT("Пароль слишком короткий (минимум 6 символов)");
            }
            else if (Password.Len() > 16)
            {
                return TEXT("Пароль слишком длинный (максимум 16 символов)");
            }
            else
            {
                return TEXT("Пароль должен содержать буквы и цифры");
            }
        }
        
        return TEXT("");
    }

    /**
     * Обновление визуального индикатора валидации
     * @param LoginScreenWidget - виджет экрана входа
     * @param FieldName - имя поля
     * @param bIsValid - валидно ли поле
     */
    static void UpdateValidationIndicator(UUserWidget* LoginScreenWidget, const FString& FieldName, bool bIsValid)
    {
        FString IndicatorName = FieldName + TEXT("ValidationIndicator");
        UImage* Indicator = LoginScreenWidget->FindWidget<UImage>(FName(*IndicatorName));
        
        if (Indicator)
        {
            if (bIsValid)
            {
                // Зеленый индикатор для валидного поля
                Indicator->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00FF00"))));
                Indicator->SetVisibility(ESlateVisibility::Visible);
            }
            else
            {
                // Красный индикатор для невалидного поля
                Indicator->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FF0000"))));
                Indicator->SetVisibility(ESlateVisibility::Visible);
            }
        }
    }
};

/**
 * Система аутентификации для экрана входа
 */
class FLoginScreenAuthenticationSystem
{
public:
    /**
     * Попытка входа в систему
     * @param Login - логин пользователя
     * @param Password - пароль пользователя
     * @return bool - успешен ли вход
     */
    static bool AttemptLogin(const FString& Login, const FString& Password)
    {
        // Валидация входных данных
        if (!FLoginScreenValidationUtils::ValidateLogin(Login))
        {
            UE_LOG(LogTemp, Warning, TEXT("Невалидный логин: %s"), *Login);
            return false;
        }
        
        if (!FLoginScreenValidationUtils::ValidatePassword(Password))
        {
            UE_LOG(LogTemp, Warning, TEXT("Невалидный пароль"));
            return false;
        }
        
        // Симуляция проверки учетных данных
        // В реальной реализации здесь будет обращение к серверу
        if (SimulateAuthentication(Login, Password))
        {
            UE_LOG(LogTemp, Log, TEXT("Успешный вход: %s"), *Login);
            return true;
        }
        else
        {
            UE_LOG(LogTemp, Warning, TEXT("Неверные учетные данные"));
            return false;
        }
    }

    /**
     * Регистрация нового пользователя
     * @param Login - логин пользователя
     * @param Password - пароль пользователя
     * @return bool - успешна ли регистрация
     */
    static bool AttemptRegistration(const FString& Login, const FString& Password)
    {
        // Валидация входных данных
        if (!FLoginScreenValidationUtils::ValidateLogin(Login))
        {
            UE_LOG(LogTemp, Warning, TEXT("Невалидный логин для регистрации: %s"), *Login);
            return false;
        }
        
        if (!FLoginScreenValidationUtils::ValidatePassword(Password))
        {
            UE_LOG(LogTemp, Warning, TEXT("Невалидный пароль для регистрации"));
            return false;
        }
        
        // Симуляция регистрации
        // В реальной реализации здесь будет обращение к серверу
        if (SimulateRegistration(Login, Password))
        {
            UE_LOG(LogTemp, Log, TEXT("Успешная регистрация: %s"), *Login);
            return true;
        }
        else
        {
            UE_LOG(LogTemp, Warning, TEXT("Ошибка регистрации"));
            return false;
        }
    }

private:
    /**
     * Симуляция аутентификации
     * @param Login - логин пользователя
     * @param Password - пароль пользователя
     * @return bool - успешна ли аутентификация
     */
    static bool SimulateAuthentication(const FString& Login, const FString& Password)
    {
        // В реальной реализации здесь будет обращение к серверу
        // Пока что симулируем успешную аутентификацию для тестовых данных
        if (Login == TEXT("test") && Password == TEXT("test123"))
        {
            return true;
        }
        
        // Симуляция задержки сети
        FPlatformProcess::Sleep(0.5f);
        
        return false;
    }

    /**
     * Симуляция регистрации
     * @param Login - логин пользователя
     * @param Password - пароль пользователя
     * @return bool - успешна ли регистрация
     */
    static bool SimulateRegistration(const FString& Login, const FString& Password)
    {
        // В реальной реализации здесь будет обращение к серверу
        // Пока что симулируем успешную регистрацию
        UE_LOG(LogTemp, Log, TEXT("Регистрация пользователя: %s"), *Login);
        
        // Симуляция задержки сети
        FPlatformProcess::Sleep(0.3f);
        
        return true;
    }
};
