
// CharacterSelectionScreen.h
#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/ScrollBox.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "CharacterSelectionScreen.generated.h"

/**
 * Структура данных персонажа
 */
USTRUCT(BlueprintType)
struct FCharacterData
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString CharacterName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    int32 Level;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString CharacterClass;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString Location;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString AvatarPath;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    bool bIsSelected;

    FCharacterData()
    {
        CharacterName = TEXT("");
        Level = 1;
        CharacterClass = TEXT("");
        Location = TEXT("");
        AvatarPath = TEXT("");
        bIsSelected = false;
    }
};

/**
 * Экран выбора персонажей
 */
UCLASS()
class UCharacterSelectionScreen : public UUserWidget
{
    GENERATED_BODY()

public:
    // Конструктор
    UCharacterSelectionScreen(const FObjectInitializer& ObjectInitializer);

    // Виртуальная функция для инициализации виджета
    virtual void NativeConstruct() override;

    // Основные UI компоненты
    UPROPERTY(meta = (BindWidget))
    class UScrollBox* CharacterListPanel;

    UPROPERTY(meta = (BindWidget))
    class UButton* CreateCharacterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* DeleteCharacterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* EnterGameButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* BackButton;

    // Функции-обработчики событий
    UFUNCTION()
    void OnCreateCharacterButtonClicked();

    UFUNCTION()
    void OnDeleteCharacterButtonClicked();

    UFUNCTION()
    void OnEnterGameButtonClicked();

    UFUNCTION()
    void OnBackButtonClicked();

    UFUNCTION()
    void OnCharacterSlotClicked(int32 CharacterIndex);

    // Функции управления персонажами
    UFUNCTION(BlueprintCallable)
    void LoadCharacterList();

    UFUNCTION(BlueprintCallable)
    void RefreshCharacterList();

    UFUNCTION(BlueprintCallable)
    void SelectCharacter(int32 CharacterIndex);

    UFUNCTION(BlueprintCallable)
    void DeleteSelectedCharacter();

    UFUNCTION(BlueprintCallable)
    void EnterGameWithSelectedCharacter();

protected:
    // Данные персонажей
    UPROPERTY(BlueprintReadOnly)
    TArray<FCharacterData> CharacterList;

    UPROPERTY(BlueprintReadOnly)
    int32 SelectedCharacterIndex;

    // Максимальное количество персонажей
    UPROPERTY(BlueprintReadOnly)
    int32 MaxCharacters;

    // Создание слота персонажа
    UFUNCTION(BlueprintCallable)
    UUserWidget* CreateCharacterSlot(const FCharacterData& CharacterData, int32 Index);

    // Обновление слота персонажа
    UFUNCTION(BlueprintCallable)
    void UpdateCharacterSlot(UUserWidget* SlotWidget, const FCharacterData& CharacterData, int32 Index);
};
