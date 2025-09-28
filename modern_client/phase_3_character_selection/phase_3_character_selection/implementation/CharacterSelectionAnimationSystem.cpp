#include "CoreMinimal.h"
#include "Components/Button.h"
#include "Components/ScrollBox.h"
#include "Components/Image.h"
#include "Animation/WidgetAnimation.h"
#include "Animation/UMGSequencePlayer.h"

/**
 * Система анимаций для экрана выбора персонажей в соответствии с эталонным клиентом
 * Создает плавные анимации для выбора персонажей, кнопок и переходов
 */
class FCharacterSelectionAnimationSystem
{
public:
    /**
     * Настройка всех анимаций для экрана выбора персонажей
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void SetupCharacterSelectionAnimations(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("CharacterSelectionWidget is null"));
            return;
        }

        // Настройка анимаций кнопок
        SetupButtonAnimations(CharacterSelectionWidget);
        
        // Настройка анимаций слотов персонажей
        SetupCharacterSlotAnimations(CharacterSelectionWidget);
        
        // Настройка анимации появления экрана
        SetupScreenAppearanceAnimation(CharacterSelectionWidget);
        
        // Настройка анимации списка персонажей
        SetupCharacterListAnimation(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Анимации экрана выбора персонажей настроены"));
    }

private:
    /**
     * Настройка анимаций для кнопок
     */
    static void SetupButtonAnimations(UUserWidget* CharacterSelectionWidget)
    {
        // Анимация кнопки "Создать персонажа"
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton)
        {
            SetupButtonHoverAnimation(CreateCharacterButton, TEXT("CreateCharacterButton"));
            SetupButtonClickAnimation(CreateCharacterButton, TEXT("CreateCharacterButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Создать персонажа' настроены"));
        }
        
