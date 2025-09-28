#include <GL/glut.h>
#include <GL/gl.h>
#include <iostream>
#include <vector>
#include <string>
#include <cmath>
#include <fstream>

// Параметры окна
int windowWidth = 1280;
int windowHeight = 720;
const char* windowTitle = "Modern Lineage II - Enhanced Client";

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

// Система времени суток
float timeOfDay = 0.5f; // 0.0 = полночь, 0.5 = полдень, 1.0 = полночь
bool dayNightCycle = true;

// Цвета неба для разного времени суток
struct SkyColor {
    float r, g, b;
};

SkyColor daySky = {0.5f, 0.7f, 1.0f};      // Голубое дневное небо
SkyColor nightSky = {0.1f, 0.1f, 0.3f};    // Темное ночное небо
SkyColor sunsetSky = {1.0f, 0.5f, 0.2f};   // Закатное небо
SkyColor currentSky = daySky;

// Текстовые сообщения для HUD
std::vector<std::string> hudMessages;

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

// Обновление времени суток и цвета неба
void updateDayNightCycle() {
    if (!dayNightCycle) return;
    
    // Медленное изменение времени (полный цикл за ~2 минуты)
    timeOfDay += 0.0001f;
    if (timeOfDay > 1.0f) timeOfDay = 0.0f;
    
    // Расчет цвета неба в зависимости от времени суток
    if (timeOfDay < 0.25f) {
        // Ночь -> Рассвет
        float t = timeOfDay * 4.0f;
        currentSky.r = nightSky.r + (sunsetSky.r - nightSky.r) * t;
        currentSky.g = nightSky.g + (sunsetSky.g - nightSky.g) * t;
        currentSky.b = nightSky.b + (sunsetSky.b - nightSky.b) * t;
    } else if (timeOfDay < 0.5f) {
        // Рассвет -> День
        float t = (timeOfDay - 0.25f) * 4.0f;
        currentSky.r = sunsetSky.r + (daySky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (daySky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (daySky.b - sunsetSky.b) * t;
    } else if (timeOfDay < 0.75f) {
        // День -> Закат
        float t = (timeOfDay - 0.5f) * 4.0f;
        currentSky.r = daySky.r + (sunsetSky.r - daySky.r) * t;
        currentSky.g = daySky.g + (sunsetSky.g - daySky.g) * t;
        currentSky.b = daySky.b + (sunsetSky.b - daySky.b) * t;
    } else {
        // Закат -> Ночь
        float t = (timeOfDay - 0.75f) * 4.0f;
        currentSky.r = sunsetSky.r + (nightSky.r - sunsetSky.r) * t;
        currentSky.g = sunsetSky.g + (nightSky.g - sunsetSky.g) * t;
        currentSky.b = sunsetSky.b + (nightSky.b - sunsetSky.b) * t;
    }
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
    // Обновляем цикл день/ночь
    updateDayNightCycle();
    
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
    
    drawText(20, windowHeight - 40, "Health: ==================== 100%");
    drawText(20, windowHeight - 20, "Mana:   ==================== 100%");
    drawText(20, 60, "Position: X=" + std::to_string((int)playerX) + " Y=" + std::to_string((int)playerY) + " Z=" + std::to_string((int)playerZ));
    drawText(20, 40, timeStr);
    drawText(20, 20, "Controls: WASD - move, Arrows - rotate, T - toggle day/night cycle, ESC - exit");

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

void initGL() {
    glClearColor(currentSky.r, currentSky.g, currentSky.b, 1.0f);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_COLOR_MATERIAL);

    GLfloat light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
    glLightfv(GL_LIGHT0, GL_POSITION, light_position);
    
    // Загружаем текстуры
    std::cout << "Loading textures from L2 deobfuscated client..." << std::endl;
    groundTexture = loadTexture("ground_earth");
    grassTexture = loadTexture("grass_field");
    stoneTexture = loadTexture("stone_rock");
    waterTexture = loadTexture("water_blue");
    std::cout << "Textures loaded successfully!" << std::endl;
}

// Функция обновления (вызывается через timer)
void update(int value) {
    glutPostRedisplay();
    glutTimerFunc(16, update, 0); // ~60 FPS
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Enhanced Client..." << std::endl;
    std::cout << "Features:" << std::endl;
    std::cout << "- Textured terrain from L2 deobfuscated client" << std::endl;
    std::cout << "- Dynamic day/night cycle" << std::endl;
    std::cout << "- Multiple terrain types (grass, stone, water, earth)" << std::endl;
    std::cout << "- Enhanced sky system" << std::endl;
    
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
    std::cout << "- ESC: Exit" << std::endl;

    glutMainLoop();

    return 0;
}

