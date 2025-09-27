#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Button.h"
#include "Components/ScrollBox.h"
#include "Components/Image.h"
#include "Components/TextBlock.h"
#include "Components/UniformGridPanel.h"
#include "Components/CanvasPanel.h"
#include "Components/ProgressBar.h"
#include "Components/Slider.h"
#include "SlaveManagementWidget.generated.h"

UCLASS()
class MODERNLINEAGE2_API USlaveManagementWidget : public UUserWidget
{
    GENERATED_BODY()

public:
    USlaveManagementWidget(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UButton* CloseButton;

    UPROPERTY(meta = (BindWidget))
    class UScrollBox* SlaveScrollBox;

    UPROPERTY(meta = (BindWidget))
    class UUniformGridPanel* SlaveGrid;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* TitleText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* SelectedSlaveName;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* SelectedSlaveType;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* SelectedSlaveLevel;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* SelectedSlaveExperience;

    UPROPERTY(meta = (BindWidget))
    class UProgressBar* LoyaltyBar;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* LoyaltyText;

    UPROPERTY(meta = (BindWidget))
    class UProgressBar* ExperienceBar;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* ExperienceText;

    UPROPERTY(meta = (BindWidget))
    class UButton* FeedButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* TrainButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* PunishButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* RewardButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* SellButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* FreeButton;

    UPROPERTY(meta = (BindWidget))
    class USlider* WorkIntensitySlider;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* WorkIntensityText;

    UPROPERTY(meta = (BindWidget))
    class UCanvasPanel* PreviewPanel;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnCloseButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnSlaveItemClicked(FString SlaveID);

    UFUNCTION(BlueprintCallable)
    void OnFeedButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnTrainButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnPunishButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnRewardButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnSellButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnFreeButtonClicked();

    UFUNCTION(BlueprintCallable)
    void OnWorkIntensityChanged(float Value);

    UFUNCTION(BlueprintCallable)
    void RefreshSlaveList();

    UFUNCTION(BlueprintCallable)
    void UpdateSelectedSlaveInfo(FString SlaveID);

private:
    FString SelectedSlaveID;
    void SetupUI();
    void CreateSlaveItem(FString SlaveID);
    void UpdateSlaveInfo();
    void UpdateWorkIntensityDisplay();
};
