#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Character.h"
#include "Components/SkeletalMeshComponent.h"
#include "Components/StaticMeshComponent.h"
#include "Components/SceneComponent.h"
#include "Components/CapsuleComponent.h"
#include "GameFramework/CharacterMovementComponent.h"
#include "GameFramework/SpringArmComponent.h"
#include "Camera/CameraComponent.h"
#include "L2Character.generated.h"

// Forward declarations
class UAdultContentManager;
class UCostumeSystem;
class UInventoryComponent;
class UStatsComponent;

/**
 * Base character class for Modern Lineage II
 * Ported from deobfuscated UnrealScript code
 */
UCLASS(BlueprintType, Blueprintable)
class MODERNLINEAGE2_API AL2Character : public ACharacter
{
	GENERATED_BODY()

public:
	AL2Character();

protected:
	virtual void BeginPlay() override;

public:
	virtual void Tick(float DeltaTime) override;
	virtual void SetupPlayerInputComponent(class UInputComponent* PlayerInputComponent) override;

	// ===== BASE STATS (Ported from deobfuscated code) =====
	
	/** Strength - affects physical damage and carrying capacity */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 STR = 10;
	
	/** Dexterity - affects attack speed and accuracy */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 DEX = 10;
	
	/** Constitution - affects health and stamina */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 CON = 10;
	
	/** Intelligence - affects magical damage and mana */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 INT = 10;
	
	/** Wisdom - affects magical resistance and mana regeneration */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 WIT = 10;
	
	/** Mental - affects magical accuracy and critical hit chance */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats", meta = (ClampMin = "1", ClampMax = "100"))
	int32 MEN = 10;

	// ===== DERIVED STATS =====
	
	/** Current Health Points */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Health")
	float CurrentHP = 100.0f;
	
	/** Maximum Health Points */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Health")
	float MaxHP = 100.0f;
	
	/** Current Mana Points */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Mana")
	float CurrentMP = 100.0f;
	
	/** Maximum Mana Points */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Mana")
	float MaxMP = 100.0f;
	
	/** Current Experience Points */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Experience")
	int32 CurrentXP = 0;
	
	/** Experience Points needed for next level */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Experience")
	int32 XPToNextLevel = 1000;
	
	/** Current Level */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Level")
	int32 Level = 1;

	// ===== GENDER AND APPEARANCE =====
	
	/** Character gender (true = female, false = male) */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Appearance")
	bool bIsFemale = false;
	
	/** Character race/class */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Appearance")
	FString CharacterClass = TEXT("Human");
	
	/** Character name */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Appearance")
	FString CharacterName = TEXT("Unknown");

	// ===== ADULT CONTENT SYSTEM =====
	
	/** Adult content manager for detailed female models */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Adult Content")
	UAdultContentManager* AdultContentManager;
	
	/** Enable adult content features */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Adult Content")
	bool bEnableAdultContent = false;

	// ===== COSTUME SYSTEM =====
	
	/** Costume system for BnS-style wardrobe */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Costume")
	UCostumeSystem* CostumeSystem;
	
	/** Current costume slots */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Costume")
	TMap<FString, FString> CostumeSlots;

	// ===== INVENTORY AND EQUIPMENT =====
	
	/** Inventory component */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Inventory")
	UInventoryComponent* InventoryComponent;
	
	/** Stats component */
	UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Stats")
	UStatsComponent* StatsComponent;

	// ===== CAMERA SYSTEM =====
	
	/** Spring arm for third person camera */
	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = "Camera")
	USpringArmComponent* SpringArm;
	
	/** Third person camera */
	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = "Camera")
	UCameraComponent* ThirdPersonCamera;

	// ===== FUNCTIONS =====
	
	/** Calculate derived stats based on base stats */
	UFUNCTION(BlueprintCallable, Category = "Stats")
	void CalculateDerivedStats();
	
	/** Level up the character */
	UFUNCTION(BlueprintCallable, Category = "Level")
	void LevelUp();
	
	/** Take damage with armor calculation */
	UFUNCTION(BlueprintCallable, Category = "Combat")
	virtual float TakeDamage(float DamageAmount, struct FDamageEvent const& DamageEvent, class AController* EventInstigator, AActor* DamageCauser) override;
	
	/** Heal the character */
	UFUNCTION(BlueprintCallable, Category = "Health")
	void Heal(float HealAmount);
	
	/** Restore mana */
	UFUNCTION(BlueprintCallable, Category = "Mana")
	void RestoreMana(float ManaAmount);
	
	/** Check if character is alive */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Health")
	bool IsAlive() const;
	
	/** Get health percentage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Health")
	float GetHealthPercentage() const;
	
	/** Get mana percentage */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Mana")
	float GetManaPercentage() const;

	// ===== ADULT CONTENT FUNCTIONS =====
	
	/** Initialize adult content system */
	UFUNCTION(BlueprintCallable, Category = "Adult Content")
	void InitializeAdultContent();
	
	/** Update adult content based on character state */
	UFUNCTION(BlueprintCallable, Category = "Adult Content")
	void UpdateAdultContent();

	// ===== COSTUME FUNCTIONS =====
	
	/** Equip costume piece */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	void EquipCostumePiece(const FString& SlotName, const FString& CostumeID);
	
	/** Remove costume piece */
	UFUNCTION(BlueprintCallable, Category = "Costume")
	void RemoveCostumePiece(const FString& SlotName);
	
	/** Get current costume piece */
	UFUNCTION(BlueprintCallable, BlueprintPure, Category = "Costume")
	FString GetCostumePiece(const FString& SlotName) const;

protected:
	// ===== INTERNAL FUNCTIONS =====
	
	/** Initialize character components */
	void InitializeComponents();
	
	/** Setup camera system */
	void SetupCamera();
	
	/** Update character appearance */
	void UpdateAppearance();
	
	/** Handle level up effects */
	void OnLevelUp();
};
