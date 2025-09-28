#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Система создания эталонного клиента на основе анализа
Создает "идеальный" клиент для сравнения с нашим современным клиентом
"""

import os
import sys
import json
import subprocess
from pathlib import Path
from typing import Dict, List, Any
import shutil

class ReferenceClientSystem:
    """Система создания эталонного клиента"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.reference_client_path = self.project_root / "reference_client"
        self.original_client = self.project_root / "client"
        self.deobfuscated_client = self.project_root / "client_deobfuscation"
        
    def create_reference_client(self):
        """Создает эталонный клиент на основе оригинального"""
        print("🏗️ Создание эталонного клиента...")
        
        # Создаем директорию эталонного клиента
        self.reference_client_path.mkdir(exist_ok=True)
        
        # Копируем основные файлы из оригинального клиента
        self.copy_essential_files()
        
        # Создаем структуру для анализа
        self.create_analysis_structure()
        
        # Создаем документацию эталонного клиента
        self.create_reference_documentation()
        
        print("✅ Эталонный клиент создан!")
    
    def copy_essential_files(self):
        """Копирует основные файлы из оригинального клиента"""
        print("📁 Копирование основных файлов...")
        
        # Копируем исполняемый файл
        if (self.original_client / "LineageII.exe").exists():
            shutil.copy2(
                self.original_client / "LineageII.exe",
                self.reference_client_path / "LineageII.exe"
            )
        
        # Копируем DLL файлы
        for dll_file in self.original_client.glob("*.dll"):
            shutil.copy2(dll_file, self.reference_client_path)
        
        # Копируем важные директории
        important_dirs = [
            "textures", "sounds", "music", "animations", 
            "maps", "staticmeshes", "l2text", "system"
        ]
        
        for dir_name in important_dirs:
            src_dir = self.original_client / dir_name
            if src_dir.exists():
                dst_dir = self.reference_client_path / dir_name
                if dst_dir.exists():
                    shutil.rmtree(dst_dir)
                try:
                    shutil.copytree(src_dir, dst_dir)
                except Exception as e:
                    print(f"⚠️ Ошибка копирования {dir_name}: {e}")
                    # Создаем пустую директорию если не удалось скопировать
                    dst_dir.mkdir(exist_ok=True)
    
    def create_analysis_structure(self):
        """Создает структуру для анализа эталонного клиента"""
        print("🔍 Создание структуры анализа...")
        
        analysis_dir = self.reference_client_path / "analysis"
        analysis_dir.mkdir(exist_ok=True)
        
        # Создаем поддиректории
        subdirs = [
            "gui_elements", "textures", "sounds", "maps", 
            "models", "scripts", "configs", "documentation"
        ]
        
        for subdir in subdirs:
            (analysis_dir / subdir).mkdir(exist_ok=True)
    
    def create_reference_documentation(self):
        """Создает документацию эталонного клиента"""
        print("📚 Создание документации эталонного клиента...")
        
        doc_content = """# 🎯 Эталонный клиент Lineage II

## 📋 Описание

Этот клиент является эталоном для сравнения с нашим современным клиентом.
Создан на основе оригинального клиента с добавлением анализа и документации.

## 🎮 Что должен показывать пользователю

### 1. Экран входа в игру
- **Фон**: Темное фэнтези изображение с логотипом Lineage II
- **Элементы**:
  - Поле ввода логина (светлый текст на темном фоне)
  - Поле ввода пароля (скрытый ввод)
  - Кнопка "Войти" (золотая кнопка)
  - Кнопка "Регистрация" (серебряная кнопка)
  - Кнопка "Настройки" (серая кнопка)
- **Стиль**: Классический MMORPG интерфейс с темной темой

### 2. Экран выбора персонажей
- **Левая панель**: Список персонажей с информацией
- **Правая панель**: 3D модель выбранного персонажа
- **Элементы**:
  - Список персонажей (имя, уровень, класс)
  - 3D модель персонажа (вращается)
  - Информация о персонаже
  - Кнопка "Войти в игру"
  - Кнопка "Создать персонажа"
  - Кнопка "Удалить персонажа"
- **Стиль**: Темная тема с синими акцентами

### 3. Экран создания персонажа
- **Центр**: 3D модель персонажа (полный контроль камеры)
- **Панели настроек**:
  - Выбор расы (Человек, Эльф, Темный Эльф, Орк, Дворф)
  - Выбор пола
  - Выбор класса
  - Настройка внешности:
    - Цвет волос
    - Прическа
    - Цвет глаз
    - Размер груди (для женских персонажей)
    - Мускулистость
    - Высота
  - Ввод имени персонажа
- **Кнопки**: "Создать", "Отмена"
- **Стиль**: Интуитивный интерфейс с предварительным просмотром

### 4. Игровой интерфейс
- **Верхний левый угол**: 
  - Полоска здоровья (красная)
  - Полоска маны (синяя)
  - Полоска опыта (зеленая)
- **Нижний центр**: Панель скилов (горячие клавиши)
- **Нижний правый угол**: Панель инвентаря
- **Верхний правый угол**: Миникарта
- **Нижний левый угол**: Чат
- **Центр экрана**: 3D игровой мир
- **Стиль**: Классический MMORPG интерфейс

### 5. Панель инвентаря
- **Сетка**: 8x6 слотов для предметов
- **Элементы**:
  - Иконки предметов с рамками качества
  - Информация о предмете при наведении
  - Кнопки сортировки
  - Кнопки действий (Использовать, Выбросить, Продать)
- **Стиль**: Модальное окно с темной темой

### 6. Панель скилов
- **Дерево скилов**: Древовидная структура по классам
- **Элементы**:
  - Иконки скилов
  - Описания скилов
  - Требования для изучения
  - Кнопки "Изучить" и "Забыть"
- **Категории**: Активные, Пассивные, Магические, Боевые

### 7. Чат
- **Окно чата**: История сообщений с прокруткой
- **Поле ввода**: Ввод сообщений
- **Каналы**: Общий, Торговля, Клан, Личные, Системные
- **Особенности**: Цветовое кодирование, фильтрация

### 8. Миникарта
- **Карта области**: Миниатюрная карта с позицией игрока
- **Элементы**:
  - Позиция игрока (белая точка)
  - Позиции других игроков (синие точки)
  - Позиции NPC (зеленые точки)
  - Позиции мобов (красные точки)
- **Функции**: Масштабирование, фильтры, маркеры

## 🎨 Визуальный стиль

### Цветовая схема
- **Основной**: Темно-синий (#1a1a2e)
- **Вторичный**: Золотой (#ffd700)
- **Акцент**: Серебряный (#c0c0c0)
- **Фон**: Темный (#0f0f23)
- **Текст**: Светлый (#ffffff)

### Стиль интерфейса
- **Тема**: Классический MMORPG
- **Шрифт**: Стандартный игровой шрифт
- **Кнопки**: Объемные с эффектами наведения
- **Панели**: Полупрозрачные с рамками
- **Иконки**: Стилизованные под фэнтези

## 🔊 Аудио стиль

### Музыка
- **Стиль**: Эпическая оркестровая музыка
- **Темы**: Главная, города, подземелья, бой, путешествия

### Звуковые эффекты
- **Бой**: Удары, магия, крики
- **Окружение**: Природа, ветер, вода
- **Интерфейс**: Клики, уведомления
- **Движение**: Шаги, бег, прыжки

## 📊 Технические характеристики

### Разрешение
- **Минимальное**: 800x600
- **Рекомендуемое**: 1024x768
- **Максимальное**: 1920x1080

### Производительность
- **FPS**: 30-60 FPS
- **Память**: 512MB RAM
- **Видеокарта**: DirectX 9.0c

## 🎯 Критерии успеха

Наш современный клиент должен:
1. **Визуально соответствовать** эталонному клиенту
2. **Функционально повторять** все основные элементы
3. **Улучшать производительность** и стабильность
4. **Добавлять современные возможности** (лучшая графика, звук)
5. **Сохранять совместимость** с существующими серверами

## 📁 Структура файлов

```
reference_client/
├── LineageII.exe              # Исполняемый файл
├── *.dll                      # Библиотеки
├── textures/                  # Текстуры интерфейса
├── sounds/                    # Звуки и музыка
├── maps/                      # Игровые карты
├── animations/                # Анимации персонажей
├── staticmeshes/              # 3D модели
├── l2text/                    # Текстовые файлы
├── system/                    # Системные файлы
└── analysis/                  # Анализ и документация
    ├── gui_elements/          # Элементы GUI
    ├── textures/              # Анализ текстур
    ├── sounds/                # Анализ звуков
    ├── maps/                  # Анализ карт
    ├── models/                # Анализ моделей
    ├── scripts/               # Скрипты
    ├── configs/               # Конфигурации
    └── documentation/         # Документация
```

---

*Эталонный клиент создан: 28 сентября 2024*
*Цель: Создание эталона для сравнения с современным клиентом*
"""
        
        with open(self.reference_client_path / "README.md", 'w', encoding='utf-8') as f:
            f.write(doc_content)
    
    def create_gui_specification(self):
        """Создает техническую спецификацию GUI"""
        print("📋 Создание технической спецификации GUI...")
        
        spec_content = {
            "login_screen": {
                "window_size": "1024x768",
                "background": "dark_fantasy_image",
                "elements": {
                    "login_field": {
                        "position": "center",
                        "size": "200x30",
                        "style": "text_input_dark",
                        "placeholder": "Логин"
                    },
                    "password_field": {
                        "position": "center",
                        "size": "200x30", 
                        "style": "password_input_dark",
                        "placeholder": "Пароль"
                    },
                    "login_button": {
                        "position": "center",
                        "size": "100x40",
                        "style": "golden_button",
                        "text": "Войти"
                    },
                    "register_button": {
                        "position": "center",
                        "size": "100x40",
                        "style": "silver_button", 
                        "text": "Регистрация"
                    }
                }
            },
            "character_selection": {
                "window_size": "1024x768",
                "layout": "split_panel",
                "left_panel": {
                    "width": "300px",
                    "content": "character_list"
                },
                "right_panel": {
                    "width": "724px",
                    "content": "3d_character_model"
                }
            },
            "game_interface": {
                "window_size": "1024x768",
                "elements": {
                    "hp_bar": {
                        "position": "top_left",
                        "size": "200x20",
                        "color": "red"
                    },
                    "mp_bar": {
                        "position": "top_left",
                        "size": "200x20",
                        "color": "blue"
                    },
                    "exp_bar": {
                        "position": "top_left",
                        "size": "200x20",
                        "color": "green"
                    },
                    "skills_panel": {
                        "position": "bottom_center",
                        "size": "400x60",
                        "style": "hotkey_bar"
                    },
                    "inventory_panel": {
                        "position": "bottom_right",
                        "size": "200x150",
                        "style": "inventory_grid"
                    },
                    "minimap": {
                        "position": "top_right",
                        "size": "200x200",
                        "style": "minimap"
                    },
                    "chat": {
                        "position": "bottom_left",
                        "size": "300x150",
                        "style": "chat_window"
                    }
                }
            }
        }
        
        spec_file = self.reference_client_path / "analysis" / "gui_specification.json"
        with open(spec_file, 'w', encoding='utf-8') as f:
            json.dump(spec_content, f, ensure_ascii=False, indent=2)
    
    def create_comparison_tool(self):
        """Создает инструмент для сравнения клиентов"""
        print("🔧 Создание инструмента сравнения...")
        
        comparison_tool = """#!/usr/bin/env python3
# -*- coding: utf-8 -*-

\"\"\"
Инструмент сравнения эталонного и современного клиентов
\"\"\"

import json
import os
from pathlib import Path

def compare_clients():
    \"\"\"Сравнивает эталонный и современный клиенты\"\"\"
    
    # Загружаем спецификации
    with open('analysis/gui_specification.json', 'r', encoding='utf-8') as f:
        reference_spec = json.load(f)
    
    # Здесь будет загрузка спецификации современного клиента
    # modern_spec = load_modern_client_spec()
    
    print("🔍 Сравнение клиентов:")
    print("=" * 50)
    
    # Сравниваем элементы интерфейса
    for screen_name, screen_spec in reference_spec.items():
        print(f"\\n📱 {screen_name.upper()}:")
        
        if "elements" in screen_spec:
            for element_name, element_spec in screen_spec["elements"].items():
                print(f"  ✅ {element_name}: {element_spec.get('style', 'N/A')}")
        else:
            print(f"  📋 {screen_spec.get('layout', 'N/A')}")

if __name__ == "__main__":
    compare_clients()
"""
        
        tool_file = self.reference_client_path / "analysis" / "compare_clients.py"
        with open(tool_file, 'w', encoding='utf-8') as f:
            f.write(comparison_tool)
        
        # Делаем файл исполняемым
        os.chmod(tool_file, 0o755)

def main():
    """Основная функция"""
    print("🎯 Создание эталонного клиента Lineage II")
    print("=" * 50)
    
    # Создаем систему эталонного клиента
    ref_system = ReferenceClientSystem("/home/ni/Projects/la2bots")
    
    # Создаем эталонный клиент
    ref_system.create_reference_client()
    
    # Создаем техническую спецификацию
    ref_system.create_gui_specification()
    
    # Создаем инструмент сравнения
    ref_system.create_comparison_tool()
    
    print("\\n✅ Эталонный клиент создан!")
    print("📁 Расположение: reference_client/")
    print("📚 Документация: reference_client/README.md")
    print("🔧 Инструменты: reference_client/analysis/")

if __name__ == "__main__":
    main()
