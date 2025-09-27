#!/bin/bash
# NVIDIA оптимизации для Modern Lineage II

# Установка оптимальных настроек драйвера
nvidia-settings -a "[gpu:0]/GPUPowerMizerMode=1" 2>/dev/null || true
nvidia-settings -a "[gpu:0]/GPUMemoryTransferRateOffset[3]=1000" 2>/dev/null || true
nvidia-settings -a "[gpu:0]/GPUGraphicsClockOffset[3]=100" 2>/dev/null || true

# Настройки OpenGL
export __GL_SYNC_TO_VBLANK=1
export __GL_THREADED_OPTIMIZATIONS=1
export __GL_SHADER_DISK_CACHE=1
export __GL_SHADER_DISK_CACHE_PATH="$HOME/.cache/nvidia/GLCache"

echo "NVIDIA оптимизации применены"
