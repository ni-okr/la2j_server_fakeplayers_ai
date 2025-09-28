#include <GL/glut.h>
#include <GL/gl.h>
#include <iostream>
#include <vector>
#include <string>
#include <cmath>
#include <fstream>
#include <random>

// Параметры окна
int windowWidth = 1280;
int windowHeight = 720;
const char* windowTitle = "Modern Lineage II - Water System";

// Параметры камеры
float cameraX = 0.0f, cameraY = 10.0f, cameraZ = 20.0f;
float lookAtX = 0.0f, lookAtY = 0.0f, lookAtZ = 0.0f;
float angleY = 0.0f;
float angleX = 0.0f;
float playerMoveSpeed = 0.5f;
float playerRotateSpeed = 2.0f;

// Позиция игрока
float playerX = 0.0f, playerY = 0.5f, playerZ = 0.0f;

// Система рельефа
const int TERRAIN_SIZE = 100;
const float TERRAIN_SCALE = 2.0f;
float terrainHeight[TERRAIN_SIZE][TERRAIN_SIZE];

// Система воды
struct WaterBody {
    float x, z, width, height, depth;
    float waveTime;
    float waveAmplitude;
    float waveFrequency;
    bool isRiver;
    bool isOcean;
    bool isLake;
};

std::vector<WaterBody> waterBodies;

// Система частиц дождя
struct RainDrop {
    float x, y, z;
    float speed;
    float size;
    float life;
    float splashRadius;
};

std::vector<RainDrop> rainDrops;

// Система волн на воде
struct Wave {
    float x, z, amplitude, frequency, speed, life;
    float directionX, directionZ;
};

std::vector<Wave> waves;

// Система текстур
GLuint groundTexture = 0;
GLuint grassTexture = 0;
GLuint stoneTexture = 0;
GLuint waterTexture = 0;
GLuint waterNormalTexture = 0;
GLuint waterFoamTexture = 0;

// Система времени и погоды
float timeOfDay = 0.5f;
bool dayNightCycle = true;
float weatherIntensity = 0.0f;
bool isRaining = false;
float windStrength = 0.0f;

// Цвета неба
struct SkyColor {
    float r, g, b;
};

SkyColor daySky = {0.5f, 0.7f, 1.0f};
SkyColor nightSky = {0.1f, 0.1f, 0.3f};
SkyColor sunsetSky = {1.0f, 0.5f, 0.2f};
SkyColor rainSky = {0.4f, 0.4f, 0.5f};
SkyColor currentSky = daySky;

// Генератор случайных чисел
std::random_device rd;
std::mt19937 gen(rd());
std::uniform_real_distribution<float> dis(0.0f, 1.0f);

// Инициализация водных объектов
void initWaterBodies() {
    waterBodies.clear();
    
    // Создаем океан в центре
    WaterBody ocean;
    ocean.x = 0.0f;
    ocean.z = 0.0f;
    ocean.width = 60.0f;
    ocean.height = 60.0f;
    ocean.depth = -2.0f;
    ocean.waveTime = 0.0f;
    ocean.waveAmplitude = 1.5f;
    ocean.waveFrequency = 0.1f;
    ocean.isOcean = true;
    ocean.isRiver = false;
    ocean.isLake = false;
    waterBodies.push_back(ocean);
    
    // Создаем реки
    for (int i = 0; i < 3; i++) {
        WaterBody river;
        river.x = -80.0f + i * 80.0f;
        river.z = 0.0f;
        river.width = 8.0f;
        river.height = 120.0f;
        river.depth = -1.0f;
        river.waveTime = 0.0f;
        river.waveAmplitude = 0.5f;
        river.waveFrequency = 0.2f;
        river.isOcean = false;
        river.isRiver = true;
        river.isLake = false;
        waterBodies.push_back(river);
    }
    
    // Создаем озера
    for (int i = 0; i < 2; i++) {
        WaterBody lake;
        lake.x = -60.0f + i * 120.0f;
        lake.z = -60.0f + i * 120.0f;
        lake.width = 20.0f;
        lake.height = 20.0f;
        lake.depth = -1.5f;
        lake.waveTime = 0.0f;
        lake.waveAmplitude = 0.8f;
        lake.waveFrequency = 0.15f;
        lake.isOcean = false;
        lake.isRiver = false;
        lake.isLake = true;
        waterBodies.push_back(lake);
    }
    
    std::cout << "Initialized " << waterBodies.size() << " water bodies" << std::endl;
}

