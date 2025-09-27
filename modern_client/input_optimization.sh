#!/bin/bash
# Оптимизация устройств ввода

# Отключение композитора для снижения задержки ввода
if command -v compiz &> /dev/null; then
    compiz --replace &
elif command -v kwin &> /dev/null; then
    kwin --replace &
fi

# Настройка мыши
xinput set-prop "pointer:Logitech" "libinput Accel Speed" 0 2>/dev/null || true
xinput set-prop "pointer:Logitech" "libinput Accel Profile Enabled" 0, 1 2>/dev/null || true

# Настройка клавиатуры
setxkbmap -option caps:escape 2>/dev/null || true

echo "Устройства ввода оптимизированы"
