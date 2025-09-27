#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Particles/ParticleSystem.h"
#include "Particles/ParticleSystemComponent.h"
#include "Niagara/Public/NiagaraComponent.h"
#include "Niagara/Public/NiagaraSystem.h"
#include "ParticleEffectManager.generated.h"

/**
 * Particle Effect Data Structure
 */
USTRUCT(BlueprintType)
struct FParticleEffectData : public FTableRowBase
{
	GENERATED_BODY()

	/** Effect ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	FString EffectID;
	
	/** Effect name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	FString EffectName;
	
	/** Effect description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	FString Description;
	
	/** Effect type (Magic, Combat, Environmental, UI) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	FString EffectType;
	
	/** Particle system reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	TSoftObjectPtr<UParticleSystem> ParticleSystem;
	
	/** Niagara system reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	TSoftObjectPtr<UNiagaraSystem> NiagaraSystem;
	
	/** Effect duration (seconds) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "60.0"))
	float Duration = 5.0f;
	
	/** Effect scale */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "10.0"))
	float Scale = 1.0f;
	
	/** Effect intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "5.0"))
	float Intensity = 1.0f;
	
	/** Effect color tint */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	FLinearColor ColorTint = FLinearColor::White;
	
	/** Effect speed multiplier */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "5.0"))
	float SpeedMultiplier = 1.0f;
	
	/** Effect size multiplier */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "5.0"))
	float SizeMultiplier = 1.0f;
	
	/** Effect lifetime multiplier */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "5.0"))
	float LifetimeMultiplier = 1.0f;
	
	/** Effect emission rate multiplier */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0.1", ClampMax = "5.0"))
	float EmissionRateMultiplier = 1.0f;
	
	/** Effect priority (higher = more important) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0", ClampMax = "100"))
	int32 Priority = 50;
	
	/** Effect quality level required */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "0", ClampMax = "3"))
	int32 RequiredQualityLevel = 0;
	
	/** Effect culling distance */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect", meta = (ClampMin = "100.0", ClampMax = "10000.0"))
	float CullingDistance = 5000.0f;
	
	/** Effect batching enabled */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	bool bBatchingEnabled = true;
	
	/** Effect LOD enabled */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Effect")
	bool bLODEnabled = true;
};

/**
 * Active Particle Effect Structure
 */
USTRUCT(BlueprintType)
struct FActiveParticleEffect
{
	GENERATED_BODY()

	/** Effect ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	FString EffectID;
	
	/** Effect component */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	UParticleSystemComponent* ParticleComponent;
	
	/** Niagara component */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	UNiagaraComponent* NiagaraComponent;
	
	/** Effect start time */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	float StartTime = 0.0f;
	
	/** Effect duration */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	float Duration = 5.0f;
	
	/** Effect owner */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	AActor* Owner = nullptr;
	
	/** Effect location */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	FVector Location = FVector::ZeroVector;
	
	/** Effect rotation */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	FRotator Rotation = FRotator::ZeroRotator;
	
	/** Effect scale */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	FVector Scale = FVector::OneVector;
	
	/** Effect parameters */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	TMap<FString, float> Parameters;
	
	/** Effect is active */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Active Effect")
	bool bIsActive = true;
};

