package net.sf.l2j.botmanager.web;

import net.sf.l2j.botmanager.web.controller.BotController;
import net.sf.l2j.botmanager.utils.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

/**
 * Простой веб-сервер для REST API управления ботами
 * Использует встроенный HTTP сервер Java
 */
public class WebServer {
    
    private static final Logger logger = Logger.getLogger(WebServer.class);
    private static WebServer instance;
    
    private HttpServer server;
    private BotController botController;
    private int port;
    private boolean running;
    
    /**
     * Конструктор
     */
    private WebServer() {
        this.port = 8080;
        this.running = false;
        this.botController = new BotController();
    }
    
    /**
     * Получить экземпляр веб-сервера
     * @return экземпляр веб-сервера
     */
    public static synchronized WebServer getInstance() {
        if (instance == null) {
            instance = new WebServer();
        }
        return instance;
    }
    
    /**
     * Запустить веб-сервер
     * @param port порт для сервера
     * @return true если сервер успешно запущен
     */
    public boolean start(int port) {
        if (running) {
            logger.warn("Web server is already running");
            return true;
        }
        
        try {
            this.port = port;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Настроить маршруты
            setupRoutes();
            
            // Запустить сервер
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            
            running = true;
            logger.info("Web server started on port " + port);
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to start web server on port " + port + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Остановить веб-сервер
     */
    public void stop() {
        if (!running) {
            logger.warn("Web server is not running");
            return;
        }
        
        if (server != null) {
            server.stop(0);
            server = null;
        }
        
        running = false;
        logger.info("Web server stopped");
    }
    
    /**
     * Проверить запущен ли сервер
     * @return true если сервер запущен
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Получить порт сервера
     * @return порт сервера
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Настроить маршруты API
     */
    private void setupRoutes() {
        // GET /api/bots - получить всех ботов
        server.createContext("/api/bots", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetAllBots(exchange);
                } else {
                    sendResponse(exchange, 405, "Method Not Allowed");
                }
            }
        });
        
        // GET /api/bots/{id} - получить бота по ID
        server.createContext("/api/bots/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();
                
                if (path.matches("/api/bots/\\d+")) {
                    int botId = extractBotId(path);
                    if ("GET".equals(method)) {
                        handleGetBot(exchange, botId);
                    } else if ("DELETE".equals(method)) {
                        handleDeleteBot(exchange, botId);
                    } else {
                        sendResponse(exchange, 405, "Method Not Allowed");
                    }
                } else if (path.matches("/api/bots/\\d+/status")) {
                    int botId = extractBotId(path);
                    if ("GET".equals(method)) {
                        handleGetBotStatus(exchange, botId);
                    } else {
                        sendResponse(exchange, 405, "Method Not Allowed");
                    }
                } else if (path.matches("/api/bots/\\d+/statistics")) {
                    int botId = extractBotId(path);
                    if ("GET".equals(method)) {
                        handleGetBotStatistics(exchange, botId);
                    } else {
                        sendResponse(exchange, 405, "Method Not Allowed");
                    }
                } else {
                    sendResponse(exchange, 404, "Not Found");
                }
            }
        });
        
        // GET /api/statistics - получить общую статистику
        server.createContext("/api/statistics", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetOverallStatistics(exchange);
                } else {
                    sendResponse(exchange, 405, "Method Not Allowed");
                }
            }
        });
        
        // GET /api/health - проверить состояние системы
        server.createContext("/api/health", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetHealth(exchange);
                } else {
                    sendResponse(exchange, 405, "Method Not Allowed");
                }
            }
        });
    }
    
    /**
     * Обработать запрос получения всех ботов
     */
    private void handleGetAllBots(HttpExchange exchange) throws IOException {
        try {
            List<net.sf.l2j.botmanager.web.dto.BotDTO> bots = botController.getAllBots();
            String response = convertToJson(bots);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error handling get all bots: " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос получения бота по ID
     */
    private void handleGetBot(HttpExchange exchange, int botId) throws IOException {
        try {
            net.sf.l2j.botmanager.web.dto.BotDTO bot = botController.getBot(botId);
            if (bot != null) {
                String response = convertToJson(bot);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Bot not found");
            }
        } catch (Exception e) {
            logger.error("Error handling get bot " + botId + ": " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос удаления бота
     */
    private void handleDeleteBot(HttpExchange exchange, int botId) throws IOException {
        try {
            net.sf.l2j.botmanager.web.dto.CommandResponseDTO result = botController.removeBot(botId);
            String response = convertToJson(result);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error handling delete bot " + botId + ": " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос получения статуса бота
     */
    private void handleGetBotStatus(HttpExchange exchange, int botId) throws IOException {
        try {
            net.sf.l2j.botmanager.web.dto.BotDTO status = botController.getBotStatus(botId);
            if (status != null) {
                String response = convertToJson(status);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Bot status not found");
            }
        } catch (Exception e) {
            logger.error("Error handling get bot status " + botId + ": " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос получения статистики бота
     */
    private void handleGetBotStatistics(HttpExchange exchange, int botId) throws IOException {
        try {
            net.sf.l2j.botmanager.web.dto.BotStatisticsDTO stats = botController.getBotStatistics(botId);
            if (stats != null) {
                String response = convertToJson(stats);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "Bot statistics not found");
            }
        } catch (Exception e) {
            logger.error("Error handling get bot statistics " + botId + ": " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос получения общей статистики
     */
    private void handleGetOverallStatistics(HttpExchange exchange) throws IOException {
        try {
            net.sf.l2j.botmanager.web.dto.OverallStatisticsDTO stats = botController.getOverallStatistics();
            String response = convertToJson(stats);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error handling get overall statistics: " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Обработать запрос проверки состояния системы
     */
    private void handleGetHealth(HttpExchange exchange) throws IOException {
        try {
            Map<String, Object> health = botController.getHealth();
            String response = convertToJson(health);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error handling get health: " + e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    /**
     * Извлечь ID бота из пути
     */
    private int extractBotId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
    
    /**
     * Отправить HTTP ответ
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * Конвертировать объект в JSON (простая реализация)
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        // Простая реализация для демонстрации
        return "{\"message\": \"JSON conversion not implemented\", \"type\": \"" + obj.getClass().getSimpleName() + "\"}";
    }
}
