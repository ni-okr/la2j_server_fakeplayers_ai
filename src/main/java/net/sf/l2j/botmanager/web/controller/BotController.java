package net.sf.l2j.botmanager.web.controller;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.web.dto.*;
import net.sf.l2j.botmanager.web.service.BotWebService;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST контроллер для управления ботами
 * Предоставляет HTTP API для управления ботами через веб-интерфейс
 */
public class BotController {
    
    private static final Logger logger = Logger.getLogger(BotController.class);
    private final BotWebService botWebService;
    
    /**
     * Конструктор
     */
    public BotController() {
        this.botWebService = new BotWebService();
    }
    
    /**
     * GET /api/bots - Получить список всех ботов
     * @return список всех ботов
     */
    public List<BotDTO> getAllBots() {
        logger.info("REST API: Getting all bots");
        return botWebService.getAllBots();
    }
    
    /**
     * GET /api/bots/{id} - Получить бота по ID
     * @param botId ID бота
     * @return бот или null
     */
    public BotDTO getBot(int botId) {
        logger.info("REST API: Getting bot " + botId);
        return botWebService.getBot(botId);
    }
    
    /**
     * GET /api/bots/type/{type} - Получить ботов по типу
     * @param botType тип бота
     * @return список ботов указанного типа
     */
    public List<BotDTO> getBotsByType(String botType) {
        logger.info("REST API: Getting bots by type " + botType);
        try {
            BotType type = BotType.valueOf(botType.toUpperCase());
            return botWebService.getBotsByType(type);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid bot type: " + botType);
            return List.of();
        }
    }
    
    /**
     * GET /api/bots/{id}/status - Получить статус бота
     * @param botId ID бота
     * @return статус бота
     */
    public BotDTO getBotStatus(int botId) {
        logger.info("REST API: Getting status for bot " + botId);
        return botWebService.getBotStatus(botId);
    }
    
    /**
     * GET /api/bots/{id}/statistics - Получить статистику бота
     * @param botId ID бота
     * @return статистика бота
     */
    public BotStatisticsDTO getBotStatistics(int botId) {
        logger.info("REST API: Getting statistics for bot " + botId);
        return botWebService.getBotStatistics(botId);
    }
    
    /**
     * GET /api/statistics - Получить общую статистику системы
     * @return общая статистика
     */
    public OverallStatisticsDTO getOverallStatistics() {
        logger.info("REST API: Getting overall statistics");
        return botWebService.getOverallStatistics();
    }
    
    /**
     * POST /api/bots - Создать нового бота
     * @param request запрос на создание бота
     * @return созданный бот
     */
    public BotDTO createBot(CreateBotRequestDTO request) {
        logger.info("REST API: Creating bot " + request.getName() + " (" + request.getType() + ", level " + request.getLevel() + ")");
        
        if (!request.isValid()) {
            logger.warn("Invalid create bot request");
            return null;
        }
        
        return botWebService.createBot(request.getType(), request.getName(), request.getLevel());
    }
    
    /**
     * DELETE /api/bots/{id} - Удалить бота
     * @param botId ID бота
     * @return результат удаления
     */
    public CommandResponseDTO removeBot(int botId) {
        logger.info("REST API: Removing bot " + botId);
        
        boolean success = botWebService.removeBot(botId);
        if (success) {
            return CommandResponseDTO.success("Bot " + botId + " removed successfully");
        } else {
            return CommandResponseDTO.failure("Failed to remove bot " + botId);
        }
    }
    
    /**
     * PUT /api/bots/{id}/behavior - Установить поведение для бота
     * @param botId ID бота
     * @param request запрос на установку поведения
     * @return результат установки поведения
     */
    public CommandResponseDTO setBotBehavior(int botId, SetBehaviorRequestDTO request) {
        logger.info("REST API: Setting behavior " + request.getBehaviorType() + " for bot " + botId);
        
        if (!request.isValid()) {
            return CommandResponseDTO.failure("Invalid behavior request");
        }
        
        boolean success = botWebService.setBotBehavior(botId, request.getBehaviorType());
        if (success) {
            return CommandResponseDTO.success("Behavior " + request.getBehaviorType() + " set for bot " + botId);
        } else {
            return CommandResponseDTO.failure("Failed to set behavior for bot " + botId);
        }
    }
    
    /**
     * POST /api/bots/{id}/activate - Активировать бота
     * @param botId ID бота
     * @return результат активации
     */
    public CommandResponseDTO activateBot(int botId) {
        logger.info("REST API: Activating bot " + botId);
        
        boolean success = botWebService.activateBot(botId);
        if (success) {
            return CommandResponseDTO.success("Bot " + botId + " activated successfully");
        } else {
            return CommandResponseDTO.failure("Failed to activate bot " + botId);
        }
    }
    
    /**
     * POST /api/bots/{id}/deactivate - Деактивировать бота
     * @param botId ID бота
     * @return результат деактивации
     */
    public CommandResponseDTO deactivateBot(int botId) {
        logger.info("REST API: Deactivating bot " + botId);
        
        boolean success = botWebService.deactivateBot(botId);
        if (success) {
            return CommandResponseDTO.success("Bot " + botId + " deactivated successfully");
        } else {
            return CommandResponseDTO.failure("Failed to deactivate bot " + botId);
        }
    }
    
    /**
     * POST /api/commands - Выполнить команду
     * @param request запрос на выполнение команды
     * @return результат выполнения команды
     */
    public CommandResponseDTO executeCommand(CommandRequestDTO request) {
        logger.info("REST API: Executing command '" + request.getFullCommand() + "' by " + request.getExecutorId());
        
        if (!request.isValid()) {
            return CommandResponseDTO.failure("Invalid command request");
        }
        
        // TODO: Implement command execution through AdminCommandManager
        // For now, return a mock response
        return CommandResponseDTO.success("Command executed successfully (mock response)");
    }
    
    /**
     * GET /api/health - Проверить состояние системы
     * @return состояние системы
     */
    public Map<String, Object> getHealth() {
        logger.info("REST API: Getting system health");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.8");
        health.put("initialized", true);
        
        return health;
    }
}
