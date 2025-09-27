#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "PBRMaterialManager.h"
#include "HDRManager.h"
#include "DynamicLightingManager.h"
#include "ParticleEffectManager.h"
#include "GraphicsManager.generated.h"

/**
 * Graphics Settings Structure
 */
USTRUCT(BlueprintType)
struct FGraphicsSettings
{
	GENERATED_BODY()

	/** Overall graphics quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality")
	int32 OverallQuality = 2;
	
	/** Resolution scale (0.5-2.0) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0.5", ClampMax = "2.0"))
	float ResolutionScale = 1.0f;
	
	/** Anti-aliasing quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 AntiAliasingQuality = 2;
	
	/** Shadow quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 ShadowQuality = 2;
	
	/** Texture quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 TextureQuality = 2;
	
	/** Effect quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 EffectQuality = 2;
	
	/** Post-processing quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 PostProcessingQuality = 2;
	
	/** Foliage quality (0-3) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "3"))
	int32 FoliageQuality = 2;
	
	/** View distance scale (0.1-2.0) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0.1", ClampMax = "2.0"))
	float ViewDistanceScale = 1.0f;
	
	/** FPS limit (0 = unlimited) */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality", meta = (ClampMin = "0", ClampMax = "300"))
	int32 FPSLimit = 0;
	
	/** VSync enabled */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality")
	bool bVSyncEnabled = true;
	
	/** Fullscreen mode */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality")
	bool bFullscreenMode = true;
	
	/** Windowed fullscreen */
	UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Graphics Quality")
	bool bWindowedFullscreen = false;
};

/**
 * Graphics Preset Structure
 */
USTRUCT(BlueprintType)
struct FGraphicsPreset
{
	GENERATED_BODY()

	/** Preset name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	FString PresetName;
	
	/** Preset description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	FString Description;
	
	/** Graphics settings */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	FGraphicsSettings Settings;
	
	/** Target FPS */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	int32 TargetFPS = 60;
	
	/** Recommended for low-end systems */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	bool bRecommendedForLowEnd = false;
	
	/** Recommended for high-end systems */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Graphics Preset")
	bool bRecommendedForHighEnd = false;
};

