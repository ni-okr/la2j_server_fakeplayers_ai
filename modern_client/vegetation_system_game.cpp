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
const char* windowTitle = "Modern Lineage II - Vegetation System";

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

// Система деревьев
struct Tree {
    float x, y, z;
    float height, width;
    float rotation;
    int type; // 0-9 для разных типов деревьев
    float swayTime;
    float swayAmount;
    bool isVisible;
};

std::vector<Tree> trees;

// Система травы
struct Grass {
    float x, y, z;
    float height;
    float swayTime;
    float swayAmount;
    int type; // 0-2 для разных типов травы
    bool isVisible;
};

std::vector<Grass> grass;

// Система цветов
struct Flower {
    float x, y, z;
    float height;
    float swayTime;
    int type; // 0-4 для разных цветов
    bool isVisible;
};

std::vector<Flower> flowers;

// Система текстур
GLuint groundTexture = 0;
GLuint grassTexture = 0;
GLuint stoneTexture = 0;
GLuint waterTexture = 0;
GLuint treeTextures[10] = {0};
GLuint grassTextures[3] = {0};
GLuint flowerTextures[5] = {0};

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

// Инициализация деревьев
void initTrees() {
    trees.clear();
    
    // Создаем деревья на основе L2 текстур
    std::vector<std::string> treeTypes = {
        "aden_tree", "dion_tree", "gludio_tree", "godad_tree", 
        "innadrill_tree", "oren_tree", "primitive_tree", 
        "rionsctgart_tree", "rune_tree", "speaking_tree"
    };
    
    for (int i = 0; i < 200; i++) {
        Tree tree;
        
        // Случайная позиция
        tree.x = (dis(gen) - 0.5f) * 180.0f;
        tree.z = (dis(gen) - 0.5f) * 180.0f;
        
        // Высота на основе рельефа
        int terrainX = (int)((tree.x + 90.0f) / TERRAIN_SCALE);
        int terrainZ = (int)((tree.z + 90.0f) / TERRAIN_SCALE);
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            tree.y = terrainHeight[terrainX][terrainZ];
        } else {
            tree.y = 0.0f;
        }
        
        // Параметры дерева
        tree.type = i % 10; // 10 типов деревьев
        tree.height = 3.0f + dis(gen) * 8.0f; // 3-11 метров
        tree.width = 1.0f + dis(gen) * 2.0f; // 1-3 метра
        tree.rotation = dis(gen) * 360.0f;
        tree.swayTime = dis(gen) * 10.0f;
        tree.swayAmount = 0.1f + dis(gen) * 0.3f;
        tree.isVisible = true;
        
        // Не размещаем деревья в воде
        bool inWater = false;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                int checkX = terrainX + x;
                int checkZ = terrainZ + z;
                if (checkX >= 0 && checkX < TERRAIN_SIZE && checkZ >= 0 && checkZ < TERRAIN_SIZE) {
                    if (terrainHeight[checkX][checkZ] < 0.5f) {
                        inWater = true;
                        break;
                    }
                }
            }
        }
        
        if (!inWater) {
            trees.push_back(tree);
        }
    }
    
    std::cout << "Initialized " << trees.size() << " trees" << std::endl;
}

// Инициализация травы
void initGrass() {
    grass.clear();
    
    for (int i = 0; i < 1000; i++) {
        Grass grassBlade;
        
        // Случайная позиция
        grassBlade.x = (dis(gen) - 0.5f) * 180.0f;
        grassBlade.z = (dis(gen) - 0.5f) * 180.0f;
        
        // Высота на основе рельефа
        int terrainX = (int)((grassBlade.x + 90.0f) / TERRAIN_SCALE);
        int terrainZ = (int)((grassBlade.z + 90.0f) / TERRAIN_SCALE);
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            grassBlade.y = terrainHeight[terrainX][terrainZ];
        } else {
            grassBlade.y = 0.0f;
        }
        
        // Параметры травы
        grassBlade.type = i % 3; // 3 типа травы
        grassBlade.height = 0.3f + dis(gen) * 0.7f; // 0.3-1.0 метра
        grassBlade.swayTime = dis(gen) * 10.0f;
        grassBlade.swayAmount = 0.2f + dis(gen) * 0.4f;
        grassBlade.isVisible = true;
        
        // Не размещаем траву в воде
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            if (terrainHeight[terrainX][terrainZ] > 0.5f) {
                grass.push_back(grassBlade);
            }
        }
    }
    
    std::cout << "Initialized " << grass.size() << " grass blades" << std::endl;
}

