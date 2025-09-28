#include "LoginScreen.h"
#include "Components/EditableTextBox.h"
#include "Components/Button.h"
#include "Components/Image.h"
#include "Components/TextBlock.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"
#include "FontSetup.cpp"
#include "AnimationSystem.cpp"
#include "VisualEffects.cpp"
#include "ValidationSystem.cpp"
#include "ScreenManager.cpp"

ULoginScreen::ULoginScreen(const FObjectInitializer& ObjectInitializer)
    : Super(ObjectInitializer)
{
    // Конструктор экрана входа
}

void ULoginScreen::NativeConstruct()
{
    Super::NativeConstruct();
    
    // Настройка элементов на основе анализа эталона
    SetupElementsFromAnalysis();
    
    // Настройка шрифтов в соответствии с эталоном
    FLoginScreenFontSetup::SetupLoginScreenFonts(this);
    
    // Настройка анимаций для всех элементов
    FLoginScreenAnimationSystem::SetupLoginScreenAnimations(this);
    
    // Настройка визуальных эффектов
    FLoginScreenVisualEffects::SetupLoginScreenEffects(this);
    
    // Настройка системы валидации
    FLoginScreenValidationSystem::SetupValidationSystem(this);
    
    // Инициализация системы управления экранами
    FScreenManager::Initialize(this);
    
    // Привязка обработчиков событий
    if (LoginButton)
    {
        LoginButton->OnClicked.AddDynamic(this, &ULoginScreen::OnLoginButtonClicked);
    }
    
    if (RegisterButton)
    {
        RegisterButton->OnClicked.AddDynamic(this, &ULoginScreen::OnRegisterButtonClicked);
    }
    
    if (SettingsButton)
    {
        SettingsButton->OnClicked.AddDynamic(this, &ULoginScreen::OnSettingsButtonClicked);
    }
    
    if (LoginField)
    {
        LoginField->OnTextChanged.AddDynamic(this, &ULoginScreen::OnLoginTextChanged);
        LoginField->SetMaxLength(MAX_LOGIN_LENGTH);
    }
    
    if (PasswordField)
    {
        PasswordField->OnTextChanged.AddDynamic(this, &ULoginScreen::OnPasswordTextChanged);
        PasswordField->SetMaxLength(MAX_PASSWORD_LENGTH);
        PasswordField->SetIsPassword(true);
    }
    
    // Запуск анимации появления экрана
    FLoginScreenAnimationUtils::PlayScreenAppearanceAnimation(this);
    
    UE_LOG(LogTemp, Log, TEXT("Экран входа инициализирован с полной настройкой"));
}

void ULoginScreen::NativeDestruct()
{
    Super::NativeDestruct();
}

void ULoginScreen::SetupElementsFromAnalysis()
{
    // Настройка позиций элементов согласно эталону
    SetupElementPositions();
    
    // Настройка цветов элементов согласно эталону
    SetupElementColors();
    
    // Настройка шрифтов элементов согласно эталону
    SetupElementFonts();
}

void ULoginScreen::SetupElementPositions()
{
    // Позиции элементов из анализа эталона
    if (LoginField)
    {
        LoginField->SetPositionInViewport(FVector2D(412, 300));
        LoginField->SetDesiredSizeScale(FVector2D(200, 30));
    }
    
    if (PasswordField)
    {
        PasswordField->SetPositionInViewport(FVector2D(412, 340));
        PasswordField->SetDesiredSizeScale(FVector2D(200, 30));
    }
    
    if (LoginButton)
    {
        LoginButton->SetPositionInViewport(FVector2D(462, 380));
        LoginButton->SetDesiredSizeScale(FVector2D(100, 40));
    }
    
    if (RegisterButton)
    {
        RegisterButton->SetPositionInViewport(FVector2D(462, 430));
        RegisterButton->SetDesiredSizeScale(FVector2D(100, 40));
    }
    
    if (SettingsButton)
    {
        SettingsButton->SetPositionInViewport(FVector2D(50, 50));
        SettingsButton->SetDesiredSizeScale(FVector2D(80, 30));
    }
}

void ULoginScreen::SetupElementColors()
{
    // Цвета элементов из анализа эталона
    if (LoginField)
    {
        LoginField->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("#000000"))));
    }
    
    if (PasswordField)
    {
        PasswordField->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("#000000"))));
    }
    
    if (LoginButton)
    {
        LoginButton->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("#FFD700"))));
    }
    
    if (RegisterButton)
    {
        RegisterButton->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("#C0C0C0"))));
    }
    
    if (SettingsButton)
    {
        SettingsButton->SetColorAndOpacity(FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("#646464"))));
    }
}

void ULoginScreen::SetupElementFonts()
{
    // Настройка шрифтов согласно эталону
    // Здесь будет код настройки шрифтов
}

void ULoginScreen::OnLoginButtonClicked()
{
    // Обработка нажатия кнопки "Войти" через систему обработки событий
    FLoginScreenEventHandler::HandleLoginButtonClicked(this);
}

void ULoginScreen::OnRegisterButtonClicked()
{
    // Обработка нажатия кнопки "Регистрация" через систему обработки событий
    FLoginScreenEventHandler::HandleRegisterButtonClicked(this);
}

void ULoginScreen::OnSettingsButtonClicked()
{
    // Обработка нажатия кнопки "Настройки" через систему обработки событий
    FLoginScreenEventHandler::HandleSettingsButtonClicked(this);
}

void ULoginScreen::OnLoginTextChanged(const FText& Text)
{
    // Обработка изменения текста в поле логина через систему обработки событий
    FLoginScreenEventHandler::HandleLoginTextChanged(this, Text);
}

void ULoginScreen::OnPasswordTextChanged(const FText& Text)
{
    // Обработка изменения текста в поле пароля через систему обработки событий
    FLoginScreenEventHandler::HandlePasswordTextChanged(this, Text);
}

bool ULoginScreen::ValidateLoginInput(const FString& Login)
{
    // Валидация логина
    return Login.Len() >= 3 && Login.Len() <= MAX_LOGIN_LENGTH;
}

bool ULoginScreen::ValidatePasswordInput(const FString& Password)
{
    // Валидация пароля
    return Password.Len() >= 6 && Password.Len() <= MAX_PASSWORD_LENGTH;
}
