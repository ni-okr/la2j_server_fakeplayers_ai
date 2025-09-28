#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Тесты для экрана создания персонажа
Проверяет базовую функциональность и соответствие требованиям
"""

import unittest
import json
import os
from pathlib import Path

class TestCharacterCreationScreen(unittest.TestCase):
    """Тесты экрана создания персонажа"""
    
    def setUp(self):
        """Настройка тестов"""
        self.base_path = Path(__file__).parent.parent
        self.analysis_file = self.base_path / "analysis" / "character_creation_analysis.json"
        self.implementation_dir = self.base_path / "implementation"
    
    def test_analysis_file_exists(self):
        """Проверяет существование файла анализа"""
        self.assertTrue(self.analysis_file.exists(), "Файл анализа не найден")
    
    def test_analysis_file_content(self):
        """Проверяет содержимое файла анализа"""
        with open(self.analysis_file, 'r', encoding='utf-8') as f:
            analysis = json.load(f)
        
        self.assertIn('elements', analysis, "Отсутствует раздел elements")
        self.assertIn('functionality', analysis, "Отсутствует раздел functionality")
        self.assertIn('visual_requirements', analysis, "Отсутствует раздел visual_requirements")
    
    def test_implementation_files_exist(self):
        """Проверяет существование файлов реализации"""
        required_files = [
            "CharacterCreationScreen.h",
            "CharacterCreationScreen.cpp",
            "WBP_CharacterCreationScreen.json"
        ]
        
        for file_name in required_files:
            file_path = self.implementation_dir / file_name
            self.assertTrue(file_path.exists(), f"Файл {file_name} не найден")
    
    def test_character_creation_screen_h_content(self):
        """Проверяет содержимое заголовочного файла"""
        header_file = self.implementation_dir / "CharacterCreationScreen.h"
        with open(header_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("UCharacterCreationScreen", content, "Отсутствует класс UCharacterCreationScreen")
        self.assertIn("OnCreateCharacterButtonClicked", content, "Отсутствует обработчик создания персонажа")
        self.assertIn("OnCancelButtonClicked", content, "Отсутствует обработчик отмены")
        self.assertIn("ValidateCharacterData", content, "Отсутствует функция валидации")
    
    def test_character_creation_screen_cpp_content(self):
        """Проверяет содержимое файла реализации"""
        cpp_file = self.implementation_dir / "CharacterCreationScreen.cpp"
        with open(cpp_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("UCharacterCreationScreen::", content, "Отсутствует реализация класса")
        self.assertIn("NativeConstruct", content, "Отсутствует функция NativeConstruct")
        self.assertIn("InitializeCharacterCreation", content, "Отсутствует функция инициализации")
    
    def test_blueprint_template_content(self):
        """Проверяет содержимое шаблона Blueprint"""
        blueprint_file = self.implementation_dir / "WBP_CharacterCreationScreen.json"
        with open(blueprint_file, 'r', encoding='utf-8') as f:
            blueprint = json.load(f)
        
        self.assertIn('components', blueprint, "Отсутствует раздел components")
        self.assertIn('create_button', blueprint['components'], "Отсутствует кнопка создания")
        self.assertIn('cancel_button', blueprint['components'], "Отсутствует кнопка отмены")
        self.assertIn('name_input_field', blueprint['components'], "Отсутствует поле ввода имени")

if __name__ == '__main__':
    print("🧪 Запуск тестов экрана создания персонажа")
    print("=" * 50)
    
    unittest.main(verbosity=2)