// Инициализация капель дождя
void initRain() {
    rainDrops.clear();
    for (int i = 0; i < 2000; i++) {
        RainDrop drop;
        drop.x = dis(gen) * 200.0f - 100.0f;
        drop.y = 30.0f + dis(gen) * 20.0f;
        drop.z = dis(gen) * 200.0f - 100.0f;
        drop.speed = 2.0f + dis(gen) * 3.0f;
        drop.size = 0.1f + dis(gen) * 0.2f;
        drop.life = 1.0f;
        drop.splashRadius = 0.0f;
        rainDrops.push_back(drop);
    }
}

// Инициализация волн
void initWaves() {
    waves.clear();
    for (int i = 0; i < 50; i++) {
        Wave wave;
        wave.x = dis(gen) * 200.0f - 100.0f;
        wave.z = dis(gen) * 200.0f - 100.0f;
        wave.amplitude = 0.5f + dis(gen) * 1.0f;
        wave.frequency = 0.1f + dis(gen) * 0.2f;
        wave.speed = 0.5f + dis(gen) * 1.0f;
        wave.life = 1.0f;
        wave.directionX = (dis(gen) - 0.5f) * 2.0f;
        wave.directionZ = (dis(gen) - 0.5f) * 2.0f;
        waves.push_back(wave);
    }
}

