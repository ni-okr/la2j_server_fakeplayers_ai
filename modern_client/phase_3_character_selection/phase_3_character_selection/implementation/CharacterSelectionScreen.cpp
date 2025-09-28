
// CharacterSelectionScreen.cpp
#include "CharacterSelectionScreen.h"
#include "Components/ScrollBox.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"
#include "CharacterSelectionFontSetup.cpp"
#include "CharacterSelectionAnimationSystem.cpp"
#include "CharacterSelectionVisualEffects.cpp"
#include "CharacterManagementSystem.cpp"
#include "CharacterValidationSystem.cpp"
#include "CharacterScreenTransitions.cpp"
#include "AccessibilityEnhancements.cpp"
#include "SecurityEnhancements.cpp"
#include "PerformanceOptimization.cpp"

UCharacterSelectionScreen::UCharacterSelectionScreen(const FObjectInitializer& ObjectInitializer)
    : Super(ObjectInitializer)
{
    // Инициализация переменных
    SelectedCharacterIndex = -1;
    MaxCharacters = 7;
}

void UCharacterSelectionScreen::NativeConstruct()
{
    Super::NativeConstruct();

    // Настройка шрифтов в соответствии с эталоном
    FCharacterSelectionFontSetup::SetupCharacterSelectionFonts(this);
    
    // Настройка анимаций для всех элементов
    FCharacterSelectionAnimationSystem::SetupCharacterSelectionAnimations(this);
    
    // Настройка визуальных эффектов
    FCharacterSelectionVisualEffects::SetupCharacterSelectionEffects(this);

    // Инициализация системы управления персонажами
    FCharacterManagementSystem::InitializeCharacterManagement(this);
    
    // Инициализация системы валидации
    FCharacterValidationSystem::InitializeValidationSystem(this);
    
    // Инициализация системы переходов между экранами
    FCharacterScreenTransitions::InitializeScreenTransitions(this);

    // Инициализация системы улучшения доступности
    FAccessibilityEnhancements::InitializeAccessibilityEnhancements(this);
    
    // Инициализация системы усиления безопасности
    FSecurityEnhancements::InitializeSecurityEnhancements(this);
    
    // Инициализация системы оптимизации производительности
    FPerformanceOptimization::InitializePerformanceOptimization(this);

    // Привязка событий к кнопкам
    if (CreateCharacterButton)
    {
        CreateCharacterButton->OnClicked.AddDynamic(this, &UCharacterSelectionScreen::OnCreateCharacterButtonClicked);
    }

    if (DeleteCharacterButton)
    {
        DeleteCharacterButton->OnClicked.AddDynamic(this, &UCharacterSelectionScreen::OnDeleteCharacterButtonClicked);
    }

    if (EnterGameButton)
    {
        EnterGameButton->OnClicked.AddDynamic(this, &UCharacterSelectionScreen::OnEnterGameButtonClicked);
    }

    if (BackButton)
    {
        BackButton->OnClicked.AddDynamic(this, &UCharacterSelectionScreen::OnBackButtonClicked);
    }

    // Загрузка списка персонажей
    LoadCharacterList();

    // Запуск анимации появления экрана
    FCharacterSelectionAnimationUtils::PlayScreenAppearanceAnimation(this);

    UE_LOG(LogTemp, Log, TEXT("Экран выбора персонажей инициализирован с полной настройкой"));
}

void UCharacterSelectionScreen::OnCreateCharacterButtonClicked()
{
    // Обработка создания персонажа через систему переходов
    FCharacterScreenTransitionEventHandlers::HandleCharacterCreationTransition(this);
}

void UCharacterSelectionScreen::OnDeleteCharacterButtonClicked()
{
    // Валидация операции удаления персонажа
    if (FCharacterValidationUtils::ValidateCharacterDeletion(SelectedCharacterIndex, CharacterList.Num()))
    {
        // Обработка удаления персонажа через систему управления
        FCharacterManagementEventHandlers::HandleCharacterDeletion(this, SelectedCharacterIndex);
    }
    else
    {
        // Показываем ошибку валидации
        FCharacterValidationErrorDisplay::ShowValidationError(this, 
            TEXT("Не выбран персонаж для удаления"), TEXT("Warning"));
    }
}

void UCharacterSelectionScreen::OnEnterGameButtonClicked()
{
    // Валидация операции входа в игру
    if (FCharacterValidationUtils::ValidateGameEntry(SelectedCharacterIndex, CharacterList.Num()))
    {
        // Обработка входа в игру через систему переходов
        FCharacterScreenTransitionEventHandlers::HandleGameWorldTransition(this, SelectedCharacterIndex);
    }
    else
    {
        // Показываем ошибку валидации
        FCharacterValidationErrorDisplay::ShowValidationError(this, 
            TEXT("Не выбран персонаж для входа в игру"), TEXT("Warning"));
    }
}

void UCharacterSelectionScreen::OnBackButtonClicked()
{
    // Обработка возврата к экрану входа через систему переходов
    FCharacterScreenTransitionEventHandlers::HandleLoginScreenTransition(this);
}