/**
 * Graphics Manager for Modern Lineage II
 * Central manager for all graphics systems
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UGraphicsManager : public UActorComponent
{
	GENERATED_BODY()

public:
	UGraphicsManager();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== GRAPHICS COMPONENTS =====
	
	/** PBR Material Manager */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Components")
	UPBRMaterialManager* PBRMaterialManager;
	
	/** HDR Manager */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Components")
	UHDRManager* HDRManager;
	
	/** Dynamic Lighting Manager */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Components")
	UDynamicLightingManager* DynamicLightingManager;
	
	/** Particle Effect Manager */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Components")
	UParticleEffectManager* ParticleEffectManager;

	// ===== GRAPHICS SETTINGS =====
	
	/** Current graphics settings */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Settings")
	FGraphicsSettings CurrentGraphicsSettings;
	
	/** Available graphics presets */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Presets")
	TArray<FGraphicsPreset> GraphicsPresets;
	
	/** Current graphics preset */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Graphics Presets")
	FString CurrentPresetName = TEXT("High");

	// ===== GRAPHICS FUNCTIONS =====
	
	/** Initialize graphics system */
	UFUNCTION(BlueprintCallable, Category = "Graphics")
	void InitializeGraphicsSystem();
	
	/** Setup graphics components */
	UFUNCTION(BlueprintCallable, Category = "Graphics")
	void SetupGraphicsComponents();
	
	/** Apply graphics settings */
	UFUNCTION(BlueprintCallable, Category = "Graphics")
	void ApplyGraphicsSettings(const FGraphicsSettings& Settings);
	
	/** Get current graphics settings */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Graphics")
	FGraphicsSettings GetCurrentGraphicsSettings() const;
	
	/** Set graphics quality level */
	UFUNCTION(BlueprintCallable, Category = "Graphics")
	void SetGraphicsQualityLevel(int32 QualityLevel);
	
	/** Get graphics quality level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Graphics")
	int32 GetGraphicsQualityLevel() const;

	// ===== GRAPHICS PRESET FUNCTIONS =====
	
	/** Load graphics preset */
	UFUNCTION(BlueprintCallable, Category = "Graphics Presets")
	void LoadGraphicsPreset(const FString& PresetName);
	
	/** Save current graphics as preset */
	UFUNCTION(BlueprintCallable, Category = "Graphics Presets")
	void SaveCurrentGraphicsAsPreset(const FString& PresetName);
	
	/** Get available presets */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Graphics Presets")
	TArray<FString> GetAvailablePresets() const;
	
	/** Delete graphics preset */
	UFUNCTION(BlueprintCallable, Category = "Graphics Presets")
	bool DeleteGraphicsPreset(const FString& PresetName);
	
	/** Get preset for system specs */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Graphics Presets")
	FString GetRecommendedPreset() const;

	// ===== RESOLUTION FUNCTIONS =====
	
	/** Set resolution */
	UFUNCTION(BlueprintCallable, Category = "Resolution")
	void SetResolution(int32 Width, int32 Height);
	
	/** Get current resolution */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Resolution")
	FIntPoint GetCurrentResolution() const;
	
	/** Get available resolutions */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Resolution")
	TArray<FIntPoint> GetAvailableResolutions() const;
	
	/** Set resolution scale */
	UFUNCTION(BlueprintCallable, Category = "Resolution")
	void SetResolutionScale(float Scale);
	
	/** Get resolution scale */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Resolution")
	float GetResolutionScale() const;

	// ===== DISPLAY FUNCTIONS =====
	
	/** Set fullscreen mode */
	UFUNCTION(BlueprintCallable, Category = "Display")
	void SetFullscreenMode(bool bFullscreen);
	
	/** Get fullscreen mode */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Display")
	bool IsFullscreenMode() const;
	
	/** Set windowed fullscreen */
	UFUNCTION(BlueprintCallable, Category = "Display")
	void SetWindowedFullscreen(bool bWindowedFullscreen);
	
	/** Get windowed fullscreen */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Display")
	bool IsWindowedFullscreen() const;
	
	/** Set VSync */
	UFUNCTION(BlueprintCallable, Category = "Display")
	void SetVSync(bool bEnabled);
	
	/** Get VSync */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Display")
	bool IsVSyncEnabled() const;

	// ===== PERFORMANCE FUNCTIONS =====
	
	/** Get current FPS */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	float GetCurrentFPS() const;
	
	/** Get average FPS */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	float GetAverageFPS() const;
	
	/** Get frame time */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	float GetFrameTime() const;
	
	/** Get GPU memory usage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetGPUMemoryUsage() const;
	
	/** Get VRAM usage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetVRAMUsage() const;
	
	/** Get system memory usage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetSystemMemoryUsage() const;
	
	/** Set FPS limit */
	UFUNCTION(BlueprintCallable, Category = "Performance")
	void SetFPSLimit(int32 FPSLimit);
	
	/** Get FPS limit */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Performance")
	int32 GetFPSLimit() const;

	// ===== OPTIMIZATION FUNCTIONS =====
	
	/** Optimize graphics for performance */
	UFUNCTION(BlueprintCallable, Category = "Optimization")
	void OptimizeGraphicsForPerformance();
	
	/** Optimize graphics for quality */
	UFUNCTION(BlueprintCallable, Category = "Optimization")
	void OptimizeGraphicsForQuality();
	
	/** Auto-detect optimal settings */
	UFUNCTION(BlueprintCallable, Category = "Optimization")
	void AutoDetectOptimalSettings();
	
	/** Get performance impact */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Optimization")
	float GetPerformanceImpact() const;

	// ===== UBUNTU OPTIMIZATION FUNCTIONS =====
	
	/** Optimize for Ubuntu Linux */
	UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimization")
	void OptimizeForUbuntu();
	
	/** Set OpenGL settings */
	UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimization")
	void SetOpenGLSettings();
	
	/** Set Vulkan settings */
	UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimization")
	void SetVulkanSettings();
	
	/** Detect graphics driver */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Ubuntu Optimization")
	FString DetectGraphicsDriver() const;

	// ===== DEBUG FUNCTIONS =====
	
	/** Enable graphics debug mode */
	UFUNCTION(BlueprintCallable, Category = "Debug")
	void SetGraphicsDebugMode(bool bEnabled);
	
	/** Get graphics debug mode */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Debug")
	bool IsGraphicsDebugMode() const;
	
	/** Show graphics statistics */
	UFUNCTION(BlueprintCallable, Category = "Debug")
	void ShowGraphicsStatistics();
	
	/** Hide graphics statistics */
	UFUNCTION(BlueprintCallable, Category = "Debug")
	void HideGraphicsStatistics();

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default graphics presets */
	void SetupDefaultGraphicsPresets();
	
	/** Apply graphics quality settings */
	void ApplyGraphicsQualitySettings(int32 QualityLevel);
	
	/** Update graphics performance metrics */
	void UpdateGraphicsPerformanceMetrics(float DeltaTime);
	
	/** Detect system capabilities */
	void DetectSystemCapabilities();
	
	/** Setup Ubuntu-specific optimizations */
	void SetupUbuntuOptimizations();

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Graphics debug mode */
	UPROPERTY()
	bool bGraphicsDebugMode = false;
	
	/** Current FPS */
	UPROPERTY()
	float CurrentFPS = 0.0f;
	
	/** Average FPS */
	UPROPERTY()
	float AverageFPS = 0.0f;
	
	/** Frame time */
	UPROPERTY()
	float FrameTime = 0.0f;
	
	/** GPU memory usage */
	UPROPERTY()
	int32 GPUMemoryUsage = 0;
	
	/** VRAM usage */
	UPROPERTY()
	int32 VRAMUsage = 0;
	
	/** System memory usage */
	UPROPERTY()
	int32 SystemMemoryUsage = 0;
	
	/** Performance impact */
	UPROPERTY()
	float PerformanceImpact = 0.0f;
	
	/** FPS history */
	UPROPERTY()
	TArray<float> FPSHistory;
	
	/** Last performance update time */
	UPROPERTY()
	float LastPerformanceUpdateTime = 0.0f;
};
