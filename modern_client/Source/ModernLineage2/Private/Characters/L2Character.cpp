#include "L2Character.h"
#include "Components/SkeletalMeshComponent.h"
#include "Components/StaticMeshComponent.h"
#include "Components/CapsuleComponent.h"
#include "GameFramework/CharacterMovementComponent.h"
#include "GameFramework/SpringArmComponent.h"
#include "Camera/CameraComponent.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"

// Forward declarations for custom components
class UAdultContentManager;
class UCostumeSystem;
class UInventoryComponent;
class UStatsComponent;

AL2Character::AL2Character()
{
	PrimaryActorTick.bCanEverTick = true;

	// Set size for collision capsule
	GetCapsuleComponent()->InitCapsuleSize(42.0f, 96.0f);

	// Don't rotate when the controller rotates
	bUseControllerRotationPitch = false;
	bUseControllerRotationYaw = false;
	bUseControllerRotationRoll = false;

	// Configure character movement
	GetCharacterMovement()->bOrientRotationToMovement = true;
	GetCharacterMovement()->RotationRate = FRotator(0.0f, 540.0f, 0.0f);
	GetCharacterMovement()->JumpZVelocity = 600.0f;
	GetCharacterMovement()->AirControl = 0.35f;
	GetCharacterMovement()->MaxWalkSpeed = 500.0f;
	GetCharacterMovement()->MinAnalogWalkSpeed = 20.0f;
	GetCharacterMovement()->BrakingDecelerationWalking = 2000.0f;

	// Create spring arm component
	SpringArm = CreateDefaultSubobject<USpringArmComponent>(TEXT("SpringArm"));
	SpringArm->SetupAttachment(RootComponent);
	SpringArm->TargetArmLength = 300.0f;
	SpringArm->bUsePawnControlRotation = true;

	// Create third person camera
	ThirdPersonCamera = CreateDefaultSubobject<UCameraComponent>(TEXT("ThirdPersonCamera"));
	ThirdPersonCamera->SetupAttachment(SpringArm, USpringArmComponent::SocketName);
	ThirdPersonCamera->bUsePawnControlRotation = false;

	// Initialize stats from deobfuscated code
	STR = 10;
	DEX = 10;
	CON = 10;
	INT = 10;
	WIT = 10;
	MEN = 10;

	// Initialize derived stats
	CalculateDerivedStats();

	// Initialize components
	InitializeComponents();
}

void AL2Character::BeginPlay()
{
	Super::BeginPlay();
	
	// Initialize adult content if enabled
	if (bEnableAdultContent)
	{
		InitializeAdultContent();
	}
	
	// Update appearance
	UpdateAppearance();
}

void AL2Character::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);
	
	// Update adult content if enabled
	if (bEnableAdultContent && AdultContentManager)
	{
		UpdateAdultContent();
	}
}

void AL2Character::SetupPlayerInputComponent(UInputComponent* PlayerInputComponent)
{
	Super::SetupPlayerInputComponent(PlayerInputComponent);

	// Bind movement
	PlayerInputComponent->BindAxis("MoveForward", this, &AL2Character::MoveForward);
	PlayerInputComponent->BindAxis("MoveRight", this, &AL2Character::MoveRight);

	// Bind look
	PlayerInputComponent->BindAxis("Turn", this, &AL2Character::AddControllerYawInput);
	PlayerInputComponent->BindAxis("LookUp", this, &AL2Character::AddControllerPitchInput);

	// Bind jump
	PlayerInputComponent->BindAction("Jump", IE_Pressed, this, &AL2Character::Jump);
	PlayerInputComponent->BindAction("Jump", IE_Released, this, &AL2Character::StopJumping);
}

void AL2Character::CalculateDerivedStats()
{
	// Calculate MaxHP based on CON (from deobfuscated code)
	MaxHP = 100.0f + (CON * 10.0f) + (Level * 5.0f);
	
	// Calculate MaxMP based on INT and WIT
	MaxMP = 50.0f + (INT * 8.0f) + (WIT * 5.0f) + (Level * 3.0f);
	
	// Calculate XP needed for next level
	XPToNextLevel = 1000 + (Level * 500);
	
	// Ensure current stats don't exceed maximums
	CurrentHP = FMath::Min(CurrentHP, MaxHP);
	CurrentMP = FMath::Min(CurrentMP, MaxMP);
}

