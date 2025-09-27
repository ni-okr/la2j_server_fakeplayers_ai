#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/DataTable.h"
#include "Materials/MaterialInterface.h"
#include "Materials/MaterialInstanceDynamic.h"
#include "PBRMaterialManager.generated.h"

/**
 * PBR Material Data Structure
 */
USTRUCT(BlueprintType)
struct FPBRMaterialData : public FTableRowBase
{
	GENERATED_BODY()

	/** Material ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "PBR Material")
	FString MaterialID;
	
	/** Material name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "PBR Material")
	FString MaterialName;
	
	/** Material type (Character, Environment, Weapon, Armor) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "PBR Material")
	FString MaterialType;
	
	/** Base material reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "PBR Material")
	TSoftObjectPtr<UMaterialInterface> BaseMaterial;
	
	/** Albedo texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> AlbedoTexture;
	
	/** Normal map texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> NormalTexture;
	
	/** Roughness texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> RoughnessTexture;
	
	/** Metallic texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> MetallicTexture;
	
	/** Ambient Occlusion texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> AOTexture;
	
	/** Emissive texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Textures")
	TSoftObjectPtr<UTexture2D> EmissiveTexture;
	
	/** Base color tint */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties")
	FLinearColor BaseColor = FLinearColor::White;
	
	/** Metallic value (0.0-1.0) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float Metallic = 0.0f;
	
	/** Roughness value (0.0-1.0) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float Roughness = 0.5f;
	
	/** Specular value (0.0-1.0) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float Specular = 0.5f;
	
	/** Ambient occlusion value (0.0-1.0) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float AmbientOcclusion = 1.0f;
	
	/** Emissive color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties")
	FLinearColor EmissiveColor = FLinearColor::Black;
	
	/** Emissive intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Properties")
	float EmissiveIntensity = 1.0f;
	
	/** Enable subsurface scattering */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced")
	bool bEnableSubsurfaceScattering = false;
	
	/** Subsurface color */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced")
	FLinearColor SubsurfaceColor = FLinearColor::Red;
	
	/** Subsurface intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced")
	float SubsurfaceIntensity = 1.0f;
	
	/** Enable clear coat */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced")
	bool bEnableClearCoat = false;
	
	/** Clear coat roughness */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float ClearCoatRoughness = 0.0f;
	
	/** Clear coat intensity */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Advanced", meta = (ClampMin = "0.0", ClampMax = "1.0"))
	float ClearCoatIntensity = 1.0f;
};

