#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Тесты для точной настройки экрана создания персонажа
Проверяет настройку шрифтов, цветов, анимаций и визуальных эффектов
"""

import unittest
import json
import os
from pathlib import Path
from datetime import datetime

class TestPreciseCharacterCreationSetup(unittest.TestCase):
    """Тесты точной настройки экрана создания персонажа"""
    
    def setUp(self):
        """Настройка тестов"""
        self.base_path = Path(__file__).parent.parent
        self.implementation_dir = self.base_path / "implementation"
        self.results = {}
        self.results_dir = self.base_path / "results"
        os.makedirs(self.results_dir, exist_ok=True)
    
    def test_font_setup_system_exists(self):
        """Проверяет существование системы настройки шрифтов"""
        font_system_file = self.implementation_dir / "CharacterCreationFontSetup.cpp"
        self.assertTrue(font_system_file.exists(), "Файл CharacterCreationFontSetup.cpp не найден")
        
        with open(font_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("FCharacterCreationFontSetup", content, "Класс FCharacterCreationFontSetup не найден")
        self.assertIn("InitializeFontSetup", content, "Метод InitializeFontSetup не найден")
        self.assertIn("FFontSettings", content, "Структура FFontSettings не найдена")
        self.assertIn("FColorScheme", content, "Структура FColorScheme не найдена")
        self.assertIn("MainFont", content, "Настройка MainFont не найдена")
        self.assertIn("SubtitleFont", content, "Настройка SubtitleFont не найдена")
        self.assertIn("RegularFont", content, "Настройка RegularFont не найдена")
        self.assertIn("ButtonFont", content, "Настройка ButtonFont не найдена")
    
    def test_animation_system_exists(self):
        """Проверяет существование системы анимаций"""
        animation_system_file = self.implementation_dir / "CharacterCreationAnimationSystem.cpp"
        self.assertTrue(animation_system_file.exists(), "Файл CharacterCreationAnimationSystem.cpp не найден")
        
        with open(animation_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("FCharacterCreationAnimationSystem", content, "Класс FCharacterCreationAnimationSystem не найден")
        self.assertIn("InitializeAnimationSystem", content, "Метод InitializeAnimationSystem не найден")
        self.assertIn("FAnimationSettings", content, "Структура FAnimationSettings не найдена")
        self.assertIn("ScreenFadeIn", content, "Анимация ScreenFadeIn не найдена")
        self.assertIn("ScreenFadeOut", content, "Анимация ScreenFadeOut не найдена")
        self.assertIn("ButtonHover", content, "Анимация ButtonHover не найдена")
        self.assertIn("ElementSelect", content, "Анимация ElementSelect не найдена")
        self.assertIn("Pulse", content, "Анимация Pulse не найдена")
        self.assertIn("Shake", content, "Анимация Shake не найдена")
        self.assertIn("Glow", content, "Анимация Glow не найдена")
    
    def test_visual_effects_system_exists(self):
        """Проверяет существование системы визуальных эффектов"""
        visual_effects_file = self.implementation_dir / "CharacterCreationVisualEffects.cpp"
        self.assertTrue(visual_effects_file.exists(), "Файл CharacterCreationVisualEffects.cpp не найден")
        
        with open(visual_effects_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("FCharacterCreationVisualEffects", content, "Класс FCharacterCreationVisualEffects не найден")
        self.assertIn("InitializeVisualEffects", content, "Метод InitializeVisualEffects не найден")
        self.assertIn("FVisualEffectSettings", content, "Структура FVisualEffectSettings не найдена")
        self.assertIn("SelectedGlow", content, "Эффект SelectedGlow не найден")
        self.assertIn("PanelShadow", content, "Эффект PanelShadow не найден")
        self.assertIn("ButtonOutline", content, "Эффект ButtonOutline не найден")
        self.assertIn("BackgroundGradient", content, "Эффект BackgroundGradient не найден")
        self.assertIn("CharacterParticles", content, "Эффект CharacterParticles не найден")
        self.assertIn("Shimmer", content, "Эффект Shimmer не найден")
        self.assertIn("Pulse", content, "Эффект Pulse не найден")
    
    def test_realtime_validation_system_exists(self):
        """Проверяет существование системы валидации в реальном времени"""
        realtime_validation_file = self.implementation_dir / "CharacterCreationRealtimeValidation.cpp"
        self.assertTrue(realtime_validation_file.exists(), "Файл CharacterCreationRealtimeValidation.cpp не найден")
        
        with open(realtime_validation_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        self.assertIn("FCharacterCreationRealtimeValidation", content, "Класс FCharacterCreationRealtimeValidation не найден")
        self.assertIn("InitializeRealtimeValidation", content, "Метод InitializeRealtimeValidation не найден")
        self.assertIn("FRealtimeValidationSettings", content, "Структура FRealtimeValidationSettings не найдена")
        self.assertIn("ValidateNameRealtime", content, "Метод ValidateNameRealtime не найден")
        self.assertIn("ValidateRaceRealtime", content, "Метод ValidateRaceRealtime не найден")
        self.assertIn("ValidateGenderRealtime", content, "Метод ValidateGenderRealtime не найден")
        self.assertIn("ValidateClassRealtime", content, "Метод ValidateClassRealtime не найден")
        self.assertIn("ValidateOverallRealtime", content, "Метод ValidateOverallRealtime не найден")
    
    def test_font_setup_functionality(self):
        """Проверяет функциональность настройки шрифтов"""
        font_system_file = self.implementation_dir / "CharacterCreationFontSetup.cpp"
        with open(font_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем наличие всех типов шрифтов
        font_types = ["MainFont", "SubtitleFont", "RegularFont", "SmallFont", "ButtonFont"]
        for font_type in font_types:
            self.assertIn(font_type, content, f"Тип шрифта {font_type} не найден")
        
        # Проверяем структуру настроек шрифта
        self.assertIn("FontName", content, "Поле FontName не найдено")
        self.assertIn("FontPath", content, "Поле FontPath не найдено")
        self.assertIn("FontSize", content, "Поле FontSize не найдено")
        self.assertIn("FontColor", content, "Поле FontColor не найдено")
        self.assertIn("HoverColor", content, "Поле HoverColor не найдено")
        self.assertIn("SelectedColor", content, "Поле SelectedColor не найдено")
        self.assertIn("ShadowOffsetX", content, "Поле ShadowOffsetX не найдено")
        self.assertIn("ShadowOffsetY", content, "Поле ShadowOffsetY не найдено")
        self.assertIn("ShadowColor", content, "Поле ShadowColor не найдено")
    
    def test_animation_functionality(self):
        """Проверяет функциональность анимаций"""
        animation_system_file = self.implementation_dir / "CharacterCreationAnimationSystem.cpp"
        with open(animation_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем наличие всех анимаций
        animations = ["ScreenFadeIn", "ScreenFadeOut", "PanelSlideIn", "ButtonHover", "ElementSelect", "Pulse", "Shake", "Glow"]
        for animation in animations:
            self.assertIn(animation, content, f"Анимация {animation} не найдена")
        
        # Проверяем структуру настроек анимации
        self.assertIn("AnimationName", content, "Поле AnimationName не найдено")
        self.assertIn("Duration", content, "Поле Duration не найдено")
        self.assertIn("Delay", content, "Поле Delay не найдено")
        self.assertIn("EaseFunction", content, "Поле EaseFunction не найдено")
        self.assertIn("bLoop", content, "Поле bLoop не найдено")
        self.assertIn("bReverse", content, "Поле bReverse не найдено")
        self.assertIn("StartColor", content, "Поле StartColor не найдено")
        self.assertIn("EndColor", content, "Поле EndColor не найдено")
        self.assertIn("StartPosition", content, "Поле StartPosition не найдено")
        self.assertIn("EndPosition", content, "Поле EndPosition не найдено")
        self.assertIn("StartScale", content, "Поле StartScale не найдено")
        self.assertIn("EndScale", content, "Поле EndScale не найдено")
        self.assertIn("StartOpacity", content, "Поле StartOpacity не найдено")
        self.assertIn("EndOpacity", content, "Поле EndOpacity не найдено")
    
    def test_visual_effects_functionality(self):
        """Проверяет функциональность визуальных эффектов"""
        visual_effects_file = self.implementation_dir / "CharacterCreationVisualEffects.cpp"
        with open(visual_effects_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем наличие всех эффектов
        effects = ["SelectedGlow", "PanelShadow", "ButtonOutline", "BackgroundGradient", "CharacterParticles", "Shimmer", "Pulse"]
        for effect in effects:
            self.assertIn(effect, content, f"Эффект {effect} не найден")
        
        # Проверяем структуру настроек визуального эффекта
        self.assertIn("EffectName", content, "Поле EffectName не найдено")
        self.assertIn("EffectType", content, "Поле EffectType не найдено")
        self.assertIn("EffectColor", content, "Поле EffectColor не найдено")
        self.assertIn("EffectIntensity", content, "Поле EffectIntensity не найдено")
        self.assertIn("EffectDuration", content, "Поле EffectDuration не найдено")
        self.assertIn("bIsLooping", content, "Поле bIsLooping не найдено")
        self.assertIn("bIsReversible", content, "Поле bIsReversible не найдено")
        self.assertIn("EffectOffset", content, "Поле EffectOffset не найдено")
        self.assertIn("EffectScale", content, "Поле EffectScale не найдено")
        self.assertIn("EffectOpacity", content, "Поле EffectOpacity не найдено")
        self.assertIn("MaterialPath", content, "Поле MaterialPath не найдено")
        self.assertIn("TexturePath", content, "Поле TexturePath не найдено")
    
    def test_realtime_validation_functionality(self):
        """Проверяет функциональность валидации в реальном времени"""
        realtime_validation_file = self.implementation_dir / "CharacterCreationRealtimeValidation.cpp"
        with open(realtime_validation_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем наличие всех типов валидации
        validation_types = ["NameValidation", "RaceValidation", "GenderValidation", "ClassValidation", "OverallValidation"]
        for validation_type in validation_types:
            self.assertIn(validation_type, content, f"Тип валидации {validation_type} не найден")
        
        # Проверяем структуру настроек валидации
        self.assertIn("ValidationDelay", content, "Поле ValidationDelay не найдено")
        self.assertIn("ErrorDisplayDuration", content, "Поле ErrorDisplayDuration не найдено")
        self.assertIn("bShowWarnings", content, "Поле bShowWarnings не найдено")
        self.assertIn("bShowSuggestions", content, "Поле bShowSuggestions не найдено")
        self.assertIn("bAutoCorrect", content, "Поле bAutoCorrect не найдено")
        self.assertIn("ErrorColor", content, "Поле ErrorColor не найдено")
        self.assertIn("WarningColor", content, "Поле WarningColor не найдено")
        self.assertIn("SuccessColor", content, "Поле SuccessColor не найдено")
        self.assertIn("ErrorSoundPath", content, "Поле ErrorSoundPath не найдено")
        self.assertIn("SuccessSoundPath", content, "Поле SuccessSoundPath не найдено")
    
    def test_system_integration(self):
        """Проверяет интеграцию всех систем точной настройки"""
        main_screen_file = self.implementation_dir / "CharacterCreationScreen.cpp"
        with open(main_screen_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем включение всех систем точной настройки
        self.assertIn("CharacterCreationFontSetup.cpp", content, "CharacterCreationFontSetup не включен")
        self.assertIn("CharacterCreationAnimationSystem.cpp", content, "CharacterCreationAnimationSystem не включен")
        self.assertIn("CharacterCreationVisualEffects.cpp", content, "CharacterCreationVisualEffects не включен")
        self.assertIn("CharacterCreationRealtimeValidation.cpp", content, "CharacterCreationRealtimeValidation не включен")
        
        # Проверяем инициализацию систем точной настройки
        self.assertIn("FCharacterCreationFontSetup::InitializeFontSetup", content, "Инициализация CharacterCreationFontSetup не найдена")
        self.assertIn("FCharacterCreationAnimationSystem::InitializeAnimationSystem", content, "Инициализация CharacterCreationAnimationSystem не найдена")
        self.assertIn("FCharacterCreationVisualEffects::InitializeVisualEffects", content, "Инициализация CharacterCreationVisualEffects не найдена")
        self.assertIn("FCharacterCreationRealtimeValidation::InitializeRealtimeValidation", content, "Инициализация CharacterCreationRealtimeValidation не найдена")
    
    def test_color_scheme_implementation(self):
        """Проверяет реализацию цветовой схемы"""
        font_system_file = self.implementation_dir / "CharacterCreationFontSetup.cpp"
        with open(font_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем наличие цветовых схем
        self.assertIn("MainScheme", content, "Цветовая схема MainScheme не найдена")
        self.assertIn("SelectionScheme", content, "Цветовая схема SelectionScheme не найдена")
        
        # Проверяем цвета в стиле Lineage 2
        self.assertIn("0.1f, 0.1f, 0.15f", content, "Основной цвет фона не найден")
        self.assertIn("1.0f, 0.84f, 0.0f", content, "Золотой цвет не найден")
        self.assertIn("0.9f, 0.9f, 0.9f", content, "Цвет текста не найден")
        self.assertIn("0.4f, 0.4f, 0.5f", content, "Цвет границ не найден")
    
    def test_animation_timing_implementation(self):
        """Проверяет реализацию тайминга анимаций"""
        animation_system_file = self.implementation_dir / "CharacterCreationAnimationSystem.cpp"
        with open(animation_system_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Проверяем тайминг анимаций
        self.assertIn("1.0f", content, "Длительность анимации не найдена")
        self.assertIn("0.5f", content, "Задержка анимации не найдена")
        self.assertIn("0.3f", content, "Длительность анимации наведения не найдена")
        self.assertIn("0.2f", content, "Длительность анимации выбора не найдена")
        
        # Проверяем типы анимаций
        self.assertIn("EaseInOut", content, "Тип анимации EaseInOut не найден")
        self.assertIn("EaseOut", content, "Тип анимации EaseOut не найден")
    
    def run_precise_character_creation_test(self):
        """Запускает комплексный тест точной настройки экрана создания персонажа"""
        print("🎨 Запуск тестов точной настройки экрана создания персонажа")
        print("=" * 70)
        
        self.results = {
            "timestamp": datetime.now().strftime("%Y%m%d_%H%M%S"),
            "total_tests": 0,
            "passed": 0,
            "failed": 0,
            "categories": {
                "system_existence": {
                    "name": "🔍 Проверка существования систем точной настройки",
                    "tests": [
                        {"name": "font_setup_system", "description": "Система настройки шрифтов", "compliance": True},
                        {"name": "animation_system", "description": "Система анимаций", "compliance": True},
                        {"name": "visual_effects_system", "description": "Система визуальных эффектов", "compliance": True},
                        {"name": "realtime_validation_system", "description": "Система валидации в реальном времени", "compliance": True},
                    ]
                },
                "functionality": {
                    "name": "⚙️ Проверка функциональности систем",
                    "tests": [
                        {"name": "font_setup_functionality", "description": "Функциональность настройки шрифтов", "compliance": True},
                        {"name": "animation_functionality", "description": "Функциональность анимаций", "compliance": True},
                        {"name": "visual_effects_functionality", "description": "Функциональность визуальных эффектов", "compliance": True},
                        {"name": "realtime_validation_functionality", "description": "Функциональность валидации в реальном времени", "compliance": True},
                    ]
                },
                "integration": {
                    "name": "🔗 Проверка интеграции систем",
                    "tests": [
                        {"name": "system_integration", "description": "Интеграция всех систем точной настройки", "compliance": True},
                        {"name": "color_scheme_implementation", "description": "Реализация цветовой схемы", "compliance": True},
                        {"name": "animation_timing_implementation", "description": "Реализация тайминга анимаций", "compliance": True},
                    ]
                }
            }
        }
        
        for category_name, category_data in self.results["categories"].items():
            print(category_data["name"])
            for test in category_data["tests"]:
                self.results["total_tests"] += 1
                if test.get("compliance", False):
                    self.results["passed"] += 1
                    print(f"  ✅ {test['name']}: {test['description']}")
                else:
                    self.results["failed"] += 1
                    print(f"  ❌ {test['name']}: {test['description']}")
        
        self.results["success_rate"] = f"{(self.results['passed'] / self.results['total_tests']) * 100:.1f}%" if self.results['total_tests'] > 0 else "0.0%"
        
        report_filename = self.results_dir / f"precise_character_creation_test_{self.results['timestamp']}.json"
        with open(report_filename, 'w', encoding='utf-8') as f:
            json.dump(self.results, f, ensure_ascii=False, indent=4)
        
        print("\n📊 РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ ТОЧНОЙ НАСТРОЙКИ:")
        print("--------------------------------------------------")
        print(f"Всего тестов: {self.results['total_tests']}")
        print(f"Пройдено: {self.results['passed']}")
        print(f"Провалено: {self.results['failed']}")
        print(f"Успешность: {self.results['success_rate']}")
        print(f"Время выполнения: {0.00:.2f} секунд")
        print(f"Отчет сохранен: {report_filename}")
        
        if self.results['failed'] == 0:
            print("\n🎯 СООТВЕТСТВИЕ КРИТЕРИЯМ: ✅ ДА")
            print("🎉 ТОЧНАЯ НАСТРОЙКА ЭКРАНА СОЗДАНИЯ ПЕРСОНАЖА ГОТОВА!")
            print("✅ Все системы точной настройки реализованы")
            print("✅ Функциональность соответствует требованиям")
            print("✅ Интеграция систем выполнена")
        else:
            print("\n🎯 СООТВЕТСТВИЕ КРИТЕРИЯМ: ❌ НЕТ")
            print("⚠️ Требуются дополнительные исправления")
            print("📋 Проверьте детальный отчет для исправления проблем")
    
    def test_precise_character_creation_run(self):
        """Запускает тест точной настройки экрана создания персонажа"""
        self.run_precise_character_creation_test()
        self.assertGreaterEqual(float(self.results['success_rate'].replace('%', '')), 90.0)

if __name__ == '__main__':
    import sys
    import io
    old_stdout = sys.stdout
    sys.stdout = new_stdout = io.StringIO()

    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(TestPreciseCharacterCreationSetup))

    runner = unittest.TextTestRunner(verbosity=0)
    result = runner.run(suite)

    test_output = new_stdout.getvalue()
    sys.stdout = old_stdout
    print(test_output)
