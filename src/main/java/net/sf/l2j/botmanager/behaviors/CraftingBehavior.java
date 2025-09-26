package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение крафта
 * 
 * Бот ищет мастеров крафта, проверяет рецепты, собирает материалы
 * и создает предметы.
 */
public class CraftingBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(CraftingBehavior.class);
    
    // Константы для крафта
    private static final int SEARCH_RADIUS = 2000; // Радиус поиска мастеров
    private static final int CRAFT_RADIUS = 100; // Радиус для крафта
    private static final int MIN_HP_PERCENT = 50; // Минимальный процент HP для крафта
    private static final int MIN_MP_PERCENT = 30; // Минимальный процент MP для крафта
    
    // Состояния крафта
    private enum CraftingState {
        SEARCHING_MASTER,  // Поиск мастера крафта
        MOVING_TO_MASTER,  // Движение к мастеру
        CHECKING_RECIPES,  // Проверка рецептов
        GATHERING_MATERIALS, // Сбор материалов
        CRAFTING,          // Создание предметов
        RESTING            // Отдых
    }
    
    // Типы мастеров крафта
    private enum CraftMasterType {
        WEAPON_SMITH,      // Оружейник
        ARMOR_SMITH,       // Бронник
        JEWELER,           // Ювелир
        ALCHEMIST,         // Алхимик
        COOK,              // Повар
        DYE_MASTER         // Мастер красок
    }
    
    public CraftingBehavior() {
        super(BehaviorType.CRAFTING);
        setMinExecutionInterval(1000); // Минимум 1 секунда между действиями
        setMaxExecutionInterval(5000); // Максимум 5 секунд
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Получаем текущее состояние крафта
        CraftingState currentState = getCraftingState(context);
        
        _log.debug("Crafting state: " + currentState + " for bot " + bot.getContext().getBotId());
        
        switch (currentState) {
            case SEARCHING_MASTER:
                return searchForCraftMaster(bot, context, player);
                
            case MOVING_TO_MASTER:
                return moveToCraftMaster(bot, context, player);
                
            case CHECKING_RECIPES:
                return checkRecipes(bot, context, player);
                
            case GATHERING_MATERIALS:
                return gatherMaterials(bot, context, player);
                
            case CRAFTING:
                return performCrafting(bot, context, player);
                
            case RESTING:
                return rest(bot, context, player);
                
            default:
                setCraftingState(context, CraftingState.SEARCHING_MASTER);
                return true;
        }
    }
    
    /**
     * Поиск мастера крафта
     */
    private boolean searchForCraftMaster(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Searching for craft master...");
        
        // Ищем ближайшего мастера крафта
        L2NpcInstance craftMaster = findNearestCraftMaster(player);
        
        if (craftMaster != null) {
            // Сохраняем информацию о мастере
            context.setData("craftMasterId", craftMaster.getObjectId());
            context.setData("craftMasterX", craftMaster.getX());
            context.setData("craftMasterY", craftMaster.getY());
            context.setData("craftMasterZ", craftMaster.getZ());
            
            setCraftingState(context, CraftingState.MOVING_TO_MASTER);
            _log.info("Found craft master: " + craftMaster.getName() + " at (" + 
                     craftMaster.getX() + ", " + craftMaster.getY() + ", " + craftMaster.getZ() + ")");
            return true;
        }
        
        // Если мастер не найден, ищем случайным образом
        if (ThreadLocalRandom.current().nextInt(100) < 10) { // 10% шанс
            moveRandomly(bot, context, player);
        }
        
        return true;
    }
    
    /**
     * Движение к мастеру крафта
     */
    private boolean moveToCraftMaster(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        Integer masterX = (Integer) context.getData("craftMasterX");
        Integer masterY = (Integer) context.getData("craftMasterY");
        Integer masterZ = (Integer) context.getData("craftMasterZ");
        
        if (masterX == null || masterY == null || masterZ == null) {
            setCraftingState(context, CraftingState.SEARCHING_MASTER);
            return true;
        }
        
        double distance = player.getDistance(masterX, masterY, masterZ);
        
        if (distance <= CRAFT_RADIUS) {
            // Достигли мастера
            setCraftingState(context, CraftingState.CHECKING_RECIPES);
            _log.info("Reached craft master, checking recipes...");
            return true;
        }
        
        // Движемся к мастеру
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to craft master at (" + masterX + ", " + masterY + ", " + masterZ + ")");
        return true;
    }
    
    /**
     * Проверка рецептов
     */
    private boolean checkRecipes(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Checking available recipes...");
        
        // Симулируем проверку рецептов
        // В реальной реализации здесь будет проверка доступных рецептов
        boolean hasRecipes = ThreadLocalRandom.current().nextBoolean();
        
        if (hasRecipes) {
            // Выбираем случайный рецепт для крафта
            String recipe = getRandomRecipe();
            context.setData("selectedRecipe", recipe);
            context.setData("craftingProgress", 0);
            
            setCraftingState(context, CraftingState.GATHERING_MATERIALS);
            _log.info("Selected recipe: " + recipe);
            return true;
        } else {
            // Нет доступных рецептов, отдыхаем
            setCraftingState(context, CraftingState.RESTING);
            return true;
        }
    }
    
    /**
     * Сбор материалов
     */
    private boolean gatherMaterials(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Gathering materials...");
        
        // Симулируем сбор материалов
        // В реальной реализации здесь будет проверка инвентаря и сбор недостающих материалов
        int progress = (Integer) context.getData("craftingProgress");
        progress += ThreadLocalRandom.current().nextInt(10, 30);
        
        context.setData("craftingProgress", progress);
        
        if (progress >= 100) {
            setCraftingState(context, CraftingState.CRAFTING);
            _log.info("Materials gathered, starting crafting...");
        }
        
        return true;
    }
    
    /**
     * Выполнение крафта
     */
    private boolean performCrafting(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Performing crafting...");
        
        String recipe = (String) context.getData("selectedRecipe");
        if (recipe == null) {
            setCraftingState(context, CraftingState.CHECKING_RECIPES);
            return true;
        }
        
        // Симулируем процесс крафта
        // В реальной реализации здесь будет вызов системы крафта L2J
        boolean success = ThreadLocalRandom.current().nextInt(100) < 80; // 80% шанс успеха
        
        if (success) {
            _log.info("Successfully crafted: " + recipe);
            // Очищаем данные о крафте
            context.removeData("selectedRecipe");
            context.removeData("craftingProgress");
            context.removeData("craftMasterId");
            context.removeData("craftMasterX");
            context.removeData("craftMasterY");
            context.removeData("craftMasterZ");
            
            setCraftingState(context, CraftingState.RESTING);
        } else {
            _log.warn("Crafting failed for recipe: " + recipe);
            setCraftingState(context, CraftingState.GATHERING_MATERIALS);
        }
        
        return true;
    }
    
    /**
     * Отдых
     */
    private boolean rest(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Resting...");
        
        // Проверяем HP и MP
        double hpPercent = (player.getCurrentHp() / player.getMaxHp()) * 100;
        double mpPercent = (player.getCurrentMp() / player.getMaxMp()) * 100;
        
        if (hpPercent >= MIN_HP_PERCENT && mpPercent >= MIN_MP_PERCENT) {
            // Достаточно HP и MP, ищем нового мастера
            setCraftingState(context, CraftingState.SEARCHING_MASTER);
        } else {
            // Восстанавливаемся
            if (hpPercent < MIN_HP_PERCENT) {
                // Используем зелье лечения
                useHealingPotion(bot, context, player);
            }
            
            if (mpPercent < MIN_MP_PERCENT) {
                // Используем зелье маны
                useManaPotion(bot, context, player);
            }
        }
        
        return true;
    }
    
    /**
     * Поиск ближайшего мастера крафта
     */
    private L2NpcInstance findNearestCraftMaster(L2PcInstance player) {
        L2NpcInstance nearestMaster = null;
        double minDistance = Double.MAX_VALUE;
        
        // Получаем всех NPC в радиусе
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, SEARCH_RADIUS)) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance npc = (L2NpcInstance) obj;
                
                // Проверяем, является ли NPC мастером крафта
                if (isCraftMaster(npc)) {
                    double distance = player.getDistance(npc.getX(), npc.getY(), npc.getZ());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestMaster = npc;
                    }
                }
            }
        }
        
        return nearestMaster;
    }
    
    /**
     * Проверка, является ли NPC мастером крафта
     */
    private boolean isCraftMaster(L2NpcInstance npc) {
        // В реальной реализации здесь будет проверка по ID или имени NPC
        String npcName = npc.getName().toLowerCase();
        return npcName.contains("smith") || npcName.contains("craft") || 
               npcName.contains("jeweler") || npcName.contains("alchemist") ||
               npcName.contains("cook") || npcName.contains("dye");
    }
    
    /**
     * Получение случайного рецепта
     */
    private String getRandomRecipe() {
        String[] recipes = {
            "Iron Sword", "Steel Sword", "Mithril Sword",
            "Leather Armor", "Chain Mail", "Plate Mail",
            "Health Potion", "Mana Potion", "Stamina Potion",
            "Ring of Power", "Necklace of Wisdom", "Earring of Agility"
        };
        
        return recipes[ThreadLocalRandom.current().nextInt(recipes.length)];
    }
    
    /**
     * Случайное движение
     */
    private void moveRandomly(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        // Случайное смещение в радиусе 500 пикселей
        int offsetX = ThreadLocalRandom.current().nextInt(-500, 501);
        int offsetY = ThreadLocalRandom.current().nextInt(-500, 501);
        
        int newX = currentX + offsetX;
        int newY = currentY + offsetY;
        
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to new location at (" + newX + ", " + newY + ", " + currentZ + ")");
    }
    
    /**
     * Использование зелья лечения
     */
    private void useHealingPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        // В реальной реализации здесь будет использование зелья из инвентаря
        _log.debug("Using healing potion...");
    }
    
    /**
     * Использование зелья маны
     */
    private void useManaPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        // В реальной реализации здесь будет использование зелья из инвентаря
        _log.debug("Using mana potion...");
    }
    
    /**
     * Получение текущего состояния крафта
     */
    private CraftingState getCraftingState(BotContext context) {
        Object state = context.getData("craftingState");
        if (state instanceof CraftingState) {
            return (CraftingState) state;
        }
        return CraftingState.SEARCHING_MASTER;
    }
    
    /**
     * Установка состояния крафта
     */
    private void setCraftingState(BotContext context, CraftingState state) {
        context.setData("craftingState", state);
    }
    
    public boolean canExecute(BotContext context) {
        L2PcInstance player = context.getPlayerInstance();
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Проверяем минимальные требования для крафта
        double hpPercent = (player.getCurrentHp() / player.getMaxHp()) * 100;
        double mpPercent = (player.getCurrentMp() / player.getMaxMp()) * 100;
        
        return hpPercent >= MIN_HP_PERCENT && mpPercent >= MIN_MP_PERCENT;
    }
    
    public int getPriority(BotContext context) {
        // Приоритет крафта зависит от уровня и доступных рецептов
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return 0;
        }
        
        int level = player.getLevel();
        int basePriority = 30; // Базовый приоритет
        
        // Увеличиваем приоритет с уровнем
        if (level >= 20) basePriority += 10;
        if (level >= 40) basePriority += 10;
        if (level >= 60) basePriority += 10;
        
        return basePriority;
    }
    
    @Override
    public String getStatistics() {
        return "Crafting Behavior - Active: " + isActive() + 
               ", Min Interval: " + minExecutionInterval + 
               ", Max Interval: " + maxExecutionInterval;
    }
}
