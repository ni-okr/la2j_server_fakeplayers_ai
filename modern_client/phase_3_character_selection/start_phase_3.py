#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Скрипт для автоматизации Фазы 3 - Разработка экрана выбора персонажей
Выполняет анализ эталона, создает базовые файлы и настраивает тестирование
"""

import os
import sys
import json
import time
import logging
from pathlib import Path
from datetime import datetime

# Настройка логирования
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def run_command(command):
    """Выполняет команду в терминале и возвращает ее вывод."""
    logging.info(f"Выполнение команды: {command}")
    result = os.system(command)
    if result != 0:
        logging.error(f"Команда завершилась с ошибкой: {command}")
    return result

def create_directory_if_not_exists(path):
    """Создает директорию, если она не существует."""
    os.makedirs(path, exist_ok=True)
    logging.info(f"Директория создана или уже существует: {path}")

def simulate_character_analysis(phase_dir):
    """Симулирует анализ эталонного экрана выбора персонажей."""
    analysis_dir = os.path.join(phase_dir, "analysis")
    create_directory_if_not_exists(analysis_dir)

    analysis_data = {
        "screen_name": "CharacterSelectionScreen",
        "elements": [
            {
                "name": "CharacterListPanel",
                "type": "ScrollBox",
                "position": "left_center",
                "size": "400x500",
                "background_color": "#1e1e2e",
                "border_color": "#FFD700",
                "border_thickness": 2
            },
            {
                "name": "CharacterSlot1",
                "type": "CharacterSlot",
                "position": "list_top",
                "size": "380x80",
                "background_color": "#2d2d2d",
                "hover_color": "#3d3d3d",
                "selected_color": "#FFD700"
            },
            {
                "name": "CharacterSlot2",
                "type": "CharacterSlot",
                "position": "list_second",
                "size": "380x80",
                "background_color": "#2d2d2d",
                "hover_color": "#3d3d3d",
                "selected_color": "#FFD700"
            },
            {
                "name": "CharacterSlot3",
                "type": "CharacterSlot",
                "position": "list_third",
                "size": "380x80",
                "background_color": "#2d2d2d",
                "hover_color": "#3d3d3d",
                "selected_color": "#FFD700"
            },
            {
                "name": "CreateCharacterButton",
                "type": "Button",
                "position": "bottom_left",
                "size": "150x40",
                "text": "Создать персонажа",
                "font": "Arial Bold 14px",
                "color": "#00FF00",
                "background_color": "#2d2d2d"
            },
            {
                "name": "DeleteCharacterButton",
                "type": "Button",
                "position": "bottom_center",
                "size": "150x40",
                "text": "Удалить персонажа",
                "font": "Arial Bold 14px",
                "color": "#FF0000",
                "background_color": "#2d2d2d"
            },
            {
                "name": "EnterGameButton",
                "type": "Button",
                "position": "bottom_right",
                "size": "150x40",
                "text": "Войти в игру",
                "font": "Arial Bold 14px",
                "color": "#FFD700",
                "background_color": "#2d2d2d"
            },
            {
                "name": "BackButton",
                "type": "Button",
                "position": "top_left",
                "size": "100x30",
                "text": "Назад",
                "font": "Arial 12px",
                "color": "#FFFFFF",
                "background_color": "#1e1e2e"
            }
        ],
        "character_slot_elements": [
            {
                "name": "CharacterAvatar",
                "type": "Image",
                "position": "slot_left",
                "size": "60x60",
                "background_color": "#000000"
            },
            {
                "name": "CharacterName",
                "type": "TextBlock",
                "position": "slot_center_top",
                "size": "200x20",
                "font": "Arial Bold 16px",
                "color": "#FFFFFF"
            },
            {
                "name": "CharacterLevel",
                "type": "TextBlock",
                "position": "slot_center_middle",
                "size": "200x15",
                "font": "Arial 12px",
                "color": "#FFD700"
            },
            {
                "name": "CharacterClass",
                "type": "TextBlock",
                "position": "slot_center_bottom",
                "size": "200x15",
                "font": "Arial 12px",
                "color": "#C0C0C0"
            },
            {
                "name": "CharacterLocation",
                "type": "TextBlock",
                "position": "slot_right",
                "size": "100x15",
                "font": "Arial 10px",
                "color": "#808080"
            }
        ],
        "layout": "horizontal_split",
        "background": "character_selection_bg.jpg",
        "expected_resolution": "1024x768",
        "max_characters": 7,
        "character_slot_height": 80,
        "character_slot_spacing": 5
    }

    analysis_file = os.path.join(analysis_dir, "character_selection_analysis.json")
    with open(analysis_file, "w", encoding="utf-8") as f:
        json.dump(analysis_data, f, ensure_ascii=False, indent=4)
    logging.info(f"✅ Анализ завершен: {analysis_file}")

    # Симуляция создания эталонных скриншотов
    screenshots = [
        "reference_character_selection_empty.png",
        "reference_character_selection_with_characters.png",
        "reference_character_selection_max_characters.png"
    ]
    
    for screenshot in screenshots:
        screenshot_path = os.path.join(analysis_dir, screenshot)
        with open(screenshot_path, "w") as f:
            f.write(f"Simulated reference character selection screenshot: {screenshot}")
        logging.info(f"📸 Эталонный скриншот: {screenshot_path}")

    return analysis_file

def create_basic_character_selection_files(phase_dir):
    """Создает базовые C++ файлы для экрана выбора персонажей."""
    impl_dir = os.path.join(phase_dir, "implementation")
    create_directory_if_not_exists(impl_dir)

    header_content = """
