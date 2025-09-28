#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>
#include <iostream>
#include <cmath>
#include <cstring>

// Размеры окна
const int WINDOW_WIDTH = 1280;
const int WINDOW_HEIGHT = 720;

// Состояние игры
float playerX = 0.0f;
float playerY = 0.0f;
float playerZ = 0.0f;
float playerRotation = 0.0f;
float cameraX = 0.0f;
float cameraY = 5.0f;
float cameraZ = 10.0f;

// Функция для рисования текста
void drawText(float x, float y, const char* text) {
    glRasterPos2f(x, y);
    for (int i = 0; text[i] != '\0'; i++) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_12, text[i]);
    }
}

// Функция для рисования куба
void drawCube(float x, float y, float z, float size) {
    glPushMatrix();
    glTranslatef(x, y, z);
    glScalef(size, size, size);
    
    glBegin(GL_QUADS);
    // Передняя грань
    glColor3f(0.8f, 0.2f, 0.2f);
    glVertex3f(-1, -1,  1);
    glVertex3f( 1, -1,  1);
    glVertex3f( 1,  1,  1);
    glVertex3f(-1,  1,  1);
    
    // Задняя грань
    glColor3f(0.2f, 0.8f, 0.2f);
    glVertex3f(-1, -1, -1);
    glVertex3f(-1,  1, -1);
    glVertex3f( 1,  1, -1);
    glVertex3f( 1, -1, -1);
    
    // Верхняя грань
    glColor3f(0.2f, 0.2f, 0.8f);
    glVertex3f(-1,  1, -1);
    glVertex3f(-1,  1,  1);
    glVertex3f( 1,  1,  1);
    glVertex3f( 1,  1, -1);
    
    // Нижняя грань
    glColor3f(0.8f, 0.8f, 0.2f);
    glVertex3f(-1, -1, -1);
    glVertex3f( 1, -1, -1);
    glVertex3f( 1, -1,  1);
    glVertex3f(-1, -1,  1);
    
    // Правая грань
    glColor3f(0.8f, 0.2f, 0.8f);
    glVertex3f( 1, -1, -1);
    glVertex3f( 1,  1, -1);
    glVertex3f( 1,  1,  1);
    glVertex3f( 1, -1,  1);
    
    // Левая грань
    glColor3f(0.2f, 0.8f, 0.8f);
    glVertex3f(-1, -1, -1);
    glVertex3f(-1, -1,  1);
    glVertex3f(-1,  1,  1);
    glVertex3f(-1,  1, -1);
    glEnd();
    
    glPopMatrix();
}

// Функция отрисовки
void display() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    // Настройка камеры
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(45.0, (double)WINDOW_WIDTH / WINDOW_HEIGHT, 0.1, 100.0);
    
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    gluLookAt(cameraX, cameraY, cameraZ, playerX, playerY, playerZ, 0, 1, 0);
    
    // Рисование земли
    glColor3f(0.3f, 0.7f, 0.3f);
    glBegin(GL_QUADS);
    glVertex3f(-50, 0, -50);
    glVertex3f( 50, 0, -50);
    glVertex3f( 50, 0,  50);
    glVertex3f(-50, 0,  50);
    glEnd();
    
    // Рисование игрока
    drawCube(playerX, playerY + 1, playerZ, 1.0f);
    
    // Рисование объектов вокруг
    for (int i = -5; i <= 5; i++) {
        for (int j = -5; j <= 5; j++) {
            if (i != 0 || j != 0) {
                drawCube(i * 3, 0.5f, j * 3, 0.5f);
            }
        }
    }
    
    // Переключение в 2D режим для UI
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glOrtho(0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    
    // Рисование UI
    glColor3f(1.0f, 1.0f, 1.0f);
    drawText(10, 30, "Modern Lineage II - Simple Client");
    drawText(10, 50, "WASD - Move, Mouse - Look, ESC - Exit");
    
    // Статус
    char status[100];
    sprintf(status, "Position: %.1f, %.1f, %.1f", playerX, playerY, playerZ);
    drawText(10, WINDOW_HEIGHT - 30, status);
    
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    
    glutSwapBuffers();
}

// Обработка клавиатуры
void keyboard(unsigned char key, int x, int y) {
    switch (key) {
        case 'w':
        case 'W':
            playerX += sin(playerRotation) * 0.5f;
            playerZ += cos(playerRotation) * 0.5f;
            break;
        case 's':
        case 'S':
            playerX -= sin(playerRotation) * 0.5f;
            playerZ -= cos(playerRotation) * 0.5f;
            break;
        case 'a':
        case 'A':
            playerX -= cos(playerRotation) * 0.5f;
            playerZ += sin(playerRotation) * 0.5f;
            break;
        case 'd':
        case 'D':
            playerX += cos(playerRotation) * 0.5f;
            playerZ -= sin(playerRotation) * 0.5f;
            break;
        case 27: // ESC
            exit(0);
            break;
    }
    glutPostRedisplay();
}

// Обработка специальных клавиш
void specialKeys(int key, int x, int y) {
    switch (key) {
        case GLUT_KEY_LEFT:
            playerRotation -= 0.1f;
            break;
        case GLUT_KEY_RIGHT:
            playerRotation += 0.1f;
            break;
    }
    glutPostRedisplay();
}

// Обработка мыши
void mouse(int button, int state, int x, int y) {
    if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
        // Левый клик - взаимодействие
        std::cout << "Interaction at: " << x << ", " << y << std::endl;
    }
}

// Обновление камеры
void updateCamera() {
    cameraX = playerX - sin(playerRotation) * 5.0f;
    cameraZ = playerZ - cos(playerRotation) * 5.0f;
}

// Таймер для обновления
void timer(int value) {
    updateCamera();
    glutPostRedisplay();
    glutTimerFunc(16, timer, 0); // ~60 FPS
}

// Инициализация OpenGL
void initGL() {
    glClearColor(0.5f, 0.8f, 1.0f, 1.0f); // Небо
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    
    // Настройка света
    GLfloat lightPos[] = {0.0f, 10.0f, 0.0f, 1.0f};
    GLfloat lightAmb[] = {0.3f, 0.3f, 0.3f, 1.0f};
    GLfloat lightDiff[] = {0.8f, 0.8f, 0.8f, 1.0f};
    
    glLightfv(GL_LIGHT0, GL_POSITION, lightPos);
    glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmb);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiff);
}

int main(int argc, char** argv) {
    std::cout << "Starting Modern Lineage II Simple Client..." << std::endl;
    
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    glutCreateWindow("Modern Lineage II - Simple Client");
    
    initGL();
    
    glutDisplayFunc(display);
    glutKeyboardFunc(keyboard);
    glutSpecialFunc(specialKeys);
    glutMouseFunc(mouse);
    glutTimerFunc(16, timer, 0);
    
    std::cout << "Game started! Use WASD to move, arrow keys to rotate, ESC to exit." << std::endl;
    
    glutMainLoop();
    return 0;
}

