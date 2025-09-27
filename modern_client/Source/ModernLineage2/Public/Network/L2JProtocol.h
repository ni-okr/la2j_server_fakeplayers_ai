#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "Engine/DataTable.h"
#include "L2JProtocol.generated.h"

USTRUCT(BlueprintType)
struct FL2JPacket
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    int32 PacketID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    FString PacketType; // "Login", "Game", "Character", "Action", "Chat"

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    TArray<uint8> Data;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    int32 DataSize;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    FDateTime Timestamp;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "L2J Packet")
    bool bIsEncrypted;
};

USTRUCT(BlueprintType)
struct FL2JLoginPacket
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Login")
    FString Username;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Login")
    FString Password;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Login")
    FString ClientVersion;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Login")
    int32 ProtocolVersion;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Login")
    FString HardwareID;
};

USTRUCT(BlueprintType)
struct FL2JCharacterPacket
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    int32 CharacterID;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    FString CharacterName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    FVector Position;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    FRotator Rotation;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    int32 Level;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    int32 HP;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    int32 MP;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Character")
    int32 Experience;
};

UCLASS(BlueprintType, Blueprintable)
class MODERNLINEAGE2_API UL2JProtocol : public UObject
{
    GENERATED_BODY()

public:
    UL2JProtocol();

    // Protocol Settings
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    int32 ProtocolVersion = 746; // Lineage II Interlude protocol version

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    FString ServerAddress = TEXT("127.0.0.1");

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    int32 LoginPort = 2106;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    int32 GamePort = 7777;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    bool bUseEncryption = true;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Protocol Settings")
    float ConnectionTimeout = 30.0f;

    // Packet Management
    UPROPERTY(BlueprintReadWrite, Category = "Packet Management")
    TArray<FL2JPacket> IncomingPackets;

    UPROPERTY(BlueprintReadWrite, Category = "Packet Management")
    TArray<FL2JPacket> OutgoingPackets;

    // Functions
    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void InitializeProtocol();

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    bool ConnectToLoginServer(const FString& Username, const FString& Password);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    bool ConnectToGameServer(int32 CharacterID);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void DisconnectFromServer();

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    bool SendPacket(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket ReceivePacket();

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket CreateLoginPacket(const FString& Username, const FString& Password);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket CreateCharacterSelectPacket(int32 CharacterID);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket CreateMovePacket(const FVector& TargetPosition);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket CreateAttackPacket(int32 TargetID);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    FL2JPacket CreateChatPacket(const FString& Message, const FString& ChatType);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void ProcessIncomingPacket(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    TArray<uint8> EncryptPacketData(const TArray<uint8>& Data);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    TArray<uint8> DecryptPacketData(const TArray<uint8>& Data);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    bool ValidatePacket(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void HandleLoginResponse(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void HandleCharacterList(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void HandleCharacterUpdate(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void HandleChatMessage(const FL2JPacket& Packet);

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void SaveProtocolSettings();

    UFUNCTION(BlueprintCallable, Category = "L2J Protocol")
    void LoadProtocolSettings();

protected:
    void InitializeEncryption();
    void UpdatePacketQueue();
    void ProcessPacketQueue();
    bool IsConnectedToServer() const;

private:
    bool bIsConnected;
    bool bIsLoggedIn;
    FString CurrentSessionKey;
    TArray<uint8> EncryptionKey;
    int32 PacketSequence;
    float LastHeartbeat;
};
