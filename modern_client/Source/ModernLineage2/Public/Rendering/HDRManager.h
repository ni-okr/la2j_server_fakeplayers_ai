#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/PostProcessVolume.h"
#include "Engine/World.h"
#include "HDRManager.generated.h"

/**
 * HDR Settings Structure
 */
USTRUCT(BlueprintType)
struct FHDRSettings
{
	GENERATED_BODY()

	/** Enable HDR rendering */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR")
	bool bEnableHDR = true;
	
	/** HDR color gamut */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR")
	FString ColorGamut = TEXT("Rec2020");
	
	/** Maximum brightness (nits) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "100.0", ClampMax = "10000.0"))
	float MaxBrightness = 1000.0f;
	
	/** Minimum brightness (nits) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "0.01", ClampMax = "1.0"))
	float MinBrightness = 0.1f;
	
	/** Exposure compensation */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "-5.0", ClampMax = "5.0"))
	float ExposureCompensation = 0.0f;
	
	/** Auto exposure enabled */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR")
	bool bAutoExposure = true;
	
	/** Auto exposure min brightness */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "0.01", ClampMax = "1.0"))
	float AutoExposureMinBrightness = 0.1f;
	
	/** Auto exposure max brightness */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "1.0", ClampMax = "100.0"))
	float AutoExposureMaxBrightness = 10.0f;
	
	/** Auto exposure speed up */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "0.1", ClampMax = "10.0"))
	float AutoExposureSpeedUp = 3.0f;
	
	/** Auto exposure speed down */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR", meta = (ClampMin = "0.1", ClampMax = "10.0"))
	float AutoExposureSpeedDown = 1.0f;
};

/**
 * Tone Mapping Settings Structure
 */
USTRUCT(BlueprintType)
struct FToneMappingSettings
{
	GENERATED_BODY()

	/** Tone mapping method */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping")
	FString ToneMappingMethod = TEXT("ACES");
	
	/** Film slope */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "2.0"))
	float FilmSlope = 0.88f;
	
	/** Film toe */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float FilmToe = 0.55f;
	
	/** Film shoulder */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float FilmShoulder = 0.26f;
	
	/** Film black clip */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float FilmBlackClip = 0.0f;
	
	/** Film white clip */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float FilmWhiteClip = 0.04f;
	
	/** Saturation */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "2.0"))
	float Saturation = 1.0f;
	
	/** Contrast */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.0", ClampMax = "2.0"))
	float Contrast = 1.0f;
	
	/** Gamma */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping", meta = (ClampMin = "0.1", ClampMax = "3.0"))
	float Gamma = 2.2f;
};

