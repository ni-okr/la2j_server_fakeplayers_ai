#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/EditableTextBox.h"
#include "Components/Button.h"
#include "Components/Image.h"
#include "Components/TextBlock.h"
#include "LoginScreen.generated.h"

/**
 * Экран входа в игру с точным соответствием эталонному клиенту
 * Создан на основе анализа эталонного клиента
 */
UCLASS()
class MODERNLINEAGE2_API ULoginScreen : public UUserWidget
{
    GENERATED_BODY()

public:
    ULoginScreen(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;
    virtual void NativeDestruct() override;

    // Элементы интерфейса
    UPROPERTY(meta = (BindWidget))
    class UImage* BackgroundImage;

    UPROPERTY(meta = (BindWidget))
    class UImage* LogoImage;

    UPROPERTY(meta = (BindWidget))
    class UEditableTextBox* LoginField;

    UPROPERTY(meta = (BindWidget))
    class UEditableTextBox* PasswordField;

    UPROPERTY(meta = (BindWidget))
    class UButton* LoginButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* RegisterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* SettingsButton;

    // Обработчики событий
    UFUNCTION()
    void OnLoginButtonClicked();

    UFUNCTION()
    void OnRegisterButtonClicked();

    UFUNCTION()
    void OnSettingsButtonClicked();

    UFUNCTION()
    void OnLoginTextChanged(const FText& Text);

    UFUNCTION()
    void OnPasswordTextChanged(const FText& Text);

private:
    // Настройка элементов на основе анализа
    void SetupElementsFromAnalysis();
    
    // Настройка позиций элементов
    void SetupElementPositions();
    
    // Настройка цветов элементов
    void SetupElementColors();
    
    // Настройка шрифтов элементов
    void SetupElementFonts();
    
    // Валидация ввода
    bool ValidateLoginInput(const FString& Login);
    bool ValidatePasswordInput(const FString& Password);
    
    // Максимальная длина полей
    static constexpr int32 MAX_LOGIN_LENGTH = 16;
    static constexpr int32 MAX_PASSWORD_LENGTH = 16;
};
