#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "SlaveRebellionSystem.generated.h"

USTRUCT(BlueprintType)
struct FSlaveRebellionEvent
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    FString SlaveID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    FString OwnerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    FDateTime RebellionTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    int32 Severity; // 1-10

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    FString RebellionType; // "Escape", "Violence", "Sabotage", "Organized"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    bool bIsResolved;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion")
    FString ResolutionType; // "Suppressed", "Escaped", "Killed", "Freed"
};

USTRUCT(BlueprintType)
struct FRebellionConsequence
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Consequence")
    FString ConsequenceType; // "GoldLoss", "ItemLoss", "ReputationLoss", "SlaveDeath"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Consequence")
    int32 Value;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Consequence")
    FString Description;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API USlaveRebellionSystem : public UActorComponent
{
    GENERATED_BODY()

public:
    USlaveRebellionSystem();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Rebellion Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion Settings")
    bool bEnableRebellionSystem = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion Settings")
    float RebellionCheckInterval = 60.0f; // seconds

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion Settings")
    int32 MinLoyaltyForRebellion = 20;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion Settings")
    float BaseRebellionChance = 0.01f; // per second

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Rebellion Settings")
    float LoyaltyDecayRate = 0.1f; // per hour

    // Active Rebellions
    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Active Rebellions")
    TArray<FSlaveRebellionEvent> ActiveRebellions;

    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Rebellion History")
    TArray<FSlaveRebellionEvent> RebellionHistory;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void InitializeRebellionSystem();

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void CheckForRebellions();

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    bool TriggerRebellion(const FString& SlaveID, const FString& OwnerID, int32 Severity = 1);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void ResolveRebellion(const FString& SlaveID, const FString& ResolutionType);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    TArray<FSlaveRebellionEvent> GetActiveRebellions() const;

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    TArray<FSlaveRebellionEvent> GetRebellionHistory() const;

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    FRebellionConsequence CalculateRebellionConsequence(const FSlaveRebellionEvent& Rebellion) const;

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void ApplyRebellionConsequence(const FRebellionConsequence& Consequence, class AL2Character* Owner);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    float CalculateRebellionChance(const FString& SlaveID, int32 Loyalty) const;

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    FString DetermineRebellionType(int32 Severity, int32 Loyalty) const;

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void SuppressRebellion(const FString& SlaveID, int32 SuppressionPower);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void NegotiateWithSlave(const FString& SlaveID, int32 NegotiationSkill);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void FreeSlave(const FString& SlaveID);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void KillSlave(const FString& SlaveID);

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void SaveRebellionData();

    UFUNCTION(BlueprintCallable, Category = "Rebellion System")
    void LoadRebellionData();

protected:
    void UpdateRebellionSystem(float DeltaTime);
    void ProcessActiveRebellions(float DeltaTime);
    void UpdateRebellionHistory();
    void NotifyRebellionEvent(const FSlaveRebellionEvent& Rebellion);

private:
    float LastRebellionCheckTime;
    int32 TotalRebellions;
    int32 SuccessfulSuppressions;
    int32 FailedSuppressions;
};
