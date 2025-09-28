#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"
#include "Components/WidgetSwitcher.h"

/**
 * Система управления экранами для приложения
 * Обеспечивает переходы между различными экранами игры
 */
class FScreenManager
{
public:
    /**
     * Инициализация системы управления экранами
     * @param MainWidget - главный виджет приложения
     */
    static void Initialize(UUserWidget* MainWidget)
    {
        if (!MainWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("MainWidget is null"));
            return;
        }

        MainApplicationWidget = MainWidget;
        
        // Создаем переключатель экранов
        CreateScreenSwitcher();
        
        // Регистрируем все экраны
        RegisterAllScreens();
        
        // Устанавливаем начальный экран
        SetCurrentScreen(EScreenType::Login);
        
        UE_LOG(LogTemp, Log, TEXT("Система управления экранами инициализирована"));
    }

    /**
     * Переход к указанному экрану
     * @param ScreenType - тип экрана для перехода
     */
    static void NavigateToScreen(EScreenType ScreenType)
    {
        if (!MainApplicationWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("MainApplicationWidget не инициализирован"));
            return;
        }

        // Получаем переключатель экранов
        UWidgetSwitcher* ScreenSwitcher = MainApplicationWidget->FindWidget<UWidgetSwitcher>(TEXT("ScreenSwitcher"));
        if (!ScreenSwitcher)
        {
            UE_LOG(LogTemp, Error, TEXT("ScreenSwitcher не найден"));
            return;
        }

        // Получаем виджет экрана
        UUserWidget* TargetScreen = GetScreenWidget(ScreenType);
        if (!TargetScreen)
        {
            UE_LOG(LogTemp, Error, TEXT("Экран не найден: %d"), (int32)ScreenType);
            return;
        }

        // Выполняем переход
        ScreenSwitcher->SetActiveWidget(TargetScreen);
        CurrentScreen = ScreenType;
        
        // Запускаем анимацию перехода
        PlayScreenTransitionAnimation(ScreenType);
        
        UE_LOG(LogTemp, Log, TEXT("Переход к экрану: %d"), (int32)ScreenType);
    }

    /**
     * Получение текущего экрана
     * @return EScreenType - текущий экран
     */
    static EScreenType GetCurrentScreen()
    {
        return CurrentScreen;
    }

    /**
     * Возврат к предыдущему экрану
     */
    static void GoBack()
    {
        if (ScreenHistory.Num() > 1)
        {
            // Удаляем текущий экран из истории
            ScreenHistory.Pop();
            
            // Переходим к предыдущему экрану
            EScreenType PreviousScreen = ScreenHistory.Last();
            NavigateToScreen(PreviousScreen);
        }
        else
        {
            UE_LOG(LogTemp, Warning, TEXT("Нет предыдущего экрана для возврата"));
        }
    }

