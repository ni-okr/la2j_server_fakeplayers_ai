package net.sf.l2j.botmanager.web.service;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.management.BotManagementSystem;
import net.sf.l2j.botmanager.management.statistics.BotStatistics;
import net.sf.l2j.botmanager.management.monitoring.BotStatus;
import net.sf.l2j.botmanager.web.dto.*;
import net.sf.l2j.botmanager.utils.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Веб-сервис для управления ботами через REST API
 */
public class BotWebService {
    
    private static final Logger logger = Logger.getLogger(BotWebService.class);
    private final BotManagementSystem botManagement;
    
    /**
     * Конструктор
     */
    public BotWebService() {
        this.botManagement = BotManagementSystem.getInstance();
    }
    
    /**
     * Получить список всех ботов
     * @return список DTO ботов
     */
    public List<BotDTO> getAllBots() {
        try {
            List<EnhancedFakePlayer> bots = botManagement.getAllBots();
            List<BotDTO> botDTOs = new ArrayList<>();
            
            for (EnhancedFakePlayer bot : bots) {
                BotDTO dto = convertToBotDTO(bot);
                botDTOs.add(dto);
            }
            
            logger.info("Retrieved " + botDTOs.size() + " bots via web service");
            return botDTOs;
            
        } catch (Exception e) {
            logger.error("Error retrieving all bots: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Получить бота по ID
     * @param botId ID бота
     * @return DTO бота или null
     */
    public BotDTO getBot(int botId) {
        try {
            EnhancedFakePlayer bot = botManagement.getBot(botId);
            if (bot == null) {
                logger.warn("Bot " + botId + " not found");
                return null;
            }
            
            BotDTO dto = convertToBotDTO(bot);
            logger.info("Retrieved bot " + botId + " via web service");
            return dto;
            
        } catch (Exception e) {
            logger.error("Error retrieving bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Получить ботов по типу
     * @param botType тип бота
     * @return список DTO ботов
     */
    public List<BotDTO> getBotsByType(BotType botType) {
        try {
            List<EnhancedFakePlayer> bots = botManagement.getBotsByType(botType);
            List<BotDTO> botDTOs = new ArrayList<>();
            
            for (EnhancedFakePlayer bot : bots) {
                BotDTO dto = convertToBotDTO(bot);
                botDTOs.add(dto);
            }
            
            logger.info("Retrieved " + botDTOs.size() + " bots of type " + botType + " via web service");
            return botDTOs;
            
        } catch (Exception e) {
            logger.error("Error retrieving bots by type " + botType + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Получить статус бота
     * @param botId ID бота
     * @return DTO статуса бота или null
     */
    public BotDTO getBotStatus(int botId) {
        try {
            BotStatus status = botManagement.getBotStatus(botId);
            if (status == null) {
                logger.warn("Status for bot " + botId + " not found");
                return null;
            }
            
            BotDTO dto = convertStatusToBotDTO(status);
            logger.info("Retrieved status for bot " + botId + " via web service");
            return dto;
            
        } catch (Exception e) {
            logger.error("Error retrieving status for bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Получить статистику бота
     * @param botId ID бота
     * @return DTO статистики бота или null
     */
    public BotStatisticsDTO getBotStatistics(int botId) {
        try {
            BotStatistics stats = botManagement.getBotStatistics(botId);
            if (stats == null) {
                logger.warn("Statistics for bot " + botId + " not found");
                return null;
            }
            
            BotStatisticsDTO dto = convertToBotStatisticsDTO(stats);
            logger.info("Retrieved statistics for bot " + botId + " via web service");
            return dto;
            
        } catch (Exception e) {
            logger.error("Error retrieving statistics for bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Получить общую статистику системы
     * @return DTO общей статистики
     */
    public OverallStatisticsDTO getOverallStatistics() {
        try {
            Map<String, Object> stats = botManagement.getOverallStatistics();
            Map<BotType, Integer> botTypeStats = botManagement.getBotTypeStatistics();
            Map<BehaviorType, Integer> behaviorStats = botManagement.getBehaviorStatistics();
            
            OverallStatisticsDTO dto = new OverallStatisticsDTO(
                (Integer) stats.get("totalBots"),
                (Integer) stats.get("activeBots"),
                (Integer) stats.get("inactiveBots"),
                (Long) stats.get("systemUptime"),
                (Boolean) stats.get("initialized"),
                botTypeStats,
                behaviorStats,
                stats
            );
            
            logger.info("Retrieved overall statistics via web service");
            return dto;
            
        } catch (Exception e) {
            logger.error("Error retrieving overall statistics: " + e.getMessage());
            return new OverallStatisticsDTO();
        }
    }
    
    /**
     * Создать бота
     * @param botType тип бота
     * @param name имя бота
     * @param level уровень бота
     * @return DTO созданного бота или null
     */
    public BotDTO createBot(BotType botType, String name, int level) {
        try {
            EnhancedFakePlayer bot = botManagement.createBot(botType, name, level);
            if (bot == null) {
                logger.warn("Failed to create bot: " + name + " (" + botType + ", level " + level + ")");
                return null;
            }
            
            BotDTO dto = convertToBotDTO(bot);
            logger.info("Created bot " + name + " (" + botType + ", level " + level + ") via web service");
            return dto;
            
        } catch (Exception e) {
            logger.error("Error creating bot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Удалить бота
     * @param botId ID бота
     * @return true если бот успешно удален
     */
    public boolean removeBot(int botId) {
        try {
            boolean success = botManagement.removeBot(botId);
            if (success) {
                logger.info("Removed bot " + botId + " via web service");
            } else {
                logger.warn("Failed to remove bot " + botId);
            }
            return success;
            
        } catch (Exception e) {
            logger.error("Error removing bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Установить поведение для бота
     * @param botId ID бота
     * @param behaviorType тип поведения
     * @return true если поведение успешно установлено
     */
    public boolean setBotBehavior(int botId, BehaviorType behaviorType) {
        try {
            boolean success = botManagement.setBotBehavior(botId, behaviorType);
            if (success) {
                logger.info("Set behavior " + behaviorType + " for bot " + botId + " via web service");
            } else {
                logger.warn("Failed to set behavior " + behaviorType + " for bot " + botId);
            }
            return success;
            
        } catch (Exception e) {
            logger.error("Error setting behavior for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Активировать бота
     * @param botId ID бота
     * @return true если бот успешно активирован
     */
    public boolean activateBot(int botId) {
        try {
            boolean success = botManagement.activateBot(botId);
            if (success) {
                logger.info("Activated bot " + botId + " via web service");
            } else {
                logger.warn("Failed to activate bot " + botId);
            }
            return success;
            
        } catch (Exception e) {
            logger.error("Error activating bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Деактивировать бота
     * @param botId ID бота
     * @return true если бот успешно деактивирован
     */
    public boolean deactivateBot(int botId) {
        try {
            boolean success = botManagement.deactivateBot(botId);
            if (success) {
                logger.info("Deactivated bot " + botId + " via web service");
            } else {
                logger.warn("Failed to deactivate bot " + botId);
            }
            return success;
            
        } catch (Exception e) {
            logger.error("Error deactivating bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Конвертировать EnhancedFakePlayer в BotDTO
     * @param bot бот
     * @return DTO бота
     */
    private BotDTO convertToBotDTO(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        BotDTO dto = new BotDTO();
        dto.setId(bot.getContext().getBotId());
        dto.setName("Bot" + bot.getContext().getBotId()); // TODO: Get actual name
        dto.setType(BotType.FARMER); // TODO: Get actual type
        dto.setLevel(50); // TODO: Get actual level
        dto.setCurrentBehavior(BehaviorType.IDLE); // TODO: Get actual behavior
        dto.setActive(botManagement.isBotActive(bot.getContext().getBotId()));
        dto.setStatus("Unknown"); // TODO: Get actual status
        dto.setLocation("(0, 0, 0)"); // TODO: Get actual location
        dto.setHealthPercentage(100.0); // TODO: Get actual health
        dto.setManaPercentage(100.0); // TODO: Get actual mana
        dto.setLastAction("None"); // TODO: Get actual last action
        dto.setUptime(0); // TODO: Get actual uptime
        dto.setCreatedTime(LocalDateTime.now()); // TODO: Get actual creation time
        dto.setLastUpdate(LocalDateTime.now());
        
        return dto;
    }
    
    /**
     * Конвертировать BotStatus в BotDTO
     * @param status статус бота
     * @return DTO бота
     */
    private BotDTO convertStatusToBotDTO(BotStatus status) {
        if (status == null) {
            return null;
        }
        
        BotDTO dto = new BotDTO();
        dto.setId(status.getBotId());
        dto.setName(status.getBotName());
        dto.setType(status.getBotType());
        dto.setLevel(status.getLevel());
        dto.setCurrentBehavior(status.getCurrentBehavior());
        dto.setActive(status.isActive());
        dto.setStatus(status.getCurrentState().toString());
        dto.setLocation(status.getLocation());
        dto.setHealthPercentage(status.getHealthPercentage());
        dto.setManaPercentage(status.getManaPercentage());
        dto.setLastAction(status.getLastAction());
        dto.setUptime(status.getUptime());
        dto.setCreatedTime(LocalDateTime.now()); // TODO: Get actual creation time
        dto.setLastUpdate(status.getLastUpdate());
        
        return dto;
    }
    
    /**
     * Конвертировать BotStatistics в BotStatisticsDTO
     * @param stats статистика бота
     * @return DTO статистики бота
     */
    private BotStatisticsDTO convertToBotStatisticsDTO(BotStatistics stats) {
        if (stats == null) {
            return null;
        }
        
        BotStatisticsDTO dto = new BotStatisticsDTO();
        dto.setBotId(stats.getBotId());
        dto.setBotName(stats.getBotName());
        dto.setBotType(stats.getBotType());
        dto.setCreatedTime(stats.getCreatedTime());
        dto.setTotalUptime(stats.getTotalUptime());
        dto.setTotalActions(stats.getTotalActions());
        dto.setTotalKills(stats.getTotalKills());
        dto.setTotalDeaths(stats.getTotalDeaths());
        dto.setTotalExperience(stats.getTotalExperience());
        dto.setTotalGold(stats.getTotalGold());
        dto.setBehaviorUsage(stats.getBehaviorUsage());
        dto.setActionCounts(stats.getActionCounts());
        dto.setAveragePerformance(stats.getAveragePerformance());
        dto.setCurrentLevel(stats.getCurrentLevel());
        dto.setLastActivity(stats.getLastActivity());
        dto.setKillDeathRatio(stats.getKillDeathRatio());
        dto.setActionsPerHour(stats.getActionsPerHour());
        
        return dto;
    }
}
