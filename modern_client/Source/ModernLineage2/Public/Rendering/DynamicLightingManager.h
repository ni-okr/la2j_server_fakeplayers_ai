#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/Light.h"
#include "Engine/DirectionalLight.h"
#include "Engine/PointLight.h"
#include "Engine/SpotLight.h"
#include "Engine/SkyLight.h"
#include "Engine/World.h"
#include "DynamicLightingManager.generated.h"

/**
 * Lighting Preset Structure
 */
USTRUCT(BlueprintType)
struct FLightingPreset
{
	GENERATED_BODY()

	/** Preset name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FString PresetName;
	
	/** Preset description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FString Description;
	
	/** Time of day (0-24 hours) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "24.0"))
	float TimeOfDay = 12.0f;
	
	/** Sun direction */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FRotator SunDirection = FRotator(-45.0f, 0.0f, 0.0f);
	
	/** Sun color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FLinearColor SunColor = FLinearColor::White;
	
	/** Sun intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "20.0"))
	float SunIntensity = 10.0f;
	
	/** Sky light color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FLinearColor SkyLightColor = FLinearColor(0.5f, 0.7f, 1.0f);
	
	/** Sky light intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "10.0"))
	float SkyLightIntensity = 1.0f;
	
	/** Ambient light color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FLinearColor AmbientLightColor = FLinearColor(0.2f, 0.2f, 0.3f);
	
	/** Ambient light intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "2.0"))
	float AmbientLightIntensity = 0.3f;
	
	/** Fog color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset")
	FLinearColor FogColor = FLinearColor(0.5f, 0.6f, 0.7f);
	
	/** Fog density */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float FogDensity = 0.02f;
	
	/** Fog start distance */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "10000.0"))
	float FogStartDistance = 0.0f;
	
	/** Fog end distance */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Lighting Preset", meta = (ClampMin = "0.0", ClampMax = "10000.0"))
	float FogEndDistance = 10000.0f;
};