private:
    // Типы экранов
    enum class EScreenType
    {
        Login,              // Экран входа
        CharacterSelection, // Выбор персонажа
        CharacterCreation,  // Создание персонажа
        GameInterface,      // Игровой интерфейс
        Settings,           // Настройки
        Loading            // Экран загрузки
    };

    // Статические переменные
    static UUserWidget* MainApplicationWidget;
    static EScreenType CurrentScreen;
    static TArray<EScreenType> ScreenHistory;
    static TMap<EScreenType, UUserWidget*> ScreenWidgets;

    /**
     * Создание переключателя экранов
     */
    static void CreateScreenSwitcher()
    {
        if (!MainApplicationWidget)
        {
            return;
        }

        // Создаем переключатель экранов
        UWidgetSwitcher* ScreenSwitcher = NewObject<UWidgetSwitcher>(MainApplicationWidget);
        if (ScreenSwitcher)
        {
            ScreenSwitcher->SetName(TEXT("ScreenSwitcher"));
            ScreenSwitcher->SetAnimateTransitions(true);
            ScreenSwitcher->SetTransitionDuration(0.3f);
            
            // Добавляем к главному виджету
            MainApplicationWidget->AddToViewport();
        }
    }

    /**
     * Регистрация всех экранов
     */
    static void RegisterAllScreens()
    {
        // Регистрируем экран входа
        RegisterScreen(EScreenType::Login, TEXT("WBP_LoginScreen"));
        
        // Регистрируем экран выбора персонажей
        RegisterScreen(EScreenType::CharacterSelection, TEXT("WBP_CharacterSelection"));
        
        // Регистрируем экран создания персонажа
        RegisterScreen(EScreenType::CharacterCreation, TEXT("WBP_CharacterCreation"));
        
        // Регистрируем игровой интерфейс
        RegisterScreen(EScreenType::GameInterface, TEXT("WBP_GameInterface"));
        
        // Регистрируем экран настроек
        RegisterScreen(EScreenType::Settings, TEXT("WBP_Settings"));
        
        // Регистрируем экран загрузки
        RegisterScreen(EScreenType::Loading, TEXT("WBP_Loading"));
    }

    /**
     * Регистрация экрана
     * @param ScreenType - тип экрана
     * @param WidgetClassPath - путь к классу виджета
     */
    static void RegisterScreen(EScreenType ScreenType, const FString& WidgetClassPath)
    {
        if (!MainApplicationWidget)
        {
            return;
        }

        // Загружаем класс виджета
        UClass* WidgetClass = LoadClass<UUserWidget>(nullptr, *WidgetClassPath);
        if (WidgetClass)
        {
            // Создаем экземпляр виджета
            UUserWidget* ScreenWidget = CreateWidget<UUserWidget>(MainApplicationWidget, WidgetClass);
            if (ScreenWidget)
            {
                ScreenWidgets.Add(ScreenType, ScreenWidget);
                
                // Добавляем к переключателю экранов
                UWidgetSwitcher* ScreenSwitcher = MainApplicationWidget->FindWidget<UWidgetSwitcher>(TEXT("ScreenSwitcher"));
                if (ScreenSwitcher)
                {
                    ScreenSwitcher->AddChild(ScreenWidget);
                }
                
                UE_LOG(LogTemp, Log, TEXT("Экран зарегистрирован: %s"), *WidgetClassPath);
            }
        }
        else
        {
            UE_LOG(LogTemp, Warning, TEXT("Не удалось загрузить класс виджета: %s"), *WidgetClassPath);
        }
    }

    /**
     * Получение виджета экрана
     * @param ScreenType - тип экрана
     * @return UUserWidget* - виджет экрана
     */
    static UUserWidget* GetScreenWidget(EScreenType ScreenType)
    {
        if (UUserWidget** FoundWidget = ScreenWidgets.Find(ScreenType))
        {
            return *FoundWidget;
        }
        
        return nullptr;
    }

    /**
     * Установка текущего экрана
     * @param ScreenType - тип экрана
     */
    static void SetCurrentScreen(EScreenType ScreenType)
    {
        CurrentScreen = ScreenType;
        
        // Добавляем в историю
        if (ScreenHistory.Num() == 0 || ScreenHistory.Last() != ScreenType)
        {
            ScreenHistory.Add(ScreenType);
        }
    }

    /**
     * Воспроизведение анимации перехода экрана
     * @param ScreenType - тип экрана
     */
    static void PlayScreenTransitionAnimation(EScreenType ScreenType)
    {
        // В реальной реализации здесь будет анимация перехода
        // Пока что просто логируем
        UE_LOG(LogTemp, Log, TEXT("Воспроизведение анимации перехода к экрану: %d"), (int32)ScreenType);
    }
};

// Инициализация статических переменных
UUserWidget* FScreenManager::MainApplicationWidget = nullptr;
FScreenManager::EScreenType FScreenManager::CurrentScreen = FScreenManager::EScreenType::Login;
TArray<FScreenManager::EScreenType> FScreenManager::ScreenHistory;
TMap<FScreenManager::EScreenType, UUserWidget*> FScreenManager::ScreenWidgets;

/**
 * Система обработки событий экрана входа
 */