// Генерация рельефа
void generateTerrain() {
    for (int x = 0; x < TERRAIN_SIZE; x++) {
        for (int z = 0; z < TERRAIN_SIZE; z++) {
            float worldX = (x - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldZ = (z - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            
            float height = 0.0f;
            float distanceFromCenter = sqrt(worldX*worldX + worldZ*worldZ);
            
            if (distanceFromCenter > 80.0f) {
                height = 15.0f + sin(worldX * 0.1f) * 5.0f + cos(worldZ * 0.1f) * 5.0f;
            } else if (distanceFromCenter > 40.0f) {
                height = 5.0f + sin(worldX * 0.05f) * 3.0f + cos(worldZ * 0.05f) * 3.0f;
            } else {
                height = 1.0f + sin(worldX * 0.02f) * 0.5f + cos(worldZ * 0.02f) * 0.5f;
            }
            
            // Проверяем, находится ли точка в водном объекте
            for (const auto& water : waterBodies) {
                if (worldX >= water.x - water.width/2 && worldX <= water.x + water.width/2 &&
                    worldZ >= water.z - water.height/2 && worldZ <= water.z + water.height/2) {
                    height = water.depth;
                    break;
                }
            }
            
            terrainHeight[x][z] = height;
        }
    }
}

// Обновление времени суток
void updateDayNightCycle() {
    if (!dayNightCycle) return;
    
    timeOfDay += 0.00005f;
    if (timeOfDay > 1.0f) timeOfDay = 0.0f;
    
    if (timeOfDay < 0.25f) {
        float t = timeOfDay * 4.0f;
        currentSky.r = nightSky.r + (sunsetSky.r - nightSky.r) * t;
        currentSky.g = nightSky.g + (sunsetSky.g - nightSky.g) * t;
        currentSky.b = nightSky.b + (sunsetSky.b - nightSky.b) * t;
    } else if (timeOfDay < 0.5f) {
        float t = (timeOfDay - 0.25f) * 4.0f;
        currentSky.r = sunsetSky.r + (daySky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (daySky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (daySky.b - sunsetSky.b) * t;
    } else if (timeOfDay < 0.75f) {
        float t = (timeOfDay - 0.5f) * 4.0f;
        currentSky.r = daySky.r + (sunsetSky.r - daySky.r) * t;
        currentSky.g = daySky.g + (sunsetSky.g - daySky.g) * t;
        currentSky.b = daySky.b + (sunsetSky.b - daySky.b) * t;
    } else {
        float t = (timeOfDay - 0.75f) * 4.0f;
        currentSky.r = sunsetSky.r + (nightSky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (nightSky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (nightSky.b - sunsetSky.b) * t;
    }
    
    // Применяем погодные эффекты
    if (isRaining) {
        float rainEffect = weatherIntensity;
        currentSky.r = currentSky.r * (1.0f - rainEffect) + rainSky.r * rainEffect;
        currentSky.g = currentSky.g * (1.0f - rainEffect) + rainSky.g * rainEffect;
        currentSky.b = currentSky.b * (1.0f - rainEffect) + rainSky.b * rainEffect;
    }
}

// Обновление водных объектов
void updateWaterBodies() {
    for (auto& water : waterBodies) {
        water.waveTime += 0.016f; // ~60 FPS
        
        // Обновляем силу волн в зависимости от ветра
        if (isRaining) {
            water.waveAmplitude = 1.0f + windStrength * 2.0f;
            water.waveFrequency = 0.1f + windStrength * 0.1f;
        } else {
            water.waveAmplitude = 0.5f + windStrength * 0.5f;
            water.waveFrequency = 0.05f + windStrength * 0.05f;
        }
    }
}

// Обновление дождя
void updateRain() {
    if (!isRaining) return;
    
    for (auto& drop : rainDrops) {
        drop.y -= drop.speed;
        drop.x += windStrength * 0.5f; // Ветер
        drop.life -= 0.01f;
        
        // Проверяем столкновение с водой
        bool hitWater = false;
        for (const auto& water : waterBodies) {
            if (drop.x >= water.x - water.width/2 && drop.x <= water.x + water.width/2 &&
                drop.z >= water.z - water.height/2 && drop.z <= water.z + water.height/2 &&
                drop.y <= water.depth + 1.0f) {
                hitWater = true;
                break;
            }
        }
        
        if (drop.y < 0.0f || drop.life <= 0.0f || hitWater) {
            if (hitWater) {
                // Создаем волну от падения капли
                Wave splash;
                splash.x = drop.x;
                splash.z = drop.z;
                splash.amplitude = 0.5f;
                splash.frequency = 0.3f;
                splash.speed = 2.0f;
                splash.life = 1.0f;
                splash.directionX = (dis(gen) - 0.5f) * 4.0f;
                splash.directionZ = (dis(gen) - 0.5f) * 4.0f;
                waves.push_back(splash);
            }
            
            // Перезапуск капли
            drop.x = dis(gen) * 200.0f - 100.0f;
            drop.y = 30.0f + dis(gen) * 20.0f;
            drop.z = dis(gen) * 200.0f - 100.0f;
            drop.life = 1.0f;
        }
    }
}

// Обновление волн
void updateWaves() {
    for (auto& wave : waves) {
        wave.x += wave.directionX * wave.speed * 0.1f;
        wave.z += wave.directionZ * wave.speed * 0.1f;
        wave.life -= 0.005f;
        
        if (wave.life <= 0.0f) {
            wave.x = dis(gen) * 200.0f - 100.0f;
            wave.z = dis(gen) * 200.0f - 100.0f;
            wave.life = 1.0f;
        }
    }
}

// Рисование воды
void drawWater() {
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    for (const auto& water : waterBodies) {
        glBindTexture(GL_TEXTURE_2D, waterTexture);
        
        // Цвет воды в зависимости от времени суток
        if (timeOfDay < 0.25f || timeOfDay > 0.75f) {
            glColor4f(0.1f, 0.2f, 0.4f, 0.8f); // Темно-синий ночью
        } else if (timeOfDay < 0.5f) {
            glColor4f(0.2f, 0.5f, 0.8f, 0.7f); // Голубой днем
        } else {
            glColor4f(0.8f, 0.4f, 0.2f, 0.7f); // Оранжевый на закате
        }
        
        // Рисуем водную поверхность с волнами
        const int segments = 20;
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < segments; j++) {
                float x1 = water.x - water.width/2 + (i * water.width / segments);
                float z1 = water.z - water.height/2 + (j * water.height / segments);
                float x2 = water.x - water.width/2 + ((i+1) * water.width / segments);
                float z2 = water.z - water.height/2 + ((j+1) * water.height / segments);
                
                // Вычисляем высоту волн
                float waveHeight1 = 0.0f;
                float waveHeight2 = 0.0f;
                float waveHeight3 = 0.0f;
                float waveHeight4 = 0.0f;
                
                for (const auto& wave : waves) {
                    float dist1 = sqrt((x1 - wave.x) * (x1 - wave.x) + (z1 - wave.z) * (z1 - wave.z));
                    float dist2 = sqrt((x2 - wave.x) * (x2 - wave.x) + (z1 - wave.z) * (z1 - wave.z));
                    float dist3 = sqrt((x1 - wave.x) * (x1 - wave.x) + (z2 - wave.z) * (z2 - wave.z));
                    float dist4 = sqrt((x2 - wave.x) * (x2 - wave.x) + (z2 - wave.z) * (z2 - wave.z));
                    
                    waveHeight1 += wave.amplitude * sin(dist1 * wave.frequency + water.waveTime) * wave.life;
                    waveHeight2 += wave.amplitude * sin(dist2 * wave.frequency + water.waveTime) * wave.life;
                    waveHeight3 += wave.amplitude * sin(dist3 * wave.frequency + water.waveTime) * wave.life;
                    waveHeight4 += wave.amplitude * sin(dist4 * wave.frequency + water.waveTime) * wave.life;
                }
                
                glBegin(GL_QUADS);
                    glTexCoord2f(0.0f, 0.0f); glVertex3f(x1, water.depth + waveHeight1, z1);
                    glTexCoord2f(1.0f, 0.0f); glVertex3f(x2, water.depth + waveHeight2, z1);
                    glTexCoord2f(1.0f, 1.0f); glVertex3f(x2, water.depth + waveHeight4, z2);
                    glTexCoord2f(0.0f, 1.0f); glVertex3f(x1, water.depth + waveHeight3, z2);
                glEnd();
            }
        }
    }
    
    glDisable(GL_BLEND);
    glDisable(GL_TEXTURE_2D);
}

// Рисование дождя
void drawRain() {
    if (!isRaining) return;
    
    glDisable(GL_LIGHTING);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    glColor4f(0.7f, 0.8f, 1.0f, 0.6f);
    glLineWidth(1.0f);
    
    glBegin(GL_LINES);
    for (const auto& drop : rainDrops) {
        if (drop.life > 0.0f) {
            glVertex3f(drop.x, drop.y, drop.z);
            glVertex3f(drop.x, drop.y - 2.0f, drop.z);
        }
    }
    glEnd();
    
    glDisable(GL_BLEND);
    glEnable(GL_LIGHTING);
}

// Рисование рельефа
void drawTerrain() {
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_LIGHTING);
    
    for (int x = 0; x < TERRAIN_SIZE-1; x++) {
        for (int z = 0; z < TERRAIN_SIZE-1; z++) {
            float worldX1 = (x - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldZ1 = (z - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldX2 = ((x+1) - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldZ2 = ((z+1) - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            
            float h1 = terrainHeight[x][z];
            float h2 = terrainHeight[x+1][z];
            float h3 = terrainHeight[x][z+1];
            float h4 = terrainHeight[x+1][z+1];
            
            // Выбираем текстуру
            if (h1 > 10.0f || h2 > 10.0f || h3 > 10.0f || h4 > 10.0f) {
                glBindTexture(GL_TEXTURE_2D, stoneTexture);
            } else if (h1 > 5.0f || h2 > 5.0f || h3 > 5.0f || h4 > 5.0f) {
                glBindTexture(GL_TEXTURE_2D, grassTexture);
            } else {
                glBindTexture(GL_TEXTURE_2D, groundTexture);
            }
            
            glBegin(GL_TRIANGLES);
                glTexCoord2f(0.0f, 0.0f); glVertex3f(worldX1, h1, worldZ1);
                glTexCoord2f(1.0f, 0.0f); glVertex3f(worldX2, h2, worldZ1);
                glTexCoord2f(0.0f, 1.0f); glVertex3f(worldX1, h3, worldZ2);
                
                glTexCoord2f(1.0f, 0.0f); glVertex3f(worldX2, h2, worldZ1);
                glTexCoord2f(1.0f, 1.0f); glVertex3f(worldX2, h4, worldZ2);
                glTexCoord2f(0.0f, 1.0f); glVertex3f(worldX1, h3, worldZ2);
            glEnd();
        }
    }
    
    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LIGHTING);
}

// Рисование неба
void drawSky() {
    glDisable(GL_LIGHTING);
    glDisable(GL_DEPTH_TEST);
    
    glPushMatrix();
    glTranslatef(cameraX, cameraY, cameraZ);
    
    glBegin(GL_QUAD_STRIP);
    for (int i = 0; i <= 20; i++) {
        float angle = i * M_PI / 20.0f;
        float y = cos(angle);
        float radius = sin(angle) * 100.0f;
        
        float skyIntensity = 0.3f + y * 0.7f;
        glColor3f(currentSky.r * skyIntensity, currentSky.g * skyIntensity, currentSky.b * skyIntensity);
        
        for (int j = 0; j <= 20; j++) {
            float phi = j * 2.0f * M_PI / 20.0f;
            float x = cos(phi) * radius;
            float z = sin(phi) * radius;
            glVertex3f(x, y * 50.0f, z);
        }
    }
    glEnd();
    
    glPopMatrix();
    
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
}

void drawText(float x, float y, const std::string& text) {
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    gluOrtho2D(0, windowWidth, 0, windowHeight);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    glColor3f(1.0f, 1.0f, 1.0f);
    glRasterPos2f(x, y);
    for (char c : text) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
    }

    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
}

void renderScene() {
    updateDayNightCycle();
    updateWaterBodies();
    updateRain();
    updateWaves();
    
    glClearColor(currentSky.r, currentSky.g, currentSky.b, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    // Обновление позиции камеры
    lookAtX = playerX + sin(angleY * M_PI / 180.0f) * cos(angleX * M_PI / 180.0f) * 10.0f;
    lookAtY = playerY + sin(angleX * M_PI / 180.0f) * 10.0f;
    lookAtZ = playerZ - cos(angleY * M_PI / 180.0f) * cos(angleX * M_PI / 180.0f) * 10.0f;

    cameraX = playerX - sin(angleY * M_PI / 180.0f) * 20.0f;
    cameraY = playerY + 10.0f;
    cameraZ = playerZ + cos(angleY * M_PI / 180.0f) * 20.0f;

    gluLookAt(cameraX, cameraY, cameraZ, playerX, playerY, playerZ, 0.0f, 1.0f, 0.0f);

    // Рисуем небо
    drawSky();
    
    // Рисуем рельеф
    drawTerrain();
    
    // Рисуем воду
    drawWater();
    
    // Рисуем дождь
    drawRain();

    // Рисование игрока
    glPushMatrix();
    glTranslatef(playerX, playerY, playerZ);
    glColor3f(0.0f, 0.0f, 1.0f);
    glutSolidCube(1.0f);
    glPopMatrix();

    // HUD
    std::string timeStr = "Time: ";
    if (timeOfDay < 0.25f) timeStr += "Night";
    else if (timeOfDay < 0.5f) timeStr += "Dawn";
    else if (timeOfDay < 0.75f) timeStr += "Day";
    else timeStr += "Sunset";
    
    std::string weatherStr = "Weather: ";
    if (isRaining) {
        weatherStr += "Rain (" + std::to_string((int)(weatherIntensity * 100)) + "%)";
    } else {
        weatherStr += "Clear";
    }
    
    drawText(20, windowHeight - 80, "Health: ==================== 100%");
    drawText(20, windowHeight - 60, "Mana:   ==================== 100%");
    drawText(20, windowHeight - 40, timeStr);
    drawText(20, windowHeight - 20, weatherStr);
    drawText(20, 80, "Water Bodies: " + std::to_string(waterBodies.size()));
    drawText(20, 60, "Waves: " + std::to_string(waves.size()));
    drawText(20, 40, "Rain Drops: " + std::to_string(rainDrops.size()));
    drawText(20, 20, "Controls: WASD - move, Arrows - rotate, T - day/night, R - rain, ESC - exit");

    glutSwapBuffers();
}

void reshape(int w, int h) {
    windowWidth = w;
    windowHeight = h;
    glViewport(0, 0, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(60.0f, (GLfloat)w / (GLfloat)h, 0.1f, 1000.0f);
    glMatrixMode(GL_MODELVIEW);
}

void keyboard(unsigned char key, int x, int y) {
    float moveX = 0.0f, moveZ = 0.0f;
    float radY = angleY * M_PI / 180.0f;

    switch (key) {
        case 'w':
        case 'W':
            moveX = sin(radY) * playerMoveSpeed;
            moveZ = -cos(radY) * playerMoveSpeed;
            break;
        case 's':
        case 'S':
            moveX = -sin(radY) * playerMoveSpeed;
            moveZ = cos(radY) * playerMoveSpeed;
            break;
        case 'a':
        case 'A':
            moveX = -cos(radY) * playerMoveSpeed;
            moveZ = -sin(radY) * playerMoveSpeed;
            break;
        case 'd':
        case 'D':
            moveX = cos(radY) * playerMoveSpeed;
            moveZ = sin(radY) * playerMoveSpeed;
            break;
        case 't':
        case 'T':
            dayNightCycle = !dayNightCycle;
            std::cout << "Day/Night cycle: " << (dayNightCycle ? "ON" : "OFF") << std::endl;
            break;
        case 'r':
        case 'R':
            isRaining = !isRaining;
            if (isRaining) {
                initRain();
                weatherIntensity = 0.5f + dis(gen) * 0.5f;
                windStrength = 0.3f + dis(gen) * 0.7f;
            } else {
                weatherIntensity = 0.0f;
                windStrength = 0.0f;
            }
            std::cout << "Rain: " << (isRaining ? "ON" : "OFF") << std::endl;
            break;
        case 27: // ESC key
            exit(0);
            break;
    }
    playerX += moveX;
    playerZ += moveZ;
    glutPostRedisplay();
}

void specialKeys(int key, int x, int y) {
    switch (key) {
        case GLUT_KEY_LEFT:
            angleY -= playerRotateSpeed;
            break;
        case GLUT_KEY_RIGHT:
            angleY += playerRotateSpeed;
            break;
        case GLUT_KEY_UP:
            angleX += playerRotateSpeed;
            if (angleX > 89.0f) angleX = 89.0f;
            break;
        case GLUT_KEY_DOWN:
            angleX -= playerRotateSpeed;
            if (angleX < -89.0f) angleX = -89.0f;
            break;
    }
    glutPostRedisplay();
}

void mouse(int button, int state, int x, int y) {
    if (button == GLUT_LEFT_BUTTON) {
        if (state == GLUT_DOWN) {
            std::cout << "Interaction at: " << x << ", " << y << std::endl;
        }
    }
}

void motion(int x, int y) {
    // Вращение камеры мышью
}

// Загрузка текстур
GLuint loadTexture(const std::string& filename) {
    GLuint textureID;
    glGenTextures(1, &textureID);
    glBindTexture(GL_TEXTURE_2D, textureID);
    
    const int texWidth = 256;
    const int texHeight = 256;
    unsigned char* textureData = new unsigned char[texWidth * texHeight * 3];
    
    if (filename.find("water") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 40);
            textureData[i+1] = 50 + (rand() % 80);
            textureData[i+2] = 150 + (rand() % 100);
        }
    } else if (filename.find("grass") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 60);
            textureData[i+1] = 100 + (rand() % 100);
            textureData[i+2] = 20 + (rand() % 40);
        }
    } else if (filename.find("stone") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            int gray = 80 + (rand() % 60);
            textureData[i] = gray;
            textureData[i+1] = gray;
            textureData[i+2] = gray;
        }
    } else {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 139 + (rand() % 60);
            textureData[i+1] = 69 + (rand() % 40);
            textureData[i+2] = 19 + (rand() % 30);
        }
    }
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, texWidth, texHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, textureData);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    
    delete[] textureData;
    return textureID;
}