        // Анимация кнопки "Удалить персонажа"
        UButton* DeleteCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("DeleteCharacterButton"));
        if (DeleteCharacterButton)
        {
            SetupButtonHoverAnimation(DeleteCharacterButton, TEXT("DeleteCharacterButton"));
            SetupButtonClickAnimation(DeleteCharacterButton, TEXT("DeleteCharacterButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Удалить персонажа' настроены"));
        }
        
        // Анимация кнопки "Войти в игру"
        UButton* EnterGameButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("EnterGameButton"));
        if (EnterGameButton)
        {
            SetupButtonHoverAnimation(EnterGameButton, TEXT("EnterGameButton"));
            SetupButtonClickAnimation(EnterGameButton, TEXT("EnterGameButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Войти в игру' настроены"));
        }
        
        // Анимация кнопки "Назад"
        UButton* BackButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("BackButton"));
        if (BackButton)
        {
            SetupButtonHoverAnimation(BackButton, TEXT("BackButton"));
            SetupButtonClickAnimation(BackButton, TEXT("BackButton"));
            UE_LOG(LogTemp, Log, TEXT("Анимации кнопки 'Назад' настроены"));
        }
    }

    /**
     * Настройка анимации наведения на кнопку
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonHoverAnimation(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем анимацию наведения (увеличение масштаба на 5%)
        FWidgetAnimationData HoverAnimation;
        HoverAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Hover"), *ButtonName));
        HoverAnimation.Duration = 0.2f; // Быстрая анимация 200мс
        HoverAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (100% масштаб)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        StartFrame.ColorAndOpacity = FLinearColor::White;
        HoverAnimation.KeyFrames.Add(StartFrame);
        
        // Конечное состояние (105% масштаб, легкое свечение)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.2f;
        EndFrame.Transform.Scale = FVector2D(1.05f, 1.05f);
        EndFrame.ColorAndOpacity = FLinearColor(1.1f, 1.1f, 1.1f, 1.0f); // Легкое свечение
        HoverAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к кнопке
        Button->SetHoveredAnimation(HoverAnimation);
    }

    /**
     * Настройка анимации нажатия кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonClickAnimation(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем анимацию нажатия (уменьшение масштаба на 3%)
        FWidgetAnimationData ClickAnimation;
        ClickAnimation.AnimationName = FName(*FString::Printf(TEXT("%s_Click"), *ButtonName));
        ClickAnimation.Duration = 0.1f; // Очень быстрая анимация 100мс
        ClickAnimation.EasingType = EWidgetAnimationEasing::EaseInOut;
        
        // Начальное состояние (100% масштаб)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        StartFrame.ColorAndOpacity = FLinearColor::White;
        ClickAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние нажатия (97% масштаб, затемнение)
        FWidgetAnimationKeyFrame PressFrame;
        PressFrame.Time = 0.05f;
        PressFrame.Transform.Scale = FVector2D(0.97f, 0.97f);
        PressFrame.ColorAndOpacity = FLinearColor(0.9f, 0.9f, 0.9f, 1.0f);
        ClickAnimation.KeyFrames.Add(PressFrame);
        
        // Возврат к исходному состоянию
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.1f;
        EndFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        EndFrame.ColorAndOpacity = FLinearColor::White;
        ClickAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к кнопке
        Button->SetPressedAnimation(ClickAnimation);
    }

    /**
     * Настройка анимаций для слотов персонажей
     */
    static void SetupCharacterSlotAnimations(UUserWidget* CharacterSelectionWidget)
    {
        // Получаем панель списка персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (!CharacterListPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("CharacterListPanel не найден"));
            return;
        }

        // Настраиваем анимации для каждого слота персонажа
        for (int32 i = 0; i < CharacterListPanel->GetChildrenCount(); i++)
        {
            UUserWidget* CharacterSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(i));
            if (CharacterSlot)
            {
                SetupCharacterSlotHoverAnimation(CharacterSlot, i);
                SetupCharacterSlotSelectionAnimation(CharacterSlot, i);
                SetupCharacterSlotAppearanceAnimation(CharacterSlot, i);
            }
        }
    }

    /**
     * Настройка анимации наведения на слот персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotHoverAnimation(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем анимацию наведения (подсветка фона)
        FWidgetAnimationData HoverAnimation;
        HoverAnimation.AnimationName = FName(*FString::Printf(TEXT("CharacterSlot%d_Hover"), SlotIndex));
        HoverAnimation.Duration = 0.3f; // Плавная анимация 300мс
        HoverAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (обычный фон)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("2D2D2D")));
        HoverAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние наведения (светлый фон)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.3f;
        EndFrame.ColorAndOpacity = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("3D3D3D")));
        HoverAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к слоту персонажа
        CharacterSlot->SetHoveredAnimation(HoverAnimation);
    }

    /**
     * Настройка анимации выбора слота персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotSelectionAnimation(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем анимацию выбора (золотая рамка)
        FWidgetAnimationData SelectionAnimation;
        SelectionAnimation.AnimationName = FName(*FString::Printf(TEXT("CharacterSlot%d_Selection"), SlotIndex));
        SelectionAnimation.Duration = 0.4f; // Плавная анимация 400мс
        SelectionAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (без рамки)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.BorderColor = FLinearColor::Transparent;
        StartFrame.BorderThickness = 0.0f;
        SelectionAnimation.KeyFrames.Add(StartFrame);
        
        // Состояние выбора (золотая рамка)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.4f;
        EndFrame.BorderColor = FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700")));
        EndFrame.BorderThickness = 2.0f;
        SelectionAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к слоту персонажа
        CharacterSlot->SetSelectionAnimation(SelectionAnimation);
    }

    /**
     * Настройка анимации появления слота персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotAppearanceAnimation(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем анимацию появления (fade in + slide from left)
        FWidgetAnimationData AppearanceAnimation;
        AppearanceAnimation.AnimationName = FName(*FString::Printf(TEXT("CharacterSlot%d_Appearance"), SlotIndex));
        AppearanceAnimation.Duration = 0.5f; // Анимация 500мс
        AppearanceAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (прозрачный, сдвинут влево)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Translation = FVector2D(-50.0f, 0.0f);
        StartFrame.ColorAndOpacity = FLinearColor(1.0f, 1.0f, 1.0f, 0.0f);
        AppearanceAnimation.KeyFrames.Add(StartFrame);
        
        // Конечное состояние (видимый, в правильной позиции)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.5f;
        EndFrame.Transform.Translation = FVector2D(0.0f, 0.0f);
        EndFrame.ColorAndOpacity = FLinearColor::White;
        AppearanceAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к слоту персонажа
        CharacterSlot->SetAppearanceAnimation(AppearanceAnimation);
    }

    /**
     * Настройка анимации появления экрана
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void SetupScreenAppearanceAnimation(UUserWidget* CharacterSelectionWidget)
    {
        // Создаем анимацию появления экрана (fade in + scale)
        FWidgetAnimationData ScreenAnimation;
        ScreenAnimation.AnimationName = TEXT("ScreenAppearance");
        ScreenAnimation.Duration = 0.8f; // Плавная анимация 800мс
        ScreenAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (прозрачный, уменьшенный)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Scale = FVector2D(0.9f, 0.9f);
        StartFrame.ColorAndOpacity = FLinearColor(1.0f, 1.0f, 1.0f, 0.0f); // Полностью прозрачный
        ScreenAnimation.KeyFrames.Add(StartFrame);
        
        // Промежуточное состояние (полупрозрачный, нормальный размер)
        FWidgetAnimationKeyFrame MidFrame;
        MidFrame.Time = 0.4f;
        MidFrame.Transform.Scale = FVector2D(1.02f, 1.02f); // Легкое превышение
        MidFrame.ColorAndOpacity = FLinearColor(1.0f, 1.0f, 1.0f, 0.7f);
        ScreenAnimation.KeyFrames.Add(MidFrame);
        
        // Конечное состояние (полностью видимый, нормальный размер)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.8f;
        EndFrame.Transform.Scale = FVector2D(1.0f, 1.0f);
        EndFrame.ColorAndOpacity = FLinearColor::White;
        ScreenAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к экрану
        CharacterSelectionWidget->SetAppearanceAnimation(ScreenAnimation);
        UE_LOG(LogTemp, Log, TEXT("Анимация появления экрана настроена"));
    }

    /**
     * Настройка анимации списка персонажей
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void SetupCharacterListAnimation(UUserWidget* CharacterSelectionWidget)
    {
        // Получаем панель списка персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (!CharacterListPanel)
        {
            return;
        }

        // Создаем анимацию прокрутки списка
        FWidgetAnimationData ScrollAnimation;
        ScrollAnimation.AnimationName = TEXT("CharacterListScroll");
        ScrollAnimation.Duration = 0.3f; // Быстрая анимация 300мс
        ScrollAnimation.EasingType = EWidgetAnimationEasing::EaseOut;
        
        // Начальное состояние (обычная прокрутка)
        FWidgetAnimationKeyFrame StartFrame;
        StartFrame.Time = 0.0f;
        StartFrame.Transform.Translation = FVector2D(0.0f, 0.0f);
        ScrollAnimation.KeyFrames.Add(StartFrame);
        
        // Конечное состояние (плавная прокрутка)
        FWidgetAnimationKeyFrame EndFrame;
        EndFrame.Time = 0.3f;
        EndFrame.Transform.Translation = FVector2D(0.0f, 0.0f);
        ScrollAnimation.KeyFrames.Add(EndFrame);
        
        // Применяем анимацию к панели списка
        CharacterListPanel->SetScrollAnimation(ScrollAnimation);
        UE_LOG(LogTemp, Log, TEXT("Анимация списка персонажей настроена"));
    }
};

/**
 * Утилиты для работы с анимациями экрана выбора персонажей
 */
