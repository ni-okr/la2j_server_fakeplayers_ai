#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/DataTable.h"
#include "CostumeSystem.generated.h"

/**
 * Costume piece data structure
 */
USTRUCT(BlueprintType)
struct FCostumePieceData : public FTableRowBase
{
	GENERATED_BODY()

	/** Costume piece ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString CostumeID;
	
	/** Costume piece name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString CostumeName;
	
	/** Costume piece description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString Description;
	
	/** Slot name (Upper, Lower, Shoes, Accessories, etc.) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString SlotName;
	
	/** Costume category */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString Category;
	
	/** Mesh asset reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	TSoftObjectPtr<USkeletalMesh> MeshAsset;
	
	/** Material asset references */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	TArray<TSoftObjectPtr<UMaterialInterface>> MaterialAssets;
	
	/** Costume rarity (Common, Uncommon, Rare, Epic, Legendary) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString Rarity = TEXT("Common");
	
	/** Costume price */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	int32 Price = 0;
	
	/** Is costume piece available for purchase */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	bool bAvailableForPurchase = true;
	
	/** Required level to equip */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	int32 RequiredLevel = 1;
	
	/** Gender restriction (None, Male, Female) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume")
	FString GenderRestriction = TEXT("None");
};

/**
 * Costume slot data structure
 */
USTRUCT(BlueprintType)
struct FCostumeSlotData
{
	GENERATED_BODY()

	/** Slot name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slot")
	FString SlotName;
	
	/** Currently equipped costume piece ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slot")
	FString EquippedCostumeID;
	
	/** Is slot visible */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slot")
	bool bVisible = true;
	
	/** Slot priority for layering */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slot")
	int32 Priority = 0;
};

/**
 * Costume System for BnS-style wardrobe management
 * Allows mixing and matching costume pieces from different sets
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UCostumeSystem : public UActorComponent
{
	GENERATED_BODY()

public:
	UCostumeSystem();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== COSTUME DATA =====
	
	/** Costume pieces data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Costume Data")
	UDataTable* CostumeDataTable;
	
	/** Current costume slots */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Costume Slots")
	TMap<FString, FCostumeSlotData> CostumeSlots;
	
	/** Available costume pieces (owned by player) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Costume Inventory")
	TArray<FString> OwnedCostumePieces;

	// ===== COSTUME SETTINGS =====
	
	/** Enable costume system */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableCostumeSystem = true;
	
	/** Enable 3D preview */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnable3DPreview = true;
	
	/** Enable drag and drop in UI */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableDragAndDrop = true;
	
	/** Auto-save costume changes */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bAutoSaveChanges = true;

	// ===== COSTUME FUNCTIONS =====
	
	/** Initialize costume system */
	UFUNCTION(BlueprintCallable, Category = "Costume System")
	void InitializeCostumeSystem();
	
	/** Load costume data from table */
	UFUNCTION(BlueprintCallable, Category = "Costume Data")
	void LoadCostumeData();
	
	/** Equip costume piece */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	bool EquipCostumePiece(const FString& SlotName, const FString& CostumeID);
	
	/** Unequip costume piece */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	bool UnequipCostumePiece(const FString& SlotName);
	
	/** Get equipped costume piece */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	FString GetEquippedCostumePiece(const FString& SlotName) const;
	
	/** Get costume piece data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	FCostumePieceData GetCostumePieceData(const FString& CostumeID) const;
	
	/** Check if costume piece is owned */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	bool IsCostumePieceOwned(const FString& CostumeID) const;
	
	/** Purchase costume piece */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	bool PurchaseCostumePiece(const FString& CostumeID, int32 Price);
	
	/** Get available costume pieces for slot */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	TArray<FString> GetAvailableCostumePieces(const FString& SlotName) const;
	
	/** Get costume pieces by category */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	TArray<FString> GetCostumePiecesByCategory(const FString& Category) const;
	
	/** Save current costume configuration */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	void SaveCostumeConfiguration();
	
	/** Load costume configuration */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	void LoadCostumeConfiguration();
	
	/** Reset costume to default */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	void ResetToDefaultCostume();
	
	/** Get total costume value */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	int32 GetTotalCostumeValue() const;

	// ===== COSTUME COLLECTIONS =====
	
	/** Get costume collections */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Collections")
	TArray<FString> GetCostumeCollections() const;
	
	/** Get costume pieces in collection */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Collections")
	TArray<FString> GetCostumePiecesInCollection(const FString& CollectionName) const;
	
	/** Check if collection is complete */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Collections")
	bool IsCollectionComplete(const FString& CollectionName) const;
	
	/** Get collection completion percentage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Collections")
	float GetCollectionCompletionPercentage(const FString& CollectionName) const;

	// ===== COSTUME PREVIEW =====
	
	/** Show 3D costume preview */
	UFUNCTION(BlueprintCallable, Category = "Preview")
	void ShowCostumePreview(const FString& CostumeID);
	
	/** Hide costume preview */
	UFUNCTION(BlueprintCallable, Category = "Preview")
	void HideCostumePreview();
	
	/** Rotate preview model */
	UFUNCTION(BlueprintCallable, Category = "Preview")
	void RotatePreviewModel(float DeltaYaw, float DeltaPitch);
	
	/** Zoom preview model */
	UFUNCTION(BlueprintCallable, Category = "Preview")
	void ZoomPreviewModel(float DeltaZoom);

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default costume slots */
	void SetupDefaultCostumeSlots();
	
	/** Apply costume piece to character */
	void ApplyCostumePiece(const FString& SlotName, const FString& CostumeID);
	
	/** Remove costume piece from character */
	void RemoveCostumePiece(const FString& SlotName);
	
	/** Update costume appearance */
	void UpdateCostumeAppearance();
	
	/** Validate costume piece compatibility */
	bool ValidateCostumePiece(const FString& SlotName, const FString& CostumeID) const;
	
	/** Load costume piece assets */
	void LoadCostumePieceAssets(const FString& CostumeID);
	
	/** Unload costume piece assets */
	void UnloadCostumePieceAssets(const FString& CostumeID);

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Costume piece data cache */
	UPROPERTY()
	TMap<FString, FCostumePieceData> CostumePieceCache;
	
	/** Currently loaded costume pieces */
	UPROPERTY()
	TMap<FString, USkeletalMeshComponent*> LoadedCostumePieces;
	
	/** Preview model component */
	UPROPERTY()
	USkeletalMeshComponent* PreviewModelComponent;
	
	/** Costume configuration save data */
	UPROPERTY()
	TMap<FString, FString> SavedCostumeConfiguration;
};
