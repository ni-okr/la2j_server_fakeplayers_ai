#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "Engine/DataTable.h"
#include "AdventurerGuildSystem.generated.h"

/**
 * Adventurer companion data structure
 */
USTRUCT(BlueprintType)
struct FAdventurerData : public FTableRowBase
{
	GENERATED_BODY()

	/** Adventurer ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString AdventurerID;
	
	/** Adventurer name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString AdventurerName;
	
	/** Adventurer description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString Description;
	
	/** Adventurer class (Warrior, Mage, Archer, Cleric, Rogue) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString AdventurerClass;
	
	/** Adventurer race */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString Race;
	
	/** Adventurer gender */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	bool bIsFemale = false;
	
	/** Adventurer age */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 Age = 25;
	
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
	
	/** Adventurer level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 Level = 1;
	
	/** Hiring cost per day */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Economy")
	int32 DailyHiringCost = 500;
	
	/** Adventurer rarity (Common, Uncommon, Rare, Epic, Legendary) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString Rarity = TEXT("Common");
	
	/** Special abilities */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Abilities")
	TArray<FString> SpecialAbilities;
	
	/** Personality traits */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Personality")
	TArray<FString> PersonalityTraits;
	
	/** Combat specialization */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Combat")
	FString CombatSpecialization;
	
	/** Support specialization */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Support")
	FString SupportSpecialization;
	
	/** Relationship level with player (0-100) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Relationship")
	int32 RelationshipLevel = 0;
	
	/** Trust level (0-100) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Relationship")
	int32 TrustLevel = 0;
	
	/** Is adventurer available for hire */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Availability")
	bool bAvailableForHire = true;
	
	/** Required player level to hire */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Availability")
	int32 RequiredLevel = 1;
	
	/** Mesh asset reference */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Appearance")
	TSoftObjectPtr<USkeletalMesh> MeshAsset;
	
	/** Portrait texture */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Appearance")
	TSoftObjectPtr<UTexture2D> PortraitTexture;
};

/**
 * Player's hired adventurer data structure
 */
USTRUCT(BlueprintType)
struct FPlayerAdventurerData
{
	GENERATED_BODY()

	/** Adventurer ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FString AdventurerID;
	
	/** Hire date */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FDateTime HireDate;
	
	/** Contract duration (days) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 ContractDuration = 7;
	
	/** Current relationship level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 CurrentRelationship = 0;
	
	/** Current trust level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 CurrentTrust = 0;
	
	/** Is adventurer active */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	bool bIsActive = true;
	
	/** Assigned tasks */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	TArray<FString> AssignedTasks;
	
	/** Last interaction time */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	FDateTime LastInteractionTime;
	
	/** Experience gained together */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 SharedExperience = 0;
	
	/** Battles fought together */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer")
	int32 BattlesFought = 0;
};

/**
 * Guild quest data structure
 */
USTRUCT(BlueprintType)
struct FGuildQuestData : public FTableRowBase
{
	GENERATED_BODY()

	/** Quest ID */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	FString QuestID;
	
	/** Quest name */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	FString QuestName;
	
	/** Quest description */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	FString Description;
	
	/** Quest type (Combat, Exploration, Gathering, Escort, Rescue) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	FString QuestType;
	
	/** Quest difficulty (Easy, Normal, Hard, Expert, Master) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	FString Difficulty;
	
	/** Required level */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	int32 RequiredLevel = 1;
	
	/** Required adventurer count */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	int32 RequiredAdventurerCount = 1;
	
	/** Quest rewards */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	int32 GoldReward = 1000;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	int32 ExperienceReward = 500;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	TArray<FString> ItemRewards;
	
	/** Quest duration (hours) */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	float QuestDuration = 1.0f;
	
	/** Is quest available */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest")
	bool bIsAvailable = true;
};

/**
 * Adventurer Guild System for hiring companions
 * Allows hiring, managing, and questing with adventurer companions
 */
UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UAdventurerGuildSystem : public UActorComponent
{
	GENERATED_BODY()

public:
	UAdventurerGuildSystem();

protected:
	virtual void BeginPlay() override;

public:
	virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

	// ===== ADVENTURER DATA =====
	
	/** Adventurer data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Adventurer Data")
	UDataTable* AdventurerDataTable;
	
	/** Guild quest data table */
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Quest Data")
	UDataTable* QuestDataTable;
	
	/** Player's hired adventurers */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Player Adventurers")
	TArray<FPlayerAdventurerData> HiredAdventurers;
	
	/** Available adventurers for hire */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Available Adventurers")
	TArray<FString> AvailableAdventurers;
	
	/** Available guild quests */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Guild Quests")
	TArray<FString> AvailableQuests;

	// ===== SYSTEM SETTINGS =====
	
	/** Enable adventurer guild system */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableAdventurerGuild = true;
	
	/** Enable relationship system */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableRelationshipSystem = true;
	
	/** Enable quest system */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	bool bEnableQuestSystem = true;
	
	/** Maximum adventurers per player */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Settings")
	int32 MaxAdventurersPerPlayer = 5;

	// ===== ECONOMY SETTINGS =====
	
	/** Player's gold amount */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	int32 PlayerGold = 10000;
	
	/** Daily hiring costs */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	int32 TotalDailyCosts = 0;
	
	/** Guild reputation level */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Economy")
	int32 GuildReputation = 0;

	// ===== ADVENTURER GUILD FUNCTIONS =====
	
	/** Initialize adventurer guild system */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Guild")
	void InitializeAdventurerGuildSystem();
	
	/** Load adventurer data from table */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Data")
	void LoadAdventurerData();
	
	/** Load quest data from table */
	UFUNCTION(BlueprintCallable, Category = "Quest Data")
	void LoadQuestData();
	
	/** Hire adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Guild")
	bool HireAdventurer(const FString& AdventurerID, int32 ContractDuration = 7);
	
	/** Release adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Guild")
	bool ReleaseAdventurer(const FString& AdventurerID);
	
	/** Get adventurer data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Adventurer Data")
	FAdventurerData GetAdventurerData(const FString& AdventurerID) const;
	
	/** Get player's adventurer data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Player Adventurers")
	FPlayerAdventurerData GetPlayerAdventurerData(const FString& AdventurerID) const;
	
	/** Check if player has hired adventurer */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Player Adventurers")
	bool HasHiredAdventurer(const FString& AdventurerID) const;
	
	/** Get available adventurers for hire */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Available Adventurers")
	TArray<FString> GetAvailableAdventurers() const;
	
	/** Get adventurers by class */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Available Adventurers")
	TArray<FString> GetAdventurersByClass(const FString& AdventurerClass) const;
	
	/** Get adventurers by rarity */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Available Adventurers")
	TArray<FString> GetAdventurersByRarity(const FString& Rarity) const;

	// ===== ADVENTURER MANAGEMENT FUNCTIONS =====
	
	/** Assign task to adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Management")
	bool AssignTaskToAdventurer(const FString& AdventurerID, const FString& TaskName);
	
	/** Remove task from adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Management")
	bool RemoveTaskFromAdventurer(const FString& AdventurerID, const FString& TaskName);
	
	/** Interact with adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Management")
	void InteractWithAdventurer(const FString& AdventurerID);
	
	/** Give gift to adventurer */
	UFUNCTION(BlueprintCallable, Category = "Adventurer Management")
	bool GiveGiftToAdventurer(const FString& AdventurerID, const FString& GiftItemID);
	
	/** Get adventurer relationship level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Adventurer Management")
	int32 GetAdventurerRelationship(const FString& AdventurerID) const;
	
	/** Get adventurer trust level */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Adventurer Management")
	int32 GetAdventurerTrust(const FString& AdventurerID) const;
	
	/** Get adventurer combat effectiveness */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Adventurer Management")
	float GetAdventurerCombatEffectiveness(const FString& AdventurerID) const;

	// ===== QUEST FUNCTIONS =====
	
	/** Start guild quest */
	UFUNCTION(BlueprintCallable, Category = "Guild Quests")
	bool StartGuildQuest(const FString& QuestID, const TArray<FString>& SelectedAdventurers);
	
	/** Complete guild quest */
	UFUNCTION(BlueprintCallable, Category = "Guild Quests")
	bool CompleteGuildQuest(const FString& QuestID);
	
	/** Cancel guild quest */
	UFUNCTION(BlueprintCallable, Category = "Guild Quests")
	bool CancelGuildQuest(const FString& QuestID);
	
	/** Get quest data */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Guild Quests")
	FGuildQuestData GetQuestData(const FString& QuestID) const;
	
	/** Get available quests */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Guild Quests")
	TArray<FString> GetAvailableQuests() const;
	
	/** Get quests by type */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Guild Quests")
	TArray<FString> GetQuestsByType(const FString& QuestType) const;
	
	/** Get quests by difficulty */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Guild Quests")
	TArray<FString> GetQuestsByDifficulty(const FString& Difficulty) const;
	
	/** Get quest progress */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Guild Quests")
	float GetQuestProgress(const FString& QuestID) const;

	// ===== RELATIONSHIP FUNCTIONS =====
	
	/** Increase relationship with adventurer */
	UFUNCTION(BlueprintCallable, Category = "Relationships")
	void IncreaseRelationship(const FString& AdventurerID, int32 Amount);
	
	/** Decrease relationship with adventurer */
	UFUNCTION(BlueprintCallable, Category = "Relationships")
	void DecreaseRelationship(const FString& AdventurerID, int32 Amount);
	
	/** Increase trust with adventurer */
	UFUNCTION(BlueprintCallable, Category = "Relationships")
	void IncreaseTrust(const FString& AdventurerID, int32 Amount);
	
	/** Decrease trust with adventurer */
	UFUNCTION(BlueprintCallable, Category = "Relationships")
	void DecreaseTrust(const FString& AdventurerID, int32 Amount);
	
	/** Get relationship status */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Relationships")
	FString GetRelationshipStatus(const FString& AdventurerID) const;

	// ===== ECONOMY FUNCTIONS =====
	
	/** Pay daily hiring costs */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	void PayDailyCosts();
	
	/** Get total daily costs */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Economy")
	int32 GetTotalDailyCosts() const;
	
	/** Add gold to player */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	void AddGold(int32 Amount);
	
	/** Remove gold from player */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	bool RemoveGold(int32 Amount);
	
	/** Get player gold amount */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Economy")
	int32 GetPlayerGold() const;
	
	/** Increase guild reputation */
	UFUNCTION(BlueprintCallable, Category = "Economy")
	void IncreaseGuildReputation(int32 Amount);
	
	/** Get guild reputation */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Economy")
	int32 GetGuildReputation() const;

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Setup default guild quests */
	void SetupDefaultGuildQuests();
	
	/** Update adventurer relationships over time */
	void UpdateAdventurerRelationships(float DeltaTime);
	
	/** Process active quests */
	void ProcessActiveQuests(float DeltaTime);
	
	/** Calculate quest rewards */
	void CalculateQuestRewards(const FString& QuestID);
	
	/** Save adventurer data */
	void SaveAdventurerData();
	
	/** Load adventurer data */
	void LoadAdventurerData();

private:
	// ===== INTERNAL VARIABLES =====
	
	/** Adventurer data cache */
	UPROPERTY()
	TMap<FString, FAdventurerData> AdventurerDataCache;
	
	/** Quest data cache */
	UPROPERTY()
	TMap<FString, FGuildQuestData> QuestDataCache;
	
	/** Active quests */
	UPROPERTY()
	TMap<FString, float> ActiveQuests;
	
	/** Last daily cost payment time */
	UPROPERTY()
	FDateTime LastDailyCostPayment;
	
	/** Quest process timer */
	UPROPERTY()
	float QuestProcessTimer = 0.0f;
	
	/** Relationship update timer */
	UPROPERTY()
	float RelationshipUpdateTimer = 0.0f;
};
