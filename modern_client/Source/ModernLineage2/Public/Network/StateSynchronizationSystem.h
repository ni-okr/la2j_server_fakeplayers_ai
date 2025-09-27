#pragma once

#include "CoreMinimal.h"
#include "Components/ActorComponent.h"
#include "Engine/World.h"
#include "StateSynchronizationSystem.generated.h"

USTRUCT(BlueprintType)
struct FGameStateSnapshot
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    FDateTime Timestamp;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    int32 SnapshotID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    FString PlayerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    FVector PlayerPosition;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    FRotator PlayerRotation;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    int32 PlayerHP;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    int32 PlayerMP;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    TMap<FString, FString> PlayerStats;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    TArray<FString> ActiveBuffs;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    TArray<FString> Inventory;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Game State")
    TMap<FString, FVector> NearbyPlayers;
};

USTRUCT(BlueprintType)
struct FSynchronizationSettings
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    float SyncInterval = 0.1f; // 10 times per second

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    float PositionThreshold = 1.0f; // meters

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    float RotationThreshold = 5.0f; // degrees

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bSyncPosition = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bSyncRotation = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bSyncStats = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bSyncInventory = false;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bSyncBuffs = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    int32 MaxSnapshotHistory = 100;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UStateSynchronizationSystem : public UActorComponent
{
    GENERATED_BODY()

public:
    UStateSynchronizationSystem();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Synchronization Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    FSynchronizationSettings SyncSettings;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bEnableSynchronization = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    bool bClientAuthoritative = false;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Sync Settings")
    float PredictionTime = 0.1f;

    // State Management
    UPROPERTY(BlueprintReadWrite, Category = "State Management")
    TArray<FGameStateSnapshot> StateHistory;

    UPROPERTY(BlueprintReadWrite, Category = "State Management")
    FGameStateSnapshot CurrentState;

    UPROPERTY(BlueprintReadWrite, Category = "State Management")
    FGameStateSnapshot PredictedState;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void InitializeSynchronization();

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void CaptureCurrentState();

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void SendStateToServer();

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void ReceiveStateFromServer(const FGameStateSnapshot& ServerState);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void ApplyServerState(const FGameStateSnapshot& ServerState);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void PredictNextState(float DeltaTime);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void InterpolateToState(const FGameStateSnapshot& TargetState, float Alpha);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    bool ValidateStateChange(const FGameStateSnapshot& OldState, const FGameStateSnapshot& NewState);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void HandleStateMismatch(const FGameStateSnapshot& ClientState, const FGameStateSnapshot& ServerState);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    FGameStateSnapshot GetStateAtTime(FDateTime Timestamp) const;

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void RollbackToState(const FGameStateSnapshot& State);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void ReplayInputsFromState(const FGameStateSnapshot& State, const TArray<FString>& Inputs);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    bool ShouldSyncProperty(const FString& PropertyName) const;

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    float CalculateStateDifference(const FGameStateSnapshot& State1, const FGameStateSnapshot& State2) const;

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void CompressStateData(FGameStateSnapshot& State);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void DecompressStateData(FGameStateSnapshot& State);

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void SaveSynchronizationSettings();

    UFUNCTION(BlueprintCallable, Category = "State Synchronization")
    void LoadSynchronizationSettings();

    // Anti-cheat Functions
    UFUNCTION(BlueprintCallable, Category = "Anti-cheat")
    bool ValidatePlayerPosition(const FVector& Position);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat")
    bool ValidatePlayerStats(const TMap<FString, FString>& Stats);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat")
    bool ValidateInventoryChange(const TArray<FString>& OldInventory, const TArray<FString>& NewInventory);

    UFUNCTION(BlueprintCallable, Category = "Anti-cheat")
    void ReportSuspiciousActivity(const FString& ActivityType, const FString& Details);

protected:
    void UpdateSynchronization(float DeltaTime);
    void ProcessStateQueue();
    void CleanupStateHistory();
    void NotifyStateChange(const FGameStateSnapshot& NewState);
    bool IsSignificantChange(const FGameStateSnapshot& OldState, const FGameStateSnapshot& NewState) const;

private:
    float LastSyncTime;
    float LastCaptureTime;
    int32 NextSnapshotID;
    TArray<FGameStateSnapshot> PendingStates;
    TMap<FString, FString> LastSyncedProperties;
    bool bWaitingForServerConfirmation;
    float ServerLatency;
};
