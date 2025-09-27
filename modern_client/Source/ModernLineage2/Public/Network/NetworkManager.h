#pragma once

#include "CoreMinimal.h"
#include "Components/ActorComponent.h"
#include "Engine/World.h"
#include "Sockets.h"
#include "SocketSubsystem.h"
#include "NetworkManager.generated.h"

USTRUCT(BlueprintType)
struct FNetworkConnection
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    FString ConnectionID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    FString ServerAddress;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    int32 Port;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    FString ConnectionType; // "Login", "Game", "Chat"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    bool bIsConnected;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    FDateTime ConnectedTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    float Latency;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    int32 PacketsSent;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Connection")
    int32 PacketsReceived;
};

USTRUCT(BlueprintType)
struct FNetworkStatistics
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    float AverageLatency;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    float PacketLoss;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    int32 BytesSent;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    int32 BytesReceived;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    float Bandwidth;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Statistics")
    int32 ConnectionErrors;
};

UCLASS(BlueprintType, Blueprintable, ClassGroup=(Custom), meta=(BlueprintSpawnableComponent))
class MODERNLINEAGE2_API UNetworkManager : public UActorComponent
{
    GENERATED_BODY()

public:
    UNetworkManager();

protected:
    virtual void BeginPlay() override;

public:
    virtual void TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction) override;

    // Network Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    bool bEnableNetworking = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    float ConnectionTimeout = 30.0f;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    float HeartbeatInterval = 5.0f;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    int32 MaxRetryAttempts = 3;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    float RetryDelay = 2.0f;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Network Settings")
    bool bAutoReconnect = true;

    // Connection Management
    UPROPERTY(BlueprintReadWrite, Category = "Connection Management")
    TArray<FNetworkConnection> ActiveConnections;

    UPROPERTY(BlueprintReadWrite, Category = "Network Statistics")
    FNetworkStatistics NetworkStats;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void InitializeNetworkManager();

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    bool CreateConnection(const FString& ConnectionID, const FString& ServerAddress, int32 Port, const FString& ConnectionType);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    bool CloseConnection(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void CloseAllConnections();

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    bool IsConnectionActive(const FString& ConnectionID) const;

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    FNetworkConnection GetConnection(const FString& ConnectionID) const;

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    TArray<FNetworkConnection> GetActiveConnections() const;

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    bool SendData(const FString& ConnectionID, const TArray<uint8>& Data);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    TArray<uint8> ReceiveData(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void UpdateConnectionLatency(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void SendHeartbeat(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void HandleConnectionLost(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void AttemptReconnection(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    FNetworkStatistics GetNetworkStatistics() const;

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void ResetNetworkStatistics();

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void SaveNetworkSettings();

    UFUNCTION(BlueprintCallable, Category = "Network Manager")
    void LoadNetworkSettings();

    // Security Functions
    UFUNCTION(BlueprintCallable, Category = "Network Security")
    bool ValidateConnection(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Security")
    void EnableConnectionEncryption(const FString& ConnectionID, const TArray<uint8>& EncryptionKey);

    UFUNCTION(BlueprintCallable, Category = "Network Security")
    void DisableConnectionEncryption(const FString& ConnectionID);

    UFUNCTION(BlueprintCallable, Category = "Network Security")
    bool IsConnectionSecure(const FString& ConnectionID) const;

protected:
    void UpdateNetworkConnections(float DeltaTime);
    void UpdateNetworkStatistics(float DeltaTime);
    void ProcessIncomingData();
    void ProcessOutgoingData();
    void HandleNetworkErrors();
    void NotifyConnectionStatusChange(const FString& ConnectionID, bool bConnected);

private:
    TMap<FString, class FSocket*> SocketConnections;
    TMap<FString, TArray<uint8>> IncomingDataBuffers;
    TMap<FString, TArray<uint8>> OutgoingDataBuffers;
    TMap<FString, bool> ConnectionEncryption;
    TMap<FString, TArray<uint8>> EncryptionKeys;
    
    float LastHeartbeatTime;
    float LastStatisticsUpdate;
    int32 TotalConnectionAttempts;
    int32 SuccessfulConnections;
    int32 FailedConnections;
};
