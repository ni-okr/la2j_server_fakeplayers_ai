#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Система улучшения современного клиента на основе эталонного
Анализирует эталонный клиент и улучшает наш современный клиент
"""

import os
import sys
import json
import subprocess
from pathlib import Path
from typing import Dict, List, Any
import shutil

class ModernClientImprover:
    """Система улучшения современного клиента"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.reference_client = self.project_root / "reference_client"
        self.modern_client = self.project_root / "modern_client"
        self.original_client = self.project_root / "client"
        self.deobfuscated_client = self.project_root / "client_deobfuscation"
        
    def analyze_reference_client(self) -> Dict[str, Any]:
        """Анализирует эталонный клиент"""
        print("🔍 Анализ эталонного клиента...")
        
        analysis = {
            "gui_elements": self.analyze_gui_elements(),
            "textures": self.analyze_reference_textures(),
            "sounds": self.analyze_reference_sounds(),
            "maps": self.analyze_reference_maps(),
            "models": self.analyze_reference_models()
        }
        
        return analysis
    
    def analyze_gui_elements(self) -> Dict[str, Any]:
        """Анализирует элементы GUI эталонного клиента"""
        gui_elements = {
            "login_screen": {
                "background_textures": [],
                "button_styles": [],
                "input_fields": [],
                "layout": "centered_form"
            },
            "character_selection": {
                "character_models": [],
                "ui_panels": [],
                "layout": "split_panel"
            },
            "game_interface": {
                "hp_mp_bars": [],
                "skill_panels": [],
                "inventory_grid": [],
                "minimap": [],
                "chat_window": []
            }
        }
        
        # Анализируем текстуры интерфейса
        ui_textures_dir = self.reference_client / "textures"
        if ui_textures_dir.exists():
            for texture_file in ui_textures_dir.rglob("*.utx"):
                name = texture_file.name.lower()
                
                if any(keyword in name for keyword in ['ui', 'interface', 'menu', 'button']):
                    if 'login' in name or 'background' in name:
                        gui_elements["login_screen"]["background_textures"].append(str(texture_file))
                    elif 'button' in name:
                        gui_elements["login_screen"]["button_styles"].append(str(texture_file))
                    elif 'input' in name or 'field' in name:
                        gui_elements["login_screen"]["input_fields"].append(str(texture_file))
                    elif 'character' in name or 'char' in name:
                        gui_elements["character_selection"]["character_models"].append(str(texture_file))
                    elif 'hp' in name or 'mp' in name or 'bar' in name:
                        gui_elements["game_interface"]["hp_mp_bars"].append(str(texture_file))
                    elif 'skill' in name or 'spell' in name:
                        gui_elements["game_interface"]["skill_panels"].append(str(texture_file))
                    elif 'inventory' in name or 'item' in name:
                        gui_elements["game_interface"]["inventory_grid"].append(str(texture_file))
                    elif 'map' in name or 'minimap' in name:
                        gui_elements["game_interface"]["minimap"].append(str(texture_file))
                    elif 'chat' in name:
                        gui_elements["game_interface"]["chat_window"].append(str(texture_file))
        
        return gui_elements
    
    def analyze_reference_textures(self) -> Dict[str, Any]:
        """Анализирует текстуры эталонного клиента"""
        textures = {
            "terrain": [],
            "characters": [],
            "buildings": [],
            "effects": [],
            "ui": []
        }
        
        textures_dir = self.reference_client / "textures"
        if textures_dir.exists():
            for texture_file in textures_dir.rglob("*.utx"):
                name = texture_file.name.lower()
                
                if any(keyword in name for keyword in ['terrain', 'ground', 'grass', 'stone', 'sand', 'snow']):
                    textures["terrain"].append(str(texture_file))
                elif any(keyword in name for keyword in ['char', 'human', 'elf', 'dwarf', 'orc', 'darkelf']):
                    textures["characters"].append(str(texture_file))
                elif any(keyword in name for keyword in ['building', 'castle', 'house', 'tower', 'wall']):
                    textures["buildings"].append(str(texture_file))
                elif any(keyword in name for keyword in ['effect', 'particle', 'magic', 'spell']):
                    textures["effects"].append(str(texture_file))
                elif any(keyword in name for keyword in ['ui', 'interface', 'menu', 'button']):
                    textures["ui"].append(str(texture_file))
        
        return textures
    
    def analyze_reference_sounds(self) -> Dict[str, Any]:
        """Анализирует звуки эталонного клиента"""
        sounds = {
            "music": [],
            "effects": [],
            "voice": [],
            "ambient": []
        }
        
        sounds_dir = self.reference_client / "sounds"
        if sounds_dir.exists():
            for sound_file in sounds_dir.rglob("*"):
                if sound_file.is_file() and sound_file.suffix.lower() in ['.uax', '.ogg', '.wav']:
                    name = sound_file.name.lower()
                    
                    if 'music' in name or 'bgm' in name:
                        sounds["music"].append(str(sound_file))
                    elif 'effect' in name or 'sfx' in name:
                        sounds["effects"].append(str(sound_file))
                    elif 'voice' in name or 'speech' in name:
                        sounds["voice"].append(str(sound_file))
                    elif 'ambient' in name or 'env' in name:
                        sounds["ambient"].append(str(sound_file))
        
        return sounds
    
    def analyze_reference_maps(self) -> Dict[str, Any]:
        """Анализирует карты эталонного клиента"""
        maps = {
            "world_maps": [],
            "dungeon_maps": [],
            "city_maps": [],
            "pvp_maps": []
        }
        
        maps_dir = self.reference_client / "maps"
        if maps_dir.exists():
            for map_file in maps_dir.rglob("*.unr"):
                name = map_file.name.lower()
                
                if any(keyword in name for keyword in ['world', 'field', 'outdoor']):
                    maps["world_maps"].append(str(map_file))
                elif any(keyword in name for keyword in ['dungeon', 'cave', 'underground']):
                    maps["dungeon_maps"].append(str(map_file))
                elif any(keyword in name for keyword in ['city', 'town', 'village']):
                    maps["city_maps"].append(str(map_file))
                elif any(keyword in name for keyword in ['pvp', 'battle', 'arena']):
                    maps["pvp_maps"].append(str(map_file))
        
        return maps
    
    def analyze_reference_models(self) -> Dict[str, Any]:
        """Анализирует модели эталонного клиента"""
        models = {
            "characters": [],
            "monsters": [],
            "buildings": [],
            "items": []
        }
        
        models_dir = self.reference_client / "staticmeshes"
        if models_dir.exists():
            for model_file in models_dir.rglob("*"):
                if model_file.is_file() and model_file.suffix.lower() in ['.usx', '.3ds', '.obj']:
                    name = model_file.name.lower()
                    
                    if any(keyword in name for keyword in ['char', 'human', 'elf', 'dwarf', 'orc', 'darkelf']):
                        models["characters"].append(str(model_file))
                    elif any(keyword in name for keyword in ['monster', 'mob', 'creature']):
                        models["monsters"].append(str(model_file))
                    elif any(keyword in name for keyword in ['building', 'castle', 'house', 'tower']):
                        models["buildings"].append(str(model_file))
                    elif any(keyword in name for keyword in ['item', 'weapon', 'armor']):
                        models["items"].append(str(model_file))
        
        return models
    
    def create_improved_client_structure(self):
        """Создает улучшенную структуру современного клиента"""
        print("🏗️ Создание улучшенной структуры клиента...")
        
        # Создаем директории для улучшенного клиента
        improved_dirs = [
            "Content/UI/LoginScreen",
            "Content/UI/CharacterSelection", 
            "Content/UI/CharacterCreation",
            "Content/UI/GameInterface",
            "Content/UI/Inventory",
            "Content/UI/Skills",
            "Content/UI/Chat",
            "Content/UI/Minimap",
            "Content/Textures/UI",
            "Content/Textures/Terrain",
            "Content/Textures/Characters",
            "Content/Textures/Buildings",
            "Content/Textures/Effects",
            "Content/Sounds/Music",
            "Content/Sounds/Effects",
            "Content/Sounds/Voice",
            "Content/Sounds/Ambient",
            "Content/Maps/World",
            "Content/Maps/Dungeons",
            "Content/Maps/Cities",
            "Content/Models/Characters",
            "Content/Models/Monsters",
            "Content/Models/Buildings",
            "Content/Models/Items"
        ]
        
        for dir_path in improved_dirs:
            full_path = self.modern_client / dir_path
            full_path.mkdir(parents=True, exist_ok=True)
    
    def create_ui_blueprints(self):
        """Создает Blueprint'ы для UI элементов"""
        print("🎨 Создание UI Blueprint'ов...")
        
        ui_blueprints = {
            "LoginScreen": {
                "elements": [
                    "BackgroundImage",
                    "LoginField", 
                    "PasswordField",
                    "LoginButton",
                    "RegisterButton",
                    "SettingsButton"
                ],
                "style": "dark_fantasy_theme"
            },
            "CharacterSelection": {
                "elements": [
                    "CharacterList",
                    "CharacterModel",
                    "CharacterInfo",
                    "EnterGameButton",
                    "CreateCharacterButton",
                    "DeleteCharacterButton"
                ],
                "style": "split_panel_layout"
            },
            "CharacterCreation": {
                "elements": [
                    "RaceSelection",
                    "GenderSelection",
                    "ClassSelection",
                    "AppearanceSettings",
                    "NameInput",
                    "CharacterPreview",
                    "CreateButton",
                    "CancelButton"
                ],
                "style": "customization_panel"
            },
            "GameInterface": {
                "elements": [
                    "HPBar",
                    "MPBar", 
                    "EXPBar",
                    "SkillsPanel",
                    "InventoryPanel",
                    "MinimapPanel",
                    "ChatPanel",
                    "QuickAccessButtons",
                    "GameMenu"
                ],
                "style": "classic_mmorpg_layout"
            }
        }
        
        # Создаем файлы Blueprint'ов
        for blueprint_name, blueprint_data in ui_blueprints.items():
            blueprint_file = self.modern_client / f"Content/UI/{blueprint_name}/{blueprint_name}.uasset"
            blueprint_file.parent.mkdir(parents=True, exist_ok=True)
            
            # Создаем заглушку для Blueprint файла
            with open(blueprint_file, 'w') as f:
                f.write(f"# {blueprint_name} Blueprint\n")
                f.write(f"# Elements: {', '.join(blueprint_data['elements'])}\n")
                f.write(f"# Style: {blueprint_data['style']}\n")
    
    def create_texture_import_script(self):
        """Создает скрипт для импорта текстур из эталонного клиента"""
        print("🖼️ Создание скрипта импорта текстур...")
        
        import_script = """#!/usr/bin/env python3
# -*- coding: utf-8 -*-

\"\"\"
Скрипт импорта текстур из эталонного клиента в современный клиент
\"\"\"

import os
import shutil
from pathlib import Path

def import_textures():
    \"\"\"Импортирует текстуры из эталонного клиента\"\"\"
    
    reference_client = Path("../reference_client")
    modern_client = Path(".")
    
    # Импортируем текстуры UI
    ui_textures_dir = reference_client / "textures"
    modern_ui_dir = modern_client / "Content/Textures/UI"
    
    if ui_textures_dir.exists():
        for texture_file in ui_textures_dir.rglob("*.utx"):
            name = texture_file.name.lower()
            
            if any(keyword in name for keyword in ['ui', 'interface', 'menu', 'button']):
                dest_file = modern_ui_dir / texture_file.name
                shutil.copy2(texture_file, dest_file)
                print(f"Импортирована UI текстура: {texture_file.name}")
    
    # Импортируем текстуры рельефа
    modern_terrain_dir = modern_client / "Content/Textures/Terrain"
    
    if ui_textures_dir.exists():
        for texture_file in ui_textures_dir.rglob("*.utx"):
            name = texture_file.name.lower()
            
            if any(keyword in name for keyword in ['terrain', 'ground', 'grass', 'stone', 'sand', 'snow']):
                dest_file = modern_terrain_dir / texture_file.name
                shutil.copy2(texture_file, dest_file)
                print(f"Импортирована текстура рельефа: {texture_file.name}")
    
    print("✅ Импорт текстур завершен!")

if __name__ == "__main__":
    import_textures()
"""
        
        script_file = self.modern_client / "import_textures.py"
        with open(script_file, 'w', encoding='utf-8') as f:
            f.write(import_script)
        
        os.chmod(script_file, 0o755)
    
    def create_improvement_plan(self):
        """Создает план улучшения современного клиента"""
        print("📋 Создание плана улучшения...")
        
        improvement_plan = {
            "phase_1": {
                "name": "Базовый UI интерфейс",
                "duration": "1-2 недели",
                "tasks": [
                    "Создать экран входа в игру",
                    "Создать экран выбора персонажей",
                    "Создать экран создания персонажа",
                    "Создать базовый игровой интерфейс"
                ],
                "priority": "high"
            },
            "phase_2": {
                "name": "Игровые системы",
                "duration": "2-3 недели", 
                "tasks": [
                    "Реализовать панель инвентаря",
                    "Реализовать панель скилов",
                    "Реализовать систему чата",
                    "Реализовать миникарту"
                ],
                "priority": "high"
            },
            "phase_3": {
                "name": "Визуальные улучшения",
                "duration": "2-3 недели",
                "tasks": [
                    "Импортировать текстуры из эталонного клиента",
                    "Создать PBR материалы",
                    "Настроить освещение",
                    "Добавить эффекты частиц"
                ],
                "priority": "medium"
            },
            "phase_4": {
                "name": "Аудио системы",
                "duration": "1-2 недели",
                "tasks": [
                    "Импортировать музыку из эталонного клиента",
                    "Импортировать звуковые эффекты",
                    "Настроить 3D аудио",
                    "Добавить голосовые реплики"
                ],
                "priority": "medium"
            },
            "phase_5": {
                "name": "Оптимизация и полировка",
                "duration": "1-2 недели",
                "tasks": [
                    "Оптимизировать производительность",
                    "Исправить баги",
                    "Добавить настройки интерфейса",
                    "Тестирование и отладка"
                ],
                "priority": "high"
            }
        }
        
        plan_file = self.modern_client / "improvement_plan.json"
        with open(plan_file, 'w', encoding='utf-8') as f:
            json.dump(improvement_plan, f, ensure_ascii=False, indent=2)
    
    def create_comparison_report(self):
        """Создает отчет сравнения клиентов"""
        print("📊 Создание отчета сравнения...")
        
        # Анализируем эталонный клиент
        reference_analysis = self.analyze_reference_client()
        
        # Создаем отчет
        report = {
            "comparison_date": "2024-09-28",
            "reference_client": {
                "size": "6.1GB",
                "files": 1448,
                "textures": len(reference_analysis["textures"]["ui"]) + len(reference_analysis["textures"]["terrain"]),
                "sounds": len(reference_analysis["sounds"]["music"]) + len(reference_analysis["sounds"]["effects"]),
                "maps": len(reference_analysis["maps"]["world_maps"]) + len(reference_analysis["maps"]["city_maps"])
            },
            "modern_client": {
                "size": "2.6GB", 
                "files": 614,
                "textures": 0,  # Пока нет импортированных текстур
                "sounds": 0,    # Пока нет импортированных звуков
                "maps": 0       # Пока нет импортированных карт
            },
            "improvements_needed": [
                "Импорт текстур UI из эталонного клиента",
                "Создание экранов входа и выбора персонажей",
                "Реализация игрового интерфейса",
                "Импорт звуков и музыки",
                "Создание системы карт",
                "Оптимизация производительности"
            ],
            "recommendations": [
                "Начать с Phase 1 - базовый UI интерфейс",
                "Использовать эталонный клиент как визуальный эталон",
                "Сохранить классический стиль MMORPG",
                "Добавить современные улучшения (лучшая графика, звук)",
                "Обеспечить совместимость с L2J сервером"
            ]
        }
        
        report_file = self.modern_client / "comparison_report.json"
        with open(report_file, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
    
    def run_improvement_process(self):
        """Запускает процесс улучшения клиента"""
        print("🚀 Запуск процесса улучшения современного клиента...")
        
        # Анализируем эталонный клиент
        reference_analysis = self.analyze_reference_client()
        
        # Создаем улучшенную структуру
        self.create_improved_client_structure()
        
        # Создаем UI Blueprint'ы
        self.create_ui_blueprints()
        
        # Создаем скрипт импорта текстур
        self.create_texture_import_script()
        
        # Создаем план улучшения
        self.create_improvement_plan()
        
        # Создаем отчет сравнения
        self.create_comparison_report()
        
        print("✅ Процесс улучшения завершен!")
        print("📁 Результаты сохранены в modern_client/")
        print("📋 План улучшения: improvement_plan.json")
        print("📊 Отчет сравнения: comparison_report.json")

def main():
    """Основная функция"""
    print("🎮 Система улучшения современного клиента")
    print("=" * 50)
    
    # Создаем систему улучшения
    improver = ModernClientImprover("/home/ni/Projects/la2bots")
    
    # Запускаем процесс улучшения
    improver.run_improvement_process()
    
    print("\\n🎯 СЛЕДУЮЩИЕ ШАГИ:")
    print("1. Запустите import_textures.py для импорта текстур")
    print("2. Изучите improvement_plan.json для плана работ")
    print("3. Начните с Phase 1 - базовый UI интерфейс")
    print("4. Используйте эталонный клиент как визуальный эталон")

if __name__ == "__main__":
    main()