void AL2Character::LevelUp()
{
	Level++;
	
	// Increase base stats slightly on level up
	STR += FMath::RandRange(1, 3);
	DEX += FMath::RandRange(1, 3);
	CON += FMath::RandRange(1, 3);
	INT += FMath::RandRange(1, 3);
	WIT += FMath::RandRange(1, 3);
	MEN += FMath::RandRange(1, 3);
	
	// Recalculate derived stats
	CalculateDerivedStats();
	
	// Handle level up effects
	OnLevelUp();
	
	// Log level up
	UE_LOG(LogTemp, Warning, TEXT("Character leveled up to level %d!"), Level);
}

float AL2Character::TakeDamage(float DamageAmount, FDamageEvent const& DamageEvent, AController* EventInstigator, AActor* DamageCauser)
{
	// Calculate armor reduction (simplified)
	float ArmorReduction = FMath::Clamp(CON * 0.5f, 0.0f, 50.0f);
	float ActualDamage = FMath::Max(0.0f, DamageAmount - ArmorReduction);
	
	// Apply damage
	CurrentHP = FMath::Max(0.0f, CurrentHP - ActualDamage);
	
	// Check if character died
	if (CurrentHP <= 0.0f)
	{
		// Handle death
		UE_LOG(LogTemp, Warning, TEXT("Character died!"));
	}
	
	return ActualDamage;
}

void AL2Character::Heal(float HealAmount)
{
	CurrentHP = FMath::Min(MaxHP, CurrentHP + HealAmount);
}

void AL2Character::RestoreMana(float ManaAmount)
{
	CurrentMP = FMath::Min(MaxMP, CurrentMP + ManaAmount);
}

bool AL2Character::IsAlive() const
{
	return CurrentHP > 0.0f;
}

float AL2Character::GetHealthPercentage() const
{
	return MaxHP > 0.0f ? CurrentHP / MaxHP : 0.0f;
}

float AL2Character::GetManaPercentage() const
{
	return MaxMP > 0.0f ? CurrentMP / MaxMP : 0.0f;
}

void AL2Character::InitializeAdultContent()
{
	// TODO: Initialize adult content manager
	// This will be implemented in the adult content system
	UE_LOG(LogTemp, Warning, TEXT("Adult content system initialized"));
}

void AL2Character::UpdateAdultContent()
{
	// TODO: Update adult content based on character state
	// This will be implemented in the adult content system
}

void AL2Character::EquipCostumePiece(const FString& SlotName, const FString& CostumeID)
{
	// TODO: Implement costume system
	// This will be implemented in the costume system
	UE_LOG(LogTemp, Warning, TEXT("Equipping costume piece %s in slot %s"), *CostumeID, *SlotName);
}

void AL2Character::RemoveCostumePiece(const FString& SlotName)
{
	// TODO: Implement costume system
	// This will be implemented in the costume system
	UE_LOG(LogTemp, Warning, TEXT("Removing costume piece from slot %s"), *SlotName);
}

FString AL2Character::GetCostumePiece(const FString& SlotName) const
{
	// TODO: Implement costume system
	// This will be implemented in the costume system
	if (CostumeSlots.Contains(SlotName))
	{
		return CostumeSlots[SlotName];
	}
	return TEXT("");
}

void AL2Character::InitializeComponents()
{
	// TODO: Initialize custom components
	// This will be implemented when we create the component classes
}

void AL2Character::SetupCamera()
{
	// Camera setup is already done in constructor
}

void AL2Character::UpdateAppearance()
{
	// TODO: Update character appearance based on costume and adult content settings
	// This will be implemented in the respective systems
}

void AL2Character::OnLevelUp()
{
	// Play level up effects
	// TODO: Add visual and audio effects for level up
	UE_LOG(LogTemp, Warning, TEXT("Level up effects triggered!"));
}

// Movement functions
void AL2Character::MoveForward(float Value)
{
	if (Value != 0.0f)
	{
		const FRotator Rotation = Controller->GetControlRotation();
		const FRotator YawRotation(0, Rotation.Yaw, 0);
		const FVector Direction = FRotationMatrix(YawRotation).GetUnitAxis(EAxis::X);
		AddMovementInput(Direction, Value);
	}
}

void AL2Character::MoveRight(float Value)
{
	if (Value != 0.0f)
	{
		const FRotator Rotation = Controller->GetControlRotation();
		const FRotator YawRotation(0, Rotation.Yaw, 0);
		const FVector Direction = FRotationMatrix(YawRotation).GetUnitAxis(EAxis::Y);
		AddMovementInput(Direction, Value);
	}
}
