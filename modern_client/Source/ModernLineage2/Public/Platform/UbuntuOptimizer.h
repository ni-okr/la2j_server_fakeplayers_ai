#pragma once

#include "CoreMinimal.h"
#include "Components/ActorComponent.h"
#include "Engine/World.h"
#include "UbuntuOptimizer.generated.h"

USTRUCT(BlueprintType)
struct FSystemSpecs
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    FString CPUModel;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    int32 CPUCores;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    float CPUFrequency;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    FString GPUModel;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    int32 GPUMemory;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    int32 SystemRAM;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    FString KernelVersion;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    FString DistributionName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "System Specs")
    FString DistributionVersion;
};

USTRUCT(BlueprintType)
struct FOptimizationSettings
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    int32 GraphicsQuality; // 0-3 (Low, Medium, High, Ultra)

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    int32 TextureQuality; // 0-3

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    int32 ShadowQuality; // 0-3

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    int32 EffectsQuality; // 0-3

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    bool bVSyncEnabled;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    int32 TargetFPS;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    bool bFullscreen;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    FIntPoint Resolution;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    float RenderScale;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimization")
    bool bMultithreadingEnabled;
};

USTRUCT(BlueprintType)
struct FPerformanceMetrics
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float AverageFPS;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float MinFPS;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float MaxFPS;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float FrameTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float CPUUsage;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float GPUUsage;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float MemoryUsage;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Performance")
    float NetworkLatency;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UUbuntuOptimizer : public UActorComponent
{
    GENERATED_BODY()

public:
    UUbuntuOptimizer();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Optimization Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimizer Settings")
    bool bEnableOptimization = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimizer Settings")
    bool bAutoOptimize = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimizer Settings")
    float OptimizationInterval = 60.0f; // seconds

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimizer Settings")
    float TargetFPS = 60.0f;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Optimizer Settings")
    bool bAdaptiveQuality = true;

    // System Information
    UPROPERTY(BlueprintReadWrite, Category = "System Information")
    FSystemSpecs SystemSpecs;

    UPROPERTY(BlueprintReadWrite, Category = "Optimization Settings")
    FOptimizationSettings OptimizationSettings;

    UPROPERTY(BlueprintReadWrite, Category = "Performance Metrics")
    FPerformanceMetrics PerformanceMetrics;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void InitializeOptimizer();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void DetectSystemSpecs();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeForUbuntu();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void ApplyOptimalSettings();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeGraphicsSettings();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeNetworkSettings();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeMemoryUsage();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeCPUUsage();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void UpdatePerformanceMetrics();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void AdaptQualitySettings();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    bool IsPerformanceAcceptable() const;

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void ConfigureLinuxSpecificSettings();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeForNVIDIA();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeForAMD();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void OptimizeForIntel();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void SetupLinuxAudio();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void ConfigureInputDevices();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void SaveOptimizationProfile();

    UFUNCTION(BlueprintCallable, Category = "Ubuntu Optimizer")
    void LoadOptimizationProfile();

    // System Utilities
    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    FString ExecuteLinuxCommand(const FString& Command);

    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    bool CheckPackageInstalled(const FString& PackageName);

    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    void InstallRequiredPackages();

    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    void ConfigureSystemServices();

    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    void SetupGameModeOptimizations();

    UFUNCTION(BlueprintCallable, Category = "System Utilities")
    void ConfigureFirewall();

    // Performance Testing
    UFUNCTION(BlueprintCallable, Category = "Performance Testing")
    void RunPerformanceBenchmark();

    UFUNCTION(BlueprintCallable, Category = "Performance Testing")
    void TestGraphicsPerformance();

    UFUNCTION(BlueprintCallable, Category = "Performance Testing")
    void TestNetworkPerformance();

    UFUNCTION(BlueprintCallable, Category = "Performance Testing")
    void GeneratePerformanceReport();

protected:
    void UpdateOptimization(float DeltaTime);
    void MonitorSystemPerformance();
    void AdjustSettingsBasedOnPerformance();
    void NotifyOptimizationChange(const FString& SettingName, const FString& OldValue, const FString& NewValue);

private:
    float LastOptimizationTime;
    float LastPerformanceCheck;
    TArray<float> FPSHistory;
    TArray<float> FrameTimeHistory;
    bool bOptimizationInProgress;
    FString CurrentGPUDriver;
    FString CurrentAudioSystem;
};