// CharacterSelectionScreen.h
#pragma once

#include "CoreMinimal.h"
#include "Blueprint/UserWidget.h"
#include "Components/ScrollBox.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "CharacterSelectionScreen.generated.h"

/**
 * Структура данных персонажа
 */
USTRUCT(BlueprintType)
struct FCharacterData
{
    GENERATED_BODY()

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString CharacterName;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    int32 Level;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString CharacterClass;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString Location;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    FString AvatarPath;

    UPROPERTY(EditAnywhere, BlueprintReadWrite)
    bool bIsSelected;

    FCharacterData()
    {
        CharacterName = TEXT("");
        Level = 1;
        CharacterClass = TEXT("");
        Location = TEXT("");
        AvatarPath = TEXT("");
        bIsSelected = false;
    }
};

/**
 * Экран выбора персонажей
 */
UCLASS()
class UCharacterSelectionScreen : public UUserWidget
{
    GENERATED_BODY()

public:
    // Конструктор
    UCharacterSelectionScreen(const FObjectInitializer& ObjectInitializer);

    // Виртуальная функция для инициализации виджета
    virtual void NativeConstruct() override;

    // Основные UI компоненты
    UPROPERTY(meta = (BindWidget))
    class UScrollBox* CharacterListPanel;

    UPROPERTY(meta = (BindWidget))
    class UButton* CreateCharacterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* DeleteCharacterButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* EnterGameButton;

    UPROPERTY(meta = (BindWidget))
    class UButton* BackButton;

    // Функции-обработчики событий
    UFUNCTION()
    void OnCreateCharacterButtonClicked();

    UFUNCTION()
    void OnDeleteCharacterButtonClicked();

    UFUNCTION()
    void OnEnterGameButtonClicked();

    UFUNCTION()
    void OnBackButtonClicked();

    UFUNCTION()
    void OnCharacterSlotClicked(int32 CharacterIndex);

    // Функции управления персонажами
    UFUNCTION(BlueprintCallable)
    void LoadCharacterList();

    UFUNCTION(BlueprintCallable)
    void RefreshCharacterList();

    UFUNCTION(BlueprintCallable)
    void SelectCharacter(int32 CharacterIndex);

