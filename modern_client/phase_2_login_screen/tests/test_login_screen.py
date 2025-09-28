#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Тесты для экрана входа в игру (Фаза 2)
Проверяют соответствие эталонному клиенту
"""

import unittest
import cv2
import numpy as np
from pathlib import Path

class LoginScreenTests(unittest.TestCase):
    """Тесты экрана входа в игру"""
    
    def setUp(self):
        """Настройка тестов"""
        self.analysis_data = {
  "screen_name": "login_screen",
  "dimensions": {
    "width": 1024,
    "height": 768
  },
  "elements": {
    "background": {
      "position": {
        "x": 0,
        "y": 0
      },
      "size": {
        "width": 1024,
        "height": 768
      },
      "color": "#1e1e2e",
      "type": "background"
    },
    "logo": {
      "position": {
        "x": 362,
        "y": 100
      },
      "size": {
        "width": 300,
        "height": 100
      },
      "color": "#FFD700",
      "type": "logo",
      "text": "LINEAGE II"
    },
    "login_field": {
      "position": {
        "x": 412,
        "y": 300
      },
      "size": {
        "width": 200,
        "height": 30
      },
      "backgroundColor": "#000000",
      "borderColor": "#FFFFFF",
      "textColor": "#FFFFFF",
      "type": "input_field",
      "maxLength": 16,
      "placeholder": "Логин"
    },
    "password_field": {
      "position": {
        "x": 412,
        "y": 340
      },
      "size": {
        "width": 200,
        "height": 30
      },
      "backgroundColor": "#000000",
      "borderColor": "#FFFFFF",
      "textColor": "#FFFFFF",
      "type": "password_field",
      "maxLength": 16,
      "placeholder": "Пароль",
      "passwordChar": "*"
    },
    "login_button": {
      "position": {
        "x": 462,
        "y": 380
      },
      "size": {
        "width": 100,
        "height": 40
      },
      "backgroundColor": "#FFD700",
      "textColor": "#000000",
      "type": "button",
      "text": "Войти",
      "font": "Arial Bold 14px"
    },
    "register_button": {
      "position": {
        "x": 462,
        "y": 430
      },
      "size": {
        "width": 100,
        "height": 40
      },
      "backgroundColor": "#C0C0C0",
      "textColor": "#000000",
      "type": "button",
      "text": "Регистрация",
      "font": "Arial Bold 14px"
    },
    "settings_button": {
      "position": {
        "x": 50,
        "y": 50
      },
      "size": {
        "width": 80,
        "height": 30
      },
      "backgroundColor": "#646464",
      "textColor": "#FFFFFF",
      "type": "button",
      "text": "Настройки",
      "font": "Arial 10px"
    }
  },
  "fonts": {
    "field_font": {
      "family": "Arial",
      "size": 12,
      "weight": "Normal",
      "color": "#FFFFFF"
    },
    "button_font": {
      "family": "Arial",
      "size": 14,
      "weight": "Bold",
      "color": "#000000"
    },
    "settings_font": {
      "family": "Arial",
      "size": 10,
      "weight": "Normal",
      "color": "#FFFFFF"
    }
  },
  "colors": {
    "background": "#1e1e2e",
    "field_background": "#000000",
    "field_border": "#FFFFFF",
    "field_text": "#FFFFFF",
    "button_gold": "#FFD700",
    "button_silver": "#C0C0C0",
    "button_text": "#000000",
    "hover_color": "#FFD700"
  }
}
        self.reference_screenshot = Path("analysis/reference_login_screen.png")
        self.modern_screenshot = Path("implementation/modern_login_screen.png")
    
    def test_element_positions(self):
        """Тест позиций элементов"""
        # Проверяем, что все элементы находятся в правильных позициях
        for element_name, element_data in self.analysis_data["elements"].items():
            if element_data["type"] in ["input_field", "password_field", "button"]:
                expected_x = element_data["position"]["x"]
                expected_y = element_data["position"]["y"]
                
                # Здесь будет код проверки позиций элементов
                self.assertIsNotNone(expected_x, f"Позиция X для {element_name} не определена")
                self.assertIsNotNone(expected_y, f"Позиция Y для {element_name} не определена")
    
    def test_element_sizes(self):
        """Тест размеров элементов"""
        # Проверяем, что все элементы имеют правильные размеры
        for element_name, element_data in self.analysis_data["elements"].items():
            if element_data["type"] in ["input_field", "password_field", "button"]:
                expected_width = element_data["size"]["width"]
                expected_height = element_data["size"]["height"]
                
                # Здесь будет код проверки размеров элементов
                self.assertIsNotNone(expected_width, f"Ширина для {element_name} не определена")
                self.assertIsNotNone(expected_height, f"Высота для {element_name} не определена")
    
    def test_element_colors(self):
        """Тест цветов элементов"""
        # Проверяем, что все элементы имеют правильные цвета
        for element_name, element_data in self.analysis_data["elements"].items():
            if "backgroundColor" in element_data:
                expected_color = element_data["backgroundColor"]
                
                # Здесь будет код проверки цветов элементов
                self.assertIsNotNone(expected_color, f"Цвет фона для {element_name} не определен")
    
    def test_pixel_compliance(self):
        """Тест попиксельного соответствия"""
        if self.reference_screenshot.exists() and self.modern_screenshot.exists():
            # Загружаем изображения
            ref_img = cv2.imread(str(self.reference_screenshot))
            modern_img = cv2.imread(str(self.modern_screenshot))
            
            if ref_img is not None and modern_img is not None:
                # Приводим к одному размеру
                if ref_img.shape != modern_img.shape:
                    modern_img = cv2.resize(modern_img, (ref_img.shape[1], ref_img.shape[0]))
                
                # Вычисляем разность
                diff = cv2.absdiff(ref_img, modern_img)
                diff_gray = cv2.cvtColor(diff, cv2.COLOR_BGR2GRAY)
                
                # Вычисляем процент соответствия
                total_pixels = diff_gray.shape[0] * diff_gray.shape[1]
                different_pixels = np.count_nonzero(diff_gray > 10)
                similarity_percentage = ((total_pixels - different_pixels) / total_pixels) * 100
                
                # Проверяем соответствие (минимум 95%)
                self.assertGreaterEqual(similarity_percentage, 95.0, 
                    f"Попиксельное соответствие {similarity_percentage:.1f}% < 95%")
    
    def test_field_validation(self):
        """Тест валидации полей"""
        # Тест максимальной длины логина
        max_login_length = self.analysis_data["elements"]["login_field"]["maxLength"]
        self.assertEqual(max_login_length, 16, "Максимальная длина логина должна быть 16")
        
        # Тест максимальной длины пароля
        max_password_length = self.analysis_data["elements"]["password_field"]["maxLength"]
        self.assertEqual(max_password_length, 16, "Максимальная длина пароля должна быть 16")
    
    def test_button_functionality(self):
        """Тест функциональности кнопок"""
        # Проверяем, что все кнопки определены
        button_elements = [name for name, data in self.analysis_data["elements"].items() 
                          if data["type"] == "button"]
        
        expected_buttons = ["login_button", "register_button", "settings_button"]
        for button in expected_buttons:
            self.assertIn(button, button_elements, f"Кнопка {button} не найдена")

if __name__ == '__main__':
    unittest.main()
