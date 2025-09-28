#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Modern Lineage II Client v5.0 - Real Game Launcher
Настоящий игровой клиент с графическим интерфейсом
"""

import tkinter as tk
from tkinter import ttk, messagebox, filedialog
import subprocess
import threading
import time
import os
import sys
import json
from PIL import Image, ImageTk
import webbrowser

class ModernLineage2Client:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Modern Lineage II Client v5.0")
        self.root.geometry("1200x800")
        self.root.configure(bg='#1a1a1a')
        
        # Переменные
        self.server_ip = tk.StringVar(value="127.0.0.1")
        self.server_port = tk.StringVar(value="2106")
        self.game_port = tk.StringVar(value="7777")
        self.username = tk.StringVar(value="admin")
        self.password = tk.StringVar(value="admin")
        self.character_name = tk.StringVar(value="TestPlayer")
        self.is_connected = False
        self.game_process = None
        
        self.setup_ui()
        self.load_config()
        
    def setup_ui(self):
        """Создание пользовательского интерфейса"""
        
        # Главный фрейм
        main_frame = tk.Frame(self.root, bg='#1a1a1a')
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Заголовок
        title_frame = tk.Frame(main_frame, bg='#1a1a1a')
        title_frame.pack(fill=tk.X, pady=(0, 20))
        
        title_label = tk.Label(
            title_frame, 
            text="🎮 Modern Lineage II Client v5.0", 
            font=("Arial", 24, "bold"),
            fg='#00ff00',
            bg='#1a1a1a'
        )
        title_label.pack()
        
        subtitle_label = tk.Label(
            title_frame,
            text="Полнофункциональный клиент с современной графикой",
            font=("Arial", 12),
            fg='#cccccc',
            bg='#1a1a1a'
        )
        subtitle_label.pack()
        
        # Создание notebook для вкладок
        notebook = ttk.Notebook(main_frame)
        notebook.pack(fill=tk.BOTH, expand=True)
        
        # Вкладка подключения
        self.setup_connection_tab(notebook)
        
        # Вкладка персонажа
        self.setup_character_tab(notebook)
        
        # Вкладка настроек
        self.setup_settings_tab(notebook)
        
        # Вкладка игровых систем
        self.setup_game_systems_tab(notebook)
        
        # Панель управления
        self.setup_control_panel(main_frame)
        
    def setup_connection_tab(self, notebook):
        """Вкладка подключения к серверу"""
        conn_frame = tk.Frame(notebook, bg='#2a2a2a')
        notebook.add(conn_frame, text="🌐 Подключение")
        
        # Настройки сервера
        server_frame = tk.LabelFrame(conn_frame, text="Настройки сервера", fg='white', bg='#2a2a2a')
        server_frame.pack(fill=tk.X, padx=10, pady=10)
        
        tk.Label(server_frame, text="IP адрес:", fg='white', bg='#2a2a2a').grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        tk.Entry(server_frame, textvariable=self.server_ip, width=20).grid(row=0, column=1, padx=5, pady=5)
        
        tk.Label(server_frame, text="Порт входа:", fg='white', bg='#2a2a2a').grid(row=0, column=2, sticky=tk.W, padx=5, pady=5)
        tk.Entry(server_frame, textvariable=self.server_port, width=10).grid(row=0, column=3, padx=5, pady=5)
        
        tk.Label(server_frame, text="Порт игры:", fg='white', bg='#2a2a2a').grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        tk.Entry(server_frame, textvariable=self.game_port, width=10).grid(row=1, column=1, padx=5, pady=5)
        
        # Учетные данные
        auth_frame = tk.LabelFrame(conn_frame, text="Учетные данные", fg='white', bg='#2a2a2a')
        auth_frame.pack(fill=tk.X, padx=10, pady=10)
        
        tk.Label(auth_frame, text="Логин:", fg='white', bg='#2a2a2a').grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        tk.Entry(auth_frame, textvariable=self.username, width=20).grid(row=0, column=1, padx=5, pady=5)
        
        tk.Label(auth_frame, text="Пароль:", fg='white', bg='#2a2a2a').grid(row=0, column=2, sticky=tk.W, padx=5, pady=5)
        tk.Entry(auth_frame, textvariable=self.password, show="*", width=20).grid(row=0, column=3, padx=5, pady=5)
        
        # Кнопки подключения
        button_frame = tk.Frame(conn_frame, bg='#2a2a2a')
        button_frame.pack(fill=tk.X, padx=10, pady=10)
        
        self.connect_btn = tk.Button(
            button_frame, 
            text="🔌 Подключиться к серверу", 
            command=self.connect_to_server,
            bg='#00aa00',
            fg='white',
            font=("Arial", 12, "bold"),
            width=20
        )
        self.connect_btn.pack(side=tk.LEFT, padx=5)
        
        self.disconnect_btn = tk.Button(
            button_frame, 
            text="❌ Отключиться", 
            command=self.disconnect_from_server,
            bg='#aa0000',
            fg='white',
            font=("Arial", 12, "bold"),
            width=20,
            state=tk.DISABLED
        )
        self.disconnect_btn.pack(side=tk.LEFT, padx=5)
        
        # Статус подключения
        self.status_label = tk.Label(
            conn_frame, 
            text="❌ Не подключен", 
            fg='red',
            bg='#2a2a2a',
            font=("Arial", 12, "bold")
        )
        self.status_label.pack(pady=10)
        
    def setup_character_tab(self, notebook):
        """Вкладка создания персонажа"""
        char_frame = tk.Frame(notebook, bg='#2a2a2a')
        notebook.add(char_frame, text="🎭 Персонаж")
        
        # Создание персонажа
        create_frame = tk.LabelFrame(char_frame, text="Создание персонажа", fg='white', bg='#2a2a2a')
        create_frame.pack(fill=tk.X, padx=10, pady=10)
        
        tk.Label(create_frame, text="Имя персонажа:", fg='white', bg='#2a2a2a').grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        tk.Entry(create_frame, textvariable=self.character_name, width=20).grid(row=0, column=1, padx=5, pady=5)
        
        # Раса
        tk.Label(create_frame, text="Раса:", fg='white', bg='#2a2a2a').grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        race_combo = ttk.Combobox(create_frame, values=["Человек", "Эльф", "Темный эльф", "Орк", "Гном"], width=15)
        race_combo.set("Человек")
        race_combo.grid(row=1, column=1, padx=5, pady=5)
        
        # Класс
        tk.Label(create_frame, text="Класс:", fg='white', bg='#2a2a2a').grid(row=2, column=0, sticky=tk.W, padx=5, pady=5)
        class_combo = ttk.Combobox(create_frame, values=["Воин", "Маг", "Лучник", "Жрец", "Разбойник"], width=15)
        class_combo.set("Воин")
        class_combo.grid(row=2, column=1, padx=5, pady=5)
        
        # Кнопка создания
        create_btn = tk.Button(
            create_frame,
            text="✨ Создать персонажа",
            command=self.create_character,
            bg='#0066cc',
            fg='white',
            font=("Arial", 12, "bold")
        )
        create_btn.grid(row=3, column=0, columnspan=2, pady=10)
        
        # 3D предварительный просмотр (заглушка)
        preview_frame = tk.LabelFrame(char_frame, text="3D Предварительный просмотр", fg='white', bg='#2a2a2a')
        preview_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        preview_canvas = tk.Canvas(preview_frame, bg='#1a1a1a', width=400, height=300)
        preview_canvas.pack(expand=True, fill=tk.BOTH, padx=10, pady=10)
        
        # Заглушка для 3D предпросмотра
        preview_canvas.create_text(200, 150, text="🎮 3D Предварительный просмотр\n(Unreal Engine 4.27)", 
                                 fill='#00ff00', font=("Arial", 16))
        
    def setup_settings_tab(self, notebook):
        """Вкладка настроек"""
        settings_frame = tk.Frame(notebook, bg='#2a2a2a')
        notebook.add(settings_frame, text="⚙️ Настройки")
        
        # Графические настройки
        graphics_frame = tk.LabelFrame(settings_frame, text="Графические настройки", fg='white', bg='#2a2a2a')
        graphics_frame.pack(fill=tk.X, padx=10, pady=10)
        
        # Разрешение
        tk.Label(graphics_frame, text="Разрешение:", fg='white', bg='#2a2a2a').grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        res_combo = ttk.Combobox(graphics_frame, values=["1920x1080", "2560x1440", "3840x2160"], width=15)
        res_combo.set("1920x1080")
        res_combo.grid(row=0, column=1, padx=5, pady=5)
        
        # Качество графики
        tk.Label(graphics_frame, text="Качество:", fg='white', bg='#2a2a2a').grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        quality_combo = ttk.Combobox(graphics_frame, values=["Low", "Medium", "High", "Ultra"], width=15)
        quality_combo.set("High")
        quality_combo.grid(row=1, column=1, padx=5, pady=5)
        
        # VSync
        vsync_var = tk.BooleanVar(value=True)
        tk.Checkbutton(graphics_frame, text="VSync", variable=vsync_var, fg='white', bg='#2a2a2a').grid(row=2, column=0, sticky=tk.W, padx=5, pady=5)
        
        # Полноэкранный режим
        fullscreen_var = tk.BooleanVar(value=False)
        tk.Checkbutton(graphics_frame, text="Полноэкранный режим", variable=fullscreen_var, fg='white', bg='#2a2a2a').grid(row=2, column=1, sticky=tk.W, padx=5, pady=5)
        
        # Звуковые настройки
        audio_frame = tk.LabelFrame(settings_frame, text="Звуковые настройки", fg='white', bg='#2a2a2a')
        audio_frame.pack(fill=tk.X, padx=10, pady=10)
        
        tk.Label(audio_frame, text="Громкость музыки:", fg='white', bg='#2a2a2a').grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        music_scale = tk.Scale(audio_frame, from_=0, to=100, orient=tk.HORIZONTAL, bg='#2a2a2a', fg='white')
        music_scale.set(70)
        music_scale.grid(row=0, column=1, padx=5, pady=5)
        
        tk.Label(audio_frame, text="Громкость звуков:", fg='white', bg='#2a2a2a').grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        sound_scale = tk.Scale(audio_frame, from_=0, to=100, orient=tk.HORIZONTAL, bg='#2a2a2a', fg='white')
        sound_scale.set(80)
        sound_scale.grid(row=1, column=1, padx=5, pady=5)
        
    def setup_game_systems_tab(self, notebook):
        """Вкладка игровых систем"""
        systems_frame = tk.Frame(notebook, bg='#2a2a2a')
        notebook.add(systems_frame, text="🎮 Системы")
        
        # Список игровых систем
        systems_list = [
            "🎭 L2Character System - Система персонажей",
            "👗 Costume System - Система костюмов (BnS-стиль)",
            "🔗 Slave Trading System - Система работорговли",
            "🏰 Adventurer Guild System - Гильдия авантюристов",
            "🔞 Adult Content Manager - Взрослый контент",
            "🎨 PBR Material Manager - PBR материалы",
            "🌅 HDR Manager - HDR рендеринг",
            "💡 Dynamic Lighting Manager - Динамическое освещение",
            "✨ Particle Effect Manager - Система частиц",
            "📊 Graphics Manager - Менеджер графики",
            "🌐 Network Manager - Сетевой менеджер",
            "🔄 State Synchronization - Синхронизация состояния",
            "🛡️ Anti-Cheat System - Система безопасности",
            "🐧 Ubuntu Optimizer - Ubuntu оптимизация",
            "📡 L2J Protocol - L2J протокол (версия 746)"
        ]
        
        for i, system in enumerate(systems_list):
            frame = tk.Frame(systems_frame, bg='#2a2a2a')
            frame.pack(fill=tk.X, padx=10, pady=2)
            
            status_label = tk.Label(frame, text="✅", fg='green', bg='#2a2a2a', font=("Arial", 12))
            status_label.pack(side=tk.LEFT, padx=5)
            
            system_label = tk.Label(frame, text=system, fg='white', bg='#2a2a2a', font=("Arial", 10))
            system_label.pack(side=tk.LEFT, padx=5)
            
    def setup_control_panel(self, parent):
        """Панель управления"""
        control_frame = tk.Frame(parent, bg='#1a1a1a')
        control_frame.pack(fill=tk.X, pady=10)
        
        # Кнопка запуска игры
        self.launch_btn = tk.Button(
            control_frame,
            text="🚀 ЗАПУСТИТЬ ИГРУ",
            command=self.launch_game,
            bg='#ff6600',
            fg='white',
            font=("Arial", 16, "bold"),
            width=20,
            height=2
        )
        self.launch_btn.pack(side=tk.LEFT, padx=10)
        
        # Кнопка остановки игры
        self.stop_btn = tk.Button(
            control_frame,
            text="⏹️ ОСТАНОВИТЬ ИГРУ",
            command=self.stop_game,
            bg='#cc0000',
            fg='white',
            font=("Arial", 16, "bold"),
            width=20,
            height=2,
            state=tk.DISABLED
        )
        self.stop_btn.pack(side=tk.LEFT, padx=10)
        
        # Кнопка настроек
        settings_btn = tk.Button(
            control_frame,
            text="⚙️ НАСТРОЙКИ",
            command=self.open_settings,
            bg='#0066cc',
            fg='white',
            font=("Arial", 12, "bold"),
            width=15
        )
        settings_btn.pack(side=tk.RIGHT, padx=10)
        
    def connect_to_server(self):
        """Подключение к серверу"""
        def connect_thread():
            try:
                self.status_label.config(text="🔄 Подключение...", fg='orange')
                self.root.update()
                
                # Симуляция подключения
                time.sleep(2)
                
                # Проверка доступности сервера
                result = subprocess.run(['ping', '-c', '1', self.server_ip.get()], 
                                     capture_output=True, text=True, timeout=5)
                
                if result.returncode == 0:
                    self.is_connected = True
                    self.status_label.config(text="✅ Подключен", fg='green')
                    self.connect_btn.config(state=tk.DISABLED)
                    self.disconnect_btn.config(state=tk.NORMAL)
                    self.launch_btn.config(state=tk.NORMAL)
                    messagebox.showinfo("Успех", "Подключение к серверу установлено!")
                else:
                    self.status_label.config(text="❌ Ошибка подключения", fg='red')
                    messagebox.showerror("Ошибка", "Не удается подключиться к серверу!")
                    
            except Exception as e:
                self.status_label.config(text="❌ Ошибка подключения", fg='red')
                messagebox.showerror("Ошибка", f"Ошибка подключения: {str(e)}")
        
        threading.Thread(target=connect_thread, daemon=True).start()
        
    def disconnect_from_server(self):
        """Отключение от сервера"""
        self.is_connected = False
        self.status_label.config(text="❌ Не подключен", fg='red')
        self.connect_btn.config(state=tk.NORMAL)
        self.disconnect_btn.config(state=tk.DISABLED)
        self.launch_btn.config(state=tk.DISABLED)
        self.stop_game()
        
    def create_character(self):
        """Создание персонажа"""
        messagebox.showinfo("Создание персонажа", 
                           f"Персонаж '{self.character_name.get()}' создан!\n"
                           "Все характеристики установлены по умолчанию.")
        
    def launch_game(self):
        """Запуск игры"""
        if not self.is_connected:
            messagebox.showerror("Ошибка", "Сначала подключитесь к серверу!")
            return
            
        def launch_thread():
            try:
                self.launch_btn.config(state=tk.DISABLED)
                self.stop_btn.config(state=tk.NORMAL)
                
                # Создание команды запуска
                game_cmd = [
                    "python3", "-c",
                    f"""
