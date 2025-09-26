package net.sf.l2j.botmanager.management;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.management.statistics.BotStatistics;
import net.sf.l2j.botmanager.management.monitoring.BotStatus;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс системы управления ботами
 * Предоставляет методы для управления жизненным циклом ботов,
 * мониторинга их состояния и получения статистики
 */
public interface IBotManagementSystem {
    
    /**
     * Создать нового бота
     * @param botType тип бота
     * @param name имя бота
     * @param level уровень бота
     * @return созданный бот или null в случае ошибки
     */
    EnhancedFakePlayer createBot(BotType botType, String name, int level);
    
    /**
     * Удалить бота
     * @param botId ID бота
     * @return true если бот успешно удален
     */
    boolean removeBot(int botId);
    
    /**
     * Получить бота по ID
     * @param botId ID бота
     * @return бот или null если не найден
     */
    EnhancedFakePlayer getBot(int botId);
    
    /**
     * Получить список всех ботов
     * @return список ботов
     */
    List<EnhancedFakePlayer> getAllBots();
    
    /**
     * Получить ботов по типу
     * @param botType тип бота
     * @return список ботов указанного типа
     */
    List<EnhancedFakePlayer> getBotsByType(BotType botType);
    
    /**
     * Установить поведение для бота
     * @param botId ID бота
     * @param behaviorType тип поведения
     * @return true если поведение успешно установлено
     */
    boolean setBotBehavior(int botId, BehaviorType behaviorType);
    
    /**
     * Получить текущее поведение бота
     * @param botId ID бота
     * @return тип поведения или null
     */
    BehaviorType getBotBehavior(int botId);
    
    /**
     * Активировать бота
     * @param botId ID бота
     * @return true если бот успешно активирован
     */
    boolean activateBot(int botId);
    
    /**
     * Деактивировать бота
     * @param botId ID бота
     * @return true если бот успешно деактивирован
     */
    boolean deactivateBot(int botId);
    
    /**
     * Проверить активен ли бот
     * @param botId ID бота
     * @return true если бот активен
     */
    boolean isBotActive(int botId);
    
    /**
     * Получить статус бота
     * @param botId ID бота
     * @return статус бота
     */
    BotStatus getBotStatus(int botId);
    
    /**
     * Получить статистику бота
     * @param botId ID бота
     * @return статистика бота
     */
    BotStatistics getBotStatistics(int botId);
    
    /**
     * Получить общую статистику по всем ботам
     * @return общая статистика
     */
    Map<String, Object> getOverallStatistics();
    
    /**
     * Получить статистику по типам ботов
     * @return статистика по типам
     */
    Map<BotType, Integer> getBotTypeStatistics();
    
    /**
     * Получить статистику по поведениям
     * @return статистика по поведениям
     */
    Map<BehaviorType, Integer> getBehaviorStatistics();
    
    /**
     * Инициализировать систему управления
     */
    void initialize();
    
    /**
     * Завершить работу системы управления
     */
    void shutdown();
    
    /**
     * Проверить инициализирована ли система
     * @return true если система инициализирована
     */
    boolean isInitialized();
}
