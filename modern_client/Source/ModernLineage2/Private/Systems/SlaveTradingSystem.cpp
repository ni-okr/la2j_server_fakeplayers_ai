#include "SlaveTradingSystem.h"
#include "Engine/DataTable.h"
#include "Engine/World.h"
#include "Kismet/GameplayStatics.h"
#include "Components/ActorComponent.h"

USlaveTradingSystem::USlaveTradingSystem()
{
	PrimaryComponentTick.bCanEverTick = true;
	bEnableSlaveTrading = true;
	bEnableLoyaltySystem = true;
	bEnableRebellionSystem = true;
	bAutoSaveSlaveData = true;
}

void USlaveTradingSystem::BeginPlay()
{
	Super::BeginPlay();
	
	InitializeSlaveTradingSystem();
}

void USlaveTradingSystem::TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction)
{
	Super::TickComponent(DeltaTime, TickType, ThisTickFunction);
	
	// Обновление системы работорговли
	if (bEnableSlaveTrading)
	{
		UpdateSlaveLoyalty(DeltaTime);
		CheckForRebellions(DeltaTime);
		UpdateMarketSlaves(DeltaTime);
	}
}

void USlaveTradingSystem::InitializeSlaveTradingSystem()
{
	if (!bEnableSlaveTrading)
	{
		return;
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Initializing Slave Trading System..."));
	
	// Загрузка данных рабов
	LoadSlaveData();
	
	// Настройка невольничьего рынка
	SetupSlaveMarket();
	
	// Инициализация системы лояльности
	if (bEnableLoyaltySystem)
	{
		InitializeLoyaltySystem();
	}
	
	// Инициализация системы восстаний
	if (bEnableRebellionSystem)
	{
		InitializeRebellionSystem();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Slave Trading System initialized successfully"));
}

void USlaveTradingSystem::LoadSlaveData()
{
	if (!SlaveDataTable)
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave Data Table not set!"));
		return;
	}
	
	// Загрузка всех строк из таблицы данных
	TArray<FName> RowNames = SlaveDataTable->GetRowNames();
	
	for (const FName& RowName : RowNames)
	{
		FSlaveInfo* RowData = SlaveDataTable->FindRow<FSlaveInfo>(RowName, TEXT(""));
		if (RowData)
		{
			SlaveDataCache.Add(RowData->SlaveID, *RowData);
		}
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Loaded %d slaves from data table"), SlaveDataCache.Num());
}

bool USlaveTradingSystem::BuySlave(AL2Character* Player, const FString& SlaveID)
{
	if (!bEnableSlaveTrading || !Player)
	{
		return false;
	}
	
	// Проверка наличия раба на рынке
	if (!IsSlaveAvailableOnMarket(SlaveID))
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave %s not available on market"), *SlaveID);
		return false;
	}
	
	// Получение данных раба
	FSlaveInfo* SlaveData = GetSlaveData(SlaveID);
	if (!SlaveData)
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave data not found for %s"), *SlaveID);
		return false;
	}
	
	// Проверка достаточности средств
	if (Player->GetGold() < SlaveData->Price)
	{
		UE_LOG(LogTemp, Warning, TEXT("Insufficient funds to buy slave %s"), *SlaveID);
		return false;
	}
	
	// Проверка лимита рабов
	if (GetPlayerSlaveCount(Player) >= GetMaxSlaveLimit(Player))
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave limit reached for player"));
		return false;
	}
	
	// Создание экземпляра раба
	FSlaveInstance NewSlave;
	NewSlave.SlaveID = SlaveID;
	NewSlave.OwnerID = Player->GetUniqueID();
	NewSlave.Loyalty = SlaveData->BaseLoyalty;
	NewSlave.Experience = 0;
	NewSlave.Level = 1;
	NewSlave.bIsActive = true;
	NewSlave.PurchaseTime = FDateTime::Now();
	NewSlave.LastInteractionTime = FDateTime::Now();
	
	// Добавление раба к игроку
	PlayerSlaves.Add(Player->GetUniqueID(), NewSlave);
	
	// Списание средств
	Player->AddGold(-SlaveData->Price);
	
	// Удаление с рынка
	RemoveSlaveFromMarket(SlaveID);
	
	// Автосохранение
	if (bAutoSaveSlaveData)
	{
		SaveSlaveData();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Player bought slave %s for %d gold"), *SlaveID, SlaveData->Price);
	return true;
}

bool USlaveTradingSystem::SellSlave(AL2Character* Player, const FString& SlaveID)
{
	if (!bEnableSlaveTrading || !Player)
	{
		return false;
	}
	
	// Поиск раба у игрока
	FSlaveInstance* SlaveInstance = GetPlayerSlave(Player, SlaveID);
	if (!SlaveInstance)
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave %s not found in player's collection"), *SlaveID);
		return false;
	}
	
	// Получение данных раба
	FSlaveInfo* SlaveData = GetSlaveData(SlaveID);
	if (!SlaveData)
	{
		UE_LOG(LogTemp, Warning, TEXT("Slave data not found for %s"), *SlaveID);
		return false;
	}
	
	// Расчет цены продажи (с учетом лояльности и уровня)
	int32 SellPrice = CalculateSellPrice(*SlaveInstance, *SlaveData);
	
	// Выдача средств игроку
	Player->AddGold(SellPrice);
	
	// Удаление раба у игрока
	RemovePlayerSlave(Player, SlaveID);
	
	// Автосохранение
	if (bAutoSaveSlaveData)
	{
		SaveSlaveData();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Player sold slave %s for %d gold"), *SlaveID, SellPrice);
	return true;
}

