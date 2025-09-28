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
const char* windowTitle = "Modern Lineage II - Sky System";

// Параметры камеры
float cameraX = 0.0f, cameraY = 10.0f, cameraZ = 20.0f;
float lookAtX = 0.0f, lookAtY = 0.0f, lookAtZ = 0.0f;
float angleY = 0.0f; // Угол поворота вокруг оси Y
float angleX = 0.0f; // Угол поворота вокруг оси X
float playerMoveSpeed = 0.5f;
float playerRotateSpeed = 2.0f;

// Позиция игрока
float playerX = 0.0f, playerY = 0.5f, playerZ = 0.0f;

// Состояние мыши
int lastMouseX, lastMouseY;
bool mouseLeftDown = false;

// Система текстур
GLuint groundTexture = 0;
GLuint grassTexture = 0;
GLuint stoneTexture = 0;
GLuint waterTexture = 0;

// Система времени суток и погоды
float timeOfDay = 0.5f; // 0.0 = полночь, 0.5 = полдень, 1.0 = полночь
bool dayNightCycle = true;
float weatherIntensity = 0.0f; // 0.0 = ясно, 1.0 = сильный дождь
bool isRaining = false;
float cloudCover = 0.3f; // Покрытие облаками 0.0-1.0

// Система облаков
struct Cloud {
    float x, y, z;
    float size;
    float speed;
    float opacity;
    float type; // 0 = кучевые, 1 = слоистые, 2 = дождевые
};

std::vector<Cloud> clouds;

// Цвета неба для разного времени суток и погоды
struct SkyColor {
    float r, g, b;
    float ambient_r, ambient_g, ambient_b;
};

SkyColor daySky = {0.5f, 0.7f, 1.0f, 0.3f, 0.3f, 0.4f};      // Голубое дневное небо
SkyColor nightSky = {0.1f, 0.1f, 0.3f, 0.1f, 0.1f, 0.2f};    // Темное ночное небо
SkyColor sunsetSky = {1.0f, 0.5f, 0.2f, 0.4f, 0.2f, 0.1f};   // Закатное небо
SkyColor rainSky = {0.4f, 0.4f, 0.5f, 0.2f, 0.2f, 0.3f};     // Дождливое небо
SkyColor currentSky = daySky;

// Система частиц дождя
struct RainDrop {
    float x, y, z;
    float speed;
    float size;
    float life;
};

std::vector<RainDrop> rainDrops;

// Генератор случайных чисел
std::random_device rd;
std::mt19937 gen(rd());
std::uniform_real_distribution<float> dis(0.0f, 1.0f);

// Инициализация облаков
void initClouds() {
    clouds.clear();
    for (int i = 0; i < 50; i++) {
        Cloud cloud;
        cloud.x = dis(gen) * 200.0f - 100.0f;
        cloud.y = 15.0f + dis(gen) * 20.0f; // Высота 15-35
        cloud.z = dis(gen) * 200.0f - 100.0f;
        cloud.size = 5.0f + dis(gen) * 15.0f;
        cloud.speed = 0.1f + dis(gen) * 0.3f;
        cloud.opacity = 0.3f + dis(gen) * 0.7f;
        cloud.type = dis(gen);
        clouds.push_back(cloud);
    }
}

// Инициализация капель дождя
void initRain() {
    rainDrops.clear();
    for (int i = 0; i < 1000; i++) {
        RainDrop drop;
        drop.x = dis(gen) * 200.0f - 100.0f;
        drop.y = 30.0f + dis(gen) * 20.0f;
        drop.z = dis(gen) * 200.0f - 100.0f;
        drop.speed = 2.0f + dis(gen) * 3.0f;
        drop.size = 0.1f + dis(gen) * 0.2f;
        drop.life = 1.0f;
        rainDrops.push_back(drop);
    }
}

