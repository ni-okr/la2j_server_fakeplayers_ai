#pragma once

#include "CoreMinimal.h"
#include "Components/ActorComponent.h"
#include "Engine/World.h"
#include "AntiCheatSystem.generated.h"

USTRUCT(BlueprintType)
struct FSecurityViolation
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FString ViolationID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FString PlayerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FString ViolationType; // "SpeedHack", "TeleportHack", "ItemDupe", "StatHack", "PacketManipulation"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FDateTime ViolationTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FString Description;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    int32 Severity; // 1-10

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    TMap<FString, FString> Evidence;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    bool bAutomatic;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Security Violation")
    FString Action; // "Warning", "Kick", "TempBan", "PermBan"
};

USTRUCT(BlueprintType)
struct FPlayerBehaviorProfile
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    FString PlayerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    float AverageMovementSpeed;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    float MaxMovementSpeed;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    int32 ActionsPerMinute;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    TArray<FVector> RecentPositions;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    TArray<FDateTime> ActionTimestamps;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    int32 ViolationCount;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Behavior Profile")
    float TrustScore;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UAntiCheatSystem : public UActorComponent
{
    GENERATED_BODY()

public:
    UAntiCheatSystem();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Anti-cheat Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    bool bEnableAntiCheat = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    float MaxMovementSpeed = 300.0f; // units per second

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    float MaxTeleportDistance = 50.0f; // units

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    int32 MaxActionsPerSecond = 10;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    float CheckInterval = 1.0f; // seconds

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    int32 ViolationThreshold = 3;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Anti-cheat Settings")
    bool bLogViolations = true;

    // Security Data
    UPROPERTY(BlueprintReadWrite, Category = "Security Data")
    TArray<FSecurityViolation> SecurityViolations;

    UPROPERTY(BlueprintReadWrite, Category = "Security Data")
    TMap<FString, FPlayerBehaviorProfile> PlayerProfiles;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void InitializeAntiCheatSystem();

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void RegisterPlayerAction(const FString& PlayerID, const FString& ActionType, const FVector& Position);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    bool ValidatePlayerMovement(const FString& PlayerID, const FVector& OldPosition, const FVector& NewPosition, float DeltaTime);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    bool ValidatePlayerStats(const FString& PlayerID, const TMap<FString, int32>& Stats);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    bool ValidateItemTransaction(const FString& PlayerID, const FString& ItemID, int32 Quantity);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    bool ValidateSkillUsage(const FString& PlayerID, const FString& SkillID, float Cooldown);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void ReportViolation(const FString& PlayerID, const FString& ViolationType, const FString& Description, int32 Severity);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void ProcessViolation(const FSecurityViolation& Violation);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    FPlayerBehaviorProfile GetPlayerProfile(const FString& PlayerID) const;

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void UpdatePlayerProfile(const FString& PlayerID, const FString& ActionType, const FVector& Position);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    float CalculateTrustScore(const FString& PlayerID) const;

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    bool IsPlayerSuspicious(const FString& PlayerID) const;

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void BanPlayer(const FString& PlayerID, const FString& Reason, int32 Duration);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void KickPlayer(const FString& PlayerID, const FString& Reason);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void WarnPlayer(const FString& PlayerID, const FString& Message);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    TArray<FSecurityViolation> GetViolationHistory(const FString& PlayerID) const;

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void ClearViolationHistory(const FString& PlayerID);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void SaveSecurityData();

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat System")
    void LoadSecurityData();

    // Memory Protection
    UFUNCTION(BlueprintCallable, Category = "Memory Protection")
    bool ValidateMemoryIntegrity();

    UFUNCTION(BlueprintCallable, Category = "Memory Protection")
    bool DetectMemoryModification();

    UFUNCTION(BlueprintCallable, Category = "Memory Protection")
    void ProtectCriticalMemory();

    // Process Protection
    UFUNCTION(BlueprintCallable, Category = "Process Protection")
    bool DetectDebugger();

    UFUNCTION(BlueprintCallable, Category = "Process Protection")
    bool DetectInjectedDLL();

    UFUNCTION(BlueprintCallable, Category = "Process Protection")
    bool ValidateProcessIntegrity();

    // Network Protection
    UFUNCTION(BlueprintCallable, Category = "Network Protection")
    bool ValidatePacketIntegrity(const TArray<uint8>& PacketData);

    UFUNCTION(BlueprintCallable, Category = "Network Protection")
    bool DetectPacketManipulation(const TArray<uint8>& PacketData);

    UFUNCTION(BlueprintCallable, Category = "Network Protection")
    void EncryptSensitiveData(TArray<uint8>& Data);

protected:
    void UpdateAntiCheatSystem(float DeltaTime);
    void CheckPlayerBehavior();
    void AnalyzeBehaviorPatterns();
    void ProcessSecurityQueue();
    void NotifySecurityViolation(const FSecurityViolation& Violation);
    bool IsValidMovementSpeed(float Speed) const;
    bool IsValidTeleportDistance(float Distance) const;
    bool IsValidActionRate(int32 ActionsPerSecond) const;

private:
    float LastCheckTime;
    TMap<FString, FVector> LastPlayerPositions;
    TMap<FString, FDateTime> LastPlayerActions;
    TMap<FString, int32> PlayerActionCounts;
    TArray<FSecurityViolation> PendingViolations;
    int32 TotalViolations;
    int32 ProcessedViolations;
    bool bMemoryProtectionActive;
};
