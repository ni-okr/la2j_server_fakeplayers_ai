#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/Image.h"
#include "Components/Button.h"
#include "Components/Slider.h"
#include "Components/TextBlock.h"
#include "CostumePreview3D.generated.h"

UCLASS()
class MODERNLINEAGE2_API UCostumePreview3D : public UUserWidget
{
    GENERATED_BODY()

public:
    UCostumePreview3D(const FObjectInitializer& ObjectInitializer);

protected:
    virtual void NativeConstruct() override;

    // UI Components
    UPROPERTY(meta = (BindWidget))
    class UImage* PreviewImage;

    UPROPERTY(meta = (BindWidget))
    class UButton* RotateLeftButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* RotateRightButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* ZoomInButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* ZoomOutButton;

    UPROPERTY(meta = (BindWidget))
    class USlider* RotationSlider;

    UPROPERTY(meta = (BindWidget))
    class USlider* ZoomSlider;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* RotationText;

    UPROPERTY(meta = (BindWidget))
    class UTextBlock* ZoomText;

    // Functions
    UFUNCTION(BlueprintCallable)
    void OnRotateLeftClicked();

    UFUNCTION(BlueprintCallable)
    void OnRotateRightClicked();

    UFUNCTION(BlueprintCallable)
    void OnZoomInClicked();

    UFUNCTION(BlueprintCallable)
    void OnZoomOutClicked();

    UFUNCTION(BlueprintCallable)
    void OnRotationSliderChanged(float Value);

    UFUNCTION(BlueprintCallable)
    void OnZoomSliderChanged(float Value);

    UFUNCTION(BlueprintCallable)
    void UpdatePreview(FString CostumeID);

    UFUNCTION(BlueprintCallable)
    void ResetPreview();

private:
    float CurrentRotation;
    float CurrentZoom;
    FString CurrentCostumeID;
    
    void UpdatePreviewImage();
    void UpdateRotationText();
    void UpdateZoomText();
};
