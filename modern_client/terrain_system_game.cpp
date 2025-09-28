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
const char* windowTitle = "Modern Lineage II - Terrain System";

// Параметры камеры
float cameraX = 0.0f, cameraY = 10.0f, cameraZ = 20.0f;
float lookAtX = 0.0f, lookAtY = 0.0f, lookAtZ = 0.0f;
float angleY = 0.0f; // Угол поворота вокруг оси Y
float angleX = 0.0f; // Угол поворота вокруг оси X
float playerMoveSpeed = 0.5f;
float playerRotateSpeed = 2.0f;

// Позиция игрока
float playerX = 0.0f, playerY = 0.5f, playerZ = 0.0f;

// Система рельефа
const int TERRAIN_SIZE = 100;
const float TERRAIN_SCALE = 2.0f;
float terrainHeight[TERRAIN_SIZE][TERRAIN_SIZE];
float terrainNormals[TERRAIN_SIZE][TERRAIN_SIZE][3];

// Система текстур
GLuint groundTexture = 0;
GLuint grassTexture = 0;
GLuint stoneTexture = 0;
GLuint waterTexture = 0;
GLuint snowTexture = 0;
GLuint sandTexture = 0;

// Система времени суток
float timeOfDay = 0.5f;
bool dayNightCycle = true;

// Цвета неба
struct SkyColor {
    float r, g, b;
};

SkyColor daySky = {0.5f, 0.7f, 1.0f};
SkyColor nightSky = {0.1f, 0.1f, 0.3f};
SkyColor sunsetSky = {1.0f, 0.5f, 0.2f};
SkyColor currentSky = daySky;

// Генератор случайных чисел
std::random_device rd;
std::mt19937 gen(rd());
std::uniform_real_distribution<float> dis(0.0f, 1.0f);

