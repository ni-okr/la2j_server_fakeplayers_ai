#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Генератор эталонных скриншотов для экрана выбора персонажей
Создает эталонные изображения на основе анализа старого клиента
"""

import os
import sys
import json
import cv2
import numpy as np
from pathlib import Path
from datetime import datetime

class ReferenceScreenshotGenerator:
    """Генератор эталонных скриншотов экрана выбора персонажей"""
    
    def __init__(self, phase3_dir: str):
        self.phase3_dir = Path(phase3_dir)
        self.analysis_dir = self.phase3_dir / "analysis"
        self.reference_dir = self.analysis_dir / "reference_screenshots"
        
        # Создаем директорию для эталонных скриншотов
        self.reference_dir.mkdir(exist_ok=True)
        
        # Загружаем данные анализа
        self.analysis_data = self.load_analysis_data()
    
    def load_analysis_data(self):
        """Загружает данные анализа эталона"""
        analysis_file = self.analysis_dir / "character_selection_analysis.json"
        if analysis_file.exists():
            with open(analysis_file, 'r', encoding='utf-8') as f:
                return json.load(f)
        return {}
    
    def generate_empty_character_selection_screenshot(self):
        """Генерирует эталонный скриншот пустого экрана выбора персонажей"""
        print("📸 Генерация эталонного скриншота пустого экрана...")
        
        # Размеры экрана согласно анализу
        width = 1024
        height = 768
        
        # Создаем пустое изображение
        screenshot = np.zeros((height, width, 3), dtype=np.uint8)
        
        # Основной фон
        screenshot[:] = [30, 30, 46]  # #1e1e2e
        
        # Панель персонажей
        panel_x, panel_y = 50, 134
        panel_w, panel_h = 400, 500
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (30, 30, 46), -1)
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (255, 215, 0), 2)  # Золотая рамка
        
        # Кнопки
        buttons = [
            {"text": "Создать персонажа", "pos": (50, 650), "size": (150, 40), "color": (0, 255, 0)},
            {"text": "Удалить персонажа", "pos": (250, 650), "size": (150, 40), "color": (255, 0, 0)},
            {"text": "Войти в игру", "pos": (450, 650), "size": (150, 40), "color": (255, 215, 0)},
            {"text": "Назад", "pos": (50, 50), "size": (100, 30), "color": (255, 255, 255)}
        ]
        
        for button in buttons:
            x, y = button["pos"]
            w, h = button["size"]
            color = button["color"]
            
            # Фон кнопки
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), (45, 45, 45), -1)
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), color, 1)
            
            # Текст кнопки (симуляция)
            text_size = cv2.getTextSize(button["text"], cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)[0]
            text_x = x + (w - text_size[0]) // 2
            text_y = y + (h + text_size[1]) // 2
            cv2.putText(screenshot, button["text"], (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
        
        # Заголовок
        cv2.putText(screenshot, "Выбор персонажа", (50, 100), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 215, 0), 2)
        
        # Сохраняем скриншот
        screenshot_path = self.reference_dir / "reference_character_selection_empty.png"
        cv2.imwrite(str(screenshot_path), screenshot)
        
        print(f"  ✅ Эталонный скриншот пустого экрана создан: {screenshot_path}")
        return screenshot_path
    
    def generate_character_selection_with_characters_screenshot(self):
        """Генерирует эталонный скриншот экрана с персонажами"""
        print("📸 Генерация эталонного скриншота с персонажами...")
        
        # Размеры экрана
        width = 1024
        height = 768
        
        # Создаем базовое изображение
        screenshot = np.zeros((height, width, 3), dtype=np.uint8)
        screenshot[:] = [30, 30, 46]  # #1e1e2e
        
        # Панель персонажей
        panel_x, panel_y = 50, 134
        panel_w, panel_h = 400, 500
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (30, 30, 46), -1)
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (255, 215, 0), 2)
        
        # Слоты персонажей
        characters = [
            {"name": "ТестовыйВоин", "level": 25, "class": "Воин", "location": "Гиран"},
            {"name": "ТестовыйМаг", "level": 18, "class": "Маг", "location": "Аден"},
            {"name": "ТестовыйЛучник", "level": 22, "class": "Лучник", "location": "Гиран"}
        ]
        
        slot_height = 80
        slot_spacing = 5
        start_y = panel_y + 10
        
        for i, char in enumerate(characters):
            slot_y = start_y + i * (slot_height + slot_spacing)
            slot_x = panel_x + 10
            slot_w = panel_w - 20
            
            # Фон слота персонажа
            cv2.rectangle(screenshot, (slot_x, slot_y), (slot_x + slot_w, slot_y + slot_height), (45, 45, 45), -1)
            cv2.rectangle(screenshot, (slot_x, slot_y), (slot_x + slot_w, slot_y + slot_height), (255, 215, 0), 1)
            
            # Аватар персонажа (заглушка)
            avatar_x = slot_x + 10
            avatar_y = slot_y + 10
            avatar_size = 60
            cv2.rectangle(screenshot, (avatar_x, avatar_y), (avatar_x + avatar_size, avatar_y + avatar_size), (0, 0, 0), -1)
            cv2.rectangle(screenshot, (avatar_x, avatar_y), (avatar_x + avatar_size, avatar_y + avatar_size), (255, 255, 255), 1)
            
            # Имя персонажа
            name_x = avatar_x + avatar_size + 10
            name_y = slot_y + 20
            cv2.putText(screenshot, char["name"], (name_x, name_y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)
            
            # Уровень персонажа
            level_text = f"Уровень {char['level']}"
            level_y = name_y + 20
            cv2.putText(screenshot, level_text, (name_x, level_y), cv2.FONT_HERSHEY_SIMPLEX, 0.4, (255, 215, 0), 1)
            
            # Класс персонажа
            class_y = level_y + 15
            cv2.putText(screenshot, char["class"], (name_x, class_y), cv2.FONT_HERSHEY_SIMPLEX, 0.4, (192, 192, 192), 1)
            
            # Локация персонажа
            location_x = slot_x + slot_w - 100
            location_y = slot_y + 20
            cv2.putText(screenshot, char["location"], (location_x, location_y), cv2.FONT_HERSHEY_SIMPLEX, 0.3, (128, 128, 128), 1)
        
        # Кнопки (как в пустом экране)
        buttons = [
            {"text": "Создать персонажа", "pos": (50, 650), "size": (150, 40), "color": (0, 255, 0)},
            {"text": "Удалить персонажа", "pos": (250, 650), "size": (150, 40), "color": (255, 0, 0)},
            {"text": "Войти в игру", "pos": (450, 650), "size": (150, 40), "color": (255, 215, 0)},
            {"text": "Назад", "pos": (50, 50), "size": (100, 30), "color": (255, 255, 255)}
        ]
        
        for button in buttons:
            x, y = button["pos"]
            w, h = button["size"]
            color = button["color"]
            
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), (45, 45, 45), -1)
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), color, 1)
            
            text_size = cv2.getTextSize(button["text"], cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)[0]
            text_x = x + (w - text_size[0]) // 2
            text_y = y + (h + text_size[1]) // 2
            cv2.putText(screenshot, button["text"], (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
        
        # Заголовок
        cv2.putText(screenshot, "Выбор персонажа", (50, 100), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 215, 0), 2)
        
        # Сохраняем скриншот
        screenshot_path = self.reference_dir / "reference_character_selection_with_characters.png"
        cv2.imwrite(str(screenshot_path), screenshot)
        
        print(f"  ✅ Эталонный скриншот с персонажами создан: {screenshot_path}")
        return screenshot_path
    
    def generate_max_characters_screenshot(self):
        """Генерирует эталонный скриншот с максимальным количеством персонажей"""
        print("📸 Генерация эталонного скриншота с максимальным количеством персонажей...")
        
        # Размеры экрана
        width = 1024
        height = 768
        
        # Создаем базовое изображение
        screenshot = np.zeros((height, width, 3), dtype=np.uint8)
        screenshot[:] = [30, 30, 46]  # #1e1e2e
        
        # Панель персонажей
        panel_x, panel_y = 50, 134
        panel_w, panel_h = 400, 500
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (30, 30, 46), -1)
        cv2.rectangle(screenshot, (panel_x, panel_y), (panel_x + panel_w, panel_y + panel_h), (255, 215, 0), 2)
        
        # Максимальное количество персонажей (7)
        characters = [
            {"name": "Персонаж1", "level": 25, "class": "Воин", "location": "Гиран"},
            {"name": "Персонаж2", "level": 18, "class": "Маг", "location": "Аден"},
            {"name": "Персонаж3", "level": 22, "class": "Лучник", "location": "Гиран"},
            {"name": "Персонаж4", "level": 20, "class": "Жрец", "location": "Аден"},
            {"name": "Персонаж5", "level": 30, "class": "Рыцарь", "location": "Глодио"},
            {"name": "Персонаж6", "level": 15, "class": "Ассасин", "location": "Дион"},
            {"name": "Персонаж7", "level": 28, "class": "Друид", "location": "Орен"}
        ]
        
        slot_height = 60  # Уменьшенная высота для размещения всех персонажей
        slot_spacing = 3
        start_y = panel_y + 10
        
        for i, char in enumerate(characters):
            slot_y = start_y + i * (slot_height + slot_spacing)
            slot_x = panel_x + 10
            slot_w = panel_w - 20
            
            # Фон слота персонажа
            cv2.rectangle(screenshot, (slot_x, slot_y), (slot_x + slot_w, slot_y + slot_height), (45, 45, 45), -1)
            cv2.rectangle(screenshot, (slot_x, slot_y), (slot_x + slot_w, slot_y + slot_height), (255, 215, 0), 1)
            
            # Аватар персонажа (заглушка)
            avatar_x = slot_x + 5
            avatar_y = slot_y + 5
            avatar_size = 50
            cv2.rectangle(screenshot, (avatar_x, avatar_y), (avatar_x + avatar_size, avatar_y + avatar_size), (0, 0, 0), -1)
            cv2.rectangle(screenshot, (avatar_x, avatar_y), (avatar_x + avatar_size, avatar_y + avatar_size), (255, 255, 255), 1)
            
            # Имя персонажа
            name_x = avatar_x + avatar_size + 5
            name_y = slot_y + 15
            cv2.putText(screenshot, char["name"], (name_x, name_y), cv2.FONT_HERSHEY_SIMPLEX, 0.4, (255, 255, 255), 1)
            
            # Уровень персонажа
            level_text = f"Ур. {char['level']}"
            level_y = name_y + 12
            cv2.putText(screenshot, level_text, (name_x, level_y), cv2.FONT_HERSHEY_SIMPLEX, 0.3, (255, 215, 0), 1)
            
            # Класс персонажа
            class_y = level_y + 10
            cv2.putText(screenshot, char["class"], (name_x, class_y), cv2.FONT_HERSHEY_SIMPLEX, 0.3, (192, 192, 192), 1)
            
            # Локация персонажа
            location_x = slot_x + slot_w - 80
            location_y = slot_y + 15
            cv2.putText(screenshot, char["location"], (location_x, location_y), cv2.FONT_HERSHEY_SIMPLEX, 0.25, (128, 128, 128), 1)
        
        # Кнопки
        buttons = [
            {"text": "Создать персонажа", "pos": (50, 650), "size": (150, 40), "color": (0, 255, 0)},
            {"text": "Удалить персонажа", "pos": (250, 650), "size": (150, 40), "color": (255, 0, 0)},
            {"text": "Войти в игру", "pos": (450, 650), "size": (150, 40), "color": (255, 215, 0)},
            {"text": "Назад", "pos": (50, 50), "size": (100, 30), "color": (255, 255, 255)}
        ]
        
        for button in buttons:
            x, y = button["pos"]
            w, h = button["size"]
            color = button["color"]
            
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), (45, 45, 45), -1)
            cv2.rectangle(screenshot, (x, y), (x + w, y + h), color, 1)
            
            text_size = cv2.getTextSize(button["text"], cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)[0]
            text_x = x + (w - text_size[0]) // 2
            text_y = y + (h + text_size[1]) // 2
            cv2.putText(screenshot, button["text"], (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
        
        # Заголовок
        cv2.putText(screenshot, "Выбор персонажа", (50, 100), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 215, 0), 2)
        
        # Сохраняем скриншот
        screenshot_path = self.reference_dir / "reference_character_selection_max_characters.png"
        cv2.imwrite(str(screenshot_path), screenshot)
        
        print(f"  ✅ Эталонный скриншот с максимальным количеством персонажей создан: {screenshot_path}")
        return screenshot_path
    
    def generate_all_reference_screenshots(self):
        """Генерирует все эталонные скриншоты"""
        print("📸 Генерация всех эталонных скриншотов экрана выбора персонажей")
        print("=" * 70)
        
        start_time = datetime.now()
        
        # Генерируем все скриншоты
        empty_screenshot = self.generate_empty_character_selection_screenshot()
        with_characters_screenshot = self.generate_character_selection_with_characters_screenshot()
        max_characters_screenshot = self.generate_max_characters_screenshot()
        
        # Создаем отчет
        report = {
            "generation_timestamp": datetime.now().isoformat(),
            "generation_duration": (datetime.now() - start_time).total_seconds(),
            "generated_screenshots": [
                {
                    "name": "empty_character_selection",
                    "path": str(empty_screenshot),
                    "description": "Пустой экран выбора персонажей"
                },
                {
                    "name": "character_selection_with_characters",
                    "path": str(with_characters_screenshot),
                    "description": "Экран выбора персонажей с персонажами"
                },
                {
                    "name": "max_characters_selection",
                    "path": str(max_characters_screenshot),
                    "description": "Экран выбора персонажей с максимальным количеством персонажей"
                }
            ],
            "screenshot_specifications": {
                "resolution": "1024x768",
                "background_color": "#1e1e2e",
                "panel_color": "#1e1e2e",
                "panel_border_color": "#FFD700",
                "character_slot_color": "#2d2d2d",
                "character_slot_border_color": "#FFD700",
                "button_colors": {
                    "create": "#00FF00",
                    "delete": "#FF0000",
                    "enter": "#FFD700",
                    "back": "#FFFFFF"
                }
            }
        }
        
        # Сохраняем отчет
        report_file = self.reference_dir / "reference_screenshots_report.json"
        with open(report_file, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        # Выводим результаты
        print(f"\n📊 РЕЗУЛЬТАТЫ ГЕНЕРАЦИИ ЭТАЛОННЫХ СКРИНШОТОВ:")
        print("-" * 50)
        print(f"Создано скриншотов: {len(report['generated_screenshots'])}")
        print(f"Время генерации: {report['generation_duration']:.2f} секунд")
        print(f"Отчет сохранен: {report_file}")
        
        print(f"\n✅ Все эталонные скриншоты созданы успешно!")
        print(f"📁 Директория: {self.reference_dir}")
        
        return report

def main():
    """Основная функция"""
    phase3_dir = "/home/ni/Projects/la2bots/modern_client/phase_3_character_selection/phase_3_character_selection"
    generator = ReferenceScreenshotGenerator(phase3_dir)
    generator.generate_all_reference_screenshots()

if __name__ == "__main__":
    main()
