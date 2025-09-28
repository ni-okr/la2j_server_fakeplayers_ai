#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Скрипт импорта текстур из эталонного клиента в современный клиент
"""

import os
import shutil
from pathlib import Path

def import_textures():
    """Импортирует текстуры из эталонного клиента"""
    
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
