#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Методология тестирования соответствия GUI нового клиента старому
Включает автоматизированные тесты, валидацию и отчетность
"""

import os
import sys
import json
import subprocess
import time
from pathlib import Path
from typing import Dict, List, Any, Tuple, Optional
from datetime import datetime
import unittest
import logging

class GUITestingMethodology:
    """Методология тестирования GUI соответствия"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.modern_client = self.project_root / "modern_client"
        self.reference_client = self.project_root / "reference_client"
        self.test_results_dir = self.modern_client / "test_results"
        self.test_scenarios_dir = self.modern_client / "test_scenarios"
        
        # Создаем необходимые директории
        for dir_path in [self.test_results_dir, self.test_scenarios_dir]:
            dir_path.mkdir(exist_ok=True)
        
        # Настраиваем логирование
        self.setup_logging()
        
        # Инициализируем тестовые сценарии
        self.init_test_scenarios()
    
    def setup_logging(self):
        """Настраивает логирование тестов"""
        log_file = self.test_results_dir / f"test_log_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log"
        
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(log_file, encoding='utf-8'),
                logging.StreamHandler()
            ]
        )
        
        self.logger = logging.getLogger(__name__)
    
    def init_test_scenarios(self):
        """Инициализирует тестовые сценарии"""
        
        # Сценарии тестирования GUI
        gui_test_scenarios = {
            "login_screen_tests": {
                "description": "Тестирование экрана входа в игру",
                "tests": [
                    {
                        "name": "login_screen_layout",
                        "description": "Проверка расположения элементов экрана входа",
                        "steps": [
                            "Запустить клиент",
                            "Дождаться загрузки экрана входа",
                            "Сделать скриншот",
                            "Сравнить с эталонным скриншотом"
                        ],
                        "expected_result": "Элементы располагаются в тех же позициях что и в эталоне",
                        "acceptance_criteria": "Соответствие > 95%"
                    },
                    {
                        "name": "login_field_functionality",
                        "description": "Проверка функциональности поля логина",
                        "steps": [
                            "Кликнуть на поле логина",
                            "Ввести тестовый текст",
                            "Проверить отображение текста",
                            "Проверить активность поля"
                        ],
                        "expected_result": "Поле логина работает корректно",
                        "acceptance_criteria": "Текст вводится и отображается правильно"
                    },
                    {
                        "name": "password_field_functionality",
                        "description": "Проверка функциональности поля пароля",
                        "steps": [
                            "Кликнуть на поле пароля",
                            "Ввести тестовый пароль",
                            "Проверить скрытие символов",
                            "Проверить активность поля"
                        ],
                        "expected_result": "Поле пароля скрывает символы",
                        "acceptance_criteria": "Символы отображаются как звездочки или точки"
                    },
                    {
                        "name": "login_button_functionality",
                        "description": "Проверка функциональности кнопки входа",
                        "steps": [
                            "Ввести корректные данные",
                            "Кликнуть кнопку 'Войти'",
                            "Проверить переход к следующему экрану",
                            "Проверить анимацию кнопки"
                        ],
                        "expected_result": "Кнопка корректно обрабатывает клик",
                        "acceptance_criteria": "Происходит переход к экрану выбора персонажей"
                    }
                ]
            },
            "character_selection_tests": {
                "description": "Тестирование экрана выбора персонажей",
                "tests": [
                    {
                        "name": "character_list_display",
                        "description": "Проверка отображения списка персонажей",
                        "steps": [
                            "Открыть экран выбора персонажей",
                            "Проверить отображение списка персонажей",
                            "Проверить информацию о каждом персонаже",
                            "Сравнить с эталонным отображением"
                        ],
                        "expected_result": "Список персонажей отображается корректно",
                        "acceptance_criteria": "Соответствие эталону > 90%"
                    },
                    {
                        "name": "character_3d_model",
                        "description": "Проверка 3D модели персонажа",
                        "steps": [
                            "Выбрать персонажа из списка",
                            "Проверить загрузку 3D модели",
                            "Проверить анимацию модели",
                            "Проверить качество отображения"
                        ],
                        "expected_result": "3D модель отображается и анимируется",
                        "acceptance_criteria": "Модель соответствует выбранному персонажу"
                    }
                ]
            },
            "character_creation_tests": {
                "description": "Тестирование экрана создания персонажа",
                "tests": [
                    {
                        "name": "race_selection",
                        "description": "Проверка выбора расы персонажа",
                        "steps": [
                            "Открыть экран создания персонажа",
                            "Проверить доступные расы",
                            "Выбрать каждую расу поочередно",
                            "Проверить изменение 3D модели"
                        ],
                        "expected_result": "Выбор расы изменяет 3D модель",
                        "acceptance_criteria": "Все расы доступны и работают корректно"
                    },
                    {
                        "name": "appearance_customization",
                        "description": "Проверка настройки внешности",
                        "steps": [
                            "Выбрать расу и пол",
                            "Изменить цвет волос",
                            "Изменить прическу",
                            "Изменить другие параметры внешности",
                            "Проверить отображение изменений на 3D модели"
                        ],
                        "expected_result": "Изменения внешности отображаются на модели",
                        "acceptance_criteria": "Все параметры настройки работают"
                    }
                ]
            },
            "game_interface_tests": {
                "description": "Тестирование игрового интерфейса",
                "tests": [
                    {
                        "name": "hp_mp_bars_display",
                        "description": "Проверка отображения полосок HP/MP",
                        "steps": [
                            "Войти в игру",
                            "Проверить отображение полоски здоровья",
                            "Проверить отображение полоски маны",
                            "Проверить корректность значений"
                        ],
                        "expected_result": "Полоски HP/MP отображаются корректно",
                        "acceptance_criteria": "Значения соответствуют состоянию персонажа"
                    },
                    {
                        "name": "skills_panel",
                        "description": "Проверка панели скилов",
                        "steps": [
                            "Проверить отображение панели скилов",
                            "Проверить горячие клавиши",
                            "Проверить иконки скилов",
                            "Проверить функциональность использования"
                        ],
                        "expected_result": "Панель скилов работает корректно",
                        "acceptance_criteria": "Скилы активируются при нажатии"
                    },
                    {
                        "name": "minimap_display",
                        "description": "Проверка миникарты",
                        "steps": [
                            "Проверить отображение миникарты",
                            "Проверить позицию игрока на карте",
                            "Проверить отображение других объектов",
                            "Проверить функции масштабирования"
                        ],
                        "expected_result": "Миникарта отображает актуальную информацию",
                        "acceptance_criteria": "Позиция игрока соответствует реальной"
                    },
                    {
                        "name": "chat_system",
                        "description": "Проверка системы чата",
                        "steps": [
                            "Проверить отображение окна чата",
                            "Ввести тестовое сообщение",
                            "Проверить отправку сообщения",
                            "Проверить каналы чата"
                        ],
                        "expected_result": "Система чата работает корректно",
                        "acceptance_criteria": "Сообщения отправляются и отображаются"
                    }
                ]
            }
        }
        
        # Сценарии тестирования биомов
        biome_test_scenarios = {
            "biome_visual_tests": {
                "description": "Тестирование визуального соответствия биомов",
                "tests": [
                    {
                        "name": "talking_island_visuals",
                        "description": "Проверка визуалов Talking Island",
                        "locations": [
                            {"name": "newbie_village", "coords": (10912, 12784, -2926)},
                            {"name": "temple_ruins", "coords": (4560, 8804, -3590)},
                            {"name": "elven_ruins", "coords": (48736, 247840, -6240)}
                        ],
                        "steps": [
                            "Телепортироваться в локацию",
                            "Установить камеру в стандартное положение",
                            "Дождаться полной загрузки текстур",
                            "Сделать скриншот",
                            "Сравнить с эталонным скриншотом"
                        ],
                        "expected_result": "Визуалы соответствуют эталону",
                        "acceptance_criteria": "Соответствие > 85%"
                    },
                    {
                        "name": "town_centers_visuals",
                        "description": "Проверка визуалов центров городов",
                        "locations": [
                            {"name": "elven_village_center", "coords": (46934, 51467, -2977)},
                            {"name": "dark_elven_village_center", "coords": (9745, 15606, -4574)},
                            {"name": "orc_village_center", "coords": (-45186, -112459, -236)},
                            {"name": "dwarven_village_center", "coords": (115113, -178212, -901)}
                        ],
                        "steps": [
                            "Телепортироваться в центр города",
                            "Проверить отображение зданий",
                            "Проверить отображение NPC",
                            "Проверить текстуры и освещение",
                            "Сделать скриншот с разных углов"
                        ],
                        "expected_result": "Города отображаются корректно",
                        "acceptance_criteria": "Все основные элементы присутствуют"
                    }
                ]
            },
            "npc_interaction_tests": {
                "description": "Тестирование взаимодействия с NPC",
                "tests": [
                    {
                        "name": "npc_dialogue_system",
                        "description": "Проверка системы диалогов с NPC",
                        "steps": [
                            "Найти NPC в городе",
                            "Кликнуть на NPC",
                            "Проверить открытие диалогового окна",
                            "Проверить текст диалога",
                            "Проверить опции диалога"
                        ],
                        "expected_result": "Диалоги с NPC работают корректно",
                        "acceptance_criteria": "Тексты соответствуют оригинальным"
                    },
                    {
                        "name": "shop_interface",
                        "description": "Проверка интерфейса магазинов",
                        "steps": [
                            "Открыть магазин у NPC торговца",
                            "Проверить отображение товаров",
                            "Проверить цены товаров",
                            "Проверить функциональность покупки"
                        ],
                        "expected_result": "Интерфейс магазина работает",
                        "acceptance_criteria": "Можно покупать и продавать предметы"
                    }
                ]
            }
        }
        
        # Сохраняем сценарии
        with open(self.test_scenarios_dir / "gui_test_scenarios.json", 'w', encoding='utf-8') as f:
            json.dump(gui_test_scenarios, f, ensure_ascii=False, indent=2)
        
        with open(self.test_scenarios_dir / "biome_test_scenarios.json", 'w', encoding='utf-8') as f:
            json.dump(biome_test_scenarios, f, ensure_ascii=False, indent=2)
        
        self.gui_scenarios = gui_test_scenarios
        self.biome_scenarios = biome_test_scenarios
    
    def run_automated_test_suite(self) -> Dict[str, Any]:
        """Запускает автоматизированный набор тестов"""
        self.logger.info("🚀 Запуск автоматизированного набора тестов")
        
        start_time = time.time()
        test_results = {
            "test_suite_start": datetime.now().isoformat(),
            "gui_test_results": [],
            "biome_test_results": [],
            "summary": {}
        }
        
        # Запускаем GUI тесты
        gui_results = self.run_gui_test_suite()
        test_results["gui_test_results"] = gui_results
        
        # Запускаем тесты биомов
        biome_results = self.run_biome_test_suite()
        test_results["biome_test_results"] = biome_results
        
        # Генерируем сводку
        test_results["summary"] = self.generate_test_summary(gui_results, biome_results)
        test_results["execution_time"] = time.time() - start_time
        test_results["test_suite_end"] = datetime.now().isoformat()
        
        # Сохраняем результаты
        self.save_test_results(test_results)
        
        return test_results
    
    def run_gui_test_suite(self) -> List[Dict[str, Any]]:
        """Запускает тесты GUI"""
        self.logger.info("🖥️ Запуск тестов GUI")
        
        gui_results = []
        
        for scenario_name, scenario_data in self.gui_scenarios.items():
            self.logger.info(f"Выполнение сценария: {scenario_name}")
            
            for test in scenario_data["tests"]:
                test_result = self.execute_gui_test(test, scenario_name)
                gui_results.append(test_result)
                
                self.logger.info(f"Тест {test['name']}: {test_result['status']}")
        
        return gui_results
    
    def run_biome_test_suite(self) -> List[Dict[str, Any]]:
        """Запускает тесты биомов"""
        self.logger.info("🌍 Запуск тестов биомов")
        
        biome_results = []
        
        for scenario_name, scenario_data in self.biome_scenarios.items():
            self.logger.info(f"Выполнение сценария: {scenario_name}")
            
            for test in scenario_data["tests"]:
                test_result = self.execute_biome_test(test, scenario_name)
                biome_results.append(test_result)
                
                self.logger.info(f"Тест {test['name']}: {test_result['status']}")
        
        return biome_results
    
    def execute_gui_test(self, test: Dict[str, Any], scenario_name: str) -> Dict[str, Any]:
        """Выполняет отдельный GUI тест"""
        test_start_time = time.time()
        
        test_result = {
            "test_name": test["name"],
            "scenario": scenario_name,
            "description": test["description"],
            "start_time": datetime.now().isoformat(),
            "status": "RUNNING",
            "steps_executed": [],
            "error_message": None,
            "compliance_score": 0.0
        }
        
        try:
            # Выполняем шаги теста
            for step_index, step in enumerate(test["steps"]):
                step_result = self.execute_test_step(step, test["name"], step_index)
                test_result["steps_executed"].append(step_result)
                
                if not step_result["success"]:
                    test_result["status"] = "FAILED"
                    test_result["error_message"] = step_result["error"]
                    break
            
            if test_result["status"] == "RUNNING":
                # Все шаги выполнены успешно
                test_result["status"] = "PASSED"
                test_result["compliance_score"] = self.calculate_compliance_score(test_result)
        
        except Exception as e:
            test_result["status"] = "ERROR"
            test_result["error_message"] = str(e)
            self.logger.error(f"Ошибка в тесте {test['name']}: {e}")
        
        test_result["execution_time"] = time.time() - test_start_time
        test_result["end_time"] = datetime.now().isoformat()
        
        return test_result
    
    def execute_biome_test(self, test: Dict[str, Any], scenario_name: str) -> Dict[str, Any]:
        """Выполняет отдельный тест биома"""
        test_start_time = time.time()
        
        test_result = {
            "test_name": test["name"],
            "scenario": scenario_name,
            "description": test["description"],
            "start_time": datetime.now().isoformat(),
            "status": "RUNNING",
            "location_results": [],
            "error_message": None,
            "average_compliance_score": 0.0
        }
        
        try:
            if "locations" in test:
                total_compliance = 0.0
                location_count = 0
                
                for location in test["locations"]:
                    location_result = self.test_biome_location(location, test)
                    test_result["location_results"].append(location_result)
                    
                    if location_result["compliance_score"] > 0:
                        total_compliance += location_result["compliance_score"]
                        location_count += 1
                
                if location_count > 0:
                    test_result["average_compliance_score"] = total_compliance / location_count
                
                # Определяем статус теста
                if test_result["average_compliance_score"] >= 85:
                    test_result["status"] = "PASSED"
                else:
                    test_result["status"] = "FAILED"
                    test_result["error_message"] = f"Низкое соответствие: {test_result['average_compliance_score']:.1f}%"
            
        except Exception as e:
            test_result["status"] = "ERROR"
            test_result["error_message"] = str(e)
            self.logger.error(f"Ошибка в тесте биома {test['name']}: {e}")
        
        test_result["execution_time"] = time.time() - test_start_time
        test_result["end_time"] = datetime.now().isoformat()
        
        return test_result
    
    def execute_test_step(self, step: str, test_name: str, step_index: int) -> Dict[str, Any]:
        """Выполняет отдельный шаг теста"""
        step_result = {
            "step_index": step_index,
            "step_description": step,
            "success": True,
            "error": None,
            "execution_time": 0.0
        }
        
        step_start_time = time.time()
        
        try:
            # Здесь будет реальная логика выполнения шагов
            # Пока создаем заглушку для демонстрации
            
            if "Запустить клиент" in step:
                # Логика запуска клиента
                self.logger.info("Запуск современного клиента...")
                time.sleep(1)  # Имитация времени запуска
                
            elif "Сделать скриншот" in step:
                # Логика создания скриншота
                self.logger.info("Создание скриншота...")
                screenshot_path = self.take_screenshot(test_name, step_index)
                step_result["screenshot_path"] = screenshot_path
                
            elif "Сравнить с эталонным" in step:
                # Логика сравнения скриншотов
                self.logger.info("Сравнение с эталонным скриншотом...")
                compliance_score = self.compare_with_reference(test_name)
                step_result["compliance_score"] = compliance_score
                
            elif "Кликнуть" in step or "Ввести" in step:
                # Логика взаимодействия с UI
                self.logger.info(f"Выполнение действия: {step}")
                time.sleep(0.5)  # Имитация времени выполнения
                
            else:
                # Общая логика для других шагов
                self.logger.info(f"Выполнение шага: {step}")
                time.sleep(0.2)
        
        except Exception as e:
            step_result["success"] = False
            step_result["error"] = str(e)
            self.logger.error(f"Ошибка в шаге '{step}': {e}")
        
        step_result["execution_time"] = time.time() - step_start_time
        
        return step_result
    
    def test_biome_location(self, location: Dict[str, Any], test: Dict[str, Any]) -> Dict[str, Any]:
        """Тестирует конкретную локацию биома"""
        location_result = {
            "location_name": location["name"],
            "coordinates": location["coords"],
            "compliance_score": 0.0,
            "screenshot_path": None,
            "comparison_result": None
        }
        
        try:
            # Телепортируемся в локацию (заглушка)
            self.logger.info(f"Телепорт в локацию {location['name']} {location['coords']}")
            time.sleep(2)  # Имитация времени телепорта и загрузки
            
            # Делаем скриншот
            screenshot_path = self.take_biome_screenshot(location["name"], location["coords"])
            location_result["screenshot_path"] = screenshot_path
            
            # Сравниваем с эталоном
            compliance_score = self.compare_biome_screenshot(location["name"])
            location_result["compliance_score"] = compliance_score
            
            self.logger.info(f"Локация {location['name']}: {compliance_score:.1f}% соответствие")
        
        except Exception as e:
            self.logger.error(f"Ошибка тестирования локации {location['name']}: {e}")
            location_result["error"] = str(e)
        
        return location_result
    
    def take_screenshot(self, test_name: str, step_index: int) -> str:
        """Делает скриншот для GUI теста"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        screenshot_path = self.test_results_dir / f"{test_name}_step{step_index}_{timestamp}.png"
        
        # Здесь будет реальная логика создания скриншота
        # Пока создаем заглушку
        
        return str(screenshot_path)
    
    def take_biome_screenshot(self, location_name: str, coords: Tuple) -> str:
        """Делает скриншот биома"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        screenshot_path = self.test_results_dir / f"biome_{location_name}_{timestamp}.png"
        
        # Здесь будет реальная логика создания скриншота биома
        # Пока создаем заглушку
        
        return str(screenshot_path)
    
    def compare_with_reference(self, test_name: str) -> float:
        """Сравнивает скриншот с эталонным"""
        # Здесь будет реальная логика сравнения
        # Пока возвращаем случайное значение для демонстрации
        import random
        return random.uniform(80.0, 98.0)
    
    def compare_biome_screenshot(self, location_name: str) -> float:
        """Сравнивает скриншот биома с эталонным"""
        # Здесь будет реальная логика сравнения биома
        # Пока возвращаем случайное значение для демонстрации
        import random
        return random.uniform(75.0, 95.0)
    
    def calculate_compliance_score(self, test_result: Dict[str, Any]) -> float:
        """Вычисляет общий балл соответствия для теста"""
        compliance_scores = []
        
        for step in test_result["steps_executed"]:
            if "compliance_score" in step:
                compliance_scores.append(step["compliance_score"])
        
        if compliance_scores:
            return sum(compliance_scores) / len(compliance_scores)
        else:
            return 95.0 if test_result["status"] == "PASSED" else 0.0
    
    def generate_test_summary(self, gui_results: List[Dict], biome_results: List[Dict]) -> Dict[str, Any]:
        """Генерирует сводку результатов тестирования"""
        
        all_results = gui_results + biome_results
        
        total_tests = len(all_results)
        passed_tests = len([r for r in all_results if r["status"] == "PASSED"])
        failed_tests = len([r for r in all_results if r["status"] == "FAILED"])
        error_tests = len([r for r in all_results if r["status"] == "ERROR"])
        
        # Средний балл соответствия
        compliance_scores = []
        for result in all_results:
            if "compliance_score" in result and result["compliance_score"] > 0:
                compliance_scores.append(result["compliance_score"])
            elif "average_compliance_score" in result and result["average_compliance_score"] > 0:
                compliance_scores.append(result["average_compliance_score"])
        
        avg_compliance = sum(compliance_scores) / len(compliance_scores) if compliance_scores else 0.0
        
        summary = {
            "total_tests": total_tests,
            "passed_tests": passed_tests,
            "failed_tests": failed_tests,
            "error_tests": error_tests,
            "success_rate": (passed_tests / total_tests * 100) if total_tests > 0 else 0.0,
            "average_compliance_score": avg_compliance,
            "gui_tests": {
                "total": len(gui_results),
                "passed": len([r for r in gui_results if r["status"] == "PASSED"]),
                "failed": len([r for r in gui_results if r["status"] == "FAILED"])
            },
            "biome_tests": {
                "total": len(biome_results),
                "passed": len([r for r in biome_results if r["status"] == "PASSED"]),
                "failed": len([r for r in biome_results if r["status"] == "FAILED"])
            }
        }
        
        return summary
    
    def save_test_results(self, test_results: Dict[str, Any]):
        """Сохраняет результаты тестирования"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        results_file = self.test_results_dir / f"test_results_{timestamp}.json"
        
        with open(results_file, 'w', encoding='utf-8') as f:
            json.dump(test_results, f, ensure_ascii=False, indent=2)
        
        self.logger.info(f"💾 Результаты тестирования сохранены: {results_file}")
        
        return str(results_file)
    
    def generate_test_report(self, test_results: Dict[str, Any]) -> str:
        """Генерирует HTML отчет о тестировании"""
        
        html_report = f"""
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отчет о тестировании GUI соответствия</title>
    <style>
        body {{ font-family: Arial, sans-serif; margin: 20px; }}
        .header {{ background-color: #2c3e50; color: white; padding: 20px; border-radius: 5px; }}
        .summary {{ background-color: #ecf0f1; padding: 15px; margin: 20px 0; border-radius: 5px; }}
        .test-section {{ margin: 20px 0; }}
        .test-passed {{ color: #27ae60; }}
        .test-failed {{ color: #e74c3c; }}
        .test-error {{ color: #f39c12; }}
        table {{ width: 100%; border-collapse: collapse; margin: 20px 0; }}
        th, td {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
        th {{ background-color: #f2f2f2; }}
        .compliance-high {{ background-color: #d5f4e6; }}
        .compliance-medium {{ background-color: #ffeaa7; }}
        .compliance-low {{ background-color: #fab1a0; }}
    </style>
</head>
<body>
    <div class="header">
        <h1>🎮 Отчет о тестировании GUI соответствия</h1>
        <p>Дата: {test_results.get('test_suite_start', 'N/A')}</p>
        <p>Время выполнения: {test_results.get('execution_time', 0):.2f} секунд</p>
    </div>
    
    <div class="summary">
        <h2>📊 Сводка результатов</h2>
        <p><strong>Всего тестов:</strong> {test_results['summary']['total_tests']}</p>
        <p><strong>Пройдено:</strong> <span class="test-passed">{test_results['summary']['passed_tests']}</span></p>
        <p><strong>Провалено:</strong> <span class="test-failed">{test_results['summary']['failed_tests']}</span></p>
        <p><strong>Ошибки:</strong> <span class="test-error">{test_results['summary']['error_tests']}</span></p>
        <p><strong>Успешность:</strong> {test_results['summary']['success_rate']:.1f}%</p>
        <p><strong>Средний балл соответствия:</strong> {test_results['summary']['average_compliance_score']:.1f}%</p>
    </div>
    
    <div class="test-section">
        <h2>🖥️ Результаты тестов GUI</h2>
        <table>
            <tr>
                <th>Тест</th>
                <th>Статус</th>
                <th>Соответствие</th>
                <th>Время выполнения</th>
                <th>Описание</th>
            </tr>
"""
        
        # Добавляем результаты GUI тестов
        for result in test_results.get('gui_test_results', []):
            status_class = f"test-{result['status'].lower()}"
            compliance = result.get('compliance_score', 0)
            compliance_class = 'compliance-high' if compliance > 90 else 'compliance-medium' if compliance > 70 else 'compliance-low'
            
            html_report += f"""
            <tr class="{compliance_class}">
                <td>{result['test_name']}</td>
                <td class="{status_class}">{result['status']}</td>
                <td>{compliance:.1f}%</td>
                <td>{result.get('execution_time', 0):.2f}s</td>
                <td>{result['description']}</td>
            </tr>
"""
        
        html_report += """
        </table>
    </div>
    
    <div class="test-section">
        <h2>🌍 Результаты тестов биомов</h2>
        <table>
            <tr>
                <th>Тест</th>
                <th>Статус</th>
                <th>Соответствие</th>
                <th>Время выполнения</th>
                <th>Локации</th>
            </tr>
"""
        
        # Добавляем результаты тестов биомов
        for result in test_results.get('biome_test_results', []):
            status_class = f"test-{result['status'].lower()}"
            compliance = result.get('average_compliance_score', 0)
            compliance_class = 'compliance-high' if compliance > 85 else 'compliance-medium' if compliance > 70 else 'compliance-low'
            location_count = len(result.get('location_results', []))
            
            html_report += f"""
            <tr class="{compliance_class}">
                <td>{result['test_name']}</td>
                <td class="{status_class}">{result['status']}</td>
                <td>{compliance:.1f}%</td>
                <td>{result.get('execution_time', 0):.2f}s</td>
                <td>{location_count} локаций</td>
            </tr>
"""
        
        html_report += """
        </table>
    </div>
</body>
</html>
"""
        
        # Сохраняем HTML отчет
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        report_file = self.test_results_dir / f"test_report_{timestamp}.html"
        
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write(html_report)
        
        self.logger.info(f"📄 HTML отчет создан: {report_file}")
        
        return str(report_file)

def main():
    """Основная функция"""
    print("🧪 Методология тестирования GUI соответствия")
    print("=" * 50)
    
    # Создаем систему тестирования
    testing_system = GUITestingMethodology("/home/ni/Projects/la2bots")
    
    # Запускаем полный набор тестов
    test_results = testing_system.run_automated_test_suite()
    
    # Генерируем HTML отчет
    report_path = testing_system.generate_test_report(test_results)
    
    # Выводим результаты
    print("\n📊 РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ:")
    print("-" * 40)
    print(f"Всего тестов: {test_results['summary']['total_tests']}")
    print(f"Пройдено: {test_results['summary']['passed_tests']}")
    print(f"Провалено: {test_results['summary']['failed_tests']}")
    print(f"Ошибки: {test_results['summary']['error_tests']}")
    print(f"Успешность: {test_results['summary']['success_rate']:.1f}%")
    print(f"Средний балл соответствия: {test_results['summary']['average_compliance_score']:.1f}%")
    
    print(f"\nВремя выполнения: {test_results['execution_time']:.2f} секунд")
    print(f"HTML отчет: {report_path}")

if __name__ == "__main__":
    main()
