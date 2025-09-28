#include "CoreMinimal.h"
#include "Components/Button.h"
#include "Components/ScrollBox.h"
#include "Components/Image.h"
#include "Components/Border.h"
#include "Materials/MaterialInstanceDynamic.h"
#include "Engine/Texture2D.h"

/**
 * Система визуальных эффектов для экрана выбора персонажей в соответствии с эталонным клиентом
 * Создает эффекты наведения, выбора и интерактивности для персонажей
 */
class FCharacterSelectionVisualEffects
{
public:
    /**
     * Настройка всех визуальных эффектов для экрана выбора персонажей
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     */
    static void SetupCharacterSelectionEffects(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            UE_LOG(LogTemp, Error, TEXT("CharacterSelectionWidget is null"));
            return;
        }

        // Настройка эффектов кнопок
        SetupButtonEffects(CharacterSelectionWidget);
        
        // Настройка эффектов слотов персонажей
        SetupCharacterSlotEffects(CharacterSelectionWidget);
        
        // Настройка эффектов фона
        SetupBackgroundEffects(CharacterSelectionWidget);
        
        // Настройка эффектов списка персонажей
        SetupCharacterListEffects(CharacterSelectionWidget);
        
        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты экрана выбора персонажей настроены"));
    }

private:
    /**
     * Настройка визуальных эффектов для кнопок
     */
    static void SetupButtonEffects(UUserWidget* CharacterSelectionWidget)
    {
        // Эффекты кнопки "Создать персонажа"
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton)
        {
            SetupButtonHoverEffect(CreateCharacterButton, TEXT("CreateCharacterButton"), FColor::FromHex(TEXT("00FF00")));
            SetupButtonFocusEffect(CreateCharacterButton, TEXT("CreateCharacterButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Создать персонажа' настроены"));
        }
        
        // Эффекты кнопки "Удалить персонажа"
        UButton* DeleteCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("DeleteCharacterButton"));
        if (DeleteCharacterButton)
        {
            SetupButtonHoverEffect(DeleteCharacterButton, TEXT("DeleteCharacterButton"), FColor::FromHex(TEXT("FF0000")));
            SetupButtonFocusEffect(DeleteCharacterButton, TEXT("DeleteCharacterButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Удалить персонажа' настроены"));
        }
        
        // Эффекты кнопки "Войти в игру"
        UButton* EnterGameButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("EnterGameButton"));
        if (EnterGameButton)
        {
            SetupButtonHoverEffect(EnterGameButton, TEXT("EnterGameButton"), FColor::FromHex(TEXT("FFD700")));
            SetupButtonFocusEffect(EnterGameButton, TEXT("EnterGameButton"));
            SetupButtonGlowEffect(EnterGameButton, TEXT("EnterGameButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Войти в игру' настроены"));
        }
        
        // Эффекты кнопки "Назад"
        UButton* BackButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("BackButton"));
        if (BackButton)
        {
            SetupButtonHoverEffect(BackButton, TEXT("BackButton"), FColor::FromHex(TEXT("FFFFFF")));
            SetupButtonFocusEffect(BackButton, TEXT("BackButton"));
            UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты кнопки 'Назад' настроены"));
        }
    }

    /**
     * Настройка эффекта наведения для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     * @param HoverColor - цвет наведения
     */
    static void SetupButtonHoverEffect(UButton* Button, const FString& ButtonName, const FColor& HoverColor)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта наведения
        UMaterialInstanceDynamic* HoverMaterial = CreateButtonHoverMaterial(ButtonName);
        if (HoverMaterial)
        {
            // Настраиваем параметры материала
            HoverMaterial->SetScalarParameterValue(TEXT("GlowIntensity"), 1.3f);
            HoverMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(HoverColor));
            HoverMaterial->SetScalarParameterValue(TEXT("BorderThickness"), 2.0f);
            
            // Применяем материал к кнопке при наведении
            Button->SetHoveredMaterial(HoverMaterial);
        }

        // Настраиваем эффект тени при наведении
        FButtonStyle ButtonStyle = Button->GetStyle();
        FSlateBrush HoveredBrush = ButtonStyle.Hovered;
        HoveredBrush.DrawAs = ESlateBrushDrawType::Box;
        HoveredBrush.Margin = FMargin(2.0f);
        
        // Создаем эффект тени
        FSlateShadowEffect ShadowEffect;
        ShadowEffect.Color = FLinearColor(0.0f, 0.0f, 0.0f, 0.3f);
        ShadowEffect.Offset = FVector2D(2.0f, 2.0f);
        ShadowEffect.BlurRadius = 4.0f;
        HoveredBrush.OutlineSettings = FSlateBrushOutlineSettings(ShadowEffect);
        
        ButtonStyle.Hovered = HoveredBrush;
        Button->SetStyle(ButtonStyle);
    }

    /**
     * Настройка эффекта фокуса для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonFocusEffect(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта фокуса
        UMaterialInstanceDynamic* FocusMaterial = CreateButtonFocusMaterial(ButtonName);
        if (FocusMaterial)
        {
            // Настраиваем параметры материала
            FocusMaterial->SetScalarParameterValue(TEXT("FocusIntensity"), 1.5f);
            FocusMaterial->SetVectorParameterValue(TEXT("FocusColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("00BFFF"))));
            FocusMaterial->SetScalarParameterValue(TEXT("PulseSpeed"), 2.0f);
            
            // Применяем материал к кнопке при фокусе
            Button->SetFocusedMaterial(FocusMaterial);
        }
    }

    /**
     * Настройка эффекта свечения для кнопки
     * @param Button - кнопка для настройки
     * @param ButtonName - имя кнопки для логирования
     */
    static void SetupButtonGlowEffect(UButton* Button, const FString& ButtonName)
    {
        if (!Button)
        {
            return;
        }

        // Создаем материал для эффекта свечения
        UMaterialInstanceDynamic* GlowMaterial = CreateButtonGlowMaterial(ButtonName);
        if (GlowMaterial)
        {
            // Настраиваем параметры материала
            GlowMaterial->SetScalarParameterValue(TEXT("GlowRadius"), 8.0f);
            GlowMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            GlowMaterial->SetScalarParameterValue(TEXT("GlowOpacity"), 0.8f);
            
            // Применяем материал к кнопке
            Button->SetGlowMaterial(GlowMaterial);
        }
    }

    /**
     * Настройка визуальных эффектов для слотов персонажей
     */
    static void SetupCharacterSlotEffects(UUserWidget* CharacterSelectionWidget)
    {
        // Получаем панель списка персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (!CharacterListPanel)
        {
            UE_LOG(LogTemp, Warning, TEXT("CharacterListPanel не найден"));
            return;
        }

        // Настраиваем эффекты для каждого слота персонажа
        for (int32 i = 0; i < CharacterListPanel->GetChildrenCount(); i++)
        {
            UUserWidget* CharacterSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(i));
            if (CharacterSlot)
            {
                SetupCharacterSlotHoverEffect(CharacterSlot, i);
                SetupCharacterSlotSelectionEffect(CharacterSlot, i);
                SetupCharacterSlotGlowEffect(CharacterSlot, i);
            }
        }
    }

    /**
     * Настройка эффекта наведения для слота персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotHoverEffect(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем материал для эффекта наведения
        UMaterialInstanceDynamic* HoverMaterial = CreateCharacterSlotHoverMaterial(SlotIndex);
        if (HoverMaterial)
        {
            // Настраиваем параметры материала
            HoverMaterial->SetScalarParameterValue(TEXT("HoverIntensity"), 1.2f);
            HoverMaterial->SetVectorParameterValue(TEXT("HoverColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("3D3D3D"))));
            HoverMaterial->SetScalarParameterValue(TEXT("TransitionSpeed"), 0.3f);
            
            // Применяем материал к слоту персонажа при наведении
            CharacterSlot->SetHoveredMaterial(HoverMaterial);
        }
    }

    /**
     * Настройка эффекта выбора для слота персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotSelectionEffect(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем материал для эффекта выбора
        UMaterialInstanceDynamic* SelectionMaterial = CreateCharacterSlotSelectionMaterial(SlotIndex);
        if (SelectionMaterial)
        {
            // Настраиваем параметры материала
            SelectionMaterial->SetScalarParameterValue(TEXT("SelectionIntensity"), 1.5f);
            SelectionMaterial->SetVectorParameterValue(TEXT("SelectionColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            SelectionMaterial->SetScalarParameterValue(TEXT("BorderThickness"), 3.0f);
            SelectionMaterial->SetScalarParameterValue(TEXT("PulseSpeed"), 1.5f);
            
            // Применяем материал к слоту персонажа при выборе
            CharacterSlot->SetSelectionMaterial(SelectionMaterial);
        }
    }

    /**
     * Настройка эффекта свечения для слота персонажа
     * @param CharacterSlot - слот персонажа для настройки
     * @param SlotIndex - индекс слота для логирования
     */
    static void SetupCharacterSlotGlowEffect(UUserWidget* CharacterSlot, int32 SlotIndex)
    {
        if (!CharacterSlot)
        {
            return;
        }

        // Создаем материал для эффекта свечения
        UMaterialInstanceDynamic* GlowMaterial = CreateCharacterSlotGlowMaterial(SlotIndex);
        if (GlowMaterial)
        {
            // Настраиваем параметры материала
            GlowMaterial->SetScalarParameterValue(TEXT("GlowRadius"), 6.0f);
            GlowMaterial->SetVectorParameterValue(TEXT("GlowColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            GlowMaterial->SetScalarParameterValue(TEXT("GlowOpacity"), 0.6f);
            
            // Применяем материал к слоту персонажа
            CharacterSlot->SetGlowMaterial(GlowMaterial);
        }
    }

    /**
     * Настройка визуальных эффектов для фона
     */
    static void SetupBackgroundEffects(UUserWidget* CharacterSelectionWidget)
    {
        UImage* BackgroundImage = CharacterSelectionWidget->FindWidget<UImage>(TEXT("BackgroundImage"));
        if (!BackgroundImage)
        {
            return;
        }

        // Создаем материал для эффекта фона
        UMaterialInstanceDynamic* BackgroundMaterial = CreateBackgroundMaterial();
        if (BackgroundMaterial)
        {
            // Настраиваем параметры материала
            BackgroundMaterial->SetScalarParameterValue(TEXT("ParallaxSpeed"), 0.3f);
            BackgroundMaterial->SetVectorParameterValue(TEXT("TintColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("1e1e2e"))));
            BackgroundMaterial->SetScalarParameterValue(TEXT("Contrast"), 1.1f);
            BackgroundMaterial->SetScalarParameterValue(TEXT("Brightness"), 0.9f);
            
            // Применяем материал к фону
            BackgroundImage->SetBrushFromMaterial(BackgroundMaterial);
        }

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты фона настроены"));
    }

    /**
     * Настройка визуальных эффектов для списка персонажей
     */
    static void SetupCharacterListEffects(UUserWidget* CharacterSelectionWidget)
    {
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (!CharacterListPanel)
        {
            return;
        }

        // Создаем материал для эффекта списка
        UMaterialInstanceDynamic* ListMaterial = CreateCharacterListMaterial();
        if (ListMaterial)
        {
            // Настраиваем параметры материала
            ListMaterial->SetScalarParameterValue(TEXT("ScrollSpeed"), 0.5f);
            ListMaterial->SetVectorParameterValue(TEXT("BorderColor"), FLinearColor::FromSRGBColor(FColor::FromHex(TEXT("FFD700"))));
            ListMaterial->SetScalarParameterValue(TEXT("BorderThickness"), 2.0f);
            ListMaterial->SetScalarParameterValue(TEXT("FadeIntensity"), 0.8f);
            
            // Применяем материал к панели списка
            CharacterListPanel->SetMaterial(ListMaterial);
        }

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты списка персонажей настроены"));
    }

    // Методы создания материалов (заглушки для демонстрации)
    static UMaterialInstanceDynamic* CreateButtonHoverMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateButtonFocusMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateButtonGlowMaterial(const FString& ButtonName)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateCharacterSlotHoverMaterial(int32 SlotIndex)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateCharacterSlotSelectionMaterial(int32 SlotIndex)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateCharacterSlotGlowMaterial(int32 SlotIndex)
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateBackgroundMaterial()
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }

    static UMaterialInstanceDynamic* CreateCharacterListMaterial()
    {
        // В реальной реализации здесь будет создание материала
        return nullptr;
    }
};

/**
 * Утилиты для работы с визуальными эффектами экрана выбора персонажей
 */
class FCharacterSelectionVisualEffectsUtils
{
public:
    /**
     * Включение/выключение всех эффектов
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     * @param bEnabled - включить или выключить эффекты
     */
    static void SetEffectsEnabled(UUserWidget* CharacterSelectionWidget, bool bEnabled)
    {
        if (!CharacterSelectionWidget)
        {
            return;
        }

        // Включаем/выключаем эффекты для всех элементов
        SetButtonEffectsEnabled(CharacterSelectionWidget, bEnabled);
        SetCharacterSlotEffectsEnabled(CharacterSelectionWidget, bEnabled);
        SetBackgroundEffectsEnabled(CharacterSelectionWidget, bEnabled);
        SetCharacterListEffectsEnabled(CharacterSelectionWidget, bEnabled);

        UE_LOG(LogTemp, Log, TEXT("Визуальные эффекты %s"), bEnabled ? TEXT("включены") : TEXT("выключены"));
    }

    /**
     * Проверка соответствия эффектов эталону
     * @param CharacterSelectionWidget - виджет экрана выбора персонажей
     * @return bool - соответствуют ли эффекты эталону
     */
    static bool ValidateEffectsCompliance(UUserWidget* CharacterSelectionWidget)
    {
        if (!CharacterSelectionWidget)
        {
            return false;
        }

        bool bAllEffectsPresent = true;

        // Проверяем эффекты кнопок
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton && !CreateCharacterButton->HasHoverEffect())
        {
            UE_LOG(LogTemp, Warning, TEXT("Отсутствует эффект наведения для кнопки 'Создать персонажа'"));
            bAllEffectsPresent = false;
        }

        // Проверяем эффекты слотов персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (CharacterListPanel && CharacterListPanel->GetChildrenCount() > 0)
        {
            UUserWidget* FirstSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(0));
            if (FirstSlot && !FirstSlot->HasHoverEffect())
            {
                UE_LOG(LogTemp, Warning, TEXT("Отсутствует эффект наведения для слотов персонажей"));
                bAllEffectsPresent = false;
            }
        }

        if (bAllEffectsPresent)
        {
            UE_LOG(LogTemp, Log, TEXT("Все визуальные эффекты соответствуют эталону"));
        }

        return bAllEffectsPresent;
    }

private:
    static void SetButtonEffectsEnabled(UUserWidget* CharacterSelectionWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты кнопок
        UButton* CreateCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("CreateCharacterButton"));
        if (CreateCharacterButton)
        {
            CreateCharacterButton->SetEffectsEnabled(bEnabled);
        }

        UButton* DeleteCharacterButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("DeleteCharacterButton"));
        if (DeleteCharacterButton)
        {
            DeleteCharacterButton->SetEffectsEnabled(bEnabled);
        }

        UButton* EnterGameButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("EnterGameButton"));
        if (EnterGameButton)
        {
            EnterGameButton->SetEffectsEnabled(bEnabled);
        }

        UButton* BackButton = CharacterSelectionWidget->FindWidget<UButton>(TEXT("BackButton"));
        if (BackButton)
        {
            BackButton->SetEffectsEnabled(bEnabled);
        }
    }

    static void SetCharacterSlotEffectsEnabled(UUserWidget* CharacterSelectionWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты слотов персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (CharacterListPanel)
        {
            for (int32 i = 0; i < CharacterListPanel->GetChildrenCount(); i++)
            {
                UUserWidget* CharacterSlot = Cast<UUserWidget>(CharacterListPanel->GetChildAt(i));
                if (CharacterSlot)
                {
                    CharacterSlot->SetEffectsEnabled(bEnabled);
                }
            }
        }
    }

    static void SetBackgroundEffectsEnabled(UUserWidget* CharacterSelectionWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты фона
        UImage* BackgroundImage = CharacterSelectionWidget->FindWidget<UImage>(TEXT("BackgroundImage"));
        if (BackgroundImage)
        {
            BackgroundImage->SetEffectsEnabled(bEnabled);
        }
    }

    static void SetCharacterListEffectsEnabled(UUserWidget* CharacterSelectionWidget, bool bEnabled)
    {
        // Включаем/выключаем эффекты списка персонажей
        UScrollBox* CharacterListPanel = CharacterSelectionWidget->FindWidget<UScrollBox>(TEXT("CharacterListPanel"));
        if (CharacterListPanel)
        {
            CharacterListPanel->SetEffectsEnabled(bEnabled);
        }
    }
};