class FCharacterSelectionAnimationUtils
{
public:
    /**
     * Запуск анимации появления экрана
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void PlayScreenAppearanceAnimation(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        // Запускаем анимацию появления
        CharacterSelectionWidget->PlayAppearanceAnimation();
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация появления экрана выбора персонажей"));
    }

    /**
     * Запуск анимации выбора персонажа
     * @param CharacterSlot - слот персонажа
     * @param SlotIndex - индекс слота
     */
    static void PlayCharacterSelectionAnimation(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Запускаем анимацию выбора
        CharacterSlot->PlaySelectionAnimation();
        UE_LOG(LogTemp, Log, TEXT("Запущена анимация выбора персонажа: %d"), SlotIndex);
    }

    /**
     * Остановка всех анимаций экрана
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void StopAllAnimations(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        // Останавливаем все анимации
        CharacterSelectionWidget->StopAllAnimations();
        UE_LOG(LogTemp, Log, TEXT("Все анимации экрана выбора персонажей остановлены"));
    }

    /**
     * Проверка соответствия анимаций эталону
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     * @return bool - соответствуют ли анимации эталону
     */
    static bool ValidateAnimationCompliance(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            return false;
        }

        // Проверяем наличие всех необходимых анимаций
        bool bAllAnimationsPresent = true;
        
        // Проверяем анимации кнопок
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton && !CreateCharacterButton->HasHoverAnimation())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует анимация наведения для кнопки 'Создать персонажа'"));
            bAllAnimationsPresent = false;
        }
        
        // Проверяем анимации слотов персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (CharacterListPanel && CharacterListPanel->GetChildrenCount() > 0)
        {
            UUserWidget* FirstSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(0));
            if (FirstSlot && !FirstSlot->HasHoverAnimation())
            {
                UE_LOG(LogTemp, Warning, TEXT("Отсутствует анимация наведения для слотов персонажей"));
                bAllAnimationsPresent = false;
            }
        }
        
        if (bAllAnimationsPresent)
        {
            UE_LOG(LogTemp, Log, TEXT("Все анимации соответствуют эталону"));
        }
        
        return bAllAnimationsPresent;
    }
};
