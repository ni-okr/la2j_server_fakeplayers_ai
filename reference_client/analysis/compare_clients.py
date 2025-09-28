#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Инструмент сравнения эталонного и современного клиентов
"""

import json
import os
from pathlib import Path

def compare_clients():
    """Сравнивает эталонный и современный клиенты"""
    
    # Загружаем спецификации
    with open('analysis/gui_specification.json', 'r', encoding='utf-8') as f:
        reference_spec = json.load(f)
    
    # Здесь будет загрузка спецификации современного клиента
    # modern_spec = load_modern_client_spec()
    
    print("🔍 Сравнение клиентов:")
    print("=" * 50)
    
    # Сравниваем элементы интерфейса
    for screen_name, screen_spec in reference_spec.items():
        print(f"\n📱 {screen_name.upper()}:")
        
        if "elements" in screen_spec:
            for element_name, element_spec in screen_spec["elements"].items():
                print(f"  ✅ {element_name}: {element_spec.get('style', 'N/A')}")
        else:
            print(f"  📋 {screen_spec.get('layout', 'N/A')}")

if __name__ == "__main__":
    compare_clients()