/**
 * Particle Effect Manager for Modern Lineage II
 * Handles particle effects, VFX, and visual effects
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UParticleEffectManager : public UActorComponent
{
	GENERATED_BODY()

public:
	UParticleEffectManager();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== PARTICLE EFFECT DATA =====
	
	/** Particle effect data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Particle Data")
	UDataTable* ParticleEffectDataTable;
	
	/** Particle effect data cache */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Data")
	TMap<FString, FParticleEffectData> EffectDataCache;
	
	/** Active particle effects */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Data")
	TArray<FActiveParticleEffect> ActiveEffects;

	// ===== PARTICLE SETTINGS =====
	
	/** Enable particle effects */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings")
	bool bEnableParticleEffects = true;
	
	/** Particle quality level (0-3) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings", meta = (ClampMin = "0", ClampMax = "3"))
	int32 ParticleQualityLevel = 2;
	
	/** Maximum active effects */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings", meta = (ClampMin = "10", ClampMax = "1000"))
	int32 MaxActiveEffects = 100;
	
	/** Effect culling distance */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings", meta = (ClampMin = "100.0", ClampMax = "10000.0"))
	float GlobalCullingDistance = 5000.0f;
	
	/** Effect LOD bias */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings", meta = (ClampMin = "-2", ClampMax = "2"))
	int32 EffectLODBias = 0;
	
	/** Effect batching enabled */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Particle Settings")
	bool bEffectBatchingEnabled = true;

	// ===== PARTICLE FUNCTIONS =====
	
	/** Initialize particle effect system */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	void InitializeParticleEffectSystem();
	
	/** Load particle effect data from table */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	void LoadParticleEffectData();
	
	/** Spawn particle effect */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	int32 SpawnParticleEffect(const FString& EffectID, const FVector& Location, const FRotator& Rotation = FRotator::ZeroRotator, const FVector& Scale = FVector::OneVector, AActor* Owner = nullptr);
	
	/** Spawn particle effect with parameters */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	int32 SpawnParticleEffectWithParameters(const FString& EffectID, const FVector& Location, const FRotator& Rotation, const FVector& Scale, const TMap<FString, float>& Parameters, AActor* Owner = nullptr);
	
	/** Stop particle effect */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	bool StopParticleEffect(int32 EffectHandle);
	
	/** Stop all particle effects */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	void StopAllParticleEffects();
	
	/** Stop particle effects by type */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	void StopParticleEffectsByType(const FString& EffectType);
	
	/** Stop particle effects by owner */
	UFUNCTION(BlueprintCallable, Category = "Particle Effects")
	void StopParticleEffectsByOwner(AActor* Owner);
	
	/** Get particle effect data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Effects")
	FParticleEffectData GetParticleEffectData(const FString& EffectID) const;
	
	/** Get active particle effect */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Effects")
	FActiveParticleEffect GetActiveParticleEffect(int32 EffectHandle) const;
	
	/** Check if particle effect is active */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Effects")
	bool IsParticleEffectActive(int32 EffectHandle) const;
	
	/** Get active particle effect count */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Effects")
	int32 GetActiveParticleEffectCount() const;

	// ===== PARTICLE PARAMETER FUNCTIONS =====
	
	/** Set particle effect parameter */
	UFUNCTION(BlueprintCallable, Category = "Particle Parameters")
	bool SetParticleEffectParameter(int32 EffectHandle, const FString& ParameterName, float Value);
	
	/** Get particle effect parameter */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Parameters")
	float GetParticleEffectParameter(int32 EffectHandle, const FString& ParameterName) const;
	
	/** Set particle effect color */
	UFUNCTION(BlueprintCallable, Category = "Particle Parameters")
	bool SetParticleEffectColor(int32 EffectHandle, const FLinearColor& Color);
	
	/** Set particle effect scale */
	UFUNCTION(BlueprintCallable, Category = "Particle Parameters")
	bool SetParticleEffectScale(int32 EffectHandle, const FVector& Scale);
	
	/** Set particle effect speed */
	UFUNCTION(BlueprintCallable, Category = "Particle Parameters")
	bool SetParticleEffectSpeed(int32 EffectHandle, float Speed);

	// ===== PARTICLE QUALITY FUNCTIONS =====
	
	/** Set particle quality level */
	UFUNCTION(BlueprintCallable, Category = "Particle Quality")
	void SetParticleQualityLevel(int32 QualityLevel);
	
	/** Get particle quality level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Quality")
	int32 GetParticleQualityLevel() const;
	
	/** Update particle quality settings */
	UFUNCTION(BlueprintCallable, Category = "Particle Quality")
	void UpdateParticleQualitySettings();
	
	/** Optimize particles for performance */
	UFUNCTION(BlueprintCallable, Category = "Particle Quality")
	void OptimizeParticlesForPerformance();

	// ===== PARTICLE CULLING FUNCTIONS =====
	
	/** Set particle culling distance */
	UFUNCTION(BlueprintCallable, Category = "Particle Culling")
	void SetParticleCullingDistance(float Distance);
	
	/** Get particle culling distance */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Culling")
	float GetParticleCullingDistance() const;
	
	/** Update particle culling */
	UFUNCTION(BlueprintCallable, Category = "Particle Culling")
	void UpdateParticleCulling();
	
	/** Enable/disable particle culling */
	UFUNCTION(BlueprintCallable, Category = "Particle Culling")
	void SetParticleCullingEnabled(bool bEnabled);

	// ===== PARTICLE BATCHING FUNCTIONS =====
	
	/** Enable/disable particle batching */
	UFUNCTION(BlueprintCallable, Category = "Particle Batching")
	void SetParticleBatchingEnabled(bool bEnabled);
	
	/** Get particle batching enabled */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Batching")
	bool IsParticleBatchingEnabled() const;
	
	/** Update particle batching */
	UFUNCTION(BlueprintCallable, Category = "Particle Batching")
	void UpdateParticleBatching();

	// ===== PARTICLE PERFORMANCE FUNCTIONS =====
	
	/** Get particle performance impact */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Performance")
	float GetParticlePerformanceImpact() const;
	
	/** Get particle memory usage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Particle Performance")
	int32 GetParticleMemoryUsage() const;
	
	/** Clear particle cache */
	UFUNCTION(BlueprintCallable, Category = "Particle Performance")
	void ClearParticleCache();

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default particle effects */
	void SetupDefaultParticleEffects();
	
	/** Create particle effect component */
	UParticleSystemComponent* CreateParticleEffectComponent(const FParticleEffectData& EffectData);
	
	/** Create Niagara effect component */
	UNiagaraComponent* CreateNiagaraEffectComponent(const FParticleEffectData& EffectData);
	
	/** Update active particle effects */
	void UpdateActiveParticleEffects(float DeltaTime);
	
	/** Clean up inactive effects */
	void CleanupInactiveEffects();
	
	/** Apply quality settings to effect */
	void ApplyQualitySettingsToEffect(FActiveParticleEffect& Effect);
	
	/** Check effect culling */
	bool CheckEffectCulling(const FActiveParticleEffect& Effect) const;

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Next effect handle */
	UPROPERTY()
	int32 NextEffectHandle = 1;
	
	/** Particle culling enabled */
	UPROPERTY()
	bool bParticleCullingEnabled = true;
	
	/** Particle performance impact */
	UPROPERTY()
	float ParticlePerformanceImpact = 0.0f;
	
	/** Particle memory usage */
	UPROPERTY()
	int32 ParticleMemoryUsage = 0;
	
	/** Last culling update time */
	UPROPERTY()
	float LastCullingUpdateTime = 0.0f;
};
