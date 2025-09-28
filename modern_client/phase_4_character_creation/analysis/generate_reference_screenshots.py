#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Генератор эталонных скриншотов для экрана создания персонажа
Создает реалистичные скриншоты на основе анализа Lineage 2
"""

import os
import json
from pathlib import Path
from datetime import datetime
from PIL import Image, ImageDraw, ImageFont
import numpy as np

class ReferenceScreenshotGenerator:
    """Генератор эталонных скриншотов экрана создания персонажа"""
    
    def __init__(self, output_dir: str):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        # Цветовая схема Lineage 2
        self.colors = {
            'background': '#1a1a1a',
            'panel': '#2d2d2d',
            'text': '#ffffff',
            'button': '#4a4a4a',
            'button_hover': '#5a5a5a',
            'selected': '#00ff00',
            'error': '#ff0000',
            'border': '#666666'
        }
        
        # Размеры экрана
        self.screen_width = 1280
        self.screen_height = 720
        
        # Размеры элементов
        self.panel_width = 200
        self.panel_height = 300
        self.button_width = 100
        self.button_height = 40
        
    def create_base_screen(self):
        """Создает базовый экран с фоном"""
        img = Image.new('RGB', (self.screen_width, self.screen_height), self.colors['background'])
        draw = ImageDraw.Draw(img)
        
        # Заголовок экрана
        try:
            font_large = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 24)
            font_medium = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 16)
            font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 12)
        except:
            font_large = ImageFont.load_default()
            font_medium = ImageFont.load_default()
            font_small = ImageFont.load_default()
        
        # Заголовок
        draw.text((50, 50), "СОЗДАНИЕ ПЕРСОНАЖА", fill=self.colors['text'], font=font_large)
        
        return img, draw, font_large, font_medium, font_small
    
    def draw_panel(self, draw, x, y, width, height, title, items, selected_item=None):
        """Рисует панель с элементами"""
        # Фон панели
        draw.rectangle([x, y, x + width, y + height], fill=self.colors['panel'], outline=self.colors['border'])
        
        # Заголовок панели
        try:
            font_medium = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 16)
        except:
            font_medium = ImageFont.load_default()
        
        draw.text((x + 10, y + 10), title, fill=self.colors['text'], font=font_medium)
        
        # Элементы панели
        item_y = y + 40
        for i, item in enumerate(items):
            item_rect = [x + 10, item_y, x + width - 10, item_y + 30]
            
            # Цвет элемента
            if selected_item == i:
                fill_color = self.colors['selected']
            else:
                fill_color = self.colors['button']
            
            # Фон элемента
            draw.rectangle(item_rect, fill=fill_color, outline=self.colors['border'])
            
            # Текст элемента
            try:
                font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 12)
            except:
                font_small = ImageFont.load_default()
            
            draw.text((x + 15, item_y + 8), item, fill=self.colors['text'], font=font_small)
            item_y += 35
    
    def draw_character_preview(self, draw, x, y, width, height):
        """Рисует область предварительного просмотра персонажа"""
        # Фон области превью
        draw.rectangle([x, y, x + width, y + height], fill=self.colors['panel'], outline=self.colors['border'])
        
        # Заголовок
        try:
            font_medium = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 16)
        except:
            font_medium = ImageFont.load_default()
        
        draw.text((x + 10, y + 10), "ПРЕДВАРИТЕЛЬНЫЙ ПРОСМОТР", fill=self.colors['text'], font=font_medium)
        
        # Заглушка 3D модели
        center_x = x + width // 2
        center_y = y + height // 2
        radius = min(width, height) // 4
        
        # Круг для 3D модели
        draw.ellipse([center_x - radius, center_y - radius, center_x + radius, center_y + radius], 
                    fill=self.colors['button'], outline=self.colors['border'])
        
        # Текст "3D МОДЕЛЬ"
        try:
            font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 10)
        except:
            font_small = ImageFont.load_default()
        
        text_bbox = draw.textbbox((0, 0), "3D МОДЕЛЬ", font=font_small)
        text_width = text_bbox[2] - text_bbox[0]
        text_height = text_bbox[3] - text_bbox[1]
        
        draw.text((center_x - text_width // 2, center_y - text_height // 2), 
                 "3D МОДЕЛЬ", fill=self.colors['text'], font=font_small)
    
    def draw_input_field(self, draw, x, y, width, height, placeholder, value=""):
        """Рисует поле ввода"""
        # Фон поля
        draw.rectangle([x, y, x + width, y + height], fill=self.colors['panel'], outline=self.colors['border'])
        
        # Текст поля
        try:
            font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 12)
        except:
            font_small = ImageFont.load_default()
        
        if value:
            draw.text((x + 5, y + 8), value, fill=self.colors['text'], font=font_small)
        else:
            draw.text((x + 5, y + 8), placeholder, fill=self.colors['border'], font=font_small)
    
    def draw_button(self, draw, x, y, width, height, text, color=None):
        """Рисует кнопку"""
        if color is None:
            color = self.colors['button']
        
        # Фон кнопки
        draw.rectangle([x, y, x + width, y + height], fill=color, outline=self.colors['border'])
        
        # Текст кнопки
        try:
            font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 12)
        except:
            font_small = ImageFont.load_default()
        
        text_bbox = draw.textbbox((0, 0), text, font=font_small)
        text_width = text_bbox[2] - text_bbox[0]
        text_height = text_bbox[3] - text_bbox[1]
        
        draw.text((x + (width - text_width) // 2, y + (height - text_height) // 2), 
                 text, fill=self.colors['text'], font=font_small)
    
    def generate_empty_screen(self):
        """Генерирует пустой экран создания персонажа"""
        img, draw, font_large, font_medium, font_small = self.create_base_screen()
        
        # Панель выбора расы
        self.draw_panel(draw, 100, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР РАСЫ", ["Human", "Elf", "Dark Elf", "Orc", "Dwarf"])
        
        # Панель выбора пола
        self.draw_panel(draw, 350, 150, 150, 100, "ПОЛ", ["Male", "Female"])
        
        # Панель выбора класса
        self.draw_panel(draw, 550, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР КЛАССА", ["Fighter", "Mystic", "Scout"])
        
        # Панель кастомизации
        self.draw_panel(draw, 800, 150, 300, 400, "КАСТОМИЗАЦИЯ", 
                       ["Face", "Hair", "Body", "Clothing"])
        
        # Предварительный просмотр
        self.draw_character_preview(draw, 100, 500, 300, 200)
        
        # Поле ввода имени
        self.draw_input_field(draw, 450, 500, 200, 30, "Введите имя персонажа")
        
        # Кнопки
        self.draw_button(draw, 450, 550, self.button_width, self.button_height, "СОЗДАТЬ", "#00aa00")
        self.draw_button(draw, 600, 550, self.button_width, self.button_height, "ОТМЕНА", "#aa0000")
        
        return img
    
    def generate_race_selection_screen(self):
        """Генерирует экран с выбранной расой"""
        img, draw, font_large, font_medium, font_small = self.create_base_screen()
        
        # Панель выбора расы (Human выбран)
        self.draw_panel(draw, 100, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР РАСЫ", ["Human", "Elf", "Dark Elf", "Orc", "Dwarf"], selected_item=0)
        
        # Панель выбора пола
        self.draw_panel(draw, 350, 150, 150, 100, "ПОЛ", ["Male", "Female"])
        
        # Панель выбора класса (обновлена для Human)
        self.draw_panel(draw, 550, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР КЛАССА", ["Fighter", "Mystic", "Scout"])
        
        # Панель кастомизации
        self.draw_panel(draw, 800, 150, 300, 400, "КАСТОМИЗАЦИЯ", 
                       ["Face", "Hair", "Body", "Clothing"])
        
        # Предварительный просмотр
        self.draw_character_preview(draw, 100, 500, 300, 200)
        
        # Поле ввода имени
        self.draw_input_field(draw, 450, 500, 200, 30, "Введите имя персонажа")
        
        # Кнопки
        self.draw_button(draw, 450, 550, self.button_width, self.button_height, "СОЗДАТЬ", "#00aa00")
        self.draw_button(draw, 600, 550, self.button_width, self.button_height, "ОТМЕНА", "#aa0000")
        
        return img
    
    def generate_gender_selection_screen(self):
        """Генерирует экран с выбранным полом"""
        img, draw, font_large, font_medium, font_small = self.create_base_screen()
        
        # Панель выбора расы (Human выбран)
        self.draw_panel(draw, 100, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР РАСЫ", ["Human", "Elf", "Dark Elf", "Orc", "Dwarf"], selected_item=0)
        
        # Панель выбора пола (Male выбран)
        self.draw_panel(draw, 350, 150, 150, 100, "ПОЛ", ["Male", "Female"], selected_item=0)
        
        # Панель выбора класса
        self.draw_panel(draw, 550, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР КЛАССА", ["Fighter", "Mystic", "Scout"])
        
        # Панель кастомизации
        self.draw_panel(draw, 800, 150, 300, 400, "КАСТОМИЗАЦИЯ", 
                       ["Face", "Hair", "Body", "Clothing"])
        
        # Предварительный просмотр
        self.draw_character_preview(draw, 100, 500, 300, 200)
        
        # Поле ввода имени
        self.draw_input_field(draw, 450, 500, 200, 30, "Введите имя персонажа")
        
        # Кнопки
        self.draw_button(draw, 450, 550, self.button_width, self.button_height, "СОЗДАТЬ", "#00aa00")
        self.draw_button(draw, 600, 550, self.button_width, self.button_height, "ОТМЕНА", "#aa0000")
        
        return img
    
    def generate_class_selection_screen(self):
        """Генерирует экран с выбранным классом"""
        img, draw, font_large, font_medium, font_small = self.create_base_screen()
        
        # Панель выбора расы (Human выбран)
        self.draw_panel(draw, 100, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР РАСЫ", ["Human", "Elf", "Dark Elf", "Orc", "Dwarf"], selected_item=0)
        
        # Панель выбора пола (Male выбран)
        self.draw_panel(draw, 350, 150, 150, 100, "ПОЛ", ["Male", "Female"], selected_item=0)
        
        # Панель выбора класса (Fighter выбран)
        self.draw_panel(draw, 550, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР КЛАССА", ["Fighter", "Mystic", "Scout"], selected_item=0)
        
        # Панель кастомизации
        self.draw_panel(draw, 800, 150, 300, 400, "КАСТОМИЗАЦИЯ", 
                       ["Face", "Hair", "Body", "Clothing"])
        
        # Предварительный просмотр
        self.draw_character_preview(draw, 100, 500, 300, 200)
        
        # Поле ввода имени
        self.draw_input_field(draw, 450, 500, 200, 30, "Введите имя персонажа")
        
        # Кнопки
        self.draw_button(draw, 450, 550, self.button_width, self.button_height, "СОЗДАТЬ", "#00aa00")
        self.draw_button(draw, 600, 550, self.button_width, self.button_height, "ОТМЕНА", "#aa0000")
        
        return img
    
    def generate_customization_screen(self):
        """Генерирует экран с кастомизацией"""
        img, draw, font_large, font_medium, font_small = self.create_base_screen()
        
        # Панель выбора расы (Human выбран)
        self.draw_panel(draw, 100, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР РАСЫ", ["Human", "Elf", "Dark Elf", "Orc", "Dwarf"], selected_item=0)
        
        # Панель выбора пола (Male выбран)
        self.draw_panel(draw, 350, 150, 150, 100, "ПОЛ", ["Male", "Female"], selected_item=0)
        
        # Панель выбора класса (Fighter выбран)
        self.draw_panel(draw, 550, 150, self.panel_width, self.panel_height, 
                       "ВЫБОР КЛАССА", ["Fighter", "Mystic", "Scout"], selected_item=0)
        
        # Панель кастомизации (с выбранными опциями)
        self.draw_panel(draw, 800, 150, 300, 400, "КАСТОМИЗАЦИЯ", 
                       ["Face", "Hair", "Body", "Clothing"])
        
        # Предварительный просмотр (обновленный)
        self.draw_character_preview(draw, 100, 500, 300, 200)
        
        # Поле ввода имени (с введенным именем)
        self.draw_input_field(draw, 450, 500, 200, 30, "Введите имя персонажа", "TestPlayer")
        
        # Кнопки
        self.draw_button(draw, 450, 550, self.button_width, self.button_height, "СОЗДАТЬ", "#00aa00")
        self.draw_button(draw, 600, 550, self.button_width, self.button_height, "ОТМЕНА", "#aa0000")
        
        return img
    
    def generate_all_screenshots(self):
        """Генерирует все эталонные скриншоты"""
        print("🎨 Генерация эталонных скриншотов экрана создания персонажа")
        print("=" * 60)
        
        screenshots = [
            ("empty", "Пустой экран создания персонажа"),
            ("race_selection", "Экран с выбранной расой"),
            ("gender_selection", "Экран с выбранным полом"),
            ("class_selection", "Экран с выбранным классом"),
            ("customization", "Экран с кастомизацией")
        ]
        
        for screenshot_type, description in screenshots:
            print(f"📸 Создание скриншота: {description}")
            
            if screenshot_type == "empty":
                img = self.generate_empty_screen()
            elif screenshot_type == "race_selection":
                img = self.generate_race_selection_screen()
            elif screenshot_type == "gender_selection":
                img = self.generate_gender_selection_screen()
            elif screenshot_type == "class_selection":
                img = self.generate_class_selection_screen()
            elif screenshot_type == "customization":
                img = self.generate_customization_screen()
            
            # Сохранение скриншота
            filename = f"reference_character_creation_{screenshot_type}.png"
            filepath = self.output_dir / filename
            img.save(filepath, "PNG")
            
            print(f"✅ Сохранен: {filepath}")
        
        print(f"\\n🎉 Генерация завершена! Создано {len(screenshots)} скриншотов")
        return len(screenshots)

def main():
    """Основная функция"""
    output_dir = "/home/ni/Projects/la2bots/modern_client/phase_4_character_creation/analysis"
    generator = ReferenceScreenshotGenerator(output_dir)
    generator.generate_all_screenshots()

if __name__ == "__main__":
    main()
