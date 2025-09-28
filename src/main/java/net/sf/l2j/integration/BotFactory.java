package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.templates.L2PcTemplate;

import java.util.Random;

/**
 * Фабрика для создания ботов в L2J сервере.
 * 
 * Создает L2PcInstance и оборачивает их в EnhancedFakePlayer
 * для интеграции с системой ботов.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class BotFactory {
    
    private static final Logger logger = Logger.getLogger(BotFactory.class);
    
    /** Счетчик для генерации уникальных ID ботов */
    private static int botIdCounter = 1000000; // Начинаем с большого числа, чтобы избежать конфликтов
    
    /** Генератор случайных чисел */
    private static final Random random = new Random();
    
    /**
     * Создает бота в L2J сервере.
     * 
     * @param botType тип бота
     * @param name имя бота
     * @param level уровень бота
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return созданный бот или null при ошибке
     */
    public static EnhancedFakePlayer createBot(BotType botType, String name, int level, int x, int y, int z) {
        try {
            // Генерируем уникальный ID для бота
            int botId = generateBotId();
            
            // Создаем контекст бота
            BotContext context = new BotContext(botId);
            context.setData("botType", botType);
            context.setData("botName", name);
            context.setData("botLevel", level);
            
            // Выбираем класс в зависимости от типа бота
            ClassId classId = selectClassForBotType(botType);
            L2PcTemplate template = CharTemplateTable.getInstance().getTemplate(classId);
            
            if (template == null) {
                logger.error("Failed to get template for class: " + classId);
                return null;
            }
            
            // Создаем L2PcInstance
            L2PcInstance playerInstance = createL2PcInstance(botId, template, name, level, x, y, z);
            
            if (playerInstance == null) {
                logger.error("Failed to create L2PcInstance for bot: " + name);
                return null;
            }
            
            // Создаем EnhancedFakePlayer
            EnhancedFakePlayer bot = new EnhancedFakePlayer(context, playerInstance);
            
            // Настраиваем бота
            configureBot(bot, botType, level);
            
            // Добавляем в мир L2J
            // TODO: Добавить метод addPlayer в L2World
            
            logger.info("Created bot: " + name + " (ID: " + botId + ", Type: " + botType + ", Level: " + level + ")");
            
            return bot;
            
        } catch (Exception e) {
            logger.error("Error creating bot: " + name, e);
            return null;
        }
    }
    
    /**
     * Создает L2PcInstance для бота.
     * 
     * @param objectId ID объекта
     * @param template шаблон персонажа
     * @param name имя персонажа
     * @param level уровень
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return созданный L2PcInstance или null при ошибке
     */
    private static L2PcInstance createL2PcInstance(int objectId, L2PcTemplate template, String name, int level, int x, int y, int z) {
        try {
            // Создаем внешность персонажа
            net.sf.l2j.gameserver.model.actor.appearance.PcAppearance appearance = 
                new net.sf.l2j.gameserver.model.actor.appearance.PcAppearance(
                    (byte) random.nextInt(3), // face
                    (byte) random.nextInt(3), // hairColor
                    (byte) random.nextInt(3), // hairStyle
                    random.nextBoolean()      // sex
                );
            
            // Создаем L2PcInstance
            // TODO: Использовать правильный конструктор L2PcInstance
            L2PcInstance player = null;
            
            if (player != null) {
                // Устанавливаем имя
                player.setName(name);
                
                // Устанавливаем уровень
                player.getStat().setLevel((byte) level);
                
                // Устанавливаем позицию
                player.setXYZ(x, y, z);
                player.setHeading(0);
                
                // Устанавливаем базовые характеристики
                player.setBaseClass(player.getClassId());
                
                // Устанавливаем флаг бота
                // TODO: Добавить метод setIsFakePlayer
                
                // Инициализируем инвентарь
                player.getInventory().restore();
            }
            
            return player;
            
        } catch (Exception e) {
            logger.error("Error creating L2PcInstance", e);
            return null;
        }
    }
    
    /**
     * Выбирает класс для типа бота.
     * 
     * @param botType тип бота
     * @return ID класса
     */
    private static ClassId selectClassForBotType(BotType botType) {
        switch (botType) {
            case SOLDIER:
                // Воины
                return ClassId.values()[random.nextInt(3) + 1]; // Human Fighter, Warrior, Gladiator
            case OFFICER:
                // Офицеры - более продвинутые классы
                return ClassId.values()[random.nextInt(5) + 10]; // Paladin, Dark Avenger, etc.
            case HIGH_OFFICER:
                // Высшие офицеры - элитные классы
                return ClassId.values()[random.nextInt(3) + 20]; // Elite classes
            case VICE_GUILDMASTER:
                // Вице-гильдмастеры - магические классы
                return ClassId.values()[random.nextInt(3) + 30]; // Mage classes
            case FARMER:
                // Фермеры - базовые классы
                return ClassId.values()[random.nextInt(3) + 1]; // Basic classes
            case MERCHANT:
                // Торговцы - классы с навыками торговли
                return ClassId.values()[random.nextInt(3) + 40]; // Merchant classes
            case GUARD:
                // Охранники - защитные классы
                return ClassId.values()[random.nextInt(3) + 15]; // Guardian classes
            default:
                // По умолчанию - базовый воин
                return ClassId.values()[0];
        }
    }
    
    /**
     * Настраивает бота в зависимости от его типа.
     * 
     * @param bot бот
     * @param botType тип бота
     * @param level уровень
     */
    private static void configureBot(EnhancedFakePlayer bot, BotType botType, int level) {
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return;
        }
        
        // Устанавливаем базовые характеристики в зависимости от типа
        switch (botType) {
            case SOLDIER:
                // Солдаты - средние характеристики
                player.getStat().setExp(level * 1000L);
                break;
            case OFFICER:
                // Офицеры - высокие характеристики
                player.getStat().setExp(level * 1500L);
                break;
            case HIGH_OFFICER:
                // Высшие офицеры - очень высокие характеристики
                player.getStat().setExp(level * 2000L);
                break;
            case VICE_GUILDMASTER:
                // Вице-гильдмастеры - максимальные характеристики
                player.getStat().setExp(level * 2500L);
                break;
            case FARMER:
                // Фермеры - низкие характеристики
                player.getStat().setExp(level * 500L);
                break;
            case MERCHANT:
                // Торговцы - средние характеристики
                player.getStat().setExp(level * 1000L);
                break;
            case GUARD:
                // Охранники - высокие характеристики
                player.getStat().setExp(level * 1500L);
                break;
        }
        
        // Устанавливаем здоровье и ману
        player.setCurrentHp(player.getMaxHp());
        player.setCurrentMp(player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
        
        // Активируем бота
        bot.activate();
    }
    
    /**
     * Генерирует уникальный ID для бота.
     * 
     * @return уникальный ID
     */
    private static int generateBotId() {
        return botIdCounter++;
    }
    
    /**
     * Удаляет бота из L2J сервера.
     * 
     * @param bot бот для удаления
     */
    public static void removeBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            return;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player != null) {
            try {
            // Удаляем из мира L2J
            // TODO: Добавить метод removePlayer в L2World
                
                // Деактивируем бота
                bot.deactivate();
                
                logger.info("Removed bot: " + player.getName() + " (ID: " + player.getObjectId() + ")");
                
            } catch (Exception e) {
                logger.error("Error removing bot: " + player.getName(), e);
            }
        }
    }
    
    /**
     * Создает бота с случайными параметрами.
     * 
     * @param botType тип бота
     * @return созданный бот или null при ошибке
     */
    public static EnhancedFakePlayer createRandomBot(BotType botType) {
        String name = "Bot_" + botType.name() + "_" + System.currentTimeMillis();
        int level = random.nextInt(20) + 1; // Уровень от 1 до 20
        int x = random.nextInt(1000) - 500; // Случайная позиция
        int y = random.nextInt(1000) - 500;
        int z = 0;
        
        return createBot(botType, name, level, x, y, z);
    }
    
    /**
     * Создает группу ботов для замка.
     * 
     * @param castleId ID замка
     * @param botCount количество ботов
     * @return массив созданных ботов
     */
    public static EnhancedFakePlayer[] createCastleBots(int castleId, int botCount) {
        EnhancedFakePlayer[] bots = new EnhancedFakePlayer[botCount];
        
        for (int i = 0; i < botCount; i++) {
            BotType botType = BotType.values()[random.nextInt(BotType.values().length)];
            bots[i] = createRandomBot(botType);
        }
        
        return bots;
    }
}
