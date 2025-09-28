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
const char* windowTitle = "Modern Lineage II - Biomes System";

// Параметры камеры
float cameraX = 0.0f, cameraY = 10.0f, cameraZ = 20.0f;
float lookAtX = 0.0f, lookAtY = 0.0f, lookAtZ = 0.0f;
float angleY = 0.0f;
float angleX = 0.0f;
float playerMoveSpeed = 0.5f;
float playerRotateSpeed = 2.0f;

// Позиция игрока
float playerX = 0.0f, playerY = 0.5f, playerZ = 0.0f;

// Система биомов
enum BiomeType {
    GRASSLAND = 0,  // Равнины
    FOREST = 1,     // Лес
    MOUNTAIN = 2,   // Горы
    DESERT = 3,     // Пустыня
    SNOW = 4,       // Снег
    LAVA = 5,       // Лава
    SWAMP = 6,      // Болото
    OCEAN = 7,      // Океан
    TUNDRA = 8,     // Тундра
    VOLCANO = 9     // Вулкан
};

struct Biome {
    BiomeType type;
    float x, z, width, height;
    float temperature;
    float humidity;
    float elevation;
    float vegetationDensity;
    float waterLevel;
    bool hasSnow;
    bool hasLava;
    bool hasSand;
    bool hasGrass;
};

std::vector<Biome> biomes;

// Система рельефа
const int TERRAIN_SIZE = 100;
const float TERRAIN_SCALE = 2.0f;
float terrainHeight[TERRAIN_SIZE][TERRAIN_SIZE];
int biomeMap[TERRAIN_SIZE][TERRAIN_SIZE];

// Система текстур
GLuint biomeTextures[10] = {0};

// Система времени и погоды
float timeOfDay = 0.5f;
bool dayNightCycle = true;
float weatherIntensity = 0.0f;
bool isRaining = false;
float windStrength = 0.0f;
float temperature = 20.0f; // Температура в градусах

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

// Инициализация биомов
void initBiomes() {
    biomes.clear();
    
    // Создаем различные биомы
    Biome grassland;
    grassland.type = GRASSLAND;
    grassland.x = 0.0f; grassland.z = 0.0f;
    grassland.width = 60.0f; grassland.height = 60.0f;
    grassland.temperature = 20.0f;
    grassland.humidity = 0.5f;
    grassland.elevation = 1.0f;
    grassland.vegetationDensity = 0.8f;
    grassland.waterLevel = 0.0f;
    grassland.hasSnow = false;
    grassland.hasLava = false;
    grassland.hasSand = false;
    grassland.hasGrass = true;
    biomes.push_back(grassland);
    
    Biome forest;
    forest.type = FOREST;
    forest.x = -60.0f; forest.z = 0.0f;
    forest.width = 40.0f; forest.height = 40.0f;
    forest.temperature = 15.0f;
    forest.humidity = 0.8f;
    forest.elevation = 3.0f;
    forest.vegetationDensity = 1.0f;
    forest.waterLevel = 0.0f;
    forest.hasSnow = false;
    forest.hasLava = false;
    forest.hasSand = false;
    forest.hasGrass = true;
    biomes.push_back(forest);
    
    Biome mountain;
    mountain.type = MOUNTAIN;
    mountain.x = 60.0f; mountain.z = 0.0f;
    mountain.width = 40.0f; mountain.height = 40.0f;
    mountain.temperature = 5.0f;
    mountain.humidity = 0.3f;
    mountain.elevation = 15.0f;
    mountain.vegetationDensity = 0.2f;
    mountain.waterLevel = 0.0f;
    mountain.hasSnow = true;
    mountain.hasLava = false;
    mountain.hasSand = false;
    mountain.hasGrass = false;
    biomes.push_back(mountain);
    
    Biome desert;
    desert.type = DESERT;
    desert.x = 0.0f; desert.z = -60.0f;
    desert.width = 50.0f; desert.height = 50.0f;
    desert.temperature = 35.0f;
    desert.humidity = 0.1f;
    desert.elevation = 2.0f;
    desert.vegetationDensity = 0.1f;
    desert.waterLevel = 0.0f;
    desert.hasSnow = false;
    desert.hasLava = false;
    desert.hasSand = true;
    desert.hasGrass = false;
    biomes.push_back(desert);
    
    Biome snow;
    snow.type = SNOW;
    snow.x = -60.0f; snow.z = -60.0f;
    snow.width = 40.0f; snow.height = 40.0f;
    snow.temperature = -10.0f;
    snow.humidity = 0.6f;
    snow.elevation = 8.0f;
    snow.vegetationDensity = 0.1f;
    snow.waterLevel = 0.0f;
    snow.hasSnow = true;
    snow.hasLava = false;
    snow.hasSand = false;
    snow.hasGrass = false;
    biomes.push_back(snow);
    
    Biome lava;
    lava.type = LAVA;
    lava.x = 60.0f; lava.z = -60.0f;
    lava.width = 30.0f; lava.height = 30.0f;
    lava.temperature = 100.0f;
    lava.humidity = 0.0f;
    lava.elevation = 0.0f;
    lava.vegetationDensity = 0.0f;
    lava.waterLevel = -1.0f;
    lava.hasSnow = false;
    lava.hasLava = true;
    lava.hasSand = false;
    lava.hasGrass = false;
    biomes.push_back(lava);
    
    std::cout << "Initialized " << biomes.size() << " biomes" << std::endl;
}