class FLoginScreenEventHandler
{
public:
    /**
     * Обработка нажатия кнопки "Войти"
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void HandleLoginButtonClicked(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Получаем поля ввода
        UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
        UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
        
        if (!LoginField || !PasswordField)
        {
            UE_LOG(LogTemp, Error, TEXT("Поля ввода не найдены"));
            return;
        }

        // Получаем введенные данные
        FString Login = LoginField->GetText().ToString();
        FString Password = PasswordField->GetText().ToString();
        
        // Валидация данных
        FString ValidationError = FLoginScreenValidationUtils::GetValidationErrorMessage(Login, Password);
        if (!ValidationError.IsEmpty())
        {
            ShowErrorMessage(LoginScreenWidget, ValidationError);
            return;
        }
        
        // Показываем экран загрузки
        FScreenManager::NavigateToScreen(FScreenManager::EScreenType::Loading);
        
        // Попытка аутентификации
        if (FLoginScreenAuthenticationSystem::AttemptLogin(Login, Password))
        {
            // Успешный вход - переход к выбору персонажей
            FPlatformProcess::Sleep(1.0f); // Симуляция загрузки
            FScreenManager::NavigateToScreen(FScreenManager::EScreenType::CharacterSelection);
        }
        else
        {
            // Неудачный вход - возврат к экрану входа
            FPlatformProcess::Sleep(1.0f); // Симуляция загрузки
            FScreenManager::NavigateToScreen(FScreenManager::EScreenType::Login);
            ShowErrorMessage(LoginScreenWidget, TEXT("Неверные учетные данные"));
        }
    }

    /**
     * Обработка нажатия кнопки "Регистрация"
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void HandleRegisterButtonClicked(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Переход к экрану регистрации
        FScreenManager::NavigateToScreen(FScreenManager::EScreenType::CharacterCreation);
        
        UE_LOG(LogTemp, Log, TEXT("Переход к экрану регистрации"));
    }

    /**
     * Обработка нажатия кнопки "Настройки"
     * @param LoginScreenWidget - виджет экрана входа
     */
    static void HandleSettingsButtonClicked(UUserWidget* LoginScreenWidget)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        // Переход к экрану настроек
        FScreenManager::NavigateToScreen(FScreenManager::EScreenType::Settings);
        
        UE_LOG(LogTemp, Log, TEXT("Переход к экрану настроек"));
    }

    /**
     * Обработка изменения текста в поле логина
     * @param LoginScreenWidget - виджет экрана входа
     * @param NewText - новый текст
     */
    static void HandleLoginTextChanged(UUserWidget* LoginScreenWidget, const FText& NewText)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        FString Login = NewText.ToString();
        bool bIsValid = FLoginScreenValidationUtils::ValidateLogin(Login);
        
        // Обновляем индикатор валидации
        FLoginScreenValidationUtils::UpdateValidationIndicator(LoginScreenWidget, TEXT("Login"), bIsValid);
        
        // Ограничиваем длину
        if (Login.Len() > 16)
        {
            UEditableTextBox* LoginField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("LoginField"));
            if (LoginField)
            {
                LoginField->SetText(FText::FromString(Login.Left(16)));
            }
        }
    }

    /**
     * Обработка изменения текста в поле пароля
     * @param LoginScreenWidget - виджет экрана входа
     * @param NewText - новый текст
     */
    static void HandlePasswordTextChanged(UUserWidget* LoginScreenWidget, const FText& NewText)
    {
        if (!LoginScreenWidget)
        {
            return;
        }

        FString Password = NewText.ToString();
        bool bIsValid = FLoginScreenValidationUtils::ValidatePassword(Password);
        
        // Обновляем индикатор валидации
        FLoginScreenValidationUtils::UpdateValidationIndicator(LoginScreenWidget, TEXT("Password"), bIsValid);
        
        // Ограничиваем длину
        if (Password.Len() > 16)
        {
            UEditableTextBox* PasswordField = LoginScreenWidget->FindWidget<UEditableTextBox>(TEXT("PasswordField"));
            if (PasswordField)
            {
                PasswordField->SetText(FText::FromString(Password.Left(16)));
            }
        }
    }

private:
    /**
     * Показ сообщения об ошибке
     * @param LoginScreenWidget - виджет экрана входа
     * @param ErrorMessage - сообщение об ошибке
     */
    static void ShowErrorMessage(UUserWidget* LoginScreenWidget, const FString& ErrorMessage)
    {
        // В реальной реализации здесь будет показ модального окна с ошибкой
        UE_LOG(LogTemp, Warning, TEXT("Ошибка: %s"), *ErrorMessage);
        
        // Можно добавить визуальный индикатор ошибки
        UTextBlock* ErrorText = LoginScreenWidget->FindWidget<UTextBlock>(TEXT("ErrorText"));
        if (ErrorText)
        {
            ErrorText->SetText(FText::FromString(ErrorMessage));
            ErrorText->SetVisibility(ESlateVisibility::Visible);
        }
    }
};
