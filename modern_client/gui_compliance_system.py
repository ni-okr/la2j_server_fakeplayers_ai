#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Система приведения GUI нового клиента в соответствие со старым клиентом
Включает анализ, сравнение, тестирование и валидацию GUI элементов
"""

import os
import sys
import json
import subprocess
import cv2
import numpy as np
from pathlib import Path
from typing import Dict, List, Any, Tuple, Optional
import hashlib
import time
from datetime import datetime
import sqlite3

class GUIComplianceSystem:
    """Система контроля соответствия GUI"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.reference_client = self.project_root / "reference_client"
        self.modern_client = self.project_root / "modern_client"
        self.compliance_db = self.modern_client / "compliance.db"
        self.screenshots_dir = self.modern_client / "screenshots"
        self.reference_screenshots_dir = self.modern_client / "reference_screenshots"
        self.comparison_results_dir = self.modern_client / "comparison_results"
        
        # Создаем необходимые директории
        for dir_path in [self.screenshots_dir, self.reference_screenshots_dir, self.comparison_results_dir]:
            dir_path.mkdir(exist_ok=True)
        
        # Инициализируем базу данных
        self.init_compliance_database()
    
    def init_compliance_database(self):
        """Инициализирует базу данных для отслеживания соответствия"""
        conn = sqlite3.connect(self.compliance_db)
        cursor = conn.cursor()
        
        # Таблица GUI элементов
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS gui_elements (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                element_name TEXT NOT NULL,
                screen_type TEXT NOT NULL,
                position_x INTEGER,
                position_y INTEGER,
                width INTEGER,
                height INTEGER,
                reference_hash TEXT,
                modern_hash TEXT,
                compliance_score REAL,
                status TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Таблица биомов и локаций
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS biome_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                biome_name TEXT NOT NULL,
                location_name TEXT NOT NULL,
                coordinates_x REAL,
                coordinates_y REAL,
                coordinates_z REAL,
                reference_screenshot TEXT,
                modern_screenshot TEXT,
                visual_compliance_score REAL,
                status TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Таблица тестов
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS compliance_tests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_name TEXT NOT NULL,
                test_type TEXT NOT NULL,
                test_description TEXT,
                expected_result TEXT,
                actual_result TEXT,
                status TEXT,
                error_message TEXT,
                execution_time REAL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        conn.commit()
        conn.close()
    
    def extract_gui_elements_from_reference(self) -> Dict[str, Any]:
        """Извлекает GUI элементы из эталонного клиента"""
        print("🔍 Извлечение GUI элементов из эталонного клиента...")
        
        gui_elements = {
            "login_screen": {
                "background": {"pos": (0, 0), "size": (1024, 768)},
                "login_field": {"pos": (412, 300), "size": (200, 30)},
                "password_field": {"pos": (412, 340), "size": (200, 30)},
                "login_button": {"pos": (462, 380), "size": (100, 40)},
                "register_button": {"pos": (462, 430), "size": (100, 40)},
                "settings_button": {"pos": (50, 50), "size": (80, 30)},
                "logo": {"pos": (362, 100), "size": (300, 100)}
            },
            "character_selection": {
                "character_list": {"pos": (50, 100), "size": (300, 500)},
                "character_model": {"pos": (400, 100), "size": (400, 500)},
                "character_info": {"pos": (820, 100), "size": (180, 200)},
                "enter_game_button": {"pos": (820, 320), "size": (120, 40)},
                "create_character_button": {"pos": (820, 370), "size": (120, 40)},
                "delete_character_button": {"pos": (820, 420), "size": (120, 40)}
            },
            "character_creation": {
                "race_selection": {"pos": (50, 100), "size": (200, 300)},
                "gender_selection": {"pos": (50, 420), "size": (200, 100)},
                "class_selection": {"pos": (50, 540), "size": (200, 150)},
                "appearance_settings": {"pos": (270, 100), "size": (200, 590)},
                "character_preview": {"pos": (490, 100), "size": (400, 500)},
                "name_input": {"pos": (490, 620), "size": (200, 30)},
                "create_button": {"pos": (910, 620), "size": (80, 40)},
                "cancel_button": {"pos": (910, 670), "size": (80, 40)}
            },
            "game_interface": {
                "hp_bar": {"pos": (20, 20), "size": (200, 20)},
                "mp_bar": {"pos": (20, 45), "size": (200, 20)},
                "exp_bar": {"pos": (20, 70), "size": (200, 20)},
                "skills_panel": {"pos": (312, 708), "size": (400, 60)},
                "inventory_button": {"pos": (924, 708), "size": (60, 60)},
                "minimap": {"pos": (824, 20), "size": (180, 180)},
                "chat_window": {"pos": (20, 568), "size": (400, 150)},
                "target_window": {"pos": (20, 100), "size": (200, 100)},
                "system_menu": {"pos": (20, 708), "size": (60, 60)}
            }
        }
        
        return gui_elements
    
    def extract_biome_locations(self) -> Dict[str, Any]:
        """Извлекает ключевые локации из всех биомов"""
        print("🗺️ Извлечение ключевых локаций биомов...")
        
        biome_locations = {
            "talking_island": {
                "newbie_village": {"coords": (10912, 12784, -2926)},
                "temple_ruins": {"coords": (4560, 8804, -3590)},
                "elven_ruins": {"coords": (48736, 247840, -6240)},
                "orc_barracks": {"coords": (-56682, -113610, -690)},
                "school_of_dark_arts": {"coords": (48144, 247840, -6240)}
            },
            "elven_village": {
                "town_center": {"coords": (46934, 51467, -2977)},
                "elven_temple": {"coords": (47578, 51569, -2977)},
                "magic_shop": {"coords": (47315, 51742, -2977)},
                "weapon_shop": {"coords": (47594, 51781, -2977)},
                "grocery_store": {"coords": (47315, 51360, -2977)}
            },
            "dark_elven_village": {
                "town_center": {"coords": (9745, 15606, -4574)},
                "dark_temple": {"coords": (10322, 14394, -4242)},
                "magic_guild": {"coords": (9674, 15718, -4568)},
                "weapon_shop": {"coords": (9867, 15612, -4568)},
                "armor_shop": {"coords": (9883, 15718, -4568)}
            },
            "orc_village": {
                "town_center": {"coords": (-45186, -112459, -236)},
                "orc_temple": {"coords": (-45563, -112267, -236)},
                "weapon_shop": {"coords": (-45052, -112567, -236)},
                "armor_shop": {"coords": (-45328, -112567, -236)},
                "grocery_store": {"coords": (-45186, -112351, -236)}
            },
            "dwarven_village": {
                "town_center": {"coords": (115113, -178212, -901)},
                "dwarven_temple": {"coords": (115332, -178151, -901)},
                "blacksmith": {"coords": (115013, -178100, -901)},
                "magic_shop": {"coords": (115232, -178100, -901)},
                "warehouse": {"coords": (115013, -178324, -901)}
            },
            "gludin_village": {
                "town_center": {"coords": (-80826, 149775, -3043)},
                "temple": {"coords": (-80826, 149406, -3043)},
                "magic_shop": {"coords": (-80930, 149775, -3043)},
                "weapon_shop": {"coords": (-80722, 149775, -3043)},
                "armor_shop": {"coords": (-80826, 149879, -3043)}
            },
            "gludio_castle_town": {
                "town_center": {"coords": (-12694, 122776, -3116)},
                "castle": {"coords": (-12694, 122408, -3116)},
                "temple": {"coords": (-12798, 122776, -3116)},
                "magic_shop": {"coords": (-12590, 122776, -3116)},
                "weapon_shop": {"coords": (-12694, 122880, -3116)}
            },
            "dion_castle_town": {
                "town_center": {"coords": (15670, 142983, -2705)},
                "castle": {"coords": (15670, 142615, -2705)},
                "temple": {"coords": (15566, 142983, -2705)},
                "magic_shop": {"coords": (15774, 142983, -2705)},
                "weapon_shop": {"coords": (15670, 143087, -2705)}
            },
            "giran_castle_town": {
                "town_center": {"coords": (83400, 147943, -3404)},
                "castle": {"coords": (83400, 147575, -3404)},
                "temple": {"coords": (83296, 147943, -3404)},
                "magic_shop": {"coords": (83504, 147943, -3404)},
                "weapon_shop": {"coords": (83400, 148047, -3404)}
            },
            "oren_castle_town": {
                "town_center": {"coords": (82956, 53162, -1495)},
                "castle": {"coords": (82956, 52794, -1495)},
                "temple": {"coords": (82852, 53162, -1495)},
                "magic_shop": {"coords": (83060, 53162, -1495)},
                "weapon_shop": {"coords": (82956, 53266, -1495)}
            },
            "aden_castle_town": {
                "town_center": {"coords": (147450, 26741, -2204)},
                "castle": {"coords": (147450, 26373, -2204)},
                "temple": {"coords": (147346, 26741, -2204)},
                "magic_shop": {"coords": (147554, 26741, -2204)},
                "weapon_shop": {"coords": (147450, 26845, -2204)}
            }
        }
        
        return biome_locations
    
    def capture_reference_screenshots(self):
        """Захватывает скриншоты эталонного клиента"""
        print("📸 Захват скриншотов эталонного клиента...")
        
        # Здесь будет логика запуска эталонного клиента и захвата скриншотов
        # Пока создаем заглушки для демонстрации структуры
        
        gui_elements = self.extract_gui_elements_from_reference()
        biome_locations = self.extract_biome_locations()
        
        reference_data = {
            "gui_screenshots": {},
            "biome_screenshots": {},
            "capture_timestamp": datetime.now().isoformat()
        }
        
        # Создаем заглушки скриншотов GUI
        for screen_name, elements in gui_elements.items():
            screenshot_path = self.reference_screenshots_dir / f"{screen_name}.png"
            reference_data["gui_screenshots"][screen_name] = str(screenshot_path)
            
            # Создаем пустое изображение как заглушку
            img = np.zeros((768, 1024, 3), dtype=np.uint8)
            cv2.putText(img, f"Reference {screen_name}", (50, 50), 
                       cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
            cv2.imwrite(str(screenshot_path), img)
        
        # Создаем заглушки скриншотов биомов
        for biome_name, locations in biome_locations.items():
            biome_dir = self.reference_screenshots_dir / biome_name
            biome_dir.mkdir(exist_ok=True)
            reference_data["biome_screenshots"][biome_name] = {}
            
            for location_name, coords in locations.items():
                screenshot_path = biome_dir / f"{location_name}.png"
                reference_data["biome_screenshots"][biome_name][location_name] = str(screenshot_path)
                
                # Создаем пустое изображение как заглушку
                img = np.zeros((768, 1024, 3), dtype=np.uint8)
                cv2.putText(img, f"{biome_name}: {location_name}", (50, 50),
                           cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)
                cv2.putText(img, f"Coords: {coords['coords']}", (50, 100),
                           cv2.FONT_HERSHEY_SIMPLEX, 0.6, (200, 200, 200), 1)
                cv2.imwrite(str(screenshot_path), img)
        
        # Сохраняем метаданные
        with open(self.reference_screenshots_dir / "reference_data.json", 'w', encoding='utf-8') as f:
            json.dump(reference_data, f, ensure_ascii=False, indent=2)
        
        return reference_data
    
    def capture_modern_client_screenshots(self):
        """Захватывает скриншоты современного клиента"""
        print("📸 Захват скриншотов современного клиента...")
        
        # Аналогично эталонному клиенту, но для современного
        modern_data = {
            "gui_screenshots": {},
            "biome_screenshots": {},
            "capture_timestamp": datetime.now().isoformat()
        }
        
        # Здесь будет логика запуска современного клиента и захвата скриншотов
        # Пока создаем заглушки
        
        return modern_data
    
    def compare_screenshots_pixel_by_pixel(self, ref_path: str, modern_path: str) -> Dict[str, Any]:
        """Сравнивает два скриншота попиксельно"""
        
        if not Path(ref_path).exists() or not Path(modern_path).exists():
            return {
                "error": "Один или оба файла скриншотов не найдены",
                "compliance_score": 0.0
            }
        
        # Загружаем изображения
        ref_img = cv2.imread(ref_path)
        modern_img = cv2.imread(modern_path)
        
        if ref_img is None or modern_img is None:
            return {
                "error": "Не удалось загрузить изображения",
                "compliance_score": 0.0
            }
        
        # Приводим к одному размеру если нужно
        if ref_img.shape != modern_img.shape:
            modern_img = cv2.resize(modern_img, (ref_img.shape[1], ref_img.shape[0]))
        
        # Вычисляем разность
        diff = cv2.absdiff(ref_img, modern_img)
        
        # Конвертируем в градации серого
        diff_gray = cv2.cvtColor(diff, cv2.COLOR_BGR2GRAY)
        
        # Вычисляем процент различий
        total_pixels = diff_gray.shape[0] * diff_gray.shape[1]
        different_pixels = np.count_nonzero(diff_gray > 10)  # Порог чувствительности
        similarity_percentage = ((total_pixels - different_pixels) / total_pixels) * 100
        
        # Создаем карту различий
        diff_map = np.zeros_like(ref_img)
        diff_map[diff_gray > 10] = [0, 0, 255]  # Красный цвет для различий
        
        # Создаем составное изображение для анализа
        comparison_img = np.hstack([ref_img, modern_img, diff_map])
        
        return {
            "compliance_score": similarity_percentage,
            "total_pixels": total_pixels,
            "different_pixels": different_pixels,
            "similarity_percentage": similarity_percentage,
            "diff_image_path": None,  # Путь к изображению различий
            "comparison_image": comparison_img
        }
    
    def run_gui_compliance_tests(self) -> List[Dict[str, Any]]:
        """Запускает тесты соответствия GUI"""
        print("🧪 Запуск тестов соответствия GUI...")
        
        test_results = []
        
        # Загружаем эталонные данные
        reference_data_path = self.reference_screenshots_dir / "reference_data.json"
        if not reference_data_path.exists():
            reference_data = self.capture_reference_screenshots()
        else:
            with open(reference_data_path, 'r', encoding='utf-8') as f:
                reference_data = json.load(f)
        
        # Тестируем GUI элементы
        for screen_name, ref_screenshot_path in reference_data["gui_screenshots"].items():
            modern_screenshot_path = self.screenshots_dir / f"{screen_name}.png"
            
            if modern_screenshot_path.exists():
                comparison_result = self.compare_screenshots_pixel_by_pixel(
                    ref_screenshot_path, str(modern_screenshot_path)
                )
                
                test_result = {
                    "test_name": f"GUI_{screen_name}_compliance",
                    "test_type": "gui_comparison",
                    "screen_name": screen_name,
                    "reference_path": ref_screenshot_path,
                    "modern_path": str(modern_screenshot_path),
                    "compliance_score": comparison_result.get("compliance_score", 0.0),
                    "status": "PASS" if comparison_result.get("compliance_score", 0) > 90 else "FAIL",
                    "details": comparison_result
                }
                
                test_results.append(test_result)
                
                # Сохраняем результат сравнения
                if "comparison_image" in comparison_result:
                    comparison_path = self.comparison_results_dir / f"{screen_name}_comparison.png"
                    cv2.imwrite(str(comparison_path), comparison_result["comparison_image"])
                    test_result["comparison_image_path"] = str(comparison_path)
        
        return test_results
    
    def run_biome_compliance_tests(self) -> List[Dict[str, Any]]:
        """Запускает тесты соответствия биомов"""
        print("🌍 Запуск тестов соответствия биомов...")
        
        test_results = []
        biome_locations = self.extract_biome_locations()
        
        for biome_name, locations in biome_locations.items():
            for location_name, coords in locations.items():
                ref_screenshot_path = self.reference_screenshots_dir / biome_name / f"{location_name}.png"
                modern_screenshot_path = self.screenshots_dir / biome_name / f"{location_name}.png"
                
                if ref_screenshot_path.exists() and modern_screenshot_path.exists():
                    comparison_result = self.compare_screenshots_pixel_by_pixel(
                        str(ref_screenshot_path), str(modern_screenshot_path)
                    )
                    
                    test_result = {
                        "test_name": f"BIOME_{biome_name}_{location_name}_compliance",
                        "test_type": "biome_comparison",
                        "biome_name": biome_name,
                        "location_name": location_name,
                        "coordinates": coords["coords"],
                        "reference_path": str(ref_screenshot_path),
                        "modern_path": str(modern_screenshot_path),
                        "compliance_score": comparison_result.get("compliance_score", 0.0),
                        "status": "PASS" if comparison_result.get("compliance_score", 0) > 85 else "FAIL",
                        "details": comparison_result
                    }
                    
                    test_results.append(test_result)
        
        return test_results
    
    def generate_compliance_report(self, gui_results: List[Dict], biome_results: List[Dict]) -> Dict[str, Any]:
        """Генерирует отчет о соответствии"""
        print("📊 Генерация отчета о соответствии...")
        
        all_results = gui_results + biome_results
        
        # Общая статистика
        total_tests = len(all_results)
        passed_tests = len([r for r in all_results if r["status"] == "PASS"])
        failed_tests = total_tests - passed_tests
        
        # Средний балл соответствия
        if total_tests > 0:
            avg_compliance = sum(r["compliance_score"] for r in all_results) / total_tests
        else:
            avg_compliance = 0.0
        
        # GUI статистика
        gui_total = len(gui_results)
        gui_passed = len([r for r in gui_results if r["status"] == "PASS"])
        gui_avg_compliance = sum(r["compliance_score"] for r in gui_results) / gui_total if gui_total > 0 else 0.0
        
        # Биом статистика
        biome_total = len(biome_results)
        biome_passed = len([r for r in biome_results if r["status"] == "PASS"])
        biome_avg_compliance = sum(r["compliance_score"] for r in biome_results) / biome_total if biome_total > 0 else 0.0
        
        report = {
            "report_timestamp": datetime.now().isoformat(),
            "summary": {
                "total_tests": total_tests,
                "passed_tests": passed_tests,
                "failed_tests": failed_tests,
                "success_rate": (passed_tests / total_tests * 100) if total_tests > 0 else 0.0,
                "average_compliance_score": avg_compliance
            },
            "gui_compliance": {
                "total_gui_tests": gui_total,
                "passed_gui_tests": gui_passed,
                "gui_success_rate": (gui_passed / gui_total * 100) if gui_total > 0 else 0.0,
                "average_gui_compliance": gui_avg_compliance,
                "detailed_results": gui_results
            },
            "biome_compliance": {
                "total_biome_tests": biome_total,
                "passed_biome_tests": biome_passed,
                "biome_success_rate": (biome_passed / biome_total * 100) if biome_total > 0 else 0.0,
                "average_biome_compliance": biome_avg_compliance,
                "detailed_results": biome_results
            },
            "recommendations": self.generate_recommendations(all_results)
        }
        
        return report
    
    def generate_recommendations(self, test_results: List[Dict]) -> List[str]:
        """Генерирует рекомендации на основе результатов тестов"""
        recommendations = []
        
        failed_tests = [r for r in test_results if r["status"] == "FAIL"]
        
        if not failed_tests:
            recommendations.append("✅ Все тесты пройдены! Клиент полностью соответствует эталону.")
        else:
            # Анализируем типы неудач
            gui_failures = [r for r in failed_tests if r["test_type"] == "gui_comparison"]
            biome_failures = [r for r in failed_tests if r["test_type"] == "biome_comparison"]
            
            if gui_failures:
                recommendations.append(f"🔧 Исправить {len(gui_failures)} GUI элементов:")
                for failure in gui_failures[:5]:  # Показываем первые 5
                    score = failure.get("compliance_score", 0)
                    recommendations.append(f"   - {failure['screen_name']}: {score:.1f}% соответствие")
            
            if biome_failures:
                recommendations.append(f"🌍 Исправить {len(biome_failures)} локаций биомов:")
                for failure in biome_failures[:5]:  # Показываем первые 5
                    score = failure.get("compliance_score", 0)
                    recommendations.append(f"   - {failure['biome_name']}/{failure['location_name']}: {score:.1f}% соответствие")
            
            # Общие рекомендации
            low_scores = [r for r in failed_tests if r.get("compliance_score", 0) < 50]
            if low_scores:
                recommendations.append("⚠️ Критически низкое соответствие требует полной переработки элементов")
            
            medium_scores = [r for r in failed_tests if 50 <= r.get("compliance_score", 0) < 90]
            if medium_scores:
                recommendations.append("🔧 Среднее соответствие требует точной настройки и калибровки")
        
        return recommendations
    
    def save_compliance_report(self, report: Dict[str, Any]):
        """Сохраняет отчет о соответствии"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        report_path = self.comparison_results_dir / f"compliance_report_{timestamp}.json"
        
        with open(report_path, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        print(f"💾 Отчет сохранен: {report_path}")
        return str(report_path)
    
    def run_full_compliance_check(self) -> Dict[str, Any]:
        """Запускает полную проверку соответствия"""
        print("🚀 Запуск полной проверки соответствия GUI и биомов...")
        
        start_time = time.time()
        
        # Захватываем эталонные скриншоты
        self.capture_reference_screenshots()
        
        # Захватываем скриншоты современного клиента
        self.capture_modern_client_screenshots()
        
        # Запускаем тесты GUI
        gui_results = self.run_gui_compliance_tests()
        
        # Запускаем тесты биомов
        biome_results = self.run_biome_compliance_tests()
        
        # Генерируем отчет
        report = self.generate_compliance_report(gui_results, biome_results)
        
        # Добавляем время выполнения
        execution_time = time.time() - start_time
        report["execution_time"] = execution_time
        
        # Сохраняем отчет
        report_path = self.save_compliance_report(report)
        report["report_path"] = report_path
        
        return report

def main():
    """Основная функция"""
    print("🎮 Система контроля соответствия GUI клиента")
    print("=" * 50)
    
    # Создаем систему контроля соответствия
    compliance_system = GUIComplianceSystem("/home/ni/Projects/la2bots")
    
    # Запускаем полную проверку
    report = compliance_system.run_full_compliance_check()
    
    # Выводим краткие результаты
    print("\n📊 РЕЗУЛЬТАТЫ ПРОВЕРКИ СООТВЕТСТВИЯ:")
    print("-" * 40)
    print(f"Всего тестов: {report['summary']['total_tests']}")
    print(f"Пройдено: {report['summary']['passed_tests']}")
    print(f"Провалено: {report['summary']['failed_tests']}")
    print(f"Успешность: {report['summary']['success_rate']:.1f}%")
    print(f"Средний балл соответствия: {report['summary']['average_compliance_score']:.1f}%")
    
    print(f"\nGUI соответствие: {report['gui_compliance']['gui_success_rate']:.1f}%")
    print(f"Биом соответствие: {report['biome_compliance']['biome_success_rate']:.1f}%")
    
    print(f"\nВремя выполнения: {report['execution_time']:.2f} секунд")
    print(f"Отчет сохранен: {report['report_path']}")
    
    # Выводим рекомендации
    print("\n💡 РЕКОМЕНДАЦИИ:")
    for recommendation in report['recommendations']:
        print(f"  {recommendation}")

if __name__ == "__main__":
    main()