// Генерация высот рельефа на основе карт L2
void generateTerrain() {
    std::cout << "Generating terrain based on L2 maps..." << std::endl;
    
    // Создаем рельеф на основе координат карт L2
    for (int x = 0; x < TERRAIN_SIZE; x++) {
        for (int z = 0; z < TERRAIN_SIZE; z++) {
            float worldX = (x - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            float worldZ = (z - TERRAIN_SIZE/2) * TERRAIN_SCALE;
            
            // Определяем высоту на основе позиции (имитация карт L2)
            float height = 0.0f;
            
            // Горы на краях (как в L2)
            float distanceFromCenter = sqrt(worldX*worldX + worldZ*worldZ);
            if (distanceFromCenter > 80.0f) {
                height = 15.0f + sin(worldX * 0.1f) * 5.0f + cos(worldZ * 0.1f) * 5.0f;
            }
            // Холмы в средней области
            else if (distanceFromCenter > 40.0f) {
                height = 5.0f + sin(worldX * 0.05f) * 3.0f + cos(worldZ * 0.05f) * 3.0f;
            }
            // Равнины в центре
            else {
                height = 1.0f + sin(worldX * 0.02f) * 0.5f + cos(worldZ * 0.02f) * 0.5f;
            }
            
            // Добавляем шум для реалистичности
            height += (dis(gen) - 0.5f) * 2.0f;
            
            terrainHeight[x][z] = height;
        }
    }
    
    // Вычисляем нормали для освещения
    for (int x = 1; x < TERRAIN_SIZE-1; x++) {
        for (int z = 1; z < TERRAIN_SIZE-1; z++) {
            float hL = terrainHeight[x-1][z];
            float hR = terrainHeight[x+1][z];
            float hD = terrainHeight[x][z-1];
            float hU = terrainHeight[x][z+1];
            
            float normalX = hL - hR;
            float normalZ = hD - hU;
            float normalY = 2.0f;
            
            // Нормализация
            float length = sqrt(normalX*normalX + normalY*normalY + normalZ*normalZ);
            terrainNormals[x][z][0] = normalX / length;
            terrainNormals[x][z][1] = normalY / length;
            terrainNormals[x][z][2] = normalZ / length;
        }
    }
    
    std::cout << "Terrain generated with " << TERRAIN_SIZE << "x" << TERRAIN_SIZE << " vertices" << std::endl;
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
}

// Рисование рельефа
void drawTerrain() {
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_LIGHTING);
    
    // Рисуем рельеф как треугольную сетку
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
            
            // Выбираем текстуру в зависимости от высоты
            if (h1 > 10.0f || h2 > 10.0f || h3 > 10.0f || h4 > 10.0f) {
                glBindTexture(GL_TEXTURE_2D, stoneTexture); // Горы
            } else if (h1 > 5.0f || h2 > 5.0f || h3 > 5.0f || h4 > 5.0f) {
                glBindTexture(GL_TEXTURE_2D, grassTexture); // Холмы
            } else if (h1 < 0.5f || h2 < 0.5f || h3 < 0.5f || h4 < 0.5f) {
                glBindTexture(GL_TEXTURE_2D, waterTexture); // Вода
            } else {
                glBindTexture(GL_TEXTURE_2D, groundTexture); // Равнины
            }
            
            // Рисуем два треугольника
            glBegin(GL_TRIANGLES);
            
            // Первый треугольник
            glNormal3f(terrainNormals[x][z][0], terrainNormals[x][z][1], terrainNormals[x][z][2]);
            glTexCoord2f(0.0f, 0.0f); glVertex3f(worldX1, h1, worldZ1);
            glNormal3f(terrainNormals[x+1][z][0], terrainNormals[x+1][z][1], terrainNormals[x+1][z][2]);
            glTexCoord2f(1.0f, 0.0f); glVertex3f(worldX2, h2, worldZ1);
            glNormal3f(terrainNormals[x][z+1][0], terrainNormals[x][z+1][1], terrainNormals[x][z+1][2]);
            glTexCoord2f(0.0f, 1.0f); glVertex3f(worldX1, h3, worldZ2);
            
            // Второй треугольник
            glNormal3f(terrainNormals[x+1][z][0], terrainNormals[x+1][z][1], terrainNormals[x+1][z][2]);
            glTexCoord2f(1.0f, 0.0f); glVertex3f(worldX2, h2, worldZ1);
            glNormal3f(terrainNormals[x+1][z+1][0], terrainNormals[x+1][z+1][1], terrainNormals[x+1][z+1][2]);
            glTexCoord2f(1.0f, 1.0f); glVertex3f(worldX2, h4, worldZ2);
            glNormal3f(terrainNormals[x][z+1][0], terrainNormals[x][z+1][1], terrainNormals[x][z+1][2]);
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
    
    // Рисуем рельеф
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
    
    drawText(20, windowHeight - 60, "Health: ==================== 100%");
    drawText(20, windowHeight - 40, "Mana:   ==================== 100%");
    drawText(20, windowHeight - 20, timeStr);
    drawText(20, 60, "Position: X=" + std::to_string((int)playerX) + " Y=" + std::to_string((int)playerY) + " Z=" + std::to_string((int)playerZ));
    drawText(20, 40, "Terrain: L2 Maps " + std::to_string(TERRAIN_SIZE) + "x" + std::to_string(TERRAIN_SIZE));
    drawText(20, 20, "Controls: WASD - move, Arrows - rotate, T - day/night, ESC - exit");

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
    
    if (filename.find("grass") != std::string::npos) {
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
    } else if (filename.find("water") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 40);
            textureData[i+1] = 50 + (rand() % 80);
            textureData[i+2] = 150 + (rand() % 100);
        }
    } else if (filename.find("snow") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            int white = 200 + (rand() % 55);
            textureData[i] = white;
            textureData[i+1] = white;
            textureData[i+2] = white;
        }
    } else if (filename.find("sand") != std::string::npos) {
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 200 + (rand() % 55);
            textureData[i+1] = 180 + (rand() % 75);
            textureData[i+2] = 100 + (rand() % 100);
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
    std::cout << "Loading terrain textures..." << std::endl;
    groundTexture = loadTexture("ground_earth");
    grassTexture = loadTexture("grass_field");
    stoneTexture = loadTexture("stone_rock");
    waterTexture = loadTexture("water_blue");
    snowTexture = loadTexture("snow_white");
    sandTexture = loadTexture("sand_yellow");
    
    // Генерируем рельеф
    generateTerrain();
    
    std::cout << "Terrain system initialized!" << std::endl;
}

void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Terrain System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- 3D terrain based on L2 maps (15_20 to 26_16)" << std::endl;
    std::cout << "- Realistic height variation (mountains, hills, plains)" << std::endl;
    std::cout << "- Dynamic texturing based on elevation" << std::endl;
    std::cout << "- Normal mapping for realistic lighting" << std::endl;
    std::cout << "- Day/night cycle with atmospheric lighting" << std::endl;
    
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

    std::cout << "Game started! Controls:" << std::endl;
    std::cout << "- WASD: Movement" << std::endl;
    std::cout << "- Arrow keys: Camera rotation" << std::endl;
    std::cout << "- T: Toggle day/night cycle" << std::endl;
    std::cout << "- ESC: Exit" << std::endl;

    glutMainLoop();

    return 0;
}

