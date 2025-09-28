# 🎨 Руководство по графическим системам Modern Lineage II

## 📋 Обзор

Modern Lineage II использует передовые графические технологии для создания современного игрового опыта с реалистичной графикой и эффектами.

## 🎯 Графические системы

### 1. PBR Material Manager
**Файл**: `PBRMaterialManager.h`

Система управления PBR (Physically Based Rendering) материалами с поддержкой всех каналов:

#### Основные каналы:
- **Albedo** - базовый цвет материала
- **Normal** - карта нормалей для детализации поверхности
- **Roughness** - шероховатость поверхности (0-1)
- **Metallic** - металличность материала (0-1)
- **Ambient Occlusion** - затенение от окружающих объектов
- **Emissive** - самосвечение материала

#### Функции:
```cpp
// Создание PBR материала
UMaterialInstanceDynamic* CreatePBRMaterialInstance(const FString& MaterialID);

// Применение материала к мешу
bool ApplyPBRMaterialToMesh(UMeshComponent* MeshComponent, const FString& MaterialID);

// Конвертация L2 текстур в PBR
bool ConvertLegacyL2Texture(const FString& LegacyTexturePath, const FString& MaterialID);
```

### 2. HDR Manager
**Файл**: `HDRManager.h`

Система HDR (High Dynamic Range) рендеринга с автоматической экспозицией:

#### Настройки HDR:
- **Max Brightness** - максимальная яркость (nits)
- **Min Brightness** - минимальная яркость (nits)
- **Exposure Compensation** - компенсация экспозиции
- **Auto Exposure** - автоматическая экспозиция

#### Тональное отображение:
- **ACES** - Academy Color Encoding System
- **Film** - кинематографическое тональное отображение
- **Custom** - пользовательские настройки

#### Функции:
```cpp
// Включение/выключение HDR
void SetHDREnabled(bool bEnabled);

// Настройка яркости
void SetHDRBrightnessRange(float MinBrightness, float MaxBrightness);

// Применение пресета тонального отображения
void ApplyToneMappingPreset(const FString& PresetName);
```

### 3. Dynamic Lighting Manager
**Файл**: `DynamicLightingManager.h`

Система динамического освещения с циклом день/ночь:

#### Компоненты освещения:
- **Sun Light** - направленный свет солнца
- **Sky Light** - окружающий свет неба
- **Point Lights** - точечные источники света
- **Spot Lights** - прожекторы
- **Atmospheric Fog** - атмосферный туман

#### Цикл день/ночь:
- **Time of Day** - время суток (0-24 часа)
- **Day Duration** - продолжительность дня в реальных минутах
- **Time Speed** - скорость времени
- **Weather Effects** - погодные эффекты

#### Функции:
```cpp
// Установка времени суток
void SetTimeOfDay(float TimeOfDay);

// Загрузка пресета освещения
void LoadLightingPreset(const FString& PresetName);

// Настройка тумана
void SetFogColor(const FLinearColor& Color);
void SetFogDensity(float Density);
```

### 4. Particle Effect Manager
**Файл**: `ParticleEffectManager.h`

Система управления частицами и VFX эффектами:

#### Типы эффектов:
- **Magic** - магические эффекты
- **Combat** - боевые эффекты
- **Environmental** - окружающие эффекты
- **UI** - интерфейсные эффекты

#### Поддерживаемые системы:
- **UE4 Particle System** - классическая система частиц
- **Niagara** - современная система эффектов

#### Функции:
```cpp
// Создание эффекта
int32 SpawnParticleEffect(const FString& EffectID, const FVector& Location);

// Остановка эффекта
bool StopParticleEffect(int32 EffectHandle);

// Установка параметров эффекта
bool SetParticleEffectParameter(int32 EffectHandle, const FString& ParameterName, float Value);
```

### 5. Graphics Manager
**Файл**: `GraphicsManager.h`

Центральный менеджер всех графических систем:

#### Уровни качества:
- **Low** - низкое качество для слабых систем
- **Medium** - среднее качество
- **High** - высокое качество
- **Ultra** - максимальное качество

#### Настройки производительности:
- **Resolution Scale** - масштабирование разрешения
- **Anti-Aliasing** - сглаживание
- **Shadow Quality** - качество теней
- **Texture Quality** - качество текстур
- **Effect Quality** - качество эффектов

#### Функции:
```cpp
// Применение настроек графики
void ApplyGraphicsSettings(const FGraphicsSettings& Settings);

// Автоматическое определение оптимальных настроек
void AutoDetectOptimalSettings();

// Мониторинг производительности
float GetCurrentFPS() const;
int32 GetGPUMemoryUsage() const;
```

## 🛠️ Настройка графических систем

### 1. Автоматическая настройка
```bash
cd /home/ni/Projects/la2bots/modern_client
./setup_graphics.sh
```

### 2. Ручная настройка
1. Откройте проект в Unreal Engine 4.27
2. Скомпилируйте графические системы
3. Настройте PBR материалы
4. Протестируйте HDR рендеринг
5. Оптимизируйте для вашей системы

### 3. Ubuntu оптимизация
```cpp
// Оптимизация для Ubuntu Linux
UGraphicsManager::OptimizeForUbuntu();

// Настройка OpenGL
UGraphicsManager::SetOpenGLSettings();

// Настройка Vulkan
UGraphicsManager::SetVulkanSettings();
```

## 📊 Мониторинг производительности

### Метрики производительности:
- **FPS** - кадры в секунду
- **Frame Time** - время кадра
- **GPU Memory** - использование памяти GPU
- **VRAM Usage** - использование видеопамяти
- **System Memory** - использование системной памяти

### Оптимизация:
- **Texture Streaming** - потоковая загрузка текстур
- **LOD System** - система уровней детализации
- **Culling** - отсечение невидимых объектов
- **Batching** - группировка объектов для рендеринга

## 🎮 Интеграция с игровыми системами

### Персонажи:
- PBR материалы для кожи и одежды
- Подповерхностное рассеивание для реалистичной кожи
- Clear Coat для металлических элементов

### Окружение:
- Динамическое освещение для атмосферы
- Погодные эффекты
- Атмосферный туман

### Эффекты:
- Магические заклинания
- Боевые эффекты
- Частицы окружения

## 🔧 Отладка и диагностика

### Режим отладки:
```cpp
// Включение режима отладки графики
UGraphicsManager::SetGraphicsDebugMode(true);

// Показ статистики графики
UGraphicsManager::ShowGraphicsStatistics();
```

### Логирование:
- Все графические системы поддерживают детальное логирование
- Настройки логирования в `Config/DefaultEngine.ini`

## 📚 Дополнительные ресурсы

- [Unreal Engine 4.27 Documentation](https://docs.unrealengine.com/4.27/)
- [PBR Material Guide](https://docs.unrealengine.com/4.27/en-US/RenderingAndGraphics/Materials/PhysicallyBased/)
- [HDR Rendering Guide](https://docs.unrealengine.com/4.27/en-US/RenderingAndGraphics/LightingAndShadows/HDR/)
- [Particle Systems Guide](https://docs.unrealengine.com/4.27/en-US/Engine/Effects/ParticleSystems/)

---

*Руководство создано: 2024-09-27*  
*Версия: 4.1 - Modern Graphics System Complete*