/**
 * Dynamic Lighting Manager for Modern Lineage II
 * Handles dynamic lighting, day/night cycle, and atmospheric effects
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UDynamicLightingManager : public UActorComponent
{
	GENERATED_BODY()

public:
	UDynamicLightingManager();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== LIGHTING COMPONENTS =====
	
	/** Main directional light (sun) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Components")
	ADirectionalLight* SunLight;
	
	/** Sky light for ambient lighting */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Components")
	ASkyLight* SkyLight;
	
	/** Additional point lights */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Components")
	TArray<APointLight*> PointLights;
	
	/** Additional spot lights */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Components")
	TArray<ASpotLight*> SpotLights;

	// ===== LIGHTING SETTINGS =====
	
	/** Enable dynamic lighting */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings")
	bool bEnableDynamicLighting = true;
	
	/** Enable day/night cycle */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings")
	bool bEnableDayNightCycle = true;
	
	/** Day duration in real minutes */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings", meta = (ClampMin = "1.0", ClampMax = "1440.0"))
	float DayDurationMinutes = 20.0f;
	
	/** Current time of day (0-24 hours) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings", meta = (ClampMin = "0.0", ClampMax = "24.0"))
	float CurrentTimeOfDay = 12.0f;
	
	/** Time speed multiplier */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings", meta = (ClampMin = "0.1", ClampMax = "10.0"))
	float TimeSpeedMultiplier = 1.0f;
	
	/** Enable atmospheric fog */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings")
	bool bEnableAtmosphericFog = true;
	
	/** Enable volumetric lighting */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings")
	bool bEnableVolumetricLighting = true;
	
	/** Lighting quality level (0-3) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Settings", meta = (ClampMin = "0", ClampMax = "3"))
	int32 LightingQualityLevel = 2;

	// ===== LIGHTING PRESETS =====
	
	/** Available lighting presets */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Presets")
	TArray<FLightingPreset> LightingPresets;
	
	/** Current lighting preset */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Lighting Presets")
	FString CurrentPresetName = TEXT("Default");

	// ===== LIGHTING FUNCTIONS =====
	
	/** Initialize dynamic lighting system */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void InitializeDynamicLightingSystem();
	
	/** Setup lighting components */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void SetupLightingComponents();
	
	/** Update lighting based on time of day */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void UpdateLightingForTimeOfDay(float TimeOfDay);
	
	/** Set time of day */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void SetTimeOfDay(float TimeOfDay);
	
	/** Get current time of day */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Dynamic Lighting")
	float GetCurrentTimeOfDay() const;
	
	/** Advance time by amount */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void AdvanceTime(float TimeAmount);
	
	/** Pause/resume time */
	UFUNCTION(BlueprintCallable, Category = "Dynamic Lighting")
	void SetTimePaused(bool bPaused);
	
	/** Check if time is paused */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Dynamic Lighting")
	bool IsTimePaused() const;

	// ===== LIGHTING PRESET FUNCTIONS =====
	
	/** Load lighting preset */
	UFUNCTION(BlueprintCallable, Category = "Lighting Presets")
	void LoadLightingPreset(const FString& PresetName);
	
	/** Save current lighting as preset */
	UFUNCTION(BlueprintCallable, Category = "Lighting Presets")
	void SaveCurrentLightingAsPreset(const FString& PresetName);
	
	/** Get available presets */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Lighting Presets")
	TArray<FString> GetAvailablePresets() const;
	
	/** Delete lighting preset */
	UFUNCTION(BlueprintCallable, Category = "Lighting Presets")
	bool DeleteLightingPreset(const FString& PresetName);
	
	/** Apply lighting preset data */
	UFUNCTION(BlueprintCallable, Category = "Lighting Presets")
	void ApplyLightingPresetData(const FLightingPreset& PresetData);

	// ===== SUN LIGHT FUNCTIONS =====
	
	/** Set sun direction */
	UFUNCTION(BlueprintCallable, Category = "Sun Light")
	void SetSunDirection(const FRotator& Direction);
	
	/** Set sun color */
	UFUNCTION(BlueprintCallable, Category = "Sun Light")
	void SetSunColor(const FLinearColor& Color);
	
	/** Set sun intensity */
	UFUNCTION(BlueprintCallable, Category = "Sun Light")
	void SetSunIntensity(float Intensity);
	
	/** Enable/disable sun light */
	UFUNCTION(BlueprintCallable, Category = "Sun Light")
	void SetSunLightEnabled(bool bEnabled);

	// ===== SKY LIGHT FUNCTIONS =====
	
	/** Set sky light color */
	UFUNCTION(BlueprintCallable, Category = "Sky Light")
	void SetSkyLightColor(const FLinearColor& Color);
	
	/** Set sky light intensity */
	UFUNCTION(BlueprintCallable, Category = "Sky Light")
	void SetSkyLightIntensity(float Intensity);
	
	/** Enable/disable sky light */
	UFUNCTION(BlueprintCallable, Category = "Sky Light")
	void SetSkyLightEnabled(bool bEnabled);

	// ===== FOG FUNCTIONS =====
	
	/** Set fog color */
	UFUNCTION(BlueprintCallable, Category = "Fog")
	void SetFogColor(const FLinearColor& Color);
	
	/** Set fog density */
	UFUNCTION(BlueprintCallable, Category = "Fog")
	void SetFogDensity(float Density);
	
	/** Set fog distance */
	UFUNCTION(BlueprintCallable, Category = "Fog")
	void SetFogDistance(float StartDistance, float EndDistance);
	
	/** Enable/disable fog */
	UFUNCTION(BlueprintCallable, Category = "Fog")
	void SetFogEnabled(bool bEnabled);

	// ===== PERFORMANCE FUNCTIONS =====
	
	/** Set lighting quality level */
	UFUNCTION(BlueprintCallable, Category = "Performance")
	void SetLightingQualityLevel(int32 QualityLevel);
	
	/** Get lighting quality level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetLightingQualityLevel() const;
	
	/** Optimize lighting for performance */
	UFUNCTION(BlueprintCallable, Category = "Performance")
	void OptimizeLightingForPerformance();
	
	/** Get lighting performance impact */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	float GetLightingPerformanceImpact() const;

	// ===== WEATHER FUNCTIONS =====
	
	/** Set weather conditions */
	UFUNCTION(BlueprintCallable, Category = "Weather")
	void SetWeatherConditions(const FString& WeatherType);
	
	/** Get current weather */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Weather")
	FString GetCurrentWeather() const;
	
	/** Enable weather effects */
	UFUNCTION(BlueprintCallable, Category = "Weather")
	void SetWeatherEffectsEnabled(bool bEnabled);

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Calculate sun direction from time of day */
	FRotator CalculateSunDirectionFromTime(float TimeOfDay) const;
	
	/** Calculate sun color from time of day */
	FLinearColor CalculateSunColorFromTime(float TimeOfDay) const;
	
	/** Calculate sky light color from time of day */
	FLinearColor CalculateSkyLightColorFromTime(float TimeOfDay) const;
	
	/** Update atmospheric fog */
	void UpdateAtmosphericFog(float TimeOfDay);
	
	/** Update volumetric lighting */
	void UpdateVolumetricLighting(float TimeOfDay);
	
	/** Setup default lighting presets */
	void SetupDefaultLightingPresets();
	
	/** Apply lighting quality settings */
	void ApplyLightingQualitySettings();

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Time pause state */
	UPROPERTY()
	bool bTimePaused = false;
	
	/** Current weather type */
	UPROPERTY()
	FString CurrentWeatherType = TEXT("Clear");
	
	/** Weather effects enabled */
	UPROPERTY()
	bool bWeatherEffectsEnabled = true;
	
	/** Lighting performance impact */
	UPROPERTY()
	float LightingPerformanceImpact = 0.0f;
	
	/** Last time update */
	UPROPERTY()
	float LastTimeUpdate = 0.0f;
};
