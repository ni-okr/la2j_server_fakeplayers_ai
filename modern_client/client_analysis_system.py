#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Система анализа клиентов Lineage II для прогнозирования GUI
Анализирует все версии клиентов и создает описание ожидаемого интерфейса
"""

import os
import sys
import json
import subprocess
import hashlib
from pathlib import Path
from typing import Dict, List, Any, Optional
import re

class ClientAnalyzer:
    """Анализатор клиентов Lineage II"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.clients = {
            'original': self.project_root / 'client',
            'nogg_backup': self.project_root / 'client_nogg_backup', 
            'deobfuscated': self.project_root / 'client_deobfuscation',
            'modern': self.project_root / 'modern_client'
        }
        self.analysis_results = {}
        
    def analyze_all_clients(self) -> Dict[str, Any]:
        """Анализирует все клиенты и создает сводный отчет"""
        print("🔍 Анализ всех клиентов Lineage II...")
        
        for client_name, client_path in self.clients.items():
            print(f"\n📁 Анализ клиента: {client_name}")
            self.analysis_results[client_name] = self.analyze_client(client_path, client_name)
            
        return self.analysis_results
    
    def analyze_client(self, client_path: Path, client_name: str) -> Dict[str, Any]:
        """Анализирует отдельный клиент"""
        if not client_path.exists():
            return {"error": "Клиент не найден"}
            
        analysis = {
            "name": client_name,
            "path": str(client_path),
            "size": self.get_directory_size(client_path),
            "files": self.analyze_files(client_path),
            "executables": self.find_executables(client_path),
            "resources": self.analyze_resources(client_path),
            "gui_elements": self.analyze_gui_elements(client_path),
            "textures": self.analyze_textures(client_path),
            "sounds": self.analyze_sounds(client_path),
            "maps": self.analyze_maps(client_path)
        }
        
        return analysis
    
    def get_directory_size(self, path: Path) -> str:
        """Получает размер директории"""
        try:
            result = subprocess.run(['du', '-sh', str(path)], 
                                  capture_output=True, text=True)
            return result.stdout.split()[0]
        except:
            return "Неизвестно"
    
    def analyze_files(self, path: Path) -> Dict[str, Any]:
        """Анализирует файлы в директории"""
        files_info = {
            "total_files": 0,
            "file_types": {},
            "largest_files": [],
            "executable_files": []
        }
        
        for file_path in path.rglob('*'):
            if file_path.is_file():
                files_info["total_files"] += 1
                
                # Тип файла
                ext = file_path.suffix.lower()
                files_info["file_types"][ext] = files_info["file_types"].get(ext, 0) + 1
                
                # Размер файла
                try:
                    size = file_path.stat().st_size
                    files_info["largest_files"].append((str(file_path), size))
                except:
                    pass
                
                # Исполняемые файлы
                if file_path.suffix.lower() in ['.exe', '.dll', '.so', '.bin']:
                    files_info["executable_files"].append(str(file_path))
        
        # Сортируем по размеру
        files_info["largest_files"].sort(key=lambda x: x[1], reverse=True)
        files_info["largest_files"] = files_info["largest_files"][:10]
        
        return files_info
    
    def find_executables(self, path: Path) -> List[Dict[str, Any]]:
        """Находит исполняемые файлы"""
        executables = []
        
        for file_path in path.rglob('*'):
            if file_path.is_file() and file_path.suffix.lower() == '.exe':
                try:
                    # Анализ PE файла
                    result = subprocess.run(['file', str(file_path)], 
                                          capture_output=True, text=True)
                    file_info = result.stdout.strip()
                    
                    executables.append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": file_info,
                        "is_main_client": file_path.name.lower() == 'lineageii.exe'
                    })
                except:
                    pass
                    
        return executables
    
    def analyze_resources(self, path: Path) -> Dict[str, Any]:
        """Анализирует игровые ресурсы"""
        resources = {
            "textures": [],
            "sounds": [],
            "animations": [],
            "maps": [],
            "models": []
        }
        
        # Текстуры
        for ext in ['.utx', '.dds', '.tga', '.bmp', '.jpg', '.png']:
            for file_path in path.rglob(f'*{ext}'):
                if file_path.is_file():
                    resources["textures"].append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": ext
                    })
        
        # Звуки
        for ext in ['.uax', '.ogg', '.wav', '.mp3']:
            for file_path in path.rglob(f'*{ext}'):
                if file_path.is_file():
                    resources["sounds"].append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": ext
                    })
        
        # Анимации
        for ext in ['.ukx', '.usk']:
            for file_path in path.rglob(f'*{ext}'):
                if file_path.is_file():
                    resources["animations"].append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": ext
                    })
        
        # Карты
        for ext in ['.unr', '.umx']:
            for file_path in path.rglob(f'*{ext}'):
                if file_path.is_file():
                    resources["maps"].append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": ext
                    })
        
        # Модели
        for ext in ['.usx', '.3ds', '.obj']:
            for file_path in path.rglob(f'*{ext}'):
                if file_path.is_file():
                    resources["models"].append({
                        "name": file_path.name,
                        "path": str(file_path),
                        "size": file_path.stat().st_size,
                        "type": ext
                    })
        
        return resources
    
    def analyze_gui_elements(self, path: Path) -> Dict[str, Any]:
        """Анализирует элементы GUI"""
        gui_elements = {
            "interface_files": [],
            "text_files": [],
            "config_files": [],
            "ui_textures": []
        }
        
        # Файлы интерфейса
        for file_path in path.rglob('*'):
            if file_path.is_file():
                name = file_path.name.lower()
                
                # Интерфейсные файлы
                if any(keyword in name for keyword in ['interface', 'ui', 'gui', 'menu', 'dialog']):
                    gui_elements["interface_files"].append(str(file_path))
                
                # Текстовые файлы
                if file_path.suffix.lower() in ['.txt', '.l2text', '.string']:
                    gui_elements["text_files"].append(str(file_path))
                
                # Конфигурационные файлы
                if file_path.suffix.lower() in ['.ini', '.cfg', '.conf']:
                    gui_elements["config_files"].append(str(file_path))
                
                # UI текстуры
                if any(keyword in name for keyword in ['ui_', 'interface_', 'menu_', 'button_', 'icon_']):
                    gui_elements["ui_textures"].append(str(file_path))
        
        return gui_elements
    
    def analyze_textures(self, path: Path) -> Dict[str, Any]:
        """Анализирует текстуры для понимания визуального стиля"""
        texture_analysis = {
            "terrain_textures": [],
            "character_textures": [],
            "ui_textures": [],
            "effect_textures": [],
            "building_textures": []
        }
        
        for file_path in path.rglob('*.utx'):
            if file_path.is_file():
                name = file_path.name.lower()
                
                # Текстуры рельефа
                if any(keyword in name for keyword in ['terrain', 'ground', 'grass', 'stone', 'sand', 'snow']):
                    texture_analysis["terrain_textures"].append(str(file_path))
                
                # Текстуры персонажей
                elif any(keyword in name for keyword in ['char', 'human', 'elf', 'dwarf', 'orc', 'darkelf']):
                    texture_analysis["character_textures"].append(str(file_path))
                
                # UI текстуры
                elif any(keyword in name for keyword in ['ui', 'interface', 'menu', 'button', 'icon']):
                    texture_analysis["ui_textures"].append(str(file_path))
                
                # Эффекты
                elif any(keyword in name for keyword in ['effect', 'particle', 'magic', 'spell']):
                    texture_analysis["effect_textures"].append(str(file_path))
                
                # Здания
                elif any(keyword in name for keyword in ['building', 'castle', 'house', 'tower', 'wall']):
                    texture_analysis["building_textures"].append(str(file_path))
        
        return texture_analysis
    
    def analyze_sounds(self, path: Path) -> Dict[str, Any]:
        """Анализирует звуки"""
        sound_analysis = {
            "music": [],
            "effects": [],
            "voice": [],
            "ambient": []
        }
        
        for file_path in path.rglob('*'):
            if file_path.is_file() and file_path.suffix.lower() in ['.uax', '.ogg', '.wav']:
                name = file_path.name.lower()
                
                if 'music' in name or 'bgm' in name:
                    sound_analysis["music"].append(str(file_path))
                elif 'effect' in name or 'sfx' in name:
                    sound_analysis["effects"].append(str(file_path))
                elif 'voice' in name or 'speech' in name:
                    sound_analysis["voice"].append(str(file_path))
                elif 'ambient' in name or 'env' in name:
                    sound_analysis["ambient"].append(str(file_path))
        
        return sound_analysis
    
    def analyze_maps(self, path: Path) -> Dict[str, Any]:
        """Анализирует карты"""
        map_analysis = {
            "world_maps": [],
            "dungeon_maps": [],
            "city_maps": [],
            "pvp_maps": []
        }
        
        for file_path in path.rglob('*.unr'):
            if file_path.is_file():
                name = file_path.name.lower()
                
                if any(keyword in name for keyword in ['world', 'field', 'outdoor']):
                    map_analysis["world_maps"].append(str(file_path))
                elif any(keyword in name for keyword in ['dungeon', 'cave', 'underground']):
                    map_analysis["dungeon_maps"].append(str(file_path))
                elif any(keyword in name for keyword in ['city', 'town', 'village']):
                    map_analysis["city_maps"].append(str(file_path))
                elif any(keyword in name for keyword in ['pvp', 'battle', 'arena']):
                    map_analysis["pvp_maps"].append(str(file_path))
        
        return map_analysis
    
    def generate_gui_prediction(self) -> Dict[str, Any]:
        """Генерирует прогноз GUI на основе анализа"""
        print("\n🎨 Генерация прогноза GUI...")
        
        # Берем за основу оригинальный клиент
        original = self.analysis_results.get('original', {})
        deobfuscated = self.analysis_results.get('deobfuscated', {})
        
        gui_prediction = {
            "expected_interface": {
                "login_screen": self.predict_login_screen(original, deobfuscated),
                "character_selection": self.predict_character_selection(original, deobfuscated),
                "character_creation": self.predict_character_creation(original, deobfuscated),
                "game_interface": self.predict_game_interface(original, deobfuscated),
                "inventory": self.predict_inventory(original, deobfuscated),
                "skills": self.predict_skills(original, deobfuscated),
                "chat": self.predict_chat(original, deobfuscated),
                "minimap": self.predict_minimap(original, deobfuscated)
            },
            "visual_style": self.predict_visual_style(original, deobfuscated),
            "audio_style": self.predict_audio_style(original, deobfuscated),
            "recommendations": self.generate_recommendations()
        }
        
        return gui_prediction
    
    def predict_login_screen(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует экран входа"""
        return {
            "elements": [
                "Поле ввода логина",
                "Поле ввода пароля", 
                "Кнопка 'Войти'",
                "Кнопка 'Регистрация'",
                "Кнопка 'Настройки'",
                "Логотип игры",
                "Фоновое изображение"
            ],
            "layout": "Центрированная форма с темным фоном",
            "colors": "Темная тема с золотыми акцентами",
            "fonts": "Стандартный шрифт игры"
        }
    
    def predict_character_selection(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует экран выбора персонажа"""
        return {
            "elements": [
                "Список персонажей",
                "3D модель персонажа",
                "Информация о персонаже (уровень, класс)",
                "Кнопка 'Войти в игру'",
                "Кнопка 'Создать персонажа'",
                "Кнопка 'Удалить персонажа'"
            ],
            "layout": "Список слева, модель справа",
            "colors": "Темная тема с синими акцентами",
            "3d_models": "Полноценные 3D модели персонажей"
        }
    
    def predict_character_creation(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует экран создания персонажа"""
        return {
            "elements": [
                "Выбор расы (Человек, Эльф, Темный Эльф, Орк, Дворф)",
                "Выбор пола",
                "Выбор класса",
                "Настройка внешности",
                "Ввод имени персонажа",
                "3D предварительный просмотр",
                "Кнопка 'Создать'",
                "Кнопка 'Отмена'"
            ],
            "layout": "Панели настроек вокруг 3D модели",
            "customization": [
                "Цвет волос",
                "Прическа", 
                "Цвет глаз",
                "Размер груди (для женских персонажей)",
                "Мускулистость",
                "Высота"
            ]
        }
    
    def predict_game_interface(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует игровой интерфейс"""
        return {
            "main_elements": [
                "Панель здоровья (HP bar)",
                "Панель маны (MP bar)", 
                "Панель опыта (EXP bar)",
                "Панель скилов",
                "Панель инвентаря",
                "Миникарта",
                "Чат",
                "Кнопки быстрого доступа",
                "Меню игры"
            ],
            "layout": "Классический MMORPG интерфейс",
            "positioning": {
                "hp_mp": "Верхний левый угол",
                "skills": "Нижний центр",
                "inventory": "Нижний правый угол", 
                "minimap": "Верхний правый угол",
                "chat": "Нижний левый угол"
            }
        }
    
    def predict_inventory(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует интерфейс инвентаря"""
        return {
            "elements": [
                "Сетка слотов для предметов",
                "Информация о предмете при наведении",
                "Кнопки сортировки",
                "Кнопка 'Использовать'",
                "Кнопка 'Выбросить'",
                "Кнопка 'Продать'"
            ],
            "layout": "Модальное окно с сеткой",
            "grid_size": "8x6 слотов",
            "item_display": "Иконки предметов с рамками качества"
        }
    
    def predict_skills(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует интерфейс скилов"""
        return {
            "elements": [
                "Дерево скилов по классам",
                "Иконки скилов",
                "Описания скилов",
                "Требования для изучения",
                "Кнопка 'Изучить'",
                "Кнопка 'Забыть'"
            ],
            "layout": "Древовидная структура",
            "skill_categories": [
                "Активные скиллы",
                "Пассивные скиллы", 
                "Магические скиллы",
                "Боевые скиллы"
            ]
        }
    
    def predict_chat(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует интерфейс чата"""
        return {
            "elements": [
                "Окно чата с историей сообщений",
                "Поле ввода сообщения",
                "Кнопки каналов (Общий, Торговля, Клан, Личные)",
                "Кнопка 'Отправить'",
                "Настройки чата"
            ],
            "channels": [
                "Общий чат",
                "Торговля",
                "Клан",
                "Личные сообщения",
                "Системные сообщения"
            ],
            "features": [
                "Цветовое кодирование каналов",
                "Фильтрация сообщений",
                "Автодополнение команд"
            ]
        }
    
    def predict_minimap(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует миникарту"""
        return {
            "elements": [
                "Миниатюрная карта области",
                "Позиция игрока",
                "Позиции других игроков",
                "Позиции NPC",
                "Позиции мобов",
                "Кнопки масштабирования",
                "Кнопка 'Открыть большую карту'"
            ],
            "features": [
                "Масштабирование",
                "Фильтры отображения",
                "Маркеры целей",
                "Путевые точки"
            ]
        }
    
    def predict_visual_style(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует визуальный стиль"""
        return {
            "theme": "Фэнтези MMORPG",
            "color_scheme": {
                "primary": "Темно-синий",
                "secondary": "Золотой", 
                "accent": "Серебряный",
                "background": "Темный",
                "text": "Светлый"
            },
            "art_style": "Реалистичный фэнтези",
            "ui_style": "Классический MMORPG интерфейс",
            "textures": {
                "terrain": "Детализированные текстуры земли, травы, камня",
                "characters": "Реалистичные модели персонажей",
                "buildings": "Средневековые здания и замки",
                "effects": "Магические эффекты и частицы"
            }
        }
    
    def predict_audio_style(self, original: Dict, deobfuscated: Dict) -> Dict[str, Any]:
        """Прогнозирует аудио стиль"""
        return {
            "music": {
                "style": "Эпическая оркестровая музыка",
                "themes": [
                    "Главная тема",
                    "Музыка городов",
                    "Музыка подземелий",
                    "Боевая музыка",
                    "Музыка путешествий"
                ]
            },
            "effects": {
                "combat": "Звуки ударов, магии, крики",
                "environment": "Звуки природы, ветра, воды",
                "ui": "Звуки кликов, уведомлений",
                "movement": "Звуки шагов, бега, прыжков"
            },
            "voice": {
                "npc": "Голосовые реплики NPC",
                "player": "Звуки персонажа",
                "system": "Системные уведомления"
            }
        }
    
    def generate_recommendations(self) -> List[str]:
        """Генерирует рекомендации для современного клиента"""
        return [
            "Использовать оригинальный клиент как эталон для GUI",
            "Сохранить классический стиль интерфейса MMORPG",
            "Добавить современные улучшения (лучшая графика, звук)",
            "Обеспечить совместимость с существующими серверами",
            "Добавить настройки интерфейса для пользователей",
            "Реализовать все основные элементы оригинального клиента",
            "Улучшить производительность и стабильность",
            "Добавить поддержку современных разрешений экрана"
        ]
    
    def save_analysis(self, output_file: str = "client_analysis.json"):
        """Сохраняет результаты анализа"""
        output_path = self.project_root / "modern_client" / output_file
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(self.analysis_results, f, ensure_ascii=False, indent=2)
        
        print(f"\n💾 Результаты анализа сохранены в: {output_path}")
    
    def save_gui_prediction(self, output_file: str = "gui_prediction.json"):
        """Сохраняет прогноз GUI"""
        gui_prediction = self.generate_gui_prediction()
        output_path = self.project_root / "modern_client" / output_file
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(gui_prediction, f, ensure_ascii=False, indent=2)
        
        print(f"\n🎨 Прогноз GUI сохранен в: {output_file}")

def main():
    """Основная функция"""
    print("🎮 Система анализа клиентов Lineage II")
    print("=" * 50)
    
    # Создаем анализатор
    analyzer = ClientAnalyzer("/home/ni/Projects/la2bots")
    
    # Анализируем все клиенты
    results = analyzer.analyze_all_clients()
    
    # Сохраняем результаты
    analyzer.save_analysis()
    analyzer.save_gui_prediction()
    
    # Выводим краткий отчет
    print("\n📊 КРАТКИЙ ОТЧЕТ:")
    print("-" * 30)
    
    for client_name, analysis in results.items():
        if "error" not in analysis:
            print(f"\n{client_name.upper()}:")
            print(f"  Размер: {analysis['size']}")
            print(f"  Файлов: {analysis['files']['total_files']}")
            print(f"  Исполняемых: {len(analysis['executables'])}")
            print(f"  Текстур: {len(analysis['resources']['textures'])}")
            print(f"  Звуков: {len(analysis['resources']['sounds'])}")
            print(f"  Карт: {len(analysis['resources']['maps'])}")
    
    print("\n✅ Анализ завершен!")
    print("📁 Результаты сохранены в modern_client/")

if __name__ == "__main__":
    main()