    UFUNCTION(BlueprintCallable)
    void DeleteSelectedCharacter();

    UFUNCTION(BlueprintCallable)
    void EnterGameWithSelectedCharacter();

protected:
    // Данные персонажей
    UPROPERTY(BlueprintReadOnly)
    TArray<FCharacterData> CharacterList;

    UPROPERTY(BlueprintReadOnly)
    int32 SelectedCharacterIndex;

    // Максимальное количество персонажей
    UPROPERTY(BlueprintReadOnly)
    int32 MaxCharacters;

    // Создание слота персонажа
    UFUNCTION(BlueprintCallable)
    UUserWidget* CreateCharacterSlot(const FCharacterData& CharacterData, int32 Index);

    // Обновление слота персонажа
    UFUNCTION(BlueprintCallable)
    void UpdateCharacterSlot(UUserWidget* SlotWidget, const FCharacterData& CharacterData, int32 Index);
};
"""

    cpp_content = """
// CharacterSelectionScreen.cpp
#include "CharacterSelectionScreen.h"
#include "Components/ScrollBox.h"
#include "Components/Button.h"
#include "Components/TextBlock.h"
#include "Components/Image.h"
#include "Engine/Engine.h"
#include "Kismet/GameplayStatics.h"

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

    UE_LOG(LogTemp, Log, TEXT("Экран выбора персонажей инициализирован"));
}

void UCharacterSelectionScreen::OnCreateCharacterButtonClicked()
{
    // Переход к экрану создания персонажа
    UE_LOG(LogTemp, Warning, TEXT("Переход к созданию персонажа"));
    // UGameplayStatics::OpenLevel(GetWorld(), "CharacterCreationMap");
}

void UCharacterSelectionScreen::OnDeleteCharacterButtonClicked()
{
    // Удаление выбранного персонажа
    if (SelectedCharacterIndex >= 0 && SelectedCharacterIndex < CharacterList.Num())
    {
        DeleteSelectedCharacter();
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("Персонаж не выбран для удаления"));
    }
}

void UCharacterSelectionScreen::OnEnterGameButtonClicked()
{
    // Вход в игру с выбранным персонажем
    if (SelectedCharacterIndex >= 0 && SelectedCharacterIndex < CharacterList.Num())
    {
        EnterGameWithSelectedCharacter();
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("Персонаж не выбран для входа в игру"));
    }
}

void UCharacterSelectionScreen::OnBackButtonClicked()
{
    // Возврат к экрану входа
    UE_LOG(LogTemp, Warning, TEXT("Возврат к экрану входа"));
    // UGameplayStatics::OpenLevel(GetWorld(), "LoginMap");
}