/**
 * PBR Material Manager for Modern Lineage II
 * Handles PBR material creation, loading, and management
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UPBRMaterialManager : public UActorComponent
{
	GENERATED_BODY()

public:
	UPBRMaterialManager();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== PBR MATERIAL DATA =====
	
	/** PBR material data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "PBR Data")
	UDataTable* PBRMaterialDataTable;
	
	/** Material data cache */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "PBR Data")
	TMap<FString, FPBRMaterialData> MaterialDataCache;
	
	/** Active material instances */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "PBR Data")
	TMap<FString, UMaterialInstanceDynamic*> ActiveMaterialInstances;

	// ===== PBR SETTINGS =====
	
	/** Enable PBR materials */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnablePBRMaterials = true;
	
	/** PBR quality level (0-3) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings", meta = (ClampMin = "0", ClampMax = "3"))
	int32 PBRQualityLevel = 2;
	
	/** Enable texture streaming */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableTextureStreaming = true;
	
	/** Texture resolution multiplier */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings", meta = (ClampMin = "0.5", ClampMax = "4.0"))
	float TextureResolutionMultiplier = 1.0f;

	// ===== PBR FUNCTIONS =====
	
	/** Initialize PBR material system */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void InitializePBRMaterialSystem();
	
	/** Load PBR material data from table */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void LoadPBRMaterialData();
	
	/** Create PBR material instance */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	UMaterialInstanceDynamic* CreatePBRMaterialInstance(const FString& MaterialID);
	
	/** Get PBR material data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "PBR Materials")
	FPBRMaterialData GetPBRMaterialData(const FString& MaterialID) const;
	
	/** Apply PBR material to mesh */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	bool ApplyPBRMaterialToMesh(UMeshComponent* MeshComponent, const FString& MaterialID, int32 MaterialSlot = 0);
	
	/** Update PBR material parameters */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void UpdatePBRMaterialParameters(const FString& MaterialID, const FPBRMaterialData& NewData);
	
	/** Get material instance by ID */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "PBR Materials")
	UMaterialInstanceDynamic* GetMaterialInstance(const FString& MaterialID) const;
	
	/** Load textures for material */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void LoadMaterialTextures(const FString& MaterialID);
	
	/** Unload material textures */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void UnloadMaterialTextures(const FString& MaterialID);
	
	/** Convert legacy L2 texture to PBR */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	bool ConvertLegacyL2Texture(const FString& LegacyTexturePath, const FString& MaterialID);
	
	/** Batch convert L2 textures */
	UFUNCTION(BlueprintCallable, Category = "PBR Materials")
	void BatchConvertL2Textures(const FString& SourceDirectory, const FString& TargetDirectory);

	// ===== MATERIAL QUALITY FUNCTIONS =====
	
	/** Set PBR quality level */
	UFUNCTION(BlueprintCallable, Category = "PBR Quality")
	void SetPBRQualityLevel(int32 QualityLevel);
	
	/** Get current PBR quality level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "PBR Quality")
	int32 GetPBRQualityLevel() const;
	
	/** Update material quality settings */
	UFUNCTION(BlueprintCallable, Category = "PBR Quality")
	void UpdateMaterialQualitySettings();
	
	/** Optimize materials for performance */
	UFUNCTION(BlueprintCallable, Category = "PBR Quality")
	void OptimizeMaterialsForPerformance();

	// ===== TEXTURE MANAGEMENT FUNCTIONS =====
	
	/** Load texture with quality settings */
	UFUNCTION(BlueprintCallable, Category = "Texture Management")
	UTexture2D* LoadTextureWithQuality(const FString& TexturePath, int32 QualityLevel = -1);
	
	/** Get texture memory usage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Texture Management")
	int32 GetTextureMemoryUsage() const;
	
	/** Clear texture cache */
	UFUNCTION(BlueprintCallable, Category = "Texture Management")
	void ClearTextureCache();
	
	/** Preload textures for material */
	UFUNCTION(BlueprintCallable, Category = "Texture Management")
	void PreloadMaterialTextures(const FString& MaterialID);

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default PBR materials */
	void SetupDefaultPBRMaterials();
	
	/** Create material instance from data */
	UMaterialInstanceDynamic* CreateMaterialInstanceFromData(const FPBRMaterialData& MaterialData);
	
	/** Apply material data to instance */
	void ApplyMaterialDataToInstance(UMaterialInstanceDynamic* Instance, const FPBRMaterialData& MaterialData);
	
	/** Load texture with proper settings */
	UTexture2D* LoadTextureInternal(const FString& TexturePath, bool bSRGB = true, bool bNormalMap = false);
	
	/** Convert texture format for PBR */
	UTexture2D* ConvertTextureForPBR(UTexture2D* SourceTexture, const FString& TargetFormat);
	
	/** Setup texture streaming */
	void SetupTextureStreaming(UTexture2D* Texture, int32 QualityLevel);

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Loaded textures cache */
	UPROPERTY()
	TMap<FString, UTexture2D*> LoadedTextures;
	
	/** Material quality settings */
	UPROPERTY()
	TMap<int32, FString> QualitySettings;
	
	/** Texture memory usage tracker */
	UPROPERTY()
	int32 TotalTextureMemoryUsage = 0;
	
	/** Material conversion queue */
	UPROPERTY()
	TArray<FString> MaterialConversionQueue;
};
