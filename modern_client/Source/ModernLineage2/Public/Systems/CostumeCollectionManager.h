#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Components/ActorComponent.h"
#include "CostumeCollectionManager.generated.h"

USTRUCT(BlueprintType)
struct FCostumeCollection
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    FString CollectionID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    FString CollectionName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    FString Description;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    TArray<FString> CostumePieceIDs;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    FString Theme;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    int32 RequiredLevel;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collection")
    bool bIsUnlocked;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UCostumeCollectionManager : public UActorComponent
{
    GENERATED_BODY()

public:
    UCostumeCollectionManager();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Collections
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collections")
    TArray<FCostumeCollection> AvailableCollections;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Collections")
    TArray<FString> UnlockedCollections;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Collections")
    void InitializeCollections();

    UFUNCTION(BlueprintCallable, Category = "Collections")
    bool UnlockCollection(const FString& CollectionID);

    UFUNCTION(BlueprintCallable, Category = "Collections")
    bool IsCollectionUnlocked(const FString& CollectionID) const;

    UFUNCTION(BlueprintCallable, Category = "Collections")
    FCostumeCollection GetCollection(const FString& CollectionID) const;

    UFUNCTION(BlueprintCallable, Category = "Collections")
    TArray<FString> GetCollectionsByTheme(const FString& Theme) const;

    UFUNCTION(BlueprintCallable, Category = "Collections")
    void EquipCollection(const FString& CollectionID);

    UFUNCTION(BlueprintCallable, Category = "Collections")
    void SaveCollectionProgress();

    UFUNCTION(BlueprintCallable, Category = "Collections")
    void LoadCollectionProgress();

private:
    void SetupDefaultCollections();
    bool CheckCollectionRequirements(const FCostumeCollection& Collection) const;
};
