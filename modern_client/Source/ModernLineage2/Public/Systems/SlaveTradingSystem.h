#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/DataTable.h"
#include "SlaveTradingSystem.generated.h"

/**
 * Slave data structure
 */
USTRUCT(BlueprintType)
struct FSlaveData : public FTableRowBase
{
	GENERATED_BODY()

	/** Slave ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString SlaveID;
	
	/** Slave name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString SlaveName;
	
	/** Slave description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString Description;
	
	/** Slave type (Combat, Domestic, Decorative, Special) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString SlaveType;
	
	/** Slave race (Human, Elf, Dark Elf, Orc, Dwarf) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString Race;
	
	/** Slave gender */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	bool bIsFemale = false;
	
	/** Slave age */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	int32 Age = 18;
	
	/** Base stats */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseSTR = 10;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseDEX = 10;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseCON = 10;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseINT = 10;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseWIT = 10;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Stats")
	int32 BaseMEN = 10;
	
	/** Slave price */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Economy")
	int32 Price = 1000;
	
	/** Slave rarity (Common, Uncommon, Rare, Epic, Legendary) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString Rarity = TEXT("Common");
	
	/** Special abilities */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Abilities")
	TArray<FString> SpecialAbilities;
	
	/** Loyalty level (0-100) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Loyalty")
	int32 LoyaltyLevel = 50;
	
	/** Training level (0-100) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Training")
	int32 TrainingLevel = 0;
	
	/** Is slave available for purchase */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	bool bAvailableForPurchase = true;
	
	/** Required player level to purchase */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	int32 RequiredLevel = 1;
	
	/** Mesh asset reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Appearance")
	TSoftObjectPtr<USkeletalMesh> MeshAsset;
	
	/** Portrait texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Appearance")
	TSoftObjectPtr<UTexture2D> PortraitTexture;
};

/**
 * Slave market data structure
 */
USTRUCT(BlueprintType)
struct FSlaveMarketData
{
	GENERATED_BODY()

	/** Market name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	FString MarketName;
	
	/** Market location */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	FString Location;
	
	/** Available slaves */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	TArray<FString> AvailableSlaves;
	
	/** Market refresh time (hours) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	float RefreshTime = 24.0f;
	
	/** Last refresh time */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	FDateTime LastRefreshTime;
	
	/** Market reputation level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Market")
	int32 ReputationLevel = 1;
};

/**
 * Player's slave data structure
 */
USTRUCT(BlueprintType)
struct FPlayerSlaveData
{
	GENERATED_BODY()

	/** Slave ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FString SlaveID;
	
	/** Purchase date */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FDateTime PurchaseDate;
	
	/** Current loyalty level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	int32 CurrentLoyalty = 50;
	
	/** Current training level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	int32 CurrentTraining = 0;
	
	/** Is slave active */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	bool bIsActive = true;
	
	/** Assigned tasks */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	TArray<FString> AssignedTasks;
	
	/** Last interaction time */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave")
	FDateTime LastInteractionTime;
};

/**
 * Slave Trading System for anime fantasy slave market
 * Allows buying, selling, and managing slaves
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API USlaveTradingSystem : public UActorComponent
{
	GENERATED_BODY()

public:
	USlaveTradingSystem();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== SLAVE DATA =====
	
	/** Slave data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Slave Data")
	UDataTable* SlaveDataTable;
	
	/** Player's owned slaves */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Player Slaves")
	TArray<FPlayerSlaveData> OwnedSlaves;
	
	/** Available slaves in market */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Market")
	TArray<FString> AvailableSlaves;
	
	/** Slave markets */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Markets")
	TArray<FSlaveMarketData> SlaveMarkets;

	// ===== SYSTEM SETTINGS =====
	
	/** Enable slave trading system */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableSlaveTrading = true;
	
	/** Enable slave rebellions */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableRebellions = true;
	
	/** Enable slave training */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableSlaveTraining = true;
	
	/** Maximum slaves per player */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	int32 MaxSlavesPerPlayer = 10;

	// ===== ECONOMY SETTINGS =====
	
	/** Player's gold amount */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	int32 PlayerGold = 10000;
	
	/** Slave maintenance cost per day */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	int32 DailyMaintenanceCost = 100;
	
	/** Slave selling price multiplier */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	float SellingPriceMultiplier = 0.7f;

	// ===== SLAVE TRADING FUNCTIONS =====
	
	/** Initialize slave trading system */
	UFUNCTION(BlueprintCallable, Category = "Slave Trading")
	void InitializeSlaveTradingSystem();
	
