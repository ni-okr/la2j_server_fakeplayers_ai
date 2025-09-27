#include "CostumeSystem.h"
#include "Engine/DataTable.h"
#include "Engine/StaticMesh.h"
#include "Components/SkeletalMeshComponent.h"
#include "Materials/MaterialInterface.h"
#include "Materials/MaterialInstanceDynamic.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"

UCostumeSystem::UCostumeSystem()
{
	PrimaryComponentTick.bCanEverTick = true;
	bEnableCostumeSystem = true;
	bEnable3DPreview = true;
	bEnableDragAndDrop = true;
	bAutoSaveChanges = true;
}

void UCostumeSystem::BeginPlay()
{
	Super::BeginPlay();
	
	InitializeCostumeSystem();
}

void UCostumeSystem::TickComponent(float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction)
{
	Super::TickComponent(DeltaTime, TickType, ThisTickFunction);
	
	// Обновление системы костюмов
	if (bEnableCostumeSystem)
	{
		UpdateCostumeAppearance();
	}
}

void UCostumeSystem::InitializeCostumeSystem()
{
	if (!bEnableCostumeSystem)
	{
		return;
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Initializing Costume System..."));
	
	// Загрузка данных костюмов
	LoadCostumeData();
	
	// Настройка слотов по умолчанию
	SetupDefaultCostumeSlots();
	
	// Инициализация кэша
	CostumePieceCache.Empty();
	LoadedCostumePieces.Empty();
	SavedCostumeConfiguration.Empty();
	
	UE_LOG(LogTemp, Warning, TEXT("Costume System initialized successfully"));
}

void UCostumeSystem::LoadCostumeData()
{
	if (!CostumeDataTable)
	{
		UE_LOG(LogTemp, Warning, TEXT("Costume Data Table not set!"));
		return;
	}
	
	// Загрузка всех строк из таблицы данных
	TArray<FName> RowNames = CostumeDataTable->GetRowNames();
	
	for (const FName& RowName : RowNames)
	{
		FCostumePieceData* RowData = CostumeDataTable->FindRow<FCostumePieceData>(RowName, TEXT(""));
		if (RowData)
		{
			CostumePieceCache.Add(RowData->CostumeID, *RowData);
		}
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Loaded %d costume pieces from data table"), CostumePieceCache.Num());
}

bool UCostumeSystem::EquipCostumePiece(const FString& SlotName, const FString& CostumeID)
{
	if (!bEnableCostumeSystem)
	{
		return false;
	}
	
	// Проверка валидности костюма
	if (!ValidateCostumePiece(SlotName, CostumeID))
	{
		UE_LOG(LogTemp, Warning, TEXT("Invalid costume piece %s for slot %s"), *CostumeID, *SlotName);
		return false;
	}
	
	// Проверка владения костюмом
	if (!IsCostumePieceOwned(CostumeID))
	{
		UE_LOG(LogTemp, Warning, TEXT("Costume piece %s not owned"), *CostumeID);
		return false;
	}
	
	// Создание или обновление слота
	FCostumeSlotData SlotData;
	SlotData.SlotName = SlotName;
	SlotData.EquippedCostumeID = CostumeID;
	SlotData.bVisible = true;
	SlotData.Priority = GetSlotPriority(SlotName);
	
	CostumeSlots.Add(SlotName, SlotData);
	
	// Применение костюма к персонажу
	ApplyCostumePiece(SlotName, CostumeID);
	
	// Автосохранение
	if (bAutoSaveChanges)
	{
		SaveCostumeConfiguration();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Equipped costume piece %s in slot %s"), *CostumeID, *SlotName);
	return true;
}

bool UCostumeSystem::UnequipCostumePiece(const FString& SlotName)
{
	if (!CostumeSlots.Contains(SlotName))
	{
		return false;
	}
	
	// Удаление костюма с персонажа
	RemoveCostumePiece(SlotName);
	
	// Удаление из слотов
	CostumeSlots.Remove(SlotName);
	
	// Автосохранение
	if (bAutoSaveChanges)
	{
		SaveCostumeConfiguration();
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Unequipped costume piece from slot %s"), *SlotName);
	return true;
}

FString UCostumeSystem::GetEquippedCostumePiece(const FString& SlotName) const
{
	if (CostumeSlots.Contains(SlotName))
	{
		return CostumeSlots[SlotName].EquippedCostumeID;
	}
	return TEXT("");
}

FCostumePieceData UCostumeSystem::GetCostumePieceData(const FString& CostumeID) const
{
	if (CostumePieceCache.Contains(CostumeID))
	{
		return CostumePieceCache[CostumeID];
	}
	
	// Возврат пустых данных если костюм не найден
	return FCostumePieceData();
}

bool UCostumeSystem::IsCostumePieceOwned(const FString& CostumeID) const
{
	return OwnedCostumePieces.Contains(CostumeID);
}

bool UCostumeSystem::PurchaseCostumePiece(const FString& CostumeID, int32 Price)
{
	// Проверка наличия костюма в кэше
	if (!CostumePieceCache.Contains(CostumeID))
	{
		UE_LOG(LogTemp, Warning, TEXT("Costume piece %s not found in cache"), *CostumeID);
		return false;
	}
	
	const FCostumePieceData& CostumeData = CostumePieceCache[CostumeID];
	
	// Проверка доступности для покупки
	if (!CostumeData.bAvailableForPurchase)
	{
		UE_LOG(LogTemp, Warning, TEXT("Costume piece %s not available for purchase"), *CostumeID);
		return false;
	}
	
	// Проверка цены
	if (Price < CostumeData.Price)
	{
		UE_LOG(LogTemp, Warning, TEXT("Insufficient funds for costume piece %s"), *CostumeID);
		return false;
	}
	
	// Добавление в коллекцию
	OwnedCostumePieces.Add(CostumeID);
	
	UE_LOG(LogTemp, Warning, TEXT("Purchased costume piece %s for %d gold"), *CostumeID, CostumeData.Price);
	return true;
}

TArray<FString> UCostumeSystem::GetAvailableCostumePieces(const FString& SlotName) const
{
	TArray<FString> AvailablePieces;
	
	for (const auto& Pair : CostumePieceCache)
	{
		const FCostumePieceData& CostumeData = Pair.Value;
		
		// Проверка соответствия слоту
		if (CostumeData.SlotName == SlotName)
		{
			// Проверка владения
			if (IsCostumePieceOwned(CostumeData.CostumeID))
			{
				AvailablePieces.Add(CostumeData.CostumeID);
			}
		}
	}
	
	return AvailablePieces;
}

TArray<FString> UCostumeSystem::GetCostumePiecesByCategory(const FString& Category) const
{
	TArray<FString> CategoryPieces;
	
	for (const auto& Pair : CostumePieceCache)
	{
		const FCostumePieceData& CostumeData = Pair.Value;
		
		if (CostumeData.Category == Category)
		{
			CategoryPieces.Add(CostumeData.CostumeID);
		}
	}
	
	return CategoryPieces;
}

void UCostumeSystem::SaveCostumeConfiguration()
{
	SavedCostumeConfiguration.Empty();
	
	for (const auto& Pair : CostumeSlots)
	{
		const FCostumeSlotData& SlotData = Pair.Value;
		SavedCostumeConfiguration.Add(SlotData.SlotName, SlotData.EquippedCostumeID);
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Costume configuration saved"));
}

void UCostumeSystem::LoadCostumeConfiguration()
{
	CostumeSlots.Empty();
	
	for (const auto& Pair : SavedCostumeConfiguration)
	{
		const FString& SlotName = Pair.Key;
		const FString& CostumeID = Pair.Value;
		
		// Восстановление слота
		FCostumeSlotData SlotData;
		SlotData.SlotName = SlotName;
		SlotData.EquippedCostumeID = CostumeID;
		SlotData.bVisible = true;
		SlotData.Priority = GetSlotPriority(SlotName);
		
		CostumeSlots.Add(SlotName, SlotData);
		
		// Применение костюма
		ApplyCostumePiece(SlotName, CostumeID);
	}
	
	UE_LOG(LogTemp, Warning, TEXT("Costume configuration loaded"));
}

void UCostumeSystem::ResetToDefaultCostume()
{
	CostumeSlots.Empty();
	LoadedCostumePieces.Empty();
	
	// Сброс к базовому костюму
	SetupDefaultCostumeSlots();
	
	UE_LOG(LogTemp, Warning, TEXT("Reset to default costume"));
}

int32 UCostumeSystem::GetTotalCostumeValue() const
{
	int32 TotalValue = 0;
	
	for (const FString& CostumeID : OwnedCostumePieces)
	{
		if (CostumePieceCache.Contains(CostumeID))
		{
			TotalValue += CostumePieceCache[CostumeID].Price;
		}
	}
	
	return TotalValue;
}

void UCostumeSystem::SetupDefaultCostumeSlots()
{
	// Создание слотов по умолчанию
	TArray<FString> DefaultSlots = {
		TEXT("Upper"),      // Верх
		TEXT("Lower"),      // Низ
		TEXT("Shoes"),      // Обувь
		TEXT("Accessories"), // Аксессуары
		TEXT("Hair"),       // Волосы
		TEXT("Face"),       // Лицо
		TEXT("Hands"),      // Руки
		TEXT("Feet")        // Ноги
	};
	
	for (const FString& SlotName : DefaultSlots)
	{
		FCostumeSlotData SlotData;
		SlotData.SlotName = SlotName;
		SlotData.EquippedCostumeID = TEXT("");
		SlotData.bVisible = true;
		SlotData.Priority = GetSlotPriority(SlotName);
		
		CostumeSlots.Add(SlotName, SlotData);
	}
}

void UCostumeSystem::ApplyCostumePiece(const FString& SlotName, const FString& CostumeID)
{
	if (!CostumePieceCache.Contains(CostumeID))
	{
		return;
	}
	
	const FCostumePieceData& CostumeData = CostumePieceCache[CostumeID];
	
	// Загрузка меша костюма
	if (CostumeData.MeshAsset.IsValid())
	{
		USkeletalMesh* CostumeMesh = CostumeData.MeshAsset.LoadSynchronous();
		if (CostumeMesh)
		{
			// Создание компонента меша
			USkeletalMeshComponent* MeshComponent = NewObject<USkeletalMeshComponent>(GetOwner());
			MeshComponent->SetSkeletalMesh(CostumeMesh);
			
			// Применение материалов
			for (int32 i = 0; i < CostumeData.MaterialAssets.Num(); i++)
			{
				if (CostumeData.MaterialAssets[i].IsValid())
				{
					UMaterialInterface* Material = CostumeData.MaterialAssets[i].LoadSynchronous();
					if (Material)
					{
						MeshComponent->SetMaterial(i, Material);
					}
				}
			}
			
			// Добавление в загруженные костюмы
			LoadedCostumePieces.Add(CostumeID, MeshComponent);
			
			UE_LOG(LogTemp, Warning, TEXT("Applied costume piece %s to slot %s"), *CostumeID, *SlotName);
		}
	}
}

void UCostumeSystem::RemoveCostumePiece(const FString& SlotName)
{
	// Поиск и удаление костюма из слота
	for (const auto& Pair : LoadedCostumePieces)
	{
		const FString& CostumeID = Pair.Key;
		USkeletalMeshComponent* MeshComponent = Pair.Value;
		
		// Проверка принадлежности к слоту
		if (CostumeSlots.Contains(SlotName) && CostumeSlots[SlotName].EquippedCostumeID == CostumeID)
		{
			// Удаление компонента
			if (MeshComponent)
			{
				MeshComponent->DestroyComponent();
			}
			
			LoadedCostumePieces.Remove(CostumeID);
			break;
		}
	}
}

void UCostumeSystem::UpdateCostumeAppearance()
{
	// Обновление внешнего вида персонажа на основе текущих костюмов
	// Это место для дополнительной логики обновления
}

bool UCostumeSystem::ValidateCostumePiece(const FString& SlotName, const FString& CostumeID) const
{
	if (!CostumePieceCache.Contains(CostumeID))
	{
		return false;
	}
	
	const FCostumePieceData& CostumeData = CostumePieceCache[CostumeID];
	
	// Проверка соответствия слоту
	if (CostumeData.SlotName != SlotName)
	{
		return false;
	}
	
	// Проверка уровня персонажа
	// TODO: Добавить проверку уровня персонажа
	
	// Проверка гендерных ограничений
	// TODO: Добавить проверку гендерных ограничений
	
	return true;
}

void UCostumeSystem::LoadCostumePieceAssets(const FString& CostumeID)
{
	if (!CostumePieceCache.Contains(CostumeID))
	{
		return;
	}
	
	const FCostumePieceData& CostumeData = CostumePieceCache[CostumeID];
	
	// Предзагрузка меша
	if (CostumeData.MeshAsset.IsValid())
	{
		CostumeData.MeshAsset.LoadSynchronous();
	}
	
	// Предзагрузка материалов
	for (const TSoftObjectPtr<UMaterialInterface>& MaterialAsset : CostumeData.MaterialAssets)
	{
		if (MaterialAsset.IsValid())
		{
			MaterialAsset.LoadSynchronous();
		}
	}
}

void UCostumeSystem::UnloadCostumePieceAssets(const FString& CostumeID)
{
	// Выгрузка ресурсов костюма из памяти
	// TODO: Реализовать выгрузку ресурсов
}

int32 UCostumeSystem::GetSlotPriority(const FString& SlotName) const
{
	// Приоритеты слотов для правильного наложения
	if (SlotName == TEXT("Upper"))
		return 100;
	else if (SlotName == TEXT("Lower"))
		return 90;
	else if (SlotName == TEXT("Shoes"))
		return 80;
	else if (SlotName == TEXT("Accessories"))
		return 70;
	else if (SlotName == TEXT("Hair"))
		return 60;
	else if (SlotName == TEXT("Face"))
		return 50;
	else if (SlotName == TEXT("Hands"))
		return 40;
	else if (SlotName == TEXT("Feet"))
		return 30;
	
	return 0;
}