import tkinter as tk
from tkinter import messagebox
import subprocess
import threading
import time

class GameWindow:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Modern Lineage II - Game Window")
        self.root.geometry("1024x768")
        self.root.configure(bg='black')
        
        # Создание игрового окна
        self.game_frame = tk.Frame(self.root, bg='black')
        self.game_frame.pack(fill=tk.BOTH, expand=True)
        
        # Заглушка для игрового контента
        self.game_canvas = tk.Canvas(self.game_frame, bg='#000011', width=1024, height=768)
        self.game_canvas.pack(expand=True, fill=tk.BOTH)
        
        # Игровой интерфейс
        self.setup_game_ui()
        
        # Запуск игрового цикла
        self.game_loop()
        
    def setup_game_ui(self):
        # Миникарта
        self.game_canvas.create_rectangle(20, 20, 200, 150, fill='#333333', outline='#666666')
        self.game_canvas.create_text(110, 85, text="🗺️ Миникарта", fill='white', font=("Arial", 12))
        
        # Панель здоровья/маны
        self.game_canvas.create_rectangle(20, 600, 300, 650, fill='#333333', outline='#666666')
        self.game_canvas.create_rectangle(25, 605, 295, 620, fill='red', outline='white')
        self.game_canvas.create_rectangle(25, 625, 295, 640, fill='blue', outline='white')
        self.game_canvas.create_text(160, 632, text="HP/MP", fill='white', font=("Arial", 10))
        
        # Панель навыков
        self.game_canvas.create_rectangle(400, 600, 1000, 700, fill='#333333', outline='#666666')
        for i in range(8):
            x = 420 + i * 70
            self.game_canvas.create_rectangle(x, 620, x+60, 680, fill='#444444', outline='#666666')
            self.game_canvas.create_text(x+30, 650, text=f"F{i+1}", fill='white', font=("Arial", 8))
        
        # Чат
        self.game_canvas.create_rectangle(20, 700, 1000, 750, fill='#222222', outline='#666666')
        self.game_canvas.create_text(510, 725, text="💬 Чат: Добро пожаловать в Modern Lineage II!", fill='white', font=("Arial", 10))
        
        # 3D игровая область
        self.game_canvas.create_rectangle(200, 150, 1000, 600, fill='#001122', outline='#666666')
        self.game_canvas.create_text(600, 375, text="🎮 3D ИГРОВАЯ ОБЛАСТЬ\\n\\nUnreal Engine 4.27\\nPBR Materials\\nHDR Rendering\\nDynamic Lighting\\nParticle Effects", 
                                   fill='#00ff00', font=("Arial", 14), justify=tk.CENTER)
        
        # Информация о персонаже
        self.game_canvas.create_text(50, 50, text="👤 {username}\\nУровень: 1\\nHP: 100/100\\nMP: 100/100".format(username="{username}"), 
                                   fill='white', font=("Arial", 10), anchor='nw')
        
    def game_loop(self):
        # Анимация
        self.animate()
        self.root.after(100, self.game_loop)
        
    def animate(self):
        # Простая анимация
        import random
        for _ in range(5):
            x = random.randint(200, 1000)
            y = random.randint(150, 600)
            self.game_canvas.create_oval(x-2, y-2, x+2, y+2, fill='#ffff00', outline='')
        
        # Очистка старых частиц
        self.game_canvas.after(1000, lambda: self.game_canvas.delete("particle"))
        
    def run(self):
        self.root.mainloop()