	/** Load slave data from table */
	UFUNCTION(BlueprintCallable, Category = "Slave Data")
	void LoadSlaveData();
	
	/** Purchase slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Trading")
	bool PurchaseSlave(const FString& SlaveID);
	
	/** Sell slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Trading")
	bool SellSlave(const FString& SlaveID);
	
	/** Get slave data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Slave Data")
	FSlaveData GetSlaveData(const FString& SlaveID) const;
	
	/** Get player's slave data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Player Slaves")
	FPlayerSlaveData GetPlayerSlaveData(const FString& SlaveID) const;
	
	/** Check if player owns slave */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Player Slaves")
	bool OwnsSlave(const FString& SlaveID) const;
	
	/** Get available slaves in market */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	TArray<FString> GetAvailableSlaves() const;
	
	/** Get slaves by type */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	TArray<FString> GetSlavesByType(const FString& SlaveType) const;
	
	/** Get slaves by race */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	TArray<FString> GetSlavesByRace(const FString& Race) const;
	
	/** Get slaves by rarity */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	TArray<FString> GetSlavesByRarity(const FString& Rarity) const;

	// ===== SLAVE MANAGEMENT FUNCTIONS =====
	
	/** Assign task to slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Management")
	bool AssignTaskToSlave(const FString& SlaveID, const FString& TaskName);
	
	/** Remove task from slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Management")
	bool RemoveTaskFromSlave(const FString& SlaveID, const FString& TaskName);
	
	/** Train slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Management")
	bool TrainSlave(const FString& SlaveID, int32 TrainingAmount);
	
	/** Interact with slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Management")
	void InteractWithSlave(const FString& SlaveID);
	
	/** Free slave */
	UFUNCTION(BlueprintCallable, Category = "Slave Management")
	bool FreeSlave(const FString& SlaveID);
	
	/** Get slave loyalty level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Slave Management")
	int32 GetSlaveLoyalty(const FString& SlaveID) const;
	
	/** Get slave training level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Slave Management")
	int32 GetSlaveTraining(const FString& SlaveID) const;

	// ===== MARKET FUNCTIONS =====
	
	/** Refresh slave market */
	UFUNCTION(BlueprintCallable, Category = "Market")
	void RefreshSlaveMarket();
	
	/** Get market reputation */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	int32 GetMarketReputation(const FString& MarketName) const;
	
	/** Increase market reputation */
	UFUNCTION(BlueprintCallable, Category = "Market")
	void IncreaseMarketReputation(const FString& MarketName, int32 Amount);
	
	/** Get market refresh time remaining */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Market")
	float GetMarketRefreshTimeRemaining(const FString& MarketName) const;

	// ===== REBELLION FUNCTIONS =====
	
	/** Check for slave rebellions */
	UFUNCTION(BlueprintCallable, Category = "Rebellions")
	void CheckForRebellions();
	
	/** Suppress slave rebellion */
	UFUNCTION(BlueprintCallable, Category = "Rebellions")
	bool SuppressRebellion(const FString& SlaveID);
	
	/** Get rebellion risk level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Rebellions")
	float GetRebellionRiskLevel(const FString& SlaveID) const;

	// ===== ECONOMY FUNCTIONS =====
	
	/** Pay slave maintenance costs */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	void PayMaintenanceCosts();
	
	/** Get total maintenance cost */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Economy")
	int32 GetTotalMaintenanceCost() const;
	
	/** Add gold to player */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	void AddGold(int32 Amount);
	
	/** Remove gold from player */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	bool RemoveGold(int32 Amount);
	
	/** Get player gold amount */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Economy")
	int32 GetPlayerGold() const;

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default slave markets */
	void SetupDefaultSlaveMarkets();
	
	/** Update slave loyalty over time */
	void UpdateSlaveLoyalty(float DeltaTime);
	
	/** Calculate rebellion risk */
	float CalculateRebellionRisk(const FString& SlaveID) const;
	
	/** Process slave training */
	void ProcessSlaveTraining(float DeltaTime);
	
	/** Save slave data */
	void SaveSlaveData();
	
	/** Load slave data */
	void LoadSlaveData();

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Slave data cache */
	UPROPERTY()
	TMap<FString, FSlaveData> SlaveDataCache;
	
	/** Last maintenance payment time */
	UPROPERTY()
	FDateTime LastMaintenancePayment;
	
	/** Rebellion check timer */
	UPROPERTY()
	float RebellionCheckTimer = 0.0f;
	
	/** Training process timer */
	UPROPERTY()
	float TrainingProcessTimer = 0.0f;
};
