#include "AdventurerGuildSystem.h"
#include "Engine/DataTable.h"
#include "Engine/World.h"
#include "Kismet/GameplayStatics.h"
#include "Components/ActorComponent.h"

UAdventurerGuildSystem::UAdventurerGuildSystem()
{
	PrimaryComponentTick.bCanEverTick = true;
	bEnableAdventurerGuild = true;
	bEnableCompanionSystem = true;
	bEnableRelationshipSystem = true;
	bEnableGroupQuests = true;
	bAutoSaveGuildData = true;
}

void UAdventurerGuildSystem::BeginPlay()
{
	Super::BeginPlay();
	
	InitializeAdventurerGuildSystem();
}

void UAdventurerGuildSystem::TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction)
{
	Super::TickComponent(DeltaTime, TickType, ThisTickFunction);
	
	// Обновление системы гильдии авантюристов
	if (bEnableAdventurerGuild)
	{
		UpdateCompanionRelationships(DeltaTime);
		UpdateGroupQuests(DeltaTime);
		UpdateGuildReputation(DeltaTime);
	}
}

void UAdventurerGuildSystem::InitializeAdventurerGuildSystem()
{
	if (!bEnableAdventurerGuild)
	{
		return;
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Initializing Adventurer Guild System..."));
	
	// Загрузка данных компаньонов
	LoadCompanionData();
	
	// Настройка гильдии
	SetupAdventurerGuild();
	
	// Инициализация системы отношений
	if (bEnableRelationshipSystem)
	{
		InitializeRelationshipSystem();
	}
	
	// Инициализация групповых квестов
	if (bEnableGroupQuests)
	{
		InitializeGroupQuestSystem();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Adventurer Guild System initialized successfully"));
}

void UAdventurerGuildSystem::LoadCompanionData()
{
	if (!CompanionDataTable)
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion Data Table not set!"));
		return;
	}
	
	// Загрузка всех строк из таблицы данных
	TArray<FName> RowNames = CompanionDataTable->GetRowNames();
	
	for (const FName& RowName : RowNames)
	{
		FCompanionInfo* RowData = CompanionDataTable->FindRow<FCompanionInfo>(RowName, TEXT(""));
		if (RowData)
		{
			CompanionDataCache.Add(RowData->CompanionID, *RowData);
		}
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Loaded %d companions from data table"), CompanionDataCache.Num());
}

bool UAdventurerGuildSystem::HireCompanion(AL2Character* Player, const FString& CompanionID)
{
	if (!bEnableAdventurerGuild || !Player)
	{
		return false;
	}
	
	// Проверка доступности компаньона
	if (!IsCompanionAvailable(CompanionID))
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion %s not available"), *CompanionID);
		return false;
	}
	
	// Получение данных компаньона
	FCompanionInfo* CompanionData = GetCompanionData(CompanionID);
	if (!CompanionData)
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion data not found for %s"), *CompanionID);
		return false;
	}
	
	// Проверка достаточности средств
	if (Player->GetGold() < CompanionData->HireCost)
	{
		UE_LOG(LogTemp, Warning, TEXT("Insufficient funds to hire companion %s"), *CompanionID);
		return false;
	}
	
	// Проверка лимита компаньонов
	if (GetPlayerCompanionCount(Player) >= GetMaxCompanionLimit(Player))
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion limit reached for player"));
		return false;
	}
	
	// Проверка репутации гильдии
	if (GetGuildReputation(Player) < CompanionData->RequiredReputation)
	{
		UE_LOG(LogTemp, Warning, TEXT("Insufficient guild reputation to hire companion %s"), *CompanionID);
		return false;
	}
	
	// Создание экземпляра компаньона
	FCompanionInstance NewCompanion;
	NewCompanion.CompanionID = CompanionID;
	NewCompanion.OwnerID = Player->GetUniqueID();
	NewCompanion.Relationship = CompanionData->BaseRelationship;
	NewCompanion.Experience = 0;
	NewCompanion.Level = CompanionData->Level;
	NewCompanion.bIsActive = true;
	NewCompanion.HireTime = FDateTime::Now();
	NewCompanion.LastInteractionTime = FDateTime::Now();
	NewCompanion.MissionCount = 0;
	NewCompanion.SuccessRate = 1.0f;
	
	// Добавление компаньона к игроку
	PlayerCompanions.Add(Player->GetUniqueID(), NewCompanion);
	
	// Списание средств
	Player->AddGold(-CompanionData->HireCost);
	
	// Удаление из доступных
	RemoveCompanionFromAvailable(CompanionID);
	
	// Автосохранение
	if (bAutoSaveGuildData)
	{
		SaveGuildData();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Player hired companion %s for %d gold"), *CompanionID, CompanionData->HireCost);
	return true;
}