/**
 * HDR Manager for Modern Lineage II
 * Handles HDR rendering, tone mapping, and color grading
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UHDRManager : public UActorComponent
{
	GENERATED_BODY()

public:
	UHDRManager();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== HDR SETTINGS =====
	
	/** HDR settings */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "HDR Settings")
	FHDRSettings HDRSettings;
	
	/** Tone mapping settings */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Tone Mapping Settings")
	FToneMappingSettings ToneMappingSettings;
	
	/** Post process volume for HDR */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "HDR Components")
	APostProcessVolume* HDRPostProcessVolume;

	// ===== HDR FUNCTIONS =====
	
	/** Initialize HDR system */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void InitializeHDRSystem();
	
	/** Enable/disable HDR */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetHDREnabled(bool bEnabled);
	
	/** Check if HDR is enabled */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "HDR")
	bool IsHDREnabled() const;
	
	/** Set HDR brightness range */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetHDRBrightnessRange(float MinBrightness, float MaxBrightness);
	
	/** Set exposure compensation */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetExposureCompensation(float Exposure);
	
	/** Enable/disable auto exposure */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetAutoExposureEnabled(bool bEnabled);
	
	/** Set auto exposure range */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetAutoExposureRange(float MinBrightness, float MaxBrightness);
	
	/** Set auto exposure speed */
	UFUNCTION(BlueprintCallable, Category = "HDR")
	void SetAutoExposureSpeed(float SpeedUp, float SpeedDown);

	// ===== TONE MAPPING FUNCTIONS =====
	
	/** Set tone mapping method */
	UFUNCTION(BlueprintCallable, Category = "Tone Mapping")
	void SetToneMappingMethod(const FString& Method);
	
	/** Set film curve parameters */
	UFUNCTION(BlueprintCallable, Category = "Tone Mapping")
	void SetFilmCurveParameters(float Slope, float Toe, float Shoulder, float BlackClip, float WhiteClip);
	
	/** Set color grading parameters */
	UFUNCTION(BlueprintCallable, Category = "Tone Mapping")
	void SetColorGradingParameters(float Saturation, float Contrast, float Gamma);
	
	/** Apply tone mapping preset */
	UFUNCTION(BlueprintCallable, Category = "Tone Mapping")
	void ApplyToneMappingPreset(const FString& PresetName);

	// ===== COLOR GRADING FUNCTIONS =====
	
	/** Set color temperature */
	UFUNCTION(BlueprintCallable, Category = "Color Grading")
	void SetColorTemperature(float Temperature);
	
	/** Set color tint */
	UFUNCTION(BlueprintCallable, Category = "Color Grading")
	void SetColorTint(float Tint);
	
	/** Set color grading LUT */
	UFUNCTION(BlueprintCallable, Category = "Color Grading")
	void SetColorGradingLUT(UTexture* LUTTexture);
	
	/** Set color grading intensity */
	UFUNCTION(BlueprintCallable, Category = "Color Grading")
	void SetColorGradingIntensity(float Intensity);

	// ===== PERFORMANCE FUNCTIONS =====
	
	/** Set HDR quality level */
	UFUNCTION(BlueprintCallable, Category = "Performance")
	void SetHDRQualityLevel(int32 QualityLevel);
	
	/** Get HDR quality level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetHDRQualityLevel() const;
	
	/** Optimize HDR for performance */
	UFUNCTION(BlueprintCallable, Category = "Performance")
	void OptimizeHDRForPerformance();
	
	/** Get HDR performance impact */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	float GetHDRPerformanceImpact() const;

	// ===== PRESET FUNCTIONS =====
	
	/** Load HDR preset */
	UFUNCTION(BlueprintCallable, Category = "Presets")
	void LoadHDRPreset(const FString& PresetName);
	
	/** Save HDR preset */
	UFUNCTION(BlueprintCallable, Category = "Presets")
	void SaveHDRPreset(const FString& PresetName);
	
	/** Get available presets */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Presets")
	TArray<FString> GetAvailablePresets() const;
	
	/** Reset to default settings */
	UFUNCTION(BlueprintCallable, Category = "Presets")
	void ResetToDefaultSettings();

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup HDR post process volume */
	void SetupHDRPostProcessVolume();
	
	/** Apply HDR settings to post process */
	void ApplyHDRSettingsToPostProcess();
	
	/** Apply tone mapping settings to post process */
	void ApplyToneMappingSettingsToPostProcess();
	
	/** Update auto exposure */
	void UpdateAutoExposure(float DeltaTime);
	
	/** Calculate current exposure */
	float CalculateCurrentExposure() const;
	
	/** Setup default presets */
	void SetupDefaultPresets();

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Current HDR quality level */
	UPROPERTY()
	int32 CurrentHDRQualityLevel = 2;
	
	/** HDR presets */
	UPROPERTY()
	TMap<FString, FHDRSettings> HDRPresets;
	
	/** Tone mapping presets */
	UPROPERTY()
	TMap<FString, FToneMappingSettings> ToneMappingPresets;
	
	/** Current exposure value */
	UPROPERTY()
	float CurrentExposure = 1.0f;
	
	/** Target exposure value */
	UPROPERTY()
	float TargetExposure = 1.0f;
	
	/** Auto exposure timer */
	UPROPERTY()
	float AutoExposureTimer = 0.0f;
	
	/** HDR performance impact */
	UPROPERTY()
	float HDRPerformanceImpact = 0.0f;
};