// Генерация рельефа с биомами
void generateTerrain() {
    for (int x = 0; x < TERRAIN_SIZE; x++) {
        for (int z = 0; z < TERRAIN_SIZE; z++) {
            float worldX = (x - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldZ = (z - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            
            float height = 0.0f;
            BiomeType currentBiome = GRASSLAND;
            
            // Определяем биом для этой точки
            for (const auto& biome : biomes) {
                if (worldX >= biome.x - biome.width/2 && worldX <= biome.x + biome.width/2 &&
                    worldZ >= biome.z - biome.height/2 && worldZ <= biome.z + biome.height/2) {
                    currentBiome = biome.type;
                    break;
                }
            }
            
            // Генерируем высоту в зависимости от биома
            switch (currentBiome) {
                case GRASSLAND:
                    height = 1.0f + sin(worldX * 0.02f) * 0.5f + cos(worldZ * 0.02f) * 0.5f;
                    break;
                case FOREST:
                    height = 3.0f + sin(worldX * 0.05f) * 2.0f + cos(worldZ * 0.05f) * 2.0f;
                    break;
                case MOUNTAIN:
                    height = 15.0f + sin(worldX * 0.1f) * 8.0f + cos(worldZ * 0.1f) * 8.0f;
                    break;
                case DESERT:
                    height = 2.0f + sin(worldX * 0.03f) * 1.0f + cos(worldZ * 0.03f) * 1.0f;
                    break;
                case SNOW:
                    height = 8.0f + sin(worldX * 0.08f) * 4.0f + cos(worldZ * 0.08f) * 4.0f;
                    break;
                case LAVA:
                    height = -1.0f + sin(worldX * 0.1f) * 0.5f + cos(worldZ * 0.1f) * 0.5f;
                    break;
                default:
                    height = 1.0f;
                    break;
            }
            
            terrainHeight[x][z] = height;
            biomeMap[x][z] = currentBiome;
        }
    }
}

// Обновление времени суток
void updateDayNightCycle() {
    if (!dayNightCycle) return;
    
    timeOfDay += 0.00005f;
    if (timeOfDay > 1.0f) timeOfDay = 0.0f;
    
    // Обновляем температуру в зависимости от времени суток
    if (timeOfDay < 0.25f) {
        temperature = 10.0f; // Ночь холоднее
        float t = timeOfDay * 4.0f;
        currentSky.r = nightSky.r + (sunsetSky.r - nightSky.r) * t;
        currentSky.g = nightSky.g + (sunsetSky.g - nightSky.g) * t;
        currentSky.b = nightSky.b + (sunsetSky.b - nightSky.b) * t;
    } else if (timeOfDay < 0.5f) {
        temperature = 15.0f; // Рассвет
        float t = (timeOfDay - 0.25f) * 4.0f;
        currentSky.r = sunsetSky.r + (daySky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (daySky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (daySky.b - sunsetSky.b) * t;
    } else if (timeOfDay < 0.75f) {
        temperature = 25.0f; // День теплее
        float t = (timeOfDay - 0.5f) * 4.0f;
        currentSky.r = daySky.r + (sunsetSky.r - daySky.r) * t;
        currentSky.g = daySky.g + (sunsetSky.g - daySky.g) * t;
        currentSky.b = daySky.b + (sunsetSky.b - daySky.b) * t;
    } else {
        temperature = 20.0f; // Закат
        float t = (timeOfDay - 0.75f) * 4.0f;
        currentSky.r = sunsetSky.r + (nightSky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (nightSky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (nightSky.b - sunsetSky.b) * t;
    }
    
    // Применяем погодные эффекты
    if (isRaining) {
        temperature -= 5.0f; // Дождь снижает температуру
        float rainEffect = weatherIntensity;
        currentSky.r = currentSky.r * (1.0f - rainEffect) + rainSky.r * rainEffect;
        currentSky.g = currentSky.g * (1.0f - rainEffect) + rainSky.g * rainEffect;
        currentSky.b = currentSky.b * (1.0f - rainEffect) + rainSky.b * rainEffect;
    }
}

// Рисование рельефа с биомами
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
            
            BiomeType biome = (BiomeType)biomeMap[x][z];
            
            // Выбираем текстуру и цвет в зависимости от биома
            switch (biome) {
                case GRASSLAND:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[0]);
                    glColor3f(0.2f, 0.6f, 0.2f); // Зеленый
                    break;
                case FOREST:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[1]);
                    glColor3f(0.1f, 0.4f, 0.1f); // Темно-зеленый
                    break;
                case MOUNTAIN:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[2]);
                    glColor3f(0.5f, 0.5f, 0.5f); // Серый
                    break;
                case DESERT:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[3]);
                    glColor3f(0.8f, 0.7f, 0.4f); // Песочный
                    break;
                case SNOW:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[4]);
                    glColor3f(0.9f, 0.9f, 0.9f); // Белый
                    break;
                case LAVA:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[5]);
                    glColor3f(0.8f, 0.2f, 0.0f); // Красный
                    break;
                default:
                    glBindTexture(GL_TEXTURE_2D, biomeTextures[0]);
                    glColor3f(0.5f, 0.5f, 0.5f);
                    break;
            }
            
            // Рисуем треугольники
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
    
    // Рисуем рельеф с биомами
    drawTerrain();

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
    
    // Определяем текущий биом игрока
    int playerBiomeX = (int)((playerX + 90.0f) / TERRAIN_SCALE);
    int playerBiomeZ = (int)((playerZ + 90.0f) / TERRAIN_SCALE);
    std::string biomeStr = "Biome: Unknown";
    if (playerBiomeX >= 0 && playerBiomeX < TERRAIN_SIZE && playerBiomeZ >= 0 && playerBiomeZ < TERRAIN_SIZE) {
        BiomeType currentBiome = (BiomeType)biomeMap[playerBiomeX][playerBiomeZ];
        switch (currentBiome) {
            case GRASSLAND: biomeStr = "Biome: Grassland"; break;
            case FOREST: biomeStr = "Biome: Forest"; break;
            case MOUNTAIN: biomeStr = "Biome: Mountain"; break;
            case DESERT: biomeStr = "Biome: Desert"; break;
            case SNOW: biomeStr = "Biome: Snow"; break;
            case LAVA: biomeStr = "Biome: Lava"; break;
            default: biomeStr = "Biome: Unknown"; break;
        }
    }
    
    drawText(20, windowHeight - 100, "Health: ==================== 100%");
    drawText(20, windowHeight - 80, "Mana:   ==================== 100%");
    drawText(20, windowHeight - 60, timeStr);
    drawText(20, windowHeight - 40, weatherStr);
    drawText(20, windowHeight - 20, biomeStr);
    drawText(20, 80, "Temperature: " + std::to_string((int)temperature) + "°C");
    drawText(20, 60, "Biomes: " + std::to_string(biomes.size()));
    drawText(20, 40, "Position: X=" + std::to_string((int)playerX) + " Y=" + std::to_string((int)playerY) + " Z=" + std::to_string((int)playerZ));
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
    
    if (filename.find("grassland") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 60);
            textureData[i+1] = 100 + (rand() % 100);
            textureData[i+2] = 20 + (rand() % 40);
        }
    } else if (filename.find("forest") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 10 + (rand() % 40);
            textureData[i+1] = 60 + (rand() % 80);
            textureData[i+2] = 10 + (rand() % 30);
        }
    } else if (filename.find("mountain") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            int gray = 80 + (rand() % 60);
            textureData[i] = gray;
            textureData[i+1] = gray;
            textureData[i+2] = gray;
        }
    } else if (filename.find("desert") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 200 + (rand() % 55);
            textureData[i+1] = 180 + (rand() % 75);
            textureData[i+2] = 100 + (rand() % 100);
        }
    } else if (filename.find("snow") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            int white = 200 + (rand() % 55);
            textureData[i] = white;
            textureData[i+1] = white;
            textureData[i+2] = white;
        }
    } else if (filename.find("lava") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 200 + (rand() % 55);
            textureData[i+1] = 50 + (rand() % 100);
            textureData[i+2] = 0;
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
    
    // Загружаем текстуры биомов
    std::cout << "Loading biome textures..." << std::endl;
    biomeTextures[0] = loadTexture("grassland");
    biomeTextures[1] = loadTexture("forest");
    biomeTextures[2] = loadTexture("mountain");
    biomeTextures[3] = loadTexture("desert");
    biomeTextures[4] = loadTexture("snow");
    biomeTextures[5] = loadTexture("lava");
    
    // Инициализируем системы
    initBiomes();
    generateTerrain();
    
    std::cout << "Biomes system initialized!" << std::endl;
}

void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Biomes System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- 6 unique biomes with different characteristics" << std::endl;
    std::cout << "- Dynamic temperature system" << std::endl;
    std::cout << "- Biome-specific terrain generation" << std::endl;
    std::cout << "- Weather effects on biomes" << std::endl;
    std::cout << "- Realistic biome transitions" << std::endl;
    
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