bool UAdventurerGuildSystem::DismissCompanion(AL2Character* Player, const FString& CompanionID)
{
	if (!bEnableAdventurerGuild || !Player)
	{
		return false;
	}
	
	// Поиск компаньона у игрока
	FCompanionInstance* CompanionInstance = GetPlayerCompanion(Player, CompanionID);
	if (!CompanionInstance)
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion %s not found in player's party"), *CompanionID);
		return false;
	}
	
	// Получение данных компаньона
	FCompanionInfo* CompanionData = GetCompanionData(CompanionID);
	if (!CompanionData)
	{
		UE_LOG(LogTemp, Warning, TEXT("Companion data not found for %s"), *CompanionID);
		return false;
	}
	
	// Расчет компенсации (с учетом отношений и опыта)
	int32 Compensation = CalculateDismissalCompensation(*CompanionInstance, *CompanionData);
	
	// Выдача компенсации игроку
	Player->AddGold(Compensation);
	
	// Удаление компаньона у игрока
	RemovePlayerCompanion(Player, CompanionID);
	
	// Возврат в доступные
	AddCompanionToAvailable(CompanionID);
	
	// Автосохранение
	if (bAutoSaveGuildData)
	{
		SaveGuildData();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Player dismissed companion %s, received %d gold compensation"), *CompanionID, Compensation);
	return true;
}

TArray<FCompanionInfo> UAdventurerGuildSystem::GetAvailableCompanions() const
{
	TArray<FCompanionInfo> AvailableCompanions;
	
	for (const FString& CompanionID : AvailableCompanionList)
	{
		if (CompanionDataCache.Contains(CompanionID))
		{
			AvailableCompanions.Add(CompanionDataCache[CompanionID]);
		}
	}
	
	return AvailableCompanions;
}

