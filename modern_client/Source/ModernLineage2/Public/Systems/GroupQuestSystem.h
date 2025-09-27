#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "GroupQuestSystem.generated.h"

USTRUCT(BlueprintType)
struct FGroupQuest
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    FString QuestID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    FString QuestName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    FString Description;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    FString QuestType; // "Dungeon", "Boss", "Exploration", "Escort", "Defense"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 RequiredLevel;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 RequiredCompanions;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 Difficulty;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 Duration; // minutes

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    TArray<FString> RequiredCompanionClasses;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    TArray<FString> Rewards;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 ExperienceReward;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 GoldReward;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Group Quest")
    int32 ReputationReward;
};

USTRUCT(BlueprintType)
struct FActiveGroupQuest
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    FString QuestID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    FString OwnerID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    TArray<FString> CompanionIDs;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    FDateTime StartTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    FDateTime EndTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    int32 Progress;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    bool bIsCompleted;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Active Quest")
    bool bIsFailed;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UGroupQuestSystem : public UActorComponent
{
    GENERATED_BODY()

public:
    UGroupQuestSystem();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Quest Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Quest Settings")
    bool bEnableGroupQuests = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Quest Settings")
    int32 MaxActiveQuests = 3;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Quest Settings")
    float QuestCheckInterval = 60.0f; // seconds

    // Quest Data
    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Quest Data")
    TArray<FGroupQuest> AvailableQuests;

    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Quest Data")
    TArray<FActiveGroupQuest> ActiveQuests;

    UPROPERTY(BlueprintReadWrite, EditAnywhere, Category = "Quest Data")
    TArray<FActiveGroupQuest> CompletedQuests;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void InitializeGroupQuestSystem();

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void LoadAvailableQuests();

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    bool StartGroupQuest(const FString& QuestID, const FString& OwnerID, const TArray<FString>& CompanionIDs);

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    bool CompleteGroupQuest(const FString& QuestID, const FString& OwnerID, bool bSuccess);

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    bool CancelGroupQuest(const FString& QuestID, const FString& OwnerID);

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    TArray<FGroupQuest> GetAvailableQuestsForPlayer(const FString& OwnerID, int32 PlayerLevel) const;

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    TArray<FActiveGroupQuest> GetPlayerActiveQuests(const FString& OwnerID) const;

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    FGroupQuest GetQuestData(const FString& QuestID) const;

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    FActiveGroupQuest GetActiveQuest(const FString& QuestID, const FString& OwnerID) const;

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void UpdateQuestProgress(const FString& QuestID, const FString& OwnerID, int32 Progress);

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    bool CanStartQuest(const FString& QuestID, const FString& OwnerID, const TArray<FString>& CompanionIDs) const;

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void GiveQuestRewards(const FString& QuestID, const FString& OwnerID, const TArray<FString>& CompanionIDs);

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void SaveQuestData();

    UFUNCTION(BlueprintCallable, Category = "Group Quest System")
    void LoadQuestData();

protected:
    void UpdateActiveQuests(float DeltaTime);
    void CheckQuestCompletion();
    void ProcessQuestRewards();
    void NotifyQuestCompletion(const FString& QuestID, const FString& OwnerID, bool bSuccess);

private:
    float LastQuestUpdateTime;
    int32 TotalQuestsCompleted;
    int32 TotalQuestsFailed;
};