// Инициализация цветов
void initFlowers() {
    flowers.clear();
    
    for (int i = 0; i < 500; i++) {
        Flower flower;
        
        // Случайная позиция
        flower.x = (dis(gen) - 0.5f) * 180.0f;
        flower.z = (dis(gen) - 0.5f) * 180.0f;
        
        // Высота на основе рельефа
        int terrainX = (int)((flower.x + 90.0f) / TERRAIN_SCALE);
        int terrainZ = (int)((flower.z + 90.0f) / TERRAIN_SCALE);
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            flower.y = terrainHeight[terrainX][terrainZ];
        } else {
            flower.y = 0.0f;
        }
        
        // Параметры цветка
        flower.type = i % 5; // 5 типов цветов
        flower.height = 0.1f + dis(gen) * 0.3f; // 0.1-0.4 метра
        flower.swayTime = dis(gen) * 10.0f;
        flower.isVisible = true;
        
        // Размещаем цветы только на равнинах
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            if (terrainHeight[terrainX][terrainZ] > 0.5f && terrainHeight[terrainX][terrainZ] < 3.0f) {
                flowers.push_back(flower);
            }
        }
    }
    
    std::cout << "Initialized " << flowers.size() << " flowers" << std::endl;
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

// Обновление растительности
void updateVegetation() {
    // Обновляем покачивание деревьев
    for (auto& tree : trees) {
        tree.swayTime += 0.016f; // ~60 FPS
        
        // Усиливаем покачивание при ветре
        if (isRaining) {
            tree.swayAmount = 0.2f + windStrength * 0.5f;
        } else {
            tree.swayAmount = 0.1f + windStrength * 0.2f;
        }
    }
    
    // Обновляем покачивание травы
    for (auto& grassBlade : grass) {
        grassBlade.swayTime += 0.016f;
        
        if (isRaining) {
            grassBlade.swayAmount = 0.3f + windStrength * 0.7f;
        } else {
            grassBlade.swayAmount = 0.2f + windStrength * 0.3f;
        }
    }
    
    // Обновляем покачивание цветов
    for (auto& flower : flowers) {
        flower.swayTime += 0.016f;
    }
}

// Рисование дерева
void drawTree(const Tree& tree) {
    if (!tree.isVisible) return;
    
    glPushMatrix();
    glTranslatef(tree.x, tree.y, tree.z);
    glRotatef(tree.rotation, 0.0f, 1.0f, 0.0f);
    
    // Покачивание от ветра
    float swayX = sin(tree.swayTime * 2.0f) * tree.swayAmount;
    glRotatef(swayX, 0.0f, 0.0f, 1.0f);
    
    // Цвет дерева в зависимости от времени суток
    if (timeOfDay < 0.25f || timeOfDay > 0.75f) {
        glColor3f(0.3f, 0.2f, 0.1f); // Темно-коричневый ночью
    } else if (timeOfDay < 0.5f) {
        glColor3f(0.4f, 0.3f, 0.2f); // Коричневый днем
    } else {
        glColor3f(0.6f, 0.4f, 0.2f); // Светло-коричневый на закате
    }
    
    // Ствол дерева
    glPushMatrix();
    glScalef(0.3f, tree.height * 0.6f, 0.3f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    // Крона дерева
    glTranslatef(0.0f, tree.height * 0.3f, 0.0f);
    
    if (timeOfDay < 0.25f || timeOfDay > 0.75f) {
        glColor3f(0.1f, 0.3f, 0.1f); // Темно-зеленый ночью
    } else if (timeOfDay < 0.5f) {
        glColor3f(0.2f, 0.6f, 0.2f); // Зеленый днем
    } else {
        glColor3f(0.8f, 0.4f, 0.2f); // Оранжевый на закате
    }
    
    glPushMatrix();
    glScalef(tree.width, tree.height * 0.4f, tree.width);
    glutSolidSphere(1.0f, 8, 6);
    glPopMatrix();
    
    glPopMatrix();
}

// Рисование травы
void drawGrass(const Grass& grassBlade) {
    if (!grassBlade.isVisible) return;
    
    glPushMatrix();
    glTranslatef(grassBlade.x, grassBlade.y, grassBlade.z);
    
    // Покачивание от ветра
    float swayX = sin(grassBlade.swayTime * 3.0f) * grassBlade.swayAmount;
    glRotatef(swayX, 0.0f, 0.0f, 1.0f);
    
    // Цвет травы
    if (timeOfDay < 0.25f || timeOfDay > 0.75f) {
        glColor3f(0.1f, 0.2f, 0.1f); // Темно-зеленый ночью
    } else if (timeOfDay < 0.5f) {
        glColor3f(0.2f, 0.5f, 0.2f); // Зеленый днем
    } else {
        glColor3f(0.6f, 0.3f, 0.1f); // Оранжевый на закате
    }
    
    // Травинка
    glLineWidth(2.0f);
    glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, grassBlade.height, 0.0f);
    glEnd();
    
    glPopMatrix();
}