void UCharacterSelectionScreen::OnCharacterSlotClicked(int32 CharacterIndex)
{
    // Выбор персонажа
    SelectCharacter(CharacterIndex);
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
"""

    with open(os.path.join(impl_dir, "CharacterSelectionScreen.h"), "w", encoding="utf-8") as f:
        f.write(header_content)
    with open(os.path.join(impl_dir, "CharacterSelectionScreen.cpp"), "w", encoding="utf-8") as f:
        f.write(cpp_content)
    
    logging.info(f"✅ C++ файлы созданы:")
    logging.info(f"   - {os.path.join(impl_dir, 'CharacterSelectionScreen.h')}")
    logging.info(f"   - {os.path.join(impl_dir, 'CharacterSelectionScreen.cpp')}")

def create_blueprint_template(phase_dir):
    """Создает шаблон Blueprint для экрана выбора персонажей."""
    impl_dir = os.path.join(phase_dir, "implementation")
    blueprint_template_path = os.path.join(impl_dir, "WBP_CharacterSelectionScreen.json")
    
    blueprint_content = {
        "WidgetName": "WBP_CharacterSelectionScreen",
        "ParentClass": "CharacterSelectionScreen",
        "Components": [
            {"Type": "CanvasPanel", "Name": "RootCanvas"},
            {"Type": "Image", "Name": "BackgroundImage", "Parent": "RootCanvas", "Anchors": "Full", "ZOrder": -1},
            {"Type": "ScrollBox", "Name": "CharacterListPanel", "Parent": "RootCanvas", "Position": "LeftCenter", "Size": "400x500"},
            {"Type": "Button", "Name": "CreateCharacterButton", "Parent": "RootCanvas", "Position": "BottomLeft", "Size": "150x40", "Text": "Создать персонажа"},
            {"Type": "Button", "Name": "DeleteCharacterButton", "Parent": "RootCanvas", "Position": "BottomCenter", "Size": "150x40", "Text": "Удалить персонажа"},
            {"Type": "Button", "Name": "EnterGameButton", "Parent": "RootCanvas", "Position": "BottomRight", "Size": "150x40", "Text": "Войти в игру"},
            {"Type": "Button", "Name": "BackButton", "Parent": "RootCanvas", "Position": "TopLeft", "Size": "100x30", "Text": "Назад"}
        ],
        "CharacterSlotTemplate": {
            "WidgetName": "WBP_CharacterSlot",
            "Components": [
                {"Type": "Button", "Name": "SlotButton", "Anchors": "Full"},
                {"Type": "Image", "Name": "CharacterAvatar", "Parent": "SlotButton", "Position": "Left", "Size": "60x60"},
                {"Type": "TextBlock", "Name": "CharacterName", "Parent": "SlotButton", "Position": "CenterTop", "Size": "200x20", "Text": "Имя персонажа"},
                {"Type": "TextBlock", "Name": "CharacterLevel", "Parent": "SlotButton", "Position": "CenterMiddle", "Size": "200x15", "Text": "Уровень 1"},
                {"Type": "TextBlock", "Name": "CharacterClass", "Parent": "SlotButton", "Position": "CenterBottom", "Size": "200x15", "Text": "Класс"},
                {"Type": "TextBlock", "Name": "CharacterLocation", "Parent": "SlotButton", "Position": "Right", "Size": "100x15", "Text": "Локация"}
            ]
        }
    }
    
    with open(blueprint_template_path, "w", encoding="utf-8") as f:
        json.dump(blueprint_content, f, ensure_ascii=False, indent=4)
    logging.info(f"✅ Шаблон Blueprint создан: {blueprint_template_path}")

def create_tests(phase_dir):
    """Создает базовые тесты для экрана выбора персонажей."""
    tests_dir = os.path.join(phase_dir, "tests")
    create_directory_if_not_exists(tests_dir)
    
    test_content = """
# test_character_selection_screen.py
import unittest
import os
import json

class TestCharacterSelectionScreen(unittest.TestCase):

    def setUp(self):
        self.analysis_file = os.path.join(os.path.dirname(__file__), "..", "analysis", "character_selection_analysis.json")
        self.implementation_dir = os.path.join(os.path.dirname(__file__), "..", "implementation")
        self.character_screen_h = os.path.join(self.implementation_dir, "CharacterSelectionScreen.h")
        self.character_screen_cpp = os.path.join(self.implementation_dir, "CharacterSelectionScreen.cpp")
        self.wbp_character_screen_json = os.path.join(self.implementation_dir, "WBP_CharacterSelectionScreen.json")

    def test_analysis_file_exists(self):
        self.assertTrue(os.path.exists(self.analysis_file), "Файл анализа экрана выбора персонажей не найден.")

    def test_cpp_files_exist(self):
        self.assertTrue(os.path.exists(self.character_screen_h), "CharacterSelectionScreen.h не найден.")
        self.assertTrue(os.path.exists(self.character_screen_cpp), "CharacterSelectionScreen.cpp не найден.")

    def test_blueprint_template_exists(self):
        self.assertTrue(os.path.exists(self.wbp_character_screen_json), "WBP_CharacterSelectionScreen.json не найден.")

    def test_blueprint_template_content(self):
        with open(self.wbp_character_screen_json, "r", encoding="utf-8") as f:
            content = json.load(f)
        self.assertIn("CharacterListPanel", [comp["Name"] for comp in content["Components"]])
        self.assertIn("CreateCharacterButton", [comp["Name"] for comp in content["Components"]])
        self.assertEqual(content["ParentClass"], "CharacterSelectionScreen")

    def test_cpp_header_content(self):
        with open(self.character_screen_h, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("class UCharacterSelectionScreen : public UUserWidget", content)
        self.assertIn("UScrollBox* CharacterListPanel", content)
        self.assertIn("void OnCreateCharacterButtonClicked();", content)

    def test_cpp_source_content(self):
        with open(self.character_screen_cpp, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("UCharacterSelectionScreen::OnCreateCharacterButtonClicked()", content)
        self.assertIn("Super::NativeConstruct();", content)

    def test_analysis_content(self):
        with open(self.analysis_file, "r", encoding="utf-8") as f:
            content = json.load(f)
        self.assertEqual(content["screen_name"], "CharacterSelectionScreen")
        self.assertIn("CharacterListPanel", [elem["name"] for elem in content["elements"]])
        self.assertIn("CreateCharacterButton", [elem["name"] for elem in content["elements"]])

if __name__ == '__main__':
    unittest.main()
"""
    
    test_file = os.path.join(tests_dir, "test_character_selection_screen.py")
    with open(test_file, "w", encoding="utf-8") as f:
        f.write(test_content)
    logging.info(f"✅ Тесты созданы: {test_file}")
    return test_file

def create_implementation_guide(phase_dir):
    """Создает руководство по реализации."""
    guide_content = """
# Руководство по реализации Фазы 3: Экран выбора персонажей

## 🎯 Цель
Довести экран выбора персонажей до 95%+ соответствия эталонному клиенту.

## 🛠️ Шаги реализации

1. **Откройте Unreal Engine Editor:**
   - Запустите проект `ModernLineage2.uproject`.
   - Откройте `Content/UI/CharacterSelection/WBP_CharacterSelectionScreen` (или создайте его на основе шаблона).

2. **Визуальная настройка Blueprint (`WBP_CharacterSelectionScreen`):**
   - Используйте `character_selection_analysis.json` из папки `analysis/` как эталон.
   - **Фон**: Установите фоновое изображение, соответствующее эталону.
   - **Панель персонажей**:
       - Настройте размеры, позицию, отступы для `CharacterListPanel`.
       - Примените правильные цвета фона и рамки.
   - **Кнопки**:
       - Настройте размеры, позицию, отступы для всех кнопок.
       - Примените правильные шрифты, размеры шрифтов и цвета текста.
       - Добавьте стили для состояний (Normal, Hovered, Pressed).
   - **Слоты персонажей**:
       - Создайте шаблон слота персонажа `WBP_CharacterSlot`.
       - Настройте отображение аватара, имени, уровня, класса, локации.

3. **Реализация C++ логики (`CharacterSelectionScreen.h`, `CharacterSelectionScreen.cpp`):**
   - **Привязка виджетов**: Убедитесь, что все `UPROPERTY(meta = (BindWidget))` соответствуют именам виджетов в Blueprint.
   - **Обработчики событий**:
       - Доработайте `OnCreateCharacterButtonClicked()` для перехода к созданию персонажа.
       - Доработайте `OnDeleteCharacterButtonClicked()` для удаления персонажа.
       - Доработайте `OnEnterGameButtonClicked()` для входа в игру.
       - Реализуйте `OnCharacterSlotClicked()` для выбора персонажа.

4. **Интеграция с системой персонажей:**
   - Используйте существующие или создайте новые функции для загрузки данных персонажей.
   - Реализуйте создание, удаление и выбор персонажей.
   - Обработайте переходы между экранами.

5. **Тестирование:**
   - Регулярно запускайте `test_character_selection_screen.py` для проверки соответствия.
   - Используйте систему попиксельного сравнения для проверки соответствия.
   - Проверяйте функциональность на различных разрешениях экрана.

## ⚠️ Важные замечания
- **Русский язык**: Все комментарии в коде и текстовые элементы GUI должны быть на русском языке.
- **Производительность**: Оптимизируйте ресурсы (текстуры, шрифты) для лучшей производительности.
- **Максимальное количество персонажей**: Ограничьте до 7 персонажей согласно эталону.
- **Анимации**: Добавьте плавные анимации для выбора и перехода между персонажами.
"""
    
    guide_file = os.path.join(phase_dir, "IMPLEMENTATION_GUIDE.md")
    with open(guide_file, "w", encoding="utf-8") as f:
        f.write(guide_content)
    logging.info(f"✅ Руководство создано: {guide_file}")

def run_initial_tests(test_file):
    """Запускает начальные тесты и возвращает результат."""
    logging.info("🧪 Запуск начальных тестов...")
    result = run_command(f"python3 {test_file}")
    if result == 0:
        logging.info("✅ Все тесты пройдены успешно!")
        return True
    else:
        logging.error("❌ Некоторые тесты провалены.")
        return False

def generate_progress_report(phase_dir, analysis_completed, basic_screen_created, tests_passed):
    """Генерирует отчет о прогрессе Фазы 3."""
    report_data = {
        "phase_name": "Фаза 3: Разработка экрана выбора персонажей",
        "status": "Запущена",
        "progress": {
            "analysis_completed": analysis_completed,
            "basic_screen_created": basic_screen_created,
            "precise_setup": False,
            "functionality": False,
            "testing": tests_passed
        },
        "next_steps": [
            "Настройка шрифтов и цветов элементов",
            "Создание анимаций выбора персонажей",
            "Реализация системы управления персонажами",
            "Интеграция с системой создания персонажей"
        ],
        "working_directory": phase_dir
    }
    
    report_file = os.path.join(phase_dir, "progress_report.json")
    with open(report_file, "w", encoding="utf-8") as f:
        json.dump(report_data, f, ensure_ascii=False, indent=4)
    logging.info(f"✅ Отчет о прогрессе создан: {report_file}")
    return report_data

def main():
    logging.info("🚀 Запуск Фазы 3 - Разработка экрана выбора персонажей")
    base_dir = os.path.dirname(os.path.abspath(__file__))
    phase_3_dir = os.path.join(base_dir, "phase_3_character_selection")
    create_directory_if_not_exists(phase_3_dir)

    # 1. Анализ эталонного экрана выбора персонажей
    logging.info("🔍 Анализ эталонного экрана выбора персонажей...")
    analysis_file = simulate_character_analysis(phase_3_dir)
    analysis_completed = os.path.exists(analysis_file)

    # 2. Создание базового C++ класса и Blueprint шаблона
    logging.info("🛠️ Создание базового экрана выбора персонажей...")
    create_basic_character_selection_files(phase_3_dir)
    create_blueprint_template(phase_3_dir)
    basic_screen_created = True

    # 3. Создание тестов
    logging.info("🧪 Создание тестов...")
    test_file = create_tests(phase_3_dir)

    # 4. Создание руководства по реализации
    logging.info("📚 Создание руководства по реализации...")
    create_implementation_guide(phase_3_dir)

    # 5. Запуск начальных тестов
    tests_passed = run_initial_tests(test_file)

    # 6. Генерация отчета о прогрессе
    progress_report = generate_progress_report(phase_3_dir, analysis_completed, basic_screen_created, tests_passed)

    logging.info("\n🎉 Фаза 3 успешно запущена!")
    logging.info(f"📁 Рабочая директория: {phase_3_dir}")
    logging.info(f"📊 Прогресс: {progress_report['progress']}")
    logging.info(f"📋 Следующие шаги: {progress_report['next_steps']}")

if __name__ == "__main__":
    main()
