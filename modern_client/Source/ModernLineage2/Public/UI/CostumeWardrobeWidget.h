#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/ScrollBox.h"
#include "Components/Image.h"
#include "Components/TextBlock.h"
#include "Components/UniformGridPanel.h"
#include "Components/CanvasPanel.h"
#include "Components/ListView.h"
#include "Components/ComboBoxString.h"
#include "CostumeWardrobeWidget.generated.h"

UCLASS()
class MODERNLINEAGE2_API UCostumeWardrobeWidget : public UUserWidget
{
    GENERATED_BODY()

public:
    UCostumeWardrobeWidget(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;
    virtual void NativeDestruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UButton* CloseButton;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* CostumeScrollBox;

    UPROPERTY(meta = (BindWidget))
    class UUniformGridPanel* CostumeGrid;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* CategoryComboBox;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* SlotComboBox;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* TitleText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* DescriptionText;

    UPROPERTY(meta = (BindWidget))
    class UCanvasPanel* PreviewPanel;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnCloseButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnCategoryChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void OnSlotChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void RefreshCostumeList();

    UFUNCTION(BlueprintCallable)
    void OnCostumeItemClicked(FString CostumeID);

    UFUNCTION(BlueprintCallable)
    void OnCostumeItemHovered(FString CostumeID);

    UFUNCTION(BlueprintCallable)
    void OnCostumeItemUnhovered(FString CostumeID);

private:
    void SetupUI();
    void PopulateCategories();
    void PopulateSlots();
    void CreateCostumeItem(FString CostumeID);
};
