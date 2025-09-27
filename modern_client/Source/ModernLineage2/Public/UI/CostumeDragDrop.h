#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Image.h"
#include "Components/TextBlock.h"
#include "Components/Border.h"
#include "CostumeDragDrop.generated.h"

UCLASS()
class MODERNLINEAGE2_API UCostumeDragDrop : public UUserWidget
{
    GENERATED_BODY()

public:
    UCostumeDragDrop(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UImage* CostumeIcon;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* CostumeName;

    UPROPERTY(meta = (BindWidget))
    class UBorder* DragBorder;

    // Drag & Drop Data
    UPROPERTY(BlueprintReadWrite, Category = "DragDrop")
    FString CostumeID;

    UPROPERTY(BlueprintReadWrite, Category = "DragDrop")
    FString SourceSlot;

    UPROPERTY(BlueprintReadWrite, Category = "DragDrop")
    bool bIsDragging;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnDragStarted();

    UFUNCTION(BlueprintCallable)
    void OnDragEnded();

    UFUNCTION(BlueprintCallable)
    void OnDropAccepted(FString TargetSlot);

    UFUNCTION(BlueprintCallable)
    void OnDropRejected();

    UFUNCTION(BlueprintCallable)
    void SetCostumeData(const FString& InCostumeID, const FString& InSourceSlot);

    UFUNCTION(BlueprintCallable)
    void UpdateVisualState();

private:
    void SetupDragDropHandlers();
    bool CanDropOnSlot(const FString& TargetSlot) const;
    void UpdateDragVisuals();
};
