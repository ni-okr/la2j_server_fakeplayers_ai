#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/EditableTextBox.h"
#include "Components/Image.h"
#include "Components/ScrollBox.h"
#include "CharacterCreationScreen.generated.h"

/**
 * Экран создания персонажа
 * Обеспечивает полный функционал создания персонажа с кастомизацией
 */
UCLASS()
class MODERNLINEAGE2_API UCharacterCreationScreen : public UUserWidget
{
    GENERATED_BODY()

public:
    UCharacterCreationScreen(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;

public:
    // UI Компоненты
    UPROPERTY(meta = (BindWidget))
    class UButton* CreateCharacterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* CancelButton;

    UPROPERTY(meta = (BindWidget))
    class UEditableTextBox* NameInputField;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* RaceSelectionPanel;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* GenderSelectionPanel;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* ClassSelectionPanel;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* CustomizationPanel;

    UPROPERTY(meta = (BindWidget))
    class UImage* CharacterPreviewImage;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* ErrorMessageText;

    // Переменные состояния
    UPROPERTY(BlueprintReadOnly)
    FString SelectedRace;

    UPROPERTY(BlueprintReadOnly)
    FString SelectedGender;

    UPROPERTY(BlueprintReadOnly)
    FString SelectedClass;

    UPROPERTY(BlueprintReadOnly)
    FString CharacterName;

    UPROPERTY(BlueprintReadOnly)
    TMap<FString, FString> CustomizationOptions;

    // События
    UFUNCTION()
    void OnCreateCharacterButtonClicked();

    UFUNCTION()
    void OnCancelButtonClicked();

    UFUNCTION()
    void OnNameInputChanged(const FText& Text);

    UFUNCTION()
    void OnRaceSelected(FString RaceName);

    UFUNCTION()
    void OnGenderSelected(FString GenderName);

    UFUNCTION()
    void OnClassSelected(FString ClassName);

    UFUNCTION()
    void OnCustomizationChanged(FString OptionName, FString Value);

    // Функции управления
    UFUNCTION(BlueprintCallable)
    void InitializeCharacterCreation();

    UFUNCTION(BlueprintCallable)
    void UpdateCharacterPreview();

    UFUNCTION(BlueprintCallable)
    bool ValidateCharacterData();

    UFUNCTION(BlueprintCallable)
    void CreateCharacter();

    UFUNCTION(BlueprintCallable)
    void CancelCharacterCreation();

private:
    // Внутренние функции
    void SetupRaceSelection();
    void SetupGenderSelection();
    void SetupClassSelection();
    void SetupCustomization();
    void SetupCharacterPreview();
    void UpdateAvailableClasses();
    void UpdateCustomizationOptions();
    void ShowErrorMessage(const FString& Message);
    void HideErrorMessage();
};