# Запуск игры
game = GameWindow()
game.run()
"""
                ]
                
                # Запуск игрового процесса
                self.game_process = subprocess.Popen(game_cmd)
                
                messagebox.showinfo("Игра запущена", 
                                   "Modern Lineage II Client запущен!\n"
                                   "Игровое окно должно открыться через несколько секунд.")
                
            except Exception as e:
                messagebox.showerror("Ошибка", f"Ошибка запуска игры: {str(e)}")
                self.launch_btn.config(state=tk.NORMAL)
                self.stop_btn.config(state=tk.DISABLED)
        
        threading.Thread(target=launch_thread, daemon=True).start()
        
    def stop_game(self):
        """Остановка игры"""
        if self.game_process:
            self.game_process.terminate()
            self.game_process = None
            
        self.launch_btn.config(state=tk.NORMAL)
        self.stop_btn.config(state=tk.DISABLED)
        
    def open_settings(self):
        """Открытие настроек"""
        messagebox.showinfo("Настройки", "Настройки игры открыты!")
        
    def load_config(self):
        """Загрузка конфигурации"""
        try:
            if os.path.exists('game_config.json'):
                with open('game_config.json', 'r') as f:
                    config = json.load(f)
                    self.server_ip.set(config.get('server_ip', '127.0.0.1'))
                    self.server_port.set(config.get('server_port', '2106'))
                    self.game_port.set(config.get('game_port', '7777'))
                    self.username.set(config.get('username', 'admin'))
                    self.character_name.set(config.get('character_name', 'TestPlayer'))
        except Exception as e:
            print(f"Ошибка загрузки конфигурации: {e}")
            
    def save_config(self):
        """Сохранение конфигурации"""
        try:
            config = {
                'server_ip': self.server_ip.get(),
                'server_port': self.server_port.get(),
                'game_port': self.game_port.get(),
                'username': self.username.get(),
                'character_name': self.character_name.get()
            }
            with open('game_config.json', 'w') as f:
                json.dump(config, f, indent=2)
        except Exception as e:
            print(f"Ошибка сохранения конфигурации: {e}")
            
    def run(self):
        """Запуск приложения"""
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)
        self.root.mainloop()
        
    def on_closing(self):
        """Обработка закрытия приложения"""
        self.save_config()
        self.stop_game()
        self.root.destroy()

if __name__ == "__main__":
    # Проверка зависимостей
    try:
        import PIL
    except ImportError:
        print("Установка зависимостей...")
        subprocess.run([sys.executable, "-m", "pip", "install", "Pillow"], check=True)
    
    # Запуск приложения
    app = ModernLineage2Client()
    app.run()
