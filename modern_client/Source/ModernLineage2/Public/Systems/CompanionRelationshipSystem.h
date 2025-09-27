#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "CompanionRelationshipSystem.generated.h"

USTRUCT(BlueprintType)
struct FRelationshipEvent
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    FString CompanionID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    FString OwnerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    FDateTime EventTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    FString EventType; // "Mission", "Gift", "Training", "Conversation", "Quest"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    int32 RelationshipChange;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship")
    FString Description;
};

USTRUCT(BlueprintType)
struct FCompanionPersonality
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Personality")
    FString PersonalityType; // "Brave", "Cautious", "Loyal", "Independent", "Mysterious"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Personality")
    TArray<FString> PreferredActivities;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Personality")
    TArray<FString> DislikedActivities;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Personality")
    float LoyaltyModifier;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Personality")
    float ExperienceModifier;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UCompanionRelationshipSystem : public UActorComponent
{
    GENERATED_BODY()

public:
    UCompanionRelationshipSystem();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Relationship Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship Settings")
    bool bEnableRelationshipSystem = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship Settings")
    float RelationshipDecayTime = 120.0f; // minutes

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship Settings")
    int32 RelationshipDecayAmount = 1;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship Settings")
    int32 MaxRelationship = 100;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Relationship Settings")
    int32 MinRelationship = 0;

    // Relationship Events
    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Relationship Events")
    TArray<FRelationshipEvent> RelationshipHistory;

    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Companion Personalities")
    TMap<FString, FCompanionPersonality> CompanionPersonalities;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void InitializeRelationshipSystem();

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void UpdateCompanionRelationship(const FString& CompanionID, const FString& OwnerID, int32 DeltaRelationship, const FString& EventType, const FString& Description);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    int32 GetCompanionRelationship(const FString& CompanionID, const FString& OwnerID) const;

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void GiveGiftToCompanion(const FString& CompanionID, const FString& OwnerID, const FString& GiftType, int32 GiftValue);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void TrainWithCompanion(const FString& CompanionID, const FString& OwnerID, const FString& TrainingType);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void CompleteMissionWithCompanion(const FString& CompanionID, const FString& OwnerID, bool bSuccess, int32 MissionDifficulty);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void HaveConversationWithCompanion(const FString& CompanionID, const FString& OwnerID, const FString& ConversationTopic);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    TArray<FRelationshipEvent> GetRelationshipHistory(const FString& CompanionID, const FString& OwnerID) const;

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    FCompanionPersonality GetCompanionPersonality(const FString& CompanionID) const;

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void SetCompanionPersonality(const FString& CompanionID, const FCompanionPersonality& Personality);

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    int32 CalculateRelationshipChange(const FString& CompanionID, const FString& EventType, int32 BaseChange) const;

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    bool CanPerformActivity(const FString& CompanionID, const FString& ActivityType) const;

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void SaveRelationshipData();

    UFUNCTION(BlueprintCallable, Category = "Relationship System")
    void LoadRelationshipData();

protected:
    void UpdateRelationshipDecay(float DeltaTime);
    void ProcessRelationshipEvents();
    void NotifyRelationshipChange(const FString& CompanionID, const FString& OwnerID, int32 NewRelationship);

private:
    TMap<FString, int32> CompanionRelationships; // Key: "CompanionID_OwnerID", Value: Relationship
    float LastRelationshipUpdateTime;
    int32 TotalRelationshipEvents;
};