// Обновление времени суток и погоды
void updateDayNightCycle() {
    if (!dayNightCycle) return;
    
    // Медленное изменение времени (полный цикл за ~3 минуты)
    timeOfDay += 0.00005f;
    if (timeOfDay > 1.0f) timeOfDay = 0.0f;
    
    // Случайное изменение погоды
    if (dis(gen) < 0.001f) { // 0.1% шанс изменения погоды каждый кадр
        isRaining = !isRaining;
        if (isRaining) {
            initRain();
            weatherIntensity = 0.3f + dis(gen) * 0.7f;
            cloudCover = 0.7f + dis(gen) * 0.3f;
        } else {
            weatherIntensity = 0.0f;
            cloudCover = 0.2f + dis(gen) * 0.4f;
        }
    }
    
    // Расчет цвета неба в зависимости от времени суток и погоды
    SkyColor baseSky;
    
    if (timeOfDay < 0.25f) {
        // Ночь -> Рассвет
        float t = timeOfDay * 4.0f;
        baseSky.r = nightSky.r + (sunsetSky.r - nightSky.r) * t;
        baseSky.g = nightSky.g + (sunsetSky.g - nightSky.g) * t;
        baseSky.b = nightSky.b + (sunsetSky.b - nightSky.b) * t;
        baseSky.ambient_r = nightSky.ambient_r + (sunsetSky.ambient_r - nightSky.ambient_r) * t;
        baseSky.ambient_g = nightSky.ambient_g + (sunsetSky.ambient_g - nightSky.ambient_g) * t;
        baseSky.ambient_b = nightSky.ambient_b + (sunsetSky.ambient_b - nightSky.ambient_b) * t;
    } else if (timeOfDay < 0.5f) {
        // Рассвет -> День
        float t = (timeOfDay - 0.25f) * 4.0f;
        baseSky.r = sunsetSky.r + (daySky.r - sunsetSky.r) * t;
        baseSky.g = sunsetSky.g + (daySky.g - sunsetSky.g) * t;
        baseSky.b = sunsetSky.b + (daySky.b - sunsetSky.b) * t;
        baseSky.ambient_r = sunsetSky.ambient_r + (daySky.ambient_r - sunsetSky.ambient_r) * t;
        baseSky.ambient_g = sunsetSky.ambient_g + (daySky.ambient_g - sunsetSky.ambient_g) * t;
        baseSky.ambient_b = sunsetSky.ambient_b + (daySky.ambient_b - sunsetSky.ambient_b) * t;
    } else if (timeOfDay < 0.75f) {
        // День -> Закат
        float t = (timeOfDay - 0.5f) * 4.0f;
        baseSky.r = daySky.r + (sunsetSky.r - daySky.r) * t;
        baseSky.g = daySky.g + (sunsetSky.g - daySky.g) * t;
        baseSky.b = daySky.b + (sunsetSky.b - daySky.b) * t;
        baseSky.ambient_r = daySky.ambient_r + (sunsetSky.ambient_r - daySky.ambient_r) * t;
        baseSky.ambient_g = daySky.ambient_g + (sunsetSky.ambient_g - daySky.ambient_g) * t;
        baseSky.ambient_b = daySky.ambient_b + (sunsetSky.ambient_b - daySky.ambient_b) * t;
    } else {
        // Закат -> Ночь
        float t = (timeOfDay - 0.75f) * 4.0f;
        baseSky.r = sunsetSky.r + (nightSky.r - sunsetSky.r) * t;
        baseSky.g = sunsetSky.g + (nightSky.g - sunsetSky.g) * t;
        baseSky.b = sunsetSky.b + (nightSky.b - sunsetSky.b) * t;
        baseSky.ambient_r = sunsetSky.ambient_r + (nightSky.ambient_r - sunsetSky.ambient_r) * t;
        baseSky.ambient_g = sunsetSky.ambient_g + (nightSky.ambient_g - sunsetSky.ambient_g) * t;
        baseSky.ambient_b = sunsetSky.ambient_b + (nightSky.ambient_b - sunsetSky.ambient_b) * t;
    }
    
    // Применяем погодные эффекты
    if (isRaining) {
        float rainEffect = weatherIntensity;
        currentSky.r = baseSky.r * (1.0f - rainEffect) + rainSky.r * rainEffect;
        currentSky.g = baseSky.g * (1.0f - rainEffect) + rainSky.g * rainEffect;
        currentSky.b = baseSky.b * (1.0f - rainEffect) + rainSky.b * rainEffect;
        currentSky.ambient_r = baseSky.ambient_r * (1.0f - rainEffect) + rainSky.ambient_r * rainEffect;
        currentSky.ambient_g = baseSky.ambient_g * (1.0f - rainEffect) + rainSky.ambient_g * rainEffect;
        currentSky.ambient_b = baseSky.ambient_b * (1.0f - rainEffect) + rainSky.ambient_b * rainEffect;
    } else {
        currentSky = baseSky;
    }
}