void initGL() {
    glClearColor(currentSky.r, currentSky.g, currentSky.b, 1.0f);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_COLOR_MATERIAL);

    GLfloat light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
    glLightfv(GL_LIGHT0, GL_POSITION, light_position);
    
    // Загружаем текстуры
    std::cout << "Loading water system textures..." << std::endl;
    groundTexture = loadTexture("ground_earth");
    grassTexture = loadTexture("grass_field");
    stoneTexture = loadTexture("stone_rock");
    waterTexture = loadTexture("water_blue");
    waterNormalTexture = loadTexture("water_normal");
    waterFoamTexture = loadTexture("water_foam");
    
    // Инициализируем системы
    initWaterBodies();
    initRain();
    initWaves();
    generateTerrain();
    
    std::cout << "Water system initialized with " << waterBodies.size() << " water bodies" << std::endl;
}

void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Water System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- Advanced water system with oceans, rivers, and lakes" << std::endl;
    std::cout << "- Realistic wave simulation with physics" << std::endl;
    std::cout << "- Rain system with splash effects" << std::endl;
    std::cout << "- Dynamic water colors based on time of day" << std::endl;
    std::cout << "- Wind effects on water and rain" << std::endl;
    
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(windowWidth, windowHeight);
    glutCreateWindow(windowTitle);

    initGL();

    glutDisplayFunc(renderScene);
    glutReshapeFunc(reshape);
    glutKeyboardFunc(keyboard);
    glutSpecialFunc(specialKeys);
    glutMouseFunc(mouse);
    glutMotionFunc(motion);
    glutTimerFunc(16, update, 0);

    std::cout << "Game started! Enhanced controls:" << std::endl;
    std::cout << "- WASD: Movement" << std::endl;
    std::cout << "- Arrow keys: Camera rotation" << std::endl;
    std::cout << "- T: Toggle day/night cycle" << std::endl;
    std::cout << "- R: Toggle rain and wind" << std::endl;
    std::cout << "- ESC: Exit" << std::endl;

    glutMainLoop();

    return 0;
}

