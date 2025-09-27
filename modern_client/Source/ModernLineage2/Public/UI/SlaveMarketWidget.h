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
#include "Components/ProgressBar.h"
#include "SlaveMarketWidget.generated.h"

UCLASS()
class MODERNLINEAGE2_API USlaveMarketWidget : public UUserWidget
{
    GENERATED_BODY()

public:
    USlaveMarketWidget(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;
    virtual void NativeDestruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UButton* CloseButton;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* SlaveScrollBox;

    UPROPERTY(meta = (BindWidget))
    class UUniformGridPanel* SlaveGrid;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* TypeComboBox;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* PriceComboBox;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* LoyaltyComboBox;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* TitleText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* DescriptionText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* GoldText;

    UPROPERTY(meta = (BindWidget))
    class UCanvasPanel* PreviewPanel;

    UPROPERTY(meta = (BindWidget))
    class UProgressBar* LoyaltyBar;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* LoyaltyText;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnCloseButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnTypeFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void OnPriceFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void OnLoyaltyFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void RefreshSlaveList();

    UFUNCTION(BlueprintCallable)
    void OnSlaveItemClicked(FString SlaveID);

    UFUNCTION(BlueprintCallable)
    void OnSlaveItemHovered(FString SlaveID);

    UFUNCTION(BlueprintCallable)
    void OnSlaveItemUnhovered(FString SlaveID);

    UFUNCTION(BlueprintCallable)
    void OnBuySlaveClicked(FString SlaveID);

    UFUNCTION(BlueprintCallable)
    void OnSellSlaveClicked(FString SlaveID);

private:
    void SetupUI();
    void PopulateTypeFilters();
    void PopulatePriceFilters();
    void PopulateLoyaltyFilters();
    void CreateSlaveItem(FString SlaveID);
    void UpdateGoldDisplay();
    void UpdateLoyaltyDisplay(FString SlaveID);
};
