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
#include "CompanionRecruitmentWidget.generated.h"

UCLASS()
class MODERNLINEAGE2_API UCompanionRecruitmentWidget : public UUserWidget
{
    GENERATED_BODY()

public:
    UCompanionRecruitmentWidget(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;
    virtual void NativeDestruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UButton* CloseButton;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* CompanionScrollBox;

    UPROPERTY(meta = (BindWidget))
    class UUniformGridPanel* CompanionGrid;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* ClassComboBox;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* LevelComboBox;

    UPROPERTY(meta = (BindWidget))
    class UComboBoxString* PriceComboBox;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* TitleText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* DescriptionText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* GoldText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* ReputationText;

    UPROPERTY(meta = (BindWidget))
    class UCanvasPanel* PreviewPanel;

    UPROPERTY(meta = (BindWidget))
    class UProgressBar* ReputationBar;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* ReputationBarText;

    UPROPERTY(meta = (BindWidget))
    class UButton* HireButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* DismissButton;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnCloseButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnClassFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void OnLevelFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void OnPriceFilterChanged(FString SelectedItem);

    UFUNCTION(BlueprintCallable)
    void RefreshCompanionList();

    UFUNCTION(BlueprintCallable)
    void OnCompanionItemClicked(FString CompanionID);

    UFUNCTION(BlueprintCallable)
    void OnCompanionItemHovered(FString CompanionID);

    UFUNCTION(BlueprintCallable)
    void OnCompanionItemUnhovered(FString CompanionID);

    UFUNCTION(BlueprintCallable)
    void OnHireButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnDismissButtonClicked();

private:
    FString SelectedCompanionID;
    void SetupUI();
    void PopulateClassFilters();
    void PopulateLevelFilters();
    void PopulatePriceFilters();
    void CreateCompanionItem(FString CompanionID);
    void UpdateGoldDisplay();
    void UpdateReputationDisplay();
    void UpdateSelectedCompanionInfo(FString CompanionID);
};