TArray<FCompanionInstance> UAdventurerGuildSystem::GetPlayerCompanions(AL2Character* Player) const
{
	TArray<FCompanionInstance> PlayerCompanionList;
	
	if (!Player)
	{
		return PlayerCompanionList;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (const auto& Pair : PlayerCompanions)
	{
		if (Pair.Key == PlayerID)
		{
			PlayerCompanionList.Add(Pair.Value);
		}
	}
	
	return PlayerCompanionList;
}

void UAdventurerGuildSystem::UpdateCompanionRelationship(FCompanionInstance& Companion, int32 DeltaRelationship)
{
	if (!bEnableRelationshipSystem)
	{
		return;
	}
	
	// Изменение отношений
	Companion.Relationship = FMath::Clamp(Companion.Relationship + DeltaRelationship, 0, 100);
	
	// Обновление времени последнего взаимодействия
	Companion.LastInteractionTime = FDateTime::Now();
	
	// Проверка на уход компаньона
	if (Companion.Relationship <= 0)
	{
		TriggerCompanionDeparture(Companion);
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Companion %s relationship changed by %d, new relationship: %d"), 
		*Companion.CompanionID, DeltaRelationship, Companion.Relationship);
}

void UAdventurerGuildSystem::SetupAdventurerGuild()
{
	// Очистка доступных компаньонов
	AvailableCompanionList.Empty();
	
	// Добавление компаньонов в гильдию
	for (const auto& Pair : CompanionDataCache)
	{
		const FCompanionInfo& CompanionData = Pair.Value;
		
		// Проверка доступности для найма
		if (CompanionData.bAvailableForHire)
		{
			AvailableCompanionList.Add(CompanionData.CompanionID);
		}
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Adventurer guild setup with %d companions"), AvailableCompanionList.Num());
}

void UAdventurerGuildSystem::UpdateCompanionRelationships(float DeltaTime)
{
	if (!bEnableRelationshipSystem)
	{
		return;
	}
	
	// Обновление отношений всех компаньонов
	for (auto& Pair : PlayerCompanions)
	{
		FCompanionInstance& Companion = Pair.Value;
		
		if (Companion.bIsActive)
		{
			// Естественное снижение отношений со временем
			float TimeSinceLastInteraction = (FDateTime::Now() - Companion.LastInteractionTime).GetTotalMinutes();
			if (TimeSinceLastInteraction > RelationshipDecayTime)
			{
				int32 RelationshipDecay = FMath::RoundToInt(TimeSinceLastInteraction / RelationshipDecayTime) * RelationshipDecayAmount;
				UpdateCompanionRelationship(Companion, -RelationshipDecay);
			}
		}
	}
}

void UAdventurerGuildSystem::UpdateGroupQuests(float DeltaTime)
{
	if (!bEnableGroupQuests)
	{
		return;
	}
	
	// Обновление групповых квестов
	// Проверка прогресса квестов
	// Награждение за выполнение
}

void UAdventurerGuildSystem::UpdateGuildReputation(float DeltaTime)
{
	// Обновление репутации гильдии
	// Влияние действий игрока на репутацию
}

bool UAdventurerGuildSystem::IsCompanionAvailable(const FString& CompanionID) const
{
	return AvailableCompanionList.Contains(CompanionID);
}

FCompanionInfo* UAdventurerGuildSystem::GetCompanionData(const FString& CompanionID)
{
	if (CompanionDataCache.Contains(CompanionID))
	{
		return &CompanionDataCache[CompanionID];
	}
	return nullptr;
}

FCompanionInstance* UAdventurerGuildSystem::GetPlayerCompanion(AL2Character* Player, const FString& CompanionID)
{
	if (!Player)
	{
		return nullptr;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (auto& Pair : PlayerCompanions)
	{
		if (Pair.Key == PlayerID && Pair.Value.CompanionID == CompanionID)
		{
			return &Pair.Value;
		}
	}
	
	return nullptr;
}

int32 UAdventurerGuildSystem::GetPlayerCompanionCount(AL2Character* Player) const
{
	if (!Player)
	{
		return 0;
	}
	
	FString PlayerID = Player->GetUniqueID();
	int32 Count = 0;
	
	for (const auto& Pair : PlayerCompanions)
	{
		if (Pair.Key == PlayerID)
		{
			Count++;
		}
	}
	
	return Count;
}

int32 UAdventurerGuildSystem::GetMaxCompanionLimit(AL2Character* Player) const
{
	if (!Player)
	{
		return 0;
	}
	
	// Базовый лимит + бонусы от уровня и репутации
	int32 BaseLimit = 2;
	int32 LevelBonus = Player->GetLevel() / 15; // +1 компаньон каждые 15 уровней
	int32 ReputationBonus = GetGuildReputation(Player) / 100; // +1 компаньон за каждые 100 репутации
	
	return BaseLimit + LevelBonus + ReputationBonus;
}

int32 UAdventurerGuildSystem::GetGuildReputation(AL2Character* Player) const
{
	if (!Player)
	{
		return 0;
	}
	
	// Получение репутации игрока в гильдии
	if (PlayerGuildReputation.Contains(Player->GetUniqueID()))
	{
		return PlayerGuildReputation[Player->GetUniqueID()];
	}
	
	return 0;
}

int32 UAdventurerGuildSystem::CalculateDismissalCompensation(const FCompanionInstance& Companion, const FCompanionInfo& CompanionData) const
{
	// Базовая компенсация
	int32 BaseCompensation = CompanionData.HireCost / 2;
	
	// Модификаторы
	float RelationshipMultiplier = Companion.Relationship / 100.0f;
	float ExperienceMultiplier = 1.0f + (Companion.Experience / 1000.0f) * 0.3f;
	float MissionMultiplier = 1.0f + (Companion.MissionCount / 10.0f) * 0.2f;
	
	// Итоговая компенсация
	int32 FinalCompensation = FMath::RoundToInt(BaseCompensation * RelationshipMultiplier * ExperienceMultiplier * MissionMultiplier);
	
	return FMath::Max(FinalCompensation, 1); // Минимум 1 золота
}

void UAdventurerGuildSystem::TriggerCompanionDeparture(FCompanionInstance& Companion)
{
	// Создание события ухода компаньона
	FCompanionDepartureEvent DepartureEvent;
	DepartureEvent.CompanionID = Companion.CompanionID;
	DepartureEvent.OwnerID = Companion.OwnerID;
	DepartureEvent.DepartureTime = FDateTime::Now();
	DepartureEvent.Reason = TEXT("Low Relationship");
	
	// Добавление в список уходов
	CompanionDepartures.Add(DepartureEvent);
	
	// Деактивация компаньона
	Companion.bIsActive = false;
	
	UE_LOG(LogTemp, Warning, TEXT("Companion %s has departed due to low relationship!"), *Companion.CompanionID);
}

void UAdventurerGuildSystem::RemoveCompanionFromAvailable(const FString& CompanionID)
{
	AvailableCompanionList.Remove(CompanionID);
}

void UAdventurerGuildSystem::AddCompanionToAvailable(const FString& CompanionID)
{
	AvailableCompanionList.Add(CompanionID);
}

void UAdventurerGuildSystem::RemovePlayerCompanion(AL2Character* Player, const FString& CompanionID)
{
	if (!Player)
	{
		return;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (auto It = PlayerCompanions.CreateIterator(); It; ++It)
	{
		if (It.Key() == PlayerID && It.Value().CompanionID == CompanionID)
		{
			It.RemoveCurrent();
			break;
		}
	}
}

void UAdventurerGuildSystem::InitializeRelationshipSystem()
{
	// Настройка системы отношений
	RelationshipDecayTime = 120.0f; // 2 часа
	RelationshipDecayAmount = 1; // -1 отношение за 2 часа
	DepartureThreshold = 0; // Уход при отношениях <= 0
}

void UAdventurerGuildSystem::InitializeGroupQuestSystem()
{
	// Настройка групповых квестов
	ActiveGroupQuests.Empty();
}

void UAdventurerGuildSystem::SaveGuildData()
{
	// Сохранение данных гильдии
	// TODO: Реализовать сохранение в файл или базу данных
	UE_LOG(LogTemp, Warning, TEXT("Guild data saved"));
}

void UAdventurerGuildSystem::LoadGuildData()
{
	// Загрузка данных гильдии
	// TODO: Реализовать загрузку из файла или базы данных
	UE_LOG(LogTemp, Warning, TEXT("Guild data loaded"));
}