void UCharacterSelectionScreen::OnCharacterSlotClicked(int32 CharacterIndex)
{
    // Валидация операции выбора персонажа
    if (FCharacterValidationUtils::ValidateCharacterSelection(CharacterIndex, CharacterList.Num()))
    {
        // Обработка выбора персонажа через систему управления
        FCharacterManagementEventHandlers::HandleCharacterSelected(this, CharacterIndex);
        SelectCharacter(CharacterIndex);
    }
    else
    {
        // Показываем ошибку валидации
        FCharacterValidationErrorDisplay::ShowValidationError(this, 
            TEXT("Неверный индекс персонажа"), TEXT("Error"));
    }
}

void UCharacterSelectionScreen::LoadCharacterList()
{
    // Очистка текущего списка
    CharacterList.Empty();

    // В реальной реализации здесь будет загрузка данных с сервера
    // Пока что создаем тестовые данные
    FCharacterData TestCharacter1;
    TestCharacter1.CharacterName = TEXT("ТестовыйВоин");
    TestCharacter1.Level = 25;
    TestCharacter1.CharacterClass = TEXT("Воин");
    TestCharacter1.Location = TEXT("Гиран");
    TestCharacter1.AvatarPath = TEXT("/Game/UI/CharacterAvatars/Warrior.png");
    CharacterList.Add(TestCharacter1);

    FCharacterData TestCharacter2;
    TestCharacter2.CharacterName = TEXT("ТестовыйМаг");
    TestCharacter2.Level = 18;
    TestCharacter2.CharacterClass = TEXT("Маг");
    TestCharacter2.Location = TEXT("Аден");
    TestCharacter2.AvatarPath = TEXT("/Game/UI/CharacterAvatars/Mage.png");
    CharacterList.Add(TestCharacter2);

    // Обновление интерфейса
    RefreshCharacterList();

    UE_LOG(LogTemp, Log, TEXT("Загружено персонажей: %d"), CharacterList.Num());
}

void UCharacterSelectionScreen::RefreshCharacterList()
{
    if (!CharacterListPanel)
    {
        return;
    }

    // Очистка панели
    CharacterListPanel->ClearChildren();

    // Создание слотов для каждого персонажа
    for (int32 i = 0; i < CharacterList.Num(); i++)
    {
        UUserWidget* CharacterSlot = CreateCharacterSlot(CharacterList[i], i);
        if (CharacterSlot)
        {
            CharacterListPanel->AddChild(CharacterSlot);
        }
    }
}

void UCharacterSelectionScreen::SelectCharacter(int32 CharacterIndex)
{
    if (CharacterIndex >= 0 && CharacterIndex < CharacterList.Num())
    {
        // Сброс предыдущего выбора
        for (int32 i = 0; i < CharacterList.Num(); i++)
        {
            CharacterList[i].bIsSelected = false;
        }

        // Выбор нового персонажа
        CharacterList[CharacterIndex].bIsSelected = true;
        SelectedCharacterIndex = CharacterIndex;

        // Обновление интерфейса
        RefreshCharacterList();

        UE_LOG(LogTemp, Log, TEXT("Выбран персонаж: %s"), *CharacterList[CharacterIndex].CharacterName);
    }
}

void UCharacterSelectionScreen::DeleteSelectedCharacter()
{
    if (SelectedCharacterIndex >= 0 && SelectedCharacterIndex < CharacterList.Num())
    {
        FString CharacterName = CharacterList[SelectedCharacterIndex].CharacterName;
        
        // Удаление персонажа из списка
        CharacterList.RemoveAt(SelectedCharacterIndex);
        SelectedCharacterIndex = -1;

        // Обновление интерфейса
        RefreshCharacterList();

        UE_LOG(LogTemp, Log, TEXT("Удален персонаж: %s"), *CharacterName);
    }
}

void UCharacterSelectionScreen::EnterGameWithSelectedCharacter()
{
    if (SelectedCharacterIndex >= 0 && SelectedCharacterIndex < CharacterList.Num())
    {
        FString CharacterName = CharacterList[SelectedCharacterIndex].CharacterName;
        
        // Вход в игру с выбранным персонажем
        UE_LOG(LogTemp, Log, TEXT("Вход в игру с персонажем: %s"), *CharacterName);
        
        // UGameplayStatics::OpenLevel(GetWorld(), "GameMap");
    }
}

UUserWidget* UCharacterSelectionScreen::CreateCharacterSlot(const FCharacterData& CharacterData, int32 Index)
{
    // В реальной реализации здесь будет создание виджета слота персонажа
    // Пока что возвращаем nullptr
    return nullptr;
}

void UCharacterSelectionScreen::UpdateCharacterSlot(UUserWidget* SlotWidget, const FCharacterData& CharacterData, int32 Index)
{
    // В реальной реализации здесь будет обновление виджета слота персонажа
    // Пока что просто логируем
    UE_LOG(LogTemp, Log, TEXT("Обновление слота персонажа: %s"), *CharacterData.CharacterName);
}
