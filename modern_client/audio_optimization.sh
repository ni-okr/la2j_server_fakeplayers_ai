#!/bin/bash
# PipeWire оптимизации

# Настройка латентности
mkdir -p ~/.config/pipewire
cat > ~/.config/pipewire/pipewire.conf << 'PIPEWIRE_EOF'
context.properties = {
    default.clock.rate = 48000
    default.clock.quantum = 1024
    default.clock.min-quantum = 32
    default.clock.max-quantum = 8192
}
PIPEWIRE_EOF

systemctl --user restart pipewire

echo "PipeWire оптимизирован"
