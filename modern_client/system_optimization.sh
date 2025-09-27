#!/bin/bash
# Системные оптимизации для игр

# CPU Governor
echo "performance" | sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor 2>/dev/null || true

# I/O Scheduler
echo "deadline" | sudo tee /sys/block/*/queue/scheduler 2>/dev/null || true

# Swappiness
echo "vm.swappiness=10" | sudo tee -a /etc/sysctl.conf

# Network optimizations
echo "net.core.rmem_max = 16777216" | sudo tee -a /etc/sysctl.conf
echo "net.core.wmem_max = 16777216" | sudo tee -a /etc/sysctl.conf
echo "net.ipv4.tcp_rmem = 4096 87380 16777216" | sudo tee -a /etc/sysctl.conf
echo "net.ipv4.tcp_wmem = 4096 65536 16777216" | sudo tee -a /etc/sysctl.conf

# Apply changes
sudo sysctl -p

echo "Системные оптимизации применены"
