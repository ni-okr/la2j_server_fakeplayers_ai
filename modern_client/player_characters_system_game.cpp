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
const char* windowTitle = "Modern Lineage II - Player Characters System";

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

// Система игровых персонажей
enum CharacterRace {
    HUMAN = 0,
    ELF = 1,
    DWARF = 2,
    ORC = 3,
    DARKELF = 4
};

enum CharacterGender {
    MALE = 0,
    FEMALE = 1
};

enum CharacterClass {
    WARRIOR = 0,
    MAGE = 1,
    ARCHER = 2,
    PRIEST = 3,
    ROGUE = 4
};

struct Character {
    float x, y, z;
    float rotation;
    CharacterRace race;
    CharacterGender gender;
    CharacterClass classType;
    int level;
    float health;
    float maxHealth;
    float mana;
    float maxMana;
    float experience;
    float maxExperience;
    std::string name;
    float scale;
    float animationTime;
    bool isMoving;
    bool isAttacking;
    bool isCasting;
    float targetX, targetZ;
    float lastUpdateTime;
};

std::vector<Character> characters;

// Система текстур
GLuint characterTextures[5] = {0}; // По одной текстуре на расу

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

// Инициализация персонажей
void initCharacters() {
    characters.clear();
    
    // Создаем персонажей разных рас и классов
    std::vector<std::string> humanNames = {"Aragorn", "Gandalf", "Legolas", "Gimli", "Frodo"};
    std::vector<std::string> elfNames = {"Elrond", "Galadriel", "Thranduil", "Celeborn", "Arwen"};
    std::vector<std::string> dwarfNames = {"Thorin", "Balin", "Dwalin", "Fili", "Kili"};
    std::vector<std::string> orcNames = {"Azog", "Bolg", "Lurtz", "Gothmog", "Grishnakh"};
    std::vector<std::string> darkelfNames = {"Drizzt", "Jarlaxle", "Artemis", "Entreri", "Catti-brie"};
    
    for (int i = 0; i < 25; i++) {
        Character character;
        
        // Случайная позиция
        character.x = (dis(gen) - 0.5f) * 180.0f;
        character.z = (dis(gen) - 0.5f) * 180.0f;
        
        // Высота на основе рельефа
        int terrainX = (int)((character.x + 90.0f) / TERRAIN_SCALE);
        int terrainZ = (int)((character.z + 90.0f) / TERRAIN_SCALE);
        if (terrainX >= 0 && terrainX < TERRAIN_SIZE && terrainZ >= 0 && terrainZ < TERRAIN_SIZE) {
            character.y = terrainHeight[terrainX][terrainZ];
        } else {
            character.y = 0.0f;
        }
        
        // Параметры персонажа
        character.race = (CharacterRace)(i % 5); // 5 рас
        character.gender = (CharacterGender)(i % 2); // 2 пола
        character.classType = (CharacterClass)(i % 5); // 5 классов
        character.level = 1 + (int)(dis(gen) * 20);
        character.health = 100.0f + character.level * 10.0f;
        character.maxHealth = character.health;
        character.mana = 50.0f + character.level * 5.0f;
        character.maxMana = character.mana;
        character.experience = dis(gen) * 1000.0f;
        character.maxExperience = 1000.0f + character.level * 100.0f;
        character.scale = 0.8f + dis(gen) * 0.4f;
        character.rotation = dis(gen) * 360.0f;
        character.animationTime = 0.0f;
        character.isMoving = false;
        character.isAttacking = false;
        character.isCasting = false;
        character.targetX = character.x;
        character.targetZ = character.z;
        character.lastUpdateTime = 0.0f;
        
        // Выбираем имя в зависимости от расы
        switch (character.race) {
            case HUMAN:
                character.name = humanNames[i % humanNames.size()];
                break;
            case ELF:
                character.name = elfNames[i % elfNames.size()];
                break;
            case DWARF:
                character.name = dwarfNames[i % dwarfNames.size()];
                break;
            case ORC:
                character.name = orcNames[i % orcNames.size()];
                break;
            case DARKELF:
                character.name = darkelfNames[i % darkelfNames.size()];
                break;
        }
        
        characters.push_back(character);
    }
    
    std::cout << "Initialized " << characters.size() << " characters" << std::endl;
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

// Обновление персонажей
void updateCharacters() {
    for (auto& character : characters) {
        character.animationTime += 0.016f; // ~60 FPS
        character.lastUpdateTime += 0.016f;
        
        // Простое ИИ поведение
        if (character.lastUpdateTime > 3.0f) { // Обновляем каждые 3 секунды
            character.lastUpdateTime = 0.0f;
            
            if (dis(gen) < 0.3f) {
                // Случайное движение
                character.isMoving = true;
                character.targetX = character.x + (dis(gen) - 0.5f) * 20.0f;
                character.targetZ = character.z + (dis(gen) - 0.5f) * 20.0f;
            } else if (dis(gen) < 0.1f) {
                // Случайная атака
                character.isAttacking = true;
                character.animationTime = 0.0f;
            } else if (dis(gen) < 0.05f) {
                // Случайное заклинание
                character.isCasting = true;
                character.animationTime = 0.0f;
            } else {
                character.isMoving = false;
                character.isAttacking = false;
                character.isCasting = false;
            }
        }
        
        // Движение к цели
        if (character.isMoving) {
            float dx = character.targetX - character.x;
            float dz = character.targetZ - character.z;
            float distance = sqrt(dx * dx + dz * dz);
            
            if (distance > 1.0f) {
                float moveSpeed = 0.5f * 0.016f; // ~60 FPS
                character.x += (dx / distance) * moveSpeed;
                character.z += (dz / distance) * moveSpeed;
                character.rotation = atan2(dx, dz) * 180.0f / M_PI;
            } else {
                character.isMoving = false;
            }
        }
        
        // Сброс анимаций
        if (character.isAttacking && character.animationTime > 1.0f) {
            character.isAttacking = false;
        }
        if (character.isCasting && character.animationTime > 2.0f) {
            character.isCasting = false;
        }
    }
}

// Рисование персонажа
void drawCharacter(const Character& character) {
    glPushMatrix();
    glTranslatef(character.x, character.y, character.z);
    glRotatef(character.rotation, 0.0f, 1.0f, 0.0f);
    glScalef(character.scale, character.scale, character.scale);
    
    // Анимация
    float animOffset = 0.0f;
    if (character.isMoving) {
        animOffset = sin(character.animationTime * 4.0f) * 0.1f;
    } else if (character.isAttacking) {
        animOffset = sin(character.animationTime * 8.0f) * 0.2f;
    } else if (character.isCasting) {
        animOffset = sin(character.animationTime * 2.0f) * 0.15f;
    }
    
    glTranslatef(0.0f, animOffset, 0.0f);
    
    // Цвет в зависимости от расы
    switch (character.race) {
        case HUMAN:
            glColor3f(0.8f, 0.6f, 0.4f); // Кожа
            break;
        case ELF:
            glColor3f(0.6f, 0.8f, 0.6f); // Светло-зеленый
            break;
        case DWARF:
            glColor3f(0.6f, 0.4f, 0.2f); // Коричневый
            break;
        case ORC:
            glColor3f(0.4f, 0.6f, 0.4f); // Зеленый
            break;
        case DARKELF:
            glColor3f(0.4f, 0.2f, 0.6f); // Фиолетовый
            break;
    }
    
    // Рисуем тело персонажа
    // Голова
    glPushMatrix();
    glTranslatef(0.0f, 1.5f, 0.0f);
    glutSolidSphere(0.3f, 8, 6);
    glPopMatrix();
    
    // Тело
    glPushMatrix();
    glTranslatef(0.0f, 0.8f, 0.0f);
    glScalef(0.4f, 0.8f, 0.2f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    // Руки
    glPushMatrix();
    glTranslatef(-0.4f, 0.8f, 0.0f);
    glScalef(0.2f, 0.6f, 0.2f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    glPushMatrix();
    glTranslatef(0.4f, 0.8f, 0.0f);
    glScalef(0.2f, 0.6f, 0.2f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    // Ноги
    glPushMatrix();
    glTranslatef(-0.2f, 0.2f, 0.0f);
    glScalef(0.2f, 0.6f, 0.2f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    glPushMatrix();
    glTranslatef(0.2f, 0.2f, 0.0f);
    glScalef(0.2f, 0.6f, 0.2f);
    glutSolidCube(1.0f);
    glPopMatrix();
    
    // Оружие в зависимости от класса
    if (character.isAttacking || character.isCasting) {
        glColor3f(0.5f, 0.5f, 0.5f); // Серый для оружия
        
        switch (character.classType) {
            case WARRIOR:
                // Меч
                glPushMatrix();
                glTranslatef(0.6f, 0.8f, 0.0f);
                glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
                glScalef(0.1f, 0.8f, 0.1f);
                glutSolidCube(1.0f);
                glPopMatrix();
                break;
            case MAGE:
                // Посох
                glPushMatrix();
                glTranslatef(0.0f, 1.2f, 0.0f);
                glScalef(0.1f, 1.0f, 0.1f);
                glutSolidCube(1.0f);
                glPopMatrix();
                break;
            case ARCHER:
                // Лук
                glPushMatrix();
                glTranslatef(0.5f, 0.8f, 0.0f);
                glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                glScalef(0.1f, 0.6f, 0.1f);
                glutSolidCube(1.0f);
                glPopMatrix();
                break;
            case PRIEST:
                // Священный символ
                glPushMatrix();
                glTranslatef(0.0f, 1.0f, 0.0f);
                glutSolidSphere(0.2f, 6, 4);
                glPopMatrix();
                break;
            case ROGUE:
                // Кинжал
                glPushMatrix();
                glTranslatef(0.4f, 0.6f, 0.0f);
                glRotatef(30.0f, 0.0f, 0.0f, 1.0f);
                glScalef(0.1f, 0.4f, 0.1f);
                glutSolidCube(1.0f);
                glPopMatrix();
                break;
        }
    }
    
    // Полоски здоровья и маны
    if (character.health < character.maxHealth || character.mana < character.maxMana) {
        glPushMatrix();
        glTranslatef(0.0f, 2.2f, 0.0f);
        
        // Полоска здоровья
        glColor3f(1.0f, 0.0f, 0.0f); // Красный
        glScalef(1.0f, 0.1f, 0.1f);
        glutSolidCube(1.0f);
        
        glColor3f(0.0f, 1.0f, 0.0f); // Зеленый
        glScalef(character.health / character.maxHealth, 1.0f, 1.0f);
        glutSolidCube(1.0f);
        
        // Полоска маны
        glTranslatef(0.0f, -0.2f, 0.0f);
        glColor3f(0.0f, 0.0f, 1.0f); // Синий
        glScalef(1.0f, 1.0f, 1.0f);
        glutSolidCube(1.0f);
        
        glColor3f(0.0f, 0.5f, 1.0f); // Голубой
        glScalef(character.mana / character.maxMana, 1.0f, 1.0f);
        glutSolidCube(1.0f);
        
        glPopMatrix();
    }
    
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
            
            // Зеленая трава
            glColor3f(0.2f, 0.6f, 0.2f);
            
            glBegin(GL_TRIANGLES);
                glVertex3f(worldX1, h1, worldZ1);
                glVertex3f(worldX2, h2, worldZ1);
                glVertex3f(worldX1, h3, worldZ2);
                
                glVertex3f(worldX2, h2, worldZ1);
                glVertex3f(worldX2, h4, worldZ2);
                glVertex3f(worldX1, h3, worldZ2);
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
    updateCharacters();
    
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
    
    // Рисуем персонажей
    for (const auto& character : characters) {
        drawCharacter(character);
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
    
    // Подсчет персонажей
    int humans = 0, elves = 0, dwarves = 0, orcs = 0, darkelves = 0;
    int warriors = 0, mages = 0, archers = 0, priests = 0, rogues = 0;
    int moving = 0, attacking = 0, casting = 0;
    
    for (const auto& character : characters) {
        switch (character.race) {
            case HUMAN: humans++; break;
            case ELF: elves++; break;
            case DWARF: dwarves++; break;
            case ORC: orcs++; break;
            case DARKELF: darkelves++; break;
        }
        
        switch (character.classType) {
            case WARRIOR: warriors++; break;
            case MAGE: mages++; break;
            case ARCHER: archers++; break;
            case PRIEST: priests++; break;
            case ROGUE: rogues++; break;
        }
        
        if (character.isMoving) moving++;
        if (character.isAttacking) attacking++;
        if (character.isCasting) casting++;
    }
    
    drawText(20, windowHeight - 100, "Health: ==================== 100%");
    drawText(20, windowHeight - 80, "Mana:   ==================== 100%");
    drawText(20, windowHeight - 60, timeStr);
    drawText(20, windowHeight - 40, weatherStr);
    drawText(20, windowHeight - 20, "Characters: " + std::to_string(characters.size()));
    drawText(20, 120, "Races: H:" + std::to_string(humans) + " E:" + std::to_string(elves) + " D:" + std::to_string(dwarves) + " O:" + std::to_string(orcs) + " DE:" + std::to_string(darkelves));
    drawText(20, 100, "Classes: W:" + std::to_string(warriors) + " M:" + std::to_string(mages) + " A:" + std::to_string(archers) + " P:" + std::to_string(priests) + " R:" + std::to_string(rogues));
    drawText(20, 80, "Actions: Moving:" + std::to_string(moving) + " Attacking:" + std::to_string(attacking) + " Casting:" + std::to_string(casting));
    drawText(20, 60, "Position: X=" + std::to_string((int)playerX) + " Y=" + std::to_string((int)playerY) + " Z=" + std::to_string((int)playerZ));
    drawText(20, 40, "Nearby: " + std::to_string(characters.size()) + " characters");
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
    
    // Процедурная генерация текстур
    for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
        textureData[i] = 100 + (rand() % 155);
        textureData[i+1] = 100 + (rand() % 155);
        textureData[i+2] = 100 + (rand() % 155);
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
    std::cout << "Loading character textures..." << std::endl;
    for (int i = 0; i < 5; i++) {
        characterTextures[i] = loadTexture("character_" + std::to_string(i));
    }
    
    // Инициализируем системы
    generateTerrain();
    initCharacters();
    
    std::cout << "Player characters system initialized!" << std::endl;
}

void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Player Characters System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- 5 races from L2 deobfuscated client" << std::endl;
    std::cout << "- 5 character classes with unique weapons" << std::endl;
    std::cout << "- Detailed character models with animations" << std::endl;
    std::cout << "- Health and mana bars for all characters" << std::endl;
    std::cout << "- Realistic character AI with actions" << std::endl;
    
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