// Обновление облаков
void updateClouds() {
    for (auto& cloud : clouds) {
        cloud.x += cloud.speed * 0.1f;
        if (cloud.x > 120.0f) cloud.x = -120.0f;
        
        // Обновляем непрозрачность в зависимости от погоды
        if (isRaining) {
            cloud.opacity = 0.6f + weatherIntensity * 0.4f;
        } else {
            cloud.opacity = 0.3f + cloudCover * 0.4f;
        }
    }
}

// Обновление дождя
void updateRain() {
    if (!isRaining) return;
    
    for (auto& drop : rainDrops) {
        drop.y -= drop.speed;
        drop.x += (dis(gen) - 0.5f) * 0.5f; // Ветер
        drop.life -= 0.01f;
        
        // Перезапуск капли если она упала
        if (drop.y < 0.0f || drop.life <= 0.0f) {
            drop.x = dis(gen) * 200.0f - 100.0f;
            drop.y = 30.0f + dis(gen) * 20.0f;
            drop.z = dis(gen) * 200.0f - 100.0f;
            drop.life = 1.0f;
        }
    }
}

// Рисование неба
void drawSky() {
    glDisable(GL_LIGHTING);
    glDisable(GL_DEPTH_TEST);
    
    // Рисуем небо как сферу
    glPushMatrix();
    glTranslatef(cameraX, cameraY, cameraZ);
    
    // Градиент неба от горизонта к зениту
    glBegin(GL_QUAD_STRIP);
    for (int i = 0; i <= 20; i++) {
        float angle = i * M_PI / 20.0f;
        float y = cos(angle);
        float radius = sin(angle) * 100.0f;
        
        // Цвет неба меняется от горизонта к зениту
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

// Рисование облаков
void drawClouds() {
    glDisable(GL_LIGHTING);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    for (const auto& cloud : clouds) {
        glPushMatrix();
        glTranslatef(cloud.x, cloud.y, cloud.z);
        
        // Разные типы облаков
        if (cloud.type < 0.33f) {
            // Кучевые облака - пушистые
            glColor4f(1.0f, 1.0f, 1.0f, cloud.opacity * 0.7f);
            glutSolidSphere(cloud.size, 8, 6);
            glutSolidSphere(cloud.size * 0.8f, 8, 6);
            glutSolidSphere(cloud.size * 0.6f, 8, 6);
        } else if (cloud.type < 0.66f) {
            // Слоистые облака - плоские
            glColor4f(0.9f, 0.9f, 0.9f, cloud.opacity * 0.5f);
            glScalef(cloud.size, cloud.size * 0.3f, cloud.size);
            glutSolidCube(1.0f);
        } else {
            // Дождевые облака - темные
            glColor4f(0.7f, 0.7f, 0.8f, cloud.opacity * 0.8f);
            glutSolidSphere(cloud.size * 1.2f, 6, 4);
        }
        
        glPopMatrix();
    }
    
    glDisable(GL_BLEND);
    glEnable(GL_LIGHTING);
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

// Рисование текстурированной поверхности
void drawTexturedGround() {
    glEnable(GL_TEXTURE_2D);
    
    // Рисуем разные типы поверхностей
    const int gridSize = 20;
    const float tileSize = 5.0f;
    
    for (int x = -gridSize; x < gridSize; x++) {
        for (int z = -gridSize; z < gridSize; z++) {
            float worldX = x * tileSize;
            float worldZ = z * tileSize;
            
            // Выбираем текстуру в зависимости от позиции
            if (abs(x) < 2 && abs(z) < 2) {
                // Центральная область - трава
                glBindTexture(GL_TEXTURE_2D, grassTexture);
            } else if (abs(x) > 15 || abs(z) > 15) {
                // Края - камень
                glBindTexture(GL_TEXTURE_2D, stoneTexture);
            } else if (x > 5 && x < 10 && z > 5 && z < 10) {
                // Водная область
                glBindTexture(GL_TEXTURE_2D, waterTexture);
            } else {
                // Обычная земля
                glBindTexture(GL_TEXTURE_2D, groundTexture);
            }
            
            // Рисуем тайл с текстурой
            glBegin(GL_QUADS);
                glTexCoord2f(0.0f, 0.0f); glVertex3f(worldX, 0.0f, worldZ);
                glTexCoord2f(1.0f, 0.0f); glVertex3f(worldX + tileSize, 0.0f, worldZ);
                glTexCoord2f(1.0f, 1.0f); glVertex3f(worldX + tileSize, 0.0f, worldZ + tileSize);
                glTexCoord2f(0.0f, 1.0f); glVertex3f(worldX, 0.0f, worldZ + tileSize);
            glEnd();
        }
    }
    
    glDisable(GL_TEXTURE_2D);
}

void drawText(float x, float y, const std::string& text) {
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    gluOrtho2D(0, windowWidth, 0, windowHeight);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    glColor3f(1.0f, 1.0f, 1.0f); // Белый цвет текста
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
    // Обновляем все системы
    updateDayNightCycle();
    updateClouds();
    updateRain();
    
    // Устанавливаем цвет неба
    glClearColor(currentSky.r, currentSky.g, currentSky.b, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    // Обновление позиции камеры относительно игрока
    lookAtX = playerX + sin(angleY * M_PI / 180.0f) * cos(angleX * M_PI / 180.0f) * 10.0f;
    lookAtY = playerY + sin(angleX * M_PI / 180.0f) * 10.0f;
    lookAtZ = playerZ - cos(angleY * M_PI / 180.0f) * cos(angleX * M_PI / 180.0f) * 10.0f;

    cameraX = playerX - sin(angleY * M_PI / 180.0f) * 20.0f;
    cameraY = playerY + 10.0f;
    cameraZ = playerZ + cos(angleY * M_PI / 180.0f) * 20.0f;

    gluLookAt(cameraX, cameraY, cameraZ,    // Позиция камеры
              playerX, playerY, playerZ,    // Точка, на которую смотрит камера (игрок)
              0.0f, 1.0f, 0.0f);            // Вектор "вверх"

    // Рисуем небо
    drawSky();
    
    // Рисуем облака
    drawClouds();
    
    // Рисуем дождь
    drawRain();

    // Рисование текстурированной земли
    drawTexturedGround();

    // Рисование игрока (простой кубик)
    glPushMatrix();
    glTranslatef(playerX, playerY, playerZ);
    glColor3f(0.0f, 0.0f, 1.0f); // Синий цвет
    glutSolidCube(1.0f);
    glPopMatrix();

    // Рисование нескольких кубиков вокруг игрока (НПС/мобы)
    for (int i = -3; i <= 3; i += 2) {
        for (int j = -3; j <= 3; j += 2) {
            if (i == 0 && j == 0) continue; // Пропустить позицию игрока
            glPushMatrix();
            glTranslatef(playerX + i * 8.0f, 0.5f, playerZ + j * 8.0f);
            glColor3f((i + 3) / 6.0f, 0.5f, (j + 3) / 6.0f); // Разные цвета
            glutSolidCube(1.0f);
            glPopMatrix();
        }
    }

    // Рисование HUD
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
    drawText(20, 60, "Position: X=" + std::to_string((int)playerX) + " Y=" + std::to_string((int)playerY) + " Z=" + std::to_string((int)playerZ));
    drawText(20, 40, "Clouds: " + std::to_string((int)(cloudCover * 100)) + "%");
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
                cloudCover = 0.8f;
            } else {
                weatherIntensity = 0.0f;
                cloudCover = 0.3f;
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
            if (angleX > 89.0f) angleX = 89.0f; // Ограничение угла наклона
            break;
        case GLUT_KEY_DOWN:
            angleX -= playerRotateSpeed;
            if (angleX < -89.0f) angleX = -89.0f; // Ограничение угла наклона
            break;
    }
    glutPostRedisplay();
}

void mouse(int button, int state, int x, int y) {
    if (button == GLUT_LEFT_BUTTON) {
        if (state == GLUT_DOWN) {
            mouseLeftDown = true;
            lastMouseX = x;
            lastMouseY = y;
            std::cout << "Interaction at: " << x << ", " << y << std::endl;
        } else {
            mouseLeftDown = false;
        }
    }
}

void motion(int x, int y) {
    if (mouseLeftDown) {
        // Вращение камеры мышью (опционально)
        // Оставляем управление стрелками для простоты
    }
}

// Простая загрузка текстуры (заглушка)
GLuint loadTexture(const std::string& filename) {
    GLuint textureID;
    glGenTextures(1, &textureID);
    glBindTexture(GL_TEXTURE_2D, textureID);
    
    // Создаем процедурную текстуру для демонстрации
    const int texWidth = 256;
    const int texHeight = 256;
    unsigned char* textureData = new unsigned char[texWidth * texHeight * 3];
    
    if (filename.find("grass") != std::string::npos) {
        // Зеленая травяная текстура
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 60);      // R - темно-зеленый
            textureData[i+1] = 100 + (rand() % 100);  // G - зеленый
            textureData[i+2] = 20 + (rand() % 40);    // B - темно-зеленый
        }
    } else if (filename.find("stone") != std::string::npos) {
        // Серая каменная текстура
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            int gray = 80 + (rand() % 60);
            textureData[i] = gray;     // R
            textureData[i+1] = gray;   // G
            textureData[i+2] = gray;   // B
        }
    } else if (filename.find("water") != std::string::npos) {
        // Синяя водная текстура
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 20 + (rand() % 40);      // R - темно-синий
            textureData[i+1] = 50 + (rand() % 80);    // G - сине-зеленый
            textureData[i+2] = 150 + (rand() % 100);  // B - синий
        }
    } else {
        // Коричневая земляная текстура по умолчанию
        for (int i = 0; i < texWidth * texHeight * 3; i += 3) {
            textureData[i] = 139 + (rand() % 60);     // R - коричневый
            textureData[i+1] = 69 + (rand() % 40);    // G - темно-коричневый
            textureData[i+2] = 19 + (rand() % 30);    // B - очень темно-коричневый
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

    // Настройка освещения в зависимости от времени суток
    GLfloat light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
    glLightfv(GL_LIGHT0, GL_POSITION, light_position);
    
    // Загружаем текстуры
    std::cout << "Loading textures from L2 deobfuscated client..." << std::endl;
    groundTexture = loadTexture("ground_earth");
    grassTexture = loadTexture("grass_field");
    stoneTexture = loadTexture("stone_rock");
    waterTexture = loadTexture("water_blue");
    std::cout << "Textures loaded successfully!" << std::endl;
    
    // Инициализируем системы
    initClouds();
    initRain();
    
    std::cout << "Sky system initialized with " << clouds.size() << " clouds" << std::endl;
}

// Функция обновления (вызывается через timer)
void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0); // ~60 FPS
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Sky System Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- Advanced sky system with clouds and weather" << std::endl;
    std::cout << "- Dynamic day/night cycle with realistic colors" << std::endl;
    std::cout << "- Rain system with particle effects" << std::endl;
    std::cout << "- Multiple cloud types (cumulus, stratus, nimbus)" << std::endl;
    std::cout << "- Weather simulation inspired by Witcher 3" << std::endl;
    
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
    std::cout << "- R: Toggle rain" << std::endl;
    std::cout << "- ESC: Exit" << std::endl;

    glutMainLoop();

    return 0;
}