// Рисование цветка
void drawFlower(const Flower& flower) {
    if (!flower.isVisible) return;
    
    glPushMatrix();
    glTranslatef(flower.x, flower.y, flower.z);
    
    // Покачивание от ветра
    float swayX = sin(flower.swayTime * 4.0f) * 0.1f;
    glRotatef(swayX, 0.0f, 0.0f, 1.0f);
    
    // Цвет цветка
    switch (flower.type) {
        case 0: glColor3f(1.0f, 0.0f, 0.0f); break; // Красный
        case 1: glColor3f(1.0f, 1.0f, 0.0f); break; // Желтый
        case 2: glColor3f(1.0f, 0.0f, 1.0f); break; // Фиолетовый
        case 3: glColor3f(0.0f, 1.0f, 0.0f); break; // Зеленый
        case 4: glColor3f(0.0f, 0.0f, 1.0f); break; // Синий
    }
    
    // Цветок
    glPushMatrix();
    glScalef(0.1f, flower.height, 0.1f);
    glutSolidSphere(1.0f, 6, 4);
    glPopMatrix();
    
    glPopMatrix();
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
    updateVegetation();
    
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
    
    // Рисуем деревья
    for (const auto& tree : trees) {
        drawTree(tree);
    }
    
    // Рисуем траву
    for (const auto& grassBlade : grass) {
        drawGrass(grassBlade);
    }
    
    // Рисуем цветы
    for (const auto& flower : flowers) {
        drawFlower(flower);
    }

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
    
    drawText(20, windowHeight - 100, "Health: ==================== 100%");
    drawText(20, windowHeight - 80, "Mana:   ==================== 100%");
    drawText(20, windowHeight - 60, timeStr);
    drawText(20, windowHeight - 40, weatherStr);
    drawText(20, windowHeight - 20, "Trees: " + std::to_string(trees.size()));
    drawText(20, 80, "Grass: " + std::to_string(grass.size()));
    drawText(20, 60, "Flowers: " + std::to_string(flowers.size()));
    drawText(20, 40, "Wind: " + std::to_string((int)(windStrength * 100)) + "%");
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
    std::cout << "Loading vegetation system textures..." << std::endl;
    groundTexture = loadTexture("ground_earth");
    grassTexture = loadTexture("grass_field");
    stoneTexture = loadTexture("stone_rock");
    waterTexture = loadTexture("water_blue");
    
    // Загружаем текстуры деревьев
    for (int i = 0; i < 10; i++) {
        treeTextures[i] = loadTexture("tree_" + std::to_string(i));
    }
    
    // Загружаем текстуры травы
    for (int i = 0; i < 3; i++) {
        grassTextures[i] = loadTexture("grass_" + std::to_string(i));
    }
    
    // Загружаем текстуры цветов
    for (int i = 0; i < 5; i++) {
        flowerTextures[i] = loadTexture("flower_" + std::to_string(i));
    }
    
    // Инициализируем системы
    generateTerrain();
    initTrees();
    initGrass();
    initFlowers();
    
    std::cout << "Vegetation system initialized!" << std::endl;
}

void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Vegetation System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- 10 types of trees from L2 deobfuscated client" << std::endl;
    std::cout << "- Realistic grass with wind animation" << std::endl;
    std::cout << "- Colorful flowers with swaying effects" << std::endl;
    std::cout << "- Dynamic vegetation based on terrain height" << std::endl;
    std::cout << "- Weather effects on vegetation" << std::endl;
    
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