TArray<FSlaveInfo> USlaveTradingSystem::GetAvailableSlaves() const
{
	TArray<FSlaveInfo> AvailableSlaves;
	
	for (const FString& SlaveID : CurrentMarketSlaves)
	{
		if (SlaveDataCache.Contains(SlaveID))
		{
			AvailableSlaves.Add(SlaveDataCache[SlaveID]);
		}
	}
	
	return AvailableSlaves;
}

TArray<FSlaveInstance> USlaveTradingSystem::GetPlayerSlaves(AL2Character* Player) const
{
	TArray<FSlaveInstance> PlayerSlaveList;
	
	if (!Player)
	{
		return PlayerSlaveList;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (const auto& Pair : PlayerSlaves)
	{
		if (Pair.Key == PlayerID)
		{
			PlayerSlaveList.Add(Pair.Value);
		}
	}
	
	return PlayerSlaveList;
}

void USlaveTradingSystem::ManageSlaveLoyalty(FSlaveInstance& Slave, int32 DeltaLoyalty)
{
	if (!bEnableLoyaltySystem)
	{
		return;
	}
	
	// Изменение лояльности
	Slave.Loyalty = FMath::Clamp(Slave.Loyalty + DeltaLoyalty, 0, 100);
	
	// Обновление времени последнего взаимодействия
	Slave.LastInteractionTime = FDateTime::Now();
	
	// Проверка на восстание
	if (Slave.Loyalty <= 0)
	{
		TriggerSlaveRebellion(Slave);
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Slave %s loyalty changed by %d, new loyalty: %d"), 
		*Slave.SlaveID, DeltaLoyalty, Slave.Loyalty);
}

void USlaveTradingSystem::HandleSlaveRebellion(FSlaveInstance& Slave)
{
	if (!bEnableRebellionSystem)
	{
		return;
	}
	
	// Создание события восстания
	FSlaveRebellionEvent RebellionEvent;
	RebellionEvent.SlaveID = Slave.SlaveID;
	RebellionEvent.OwnerID = Slave.OwnerID;
	RebellionEvent.RebellionTime = FDateTime::Now();
	RebellionEvent.Severity = CalculateRebellionSeverity(Slave);
	
	// Добавление в список восстаний
	ActiveRebellions.Add(RebellionEvent);
	
	// Деактивация раба
	Slave.bIsActive = false;
	
	UE_LOG(LogTemp, Warning, TEXT("Slave %s has rebelled! Severity: %d"), 
		*Slave.SlaveID, RebellionEvent.Severity);
}

void USlaveTradingSystem::SetupSlaveMarket()
{
	// Очистка текущего рынка
	CurrentMarketSlaves.Empty();
	
	// Добавление рабов на рынок
	for (const auto& Pair : SlaveDataCache)
	{
		const FSlaveInfo& SlaveData = Pair.Value;
		
		// Проверка доступности для продажи
		if (SlaveData.bAvailableForPurchase)
		{
			CurrentMarketSlaves.Add(SlaveData.SlaveID);
		}
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Slave market setup with %d slaves"), CurrentMarketSlaves.Num());
}

void USlaveTradingSystem::UpdateSlaveLoyalty(float DeltaTime)
{
	if (!bEnableLoyaltySystem)
	{
		return;
	}
	
	// Обновление лояльности всех рабов
	for (auto& Pair : PlayerSlaves)
	{
		FSlaveInstance& Slave = Pair.Value;
		
		if (Slave.bIsActive)
		{
			// Естественное снижение лояльности со временем
			float TimeSinceLastInteraction = (FDateTime::Now() - Slave.LastInteractionTime).GetTotalMinutes();
			if (TimeSinceLastInteraction > LoyaltyDecayTime)
			{
				int32 LoyaltyDecay = FMath::RoundToInt(TimeSinceLastInteraction / LoyaltyDecayTime) * LoyaltyDecayAmount;
				ManageSlaveLoyalty(Slave, -LoyaltyDecay);
			}
		}
	}
}

void USlaveTradingSystem::CheckForRebellions(float DeltaTime)
{
	if (!bEnableRebellionSystem)
	{
		return;
	}
	
	// Проверка восстаний
	for (auto& Pair : PlayerSlaves)
	{
		FSlaveInstance& Slave = Pair.Value;
		
		if (Slave.bIsActive && Slave.Loyalty <= RebellionThreshold)
		{
			// Шанс восстания увеличивается с уменьшением лояльности
			float RebellionChance = (RebellionThreshold - Slave.Loyalty) / 100.0f;
			if (FMath::RandRange(0.0f, 1.0f) < RebellionChance * DeltaTime)
			{
				HandleSlaveRebellion(Slave);
			}
		}
	}
}

void USlaveTradingSystem::UpdateMarketSlaves(float DeltaTime)
{
	// Обновление рынка рабов
	// Добавление новых рабов, удаление проданных
	// Обновление цен в зависимости от спроса
}

bool USlaveTradingSystem::IsSlaveAvailableOnMarket(const FString& SlaveID) const
{
	return CurrentMarketSlaves.Contains(SlaveID);
}

FSlaveInfo* USlaveTradingSystem::GetSlaveData(const FString& SlaveID)
{
	if (SlaveDataCache.Contains(SlaveID))
	{
		return &SlaveDataCache[SlaveID];
	}
	return nullptr;
}

FSlaveInstance* USlaveTradingSystem::GetPlayerSlave(AL2Character* Player, const FString& SlaveID)
{
	if (!Player)
	{
		return nullptr;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (auto& Pair : PlayerSlaves)
	{
		if (Pair.Key == PlayerID && Pair.Value.SlaveID == SlaveID)
		{
			return &Pair.Value;
		}
	}
	
	return nullptr;
}

int32 USlaveTradingSystem::GetPlayerSlaveCount(AL2Character* Player) const
{
	if (!Player)
	{
		return 0;
	}
	
	FString PlayerID = Player->GetUniqueID();
	int32 Count = 0;
	
	for (const auto& Pair : PlayerSlaves)
	{
		if (Pair.Key == PlayerID)
		{
			Count++;
		}
	}
	
	return Count;
}

int32 USlaveTradingSystem::GetMaxSlaveLimit(AL2Character* Player) const
{
	if (!Player)
	{
		return 0;
	}
	
	// Базовый лимит + бонусы от уровня и предметов
	int32 BaseLimit = 3;
	int32 LevelBonus = Player->GetLevel() / 10; // +1 раб каждые 10 уровней
	int32 ItemBonus = 0; // TODO: Добавить бонусы от предметов
	
	return BaseLimit + LevelBonus + ItemBonus;
}

int32 USlaveTradingSystem::CalculateSellPrice(const FSlaveInstance& Slave, const FSlaveInfo& SlaveData) const
{
	// Базовая цена
	int32 BasePrice = SlaveData.Price;
	
	// Модификаторы
	float LoyaltyMultiplier = Slave.Loyalty / 100.0f;
	float LevelMultiplier = 1.0f + (Slave.Level - 1) * 0.1f;
	float ExperienceMultiplier = 1.0f + (Slave.Experience / 1000.0f) * 0.2f;
	
	// Итоговая цена
	int32 FinalPrice = FMath::RoundToInt(BasePrice * LoyaltyMultiplier * LevelMultiplier * ExperienceMultiplier);
	
	return FMath::Max(FinalPrice, 1); // Минимум 1 золота
}

int32 USlaveTradingSystem::CalculateRebellionSeverity(const FSlaveInstance& Slave) const
{
	// Серьезность восстания зависит от уровня раба и времени владения
	int32 BaseSeverity = 1;
	int32 LevelBonus = Slave.Level / 5;
	int32 TimeBonus = (FDateTime::Now() - Slave.PurchaseTime).GetDays() / 30;
	
	return FMath::Clamp(BaseSeverity + LevelBonus + TimeBonus, 1, 10);
}

void USlaveTradingSystem::RemoveSlaveFromMarket(const FString& SlaveID)
{
	CurrentMarketSlaves.Remove(SlaveID);
}

void USlaveTradingSystem::RemovePlayerSlave(AL2Character* Player, const FString& SlaveID)
{
	if (!Player)
	{
		return;
	}
	
	FString PlayerID = Player->GetUniqueID();
	
	for (auto It = PlayerSlaves.CreateIterator(); It; ++It)
	{
		if (It.Key() == PlayerID && It.Value().SlaveID == SlaveID)
		{
			It.RemoveCurrent();
			break;
		}
	}
}

void USlaveTradingSystem::InitializeLoyaltySystem()
{
	// Настройка системы лояльности
	LoyaltyDecayTime = 60.0f; // 1 час
	LoyaltyDecayAmount = 1; // -1 лояльность за час
	RebellionThreshold = 20; // Восстание при лояльности <= 20
}

void USlaveTradingSystem::InitializeRebellionSystem()
{
	// Настройка системы восстаний
	ActiveRebellions.Empty();
}

void USlaveTradingSystem::TriggerSlaveRebellion(FSlaveInstance& Slave)
{
	// Принудительное восстание при нулевой лояльности
	HandleSlaveRebellion(Slave);
}

void USlaveTradingSystem::SaveSlaveData()
{
	// Сохранение данных рабов
	// TODO: Реализовать сохранение в файл или базу данных
	UE_LOG(LogTemp, Warning, TEXT("Slave data saved"));
}

void USlaveTradingSystem::LoadSlaveData()
{
	// Загрузка данных рабов
	// TODO: Реализовать загрузку из файла или базы данных
	UE_LOG(LogTemp, Warning, TEXT("Slave data loaded"));
}
