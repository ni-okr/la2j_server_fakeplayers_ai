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
 * Поведение торговли
 * 
 * Бот ищет торговцев, проверяет товары, покупает и продает предметы
 * для получения прибыли.
 */
public class TradingBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(TradingBehavior.class);
    
    // Константы для торговли
    private static final int SEARCH_RADIUS = 3000; // Радиус поиска торговцев
    private static final int TRADE_RADIUS = 150; // Радиус для торговли
    private static final int MIN_HP_PERCENT = 40; // Минимальный процент HP для торговли
    private static final int MIN_MP_PERCENT = 20; // Минимальный процент MP для торговли
    private static final long TRADE_COOLDOWN = 30000; // Кулдаун между торговыми операциями (30 сек)
    
    // Состояния торговли
    private enum TradingState {
        SEARCHING_MERCHANT,  // Поиск торговца
        MOVING_TO_MERCHANT,  // Движение к торговцу
        CHECKING_GOODS,      // Проверка товаров
        BUYING,              // Покупка товаров
        SELLING,             // Продажа товаров
        RESTING              // Отдых
    }
    
    // Типы торговцев
    private enum MerchantType {
        GENERAL_MERCHANT,    // Обычный торговец
        WEAPON_MERCHANT,     // Торговец оружием
        ARMOR_MERCHANT,      // Торговец брони
        JEWELRY_MERCHANT,    // Торговец украшений
        POTION_MERCHANT,     // Торговец зелий
        SPECIAL_MERCHANT     // Специальный торговец
    }
    
    public TradingBehavior() {
        super(BehaviorType.TRADING);
        setMinExecutionInterval(2000); // Минимум 2 секунды между действиями
        setMaxExecutionInterval(8000); // Максимум 8 секунд
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Проверяем кулдаун торговли
        if (isOnTradeCooldown(context)) {
            return true;
        }
        
        // Получаем текущее состояние торговли
        TradingState currentState = getTradingState(context);
        
        _log.debug("Trading state: " + currentState + " for bot " + bot.getContext().getBotId());
        
        switch (currentState) {
            case SEARCHING_MERCHANT:
                return searchForMerchant(bot, context, player);
                
            case MOVING_TO_MERCHANT:
                return moveToMerchant(bot, context, player);
                
            case CHECKING_GOODS:
                return checkGoods(bot, context, player);
                
            case BUYING:
                return performBuying(bot, context, player);
                
            case SELLING:
                return performSelling(bot, context, player);
                
            case RESTING:
                return rest(bot, context, player);
                
            default:
                setTradingState(context, TradingState.SEARCHING_MERCHANT);
                return true;
        }
    }
    
    /**
     * Поиск торговца
     */
    private boolean searchForMerchant(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Searching for merchant...");
        
        // Ищем ближайшего торговца
        L2NpcInstance merchant = findNearestMerchant(player);
        
        if (merchant != null) {
            // Сохраняем информацию о торговце
            context.setData("merchantId", merchant.getObjectId());
            context.setData("merchantX", merchant.getX());
            context.setData("merchantY", merchant.getY());
            context.setData("merchantZ", merchant.getZ());
            context.setData("merchantType", getMerchantType(merchant));
            
            setTradingState(context, TradingState.MOVING_TO_MERCHANT);
            _log.info("Found merchant: " + merchant.getName() + " at (" + 
                     merchant.getX() + ", " + merchant.getY() + ", " + merchant.getZ() + ")");
            return true;
        }
        
        // Если торговец не найден, ищем случайным образом
        if (ThreadLocalRandom.current().nextInt(100) < 15) { // 15% шанс
            moveRandomly(bot, context, player);
        }
        
        return true;
    }
    
    /**
     * Движение к торговцу
     */
    private boolean moveToMerchant(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        Integer merchantX = (Integer) context.getData("merchantX");
        Integer merchantY = (Integer) context.getData("merchantY");
        Integer merchantZ = (Integer) context.getData("merchantZ");
        
        if (merchantX == null || merchantY == null || merchantZ == null) {
            setTradingState(context, TradingState.SEARCHING_MERCHANT);
            return true;
        }
        
        double distance = player.getDistance(merchantX, merchantY, merchantZ);
        
        if (distance <= TRADE_RADIUS) {
            // Достигли торговца
            setTradingState(context, TradingState.CHECKING_GOODS);
            _log.info("Reached merchant, checking goods...");
            return true;
        }
        
        // Движемся к торговцу
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to merchant at (" + merchantX + ", " + merchantY + ", " + merchantZ + ")");
        return true;
    }
    
    /**
     * Проверка товаров
     */
    private boolean checkGoods(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Checking merchant goods...");
        
        MerchantType merchantType = (MerchantType) context.getData("merchantType");
        if (merchantType == null) {
            merchantType = MerchantType.GENERAL_MERCHANT;
        }
        
        // Симулируем проверку товаров
        boolean hasGoodDeals = checkForGoodDeals(merchantType, player);
        
        if (hasGoodDeals) {
            // Есть выгодные предложения
            boolean shouldBuy = ThreadLocalRandom.current().nextBoolean();
            if (shouldBuy) {
                setTradingState(context, TradingState.BUYING);
                _log.info("Found good deals, starting to buy...");
            } else {
                setTradingState(context, TradingState.SELLING);
                _log.info("Found good deals, starting to sell...");
            }
            return true;
        } else {
            // Нет выгодных предложений
            setTradingState(context, TradingState.RESTING);
            _log.debug("No good deals found, resting...");
            return true;
        }
    }
    
    /**
     * Покупка товаров
     */
    private boolean performBuying(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Performing buying...");
        
        // Симулируем покупку товаров
        // В реальной реализации здесь будет взаимодействие с торговцем
        String item = getRandomItemToBuy();
        int quantity = ThreadLocalRandom.current().nextInt(1, 6);
        int price = ThreadLocalRandom.current().nextInt(100, 1000);
        
        // Проверяем, хватает ли денег
        long currentMoney = getCurrentMoney(player);
        long totalCost = (long) price * quantity;
        
        if (currentMoney >= totalCost) {
            // Покупаем товар
            _log.info("Bought " + quantity + "x " + item + " for " + totalCost + " adena");
            context.setData("lastTradeTime", System.currentTimeMillis());
            context.setData("lastTradeType", "BUY");
            context.setData("lastTradeItem", item);
            context.setData("lastTradeQuantity", quantity);
            context.setData("lastTradeCost", totalCost);
            
            setTradingState(context, TradingState.RESTING);
        } else {
            // Не хватает денег
            _log.warn("Not enough money to buy " + item + " (need: " + totalCost + ", have: " + currentMoney + ")");
            setTradingState(context, TradingState.SELLING);
        }
        
        return true;
    }
    
    /**
     * Продажа товаров
     */
    private boolean performSelling(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Performing selling...");
        
        // Симулируем продажу товаров
        // В реальной реализации здесь будет проверка инвентаря и продажа предметов
        String item = getRandomItemToSell();
        int quantity = ThreadLocalRandom.current().nextInt(1, 4);
        int price = ThreadLocalRandom.current().nextInt(50, 500);
        
        // Проверяем, есть ли товар для продажи
        boolean hasItem = checkHasItem(player, item, quantity);
        
        if (hasItem) {
            // Продаем товар
            long totalProfit = (long) price * quantity;
            _log.info("Sold " + quantity + "x " + item + " for " + totalProfit + " adena");
            context.setData("lastTradeTime", System.currentTimeMillis());
            context.setData("lastTradeType", "SELL");
            context.setData("lastTradeItem", item);
            context.setData("lastTradeQuantity", quantity);
            context.setData("lastTradeCost", totalProfit);
            
            setTradingState(context, TradingState.RESTING);
        } else {
            // Нет товара для продажи
            _log.warn("No " + item + " to sell");
            setTradingState(context, TradingState.RESTING);
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
            // Достаточно HP и MP, ищем нового торговца
            setTradingState(context, TradingState.SEARCHING_MERCHANT);
        } else {
            // Восстанавливаемся
            if (hpPercent < MIN_HP_PERCENT) {
                useHealingPotion(bot, context, player);
            }
            
            if (mpPercent < MIN_MP_PERCENT) {
                useManaPotion(bot, context, player);
            }
        }
        
        return true;
    }
    
    /**
     * Поиск ближайшего торговца
     */
    private L2NpcInstance findNearestMerchant(L2PcInstance player) {
        L2NpcInstance nearestMerchant = null;
        double minDistance = Double.MAX_VALUE;
        
        // Получаем всех NPC в радиусе
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, SEARCH_RADIUS)) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance npc = (L2NpcInstance) obj;
                
                // Проверяем, является ли NPC торговцем
                if (isMerchant(npc)) {
                    double distance = player.getDistance(npc.getX(), npc.getY(), npc.getZ());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestMerchant = npc;
                    }
                }
            }
        }
        
        return nearestMerchant;
    }
    
    /**
     * Проверка, является ли NPC торговцем
     */
    private boolean isMerchant(L2NpcInstance npc) {
        // В реальной реализации здесь будет проверка по ID или имени NPC
        String npcName = npc.getName().toLowerCase();
        return npcName.contains("merchant") || npcName.contains("trader") || 
               npcName.contains("shop") || npcName.contains("vendor") ||
               npcName.contains("dealer") || npcName.contains("seller");
    }
    
    /**
     * Получение типа торговца
     */
    private MerchantType getMerchantType(L2NpcInstance npc) {
        String npcName = npc.getName().toLowerCase();
        
        if (npcName.contains("weapon")) return MerchantType.WEAPON_MERCHANT;
        if (npcName.contains("armor")) return MerchantType.ARMOR_MERCHANT;
        if (npcName.contains("jewelry")) return MerchantType.JEWELRY_MERCHANT;
        if (npcName.contains("potion")) return MerchantType.POTION_MERCHANT;
        if (npcName.contains("special")) return MerchantType.SPECIAL_MERCHANT;
        
        return MerchantType.GENERAL_MERCHANT;
    }
    
    /**
     * Проверка выгодных предложений
     */
    private boolean checkForGoodDeals(MerchantType merchantType, L2PcInstance player) {
        // В реальной реализации здесь будет анализ цен и товаров
        return ThreadLocalRandom.current().nextInt(100) < 60; // 60% шанс найти выгодные предложения
    }
    
    /**
     * Получение случайного товара для покупки
     */
    private String getRandomItemToBuy() {
        String[] items = {
            "Health Potion", "Mana Potion", "Stamina Potion",
            "Iron Sword", "Steel Sword", "Leather Armor",
            "Ring of Power", "Necklace of Wisdom", "Earring of Agility",
            "Scroll of Enchant", "Soulshot", "Spiritshot"
        };
        
        return items[ThreadLocalRandom.current().nextInt(items.length)];
    }
    
    /**
     * Получение случайного товара для продажи
     */
    private String getRandomItemToSell() {
        String[] items = {
            "Monster Drop", "Quest Item", "Crafted Item",
            "Old Weapon", "Used Armor", "Broken Jewelry",
            "Rare Material", "Special Ingredient", "Magic Crystal"
        };
        
        return items[ThreadLocalRandom.current().nextInt(items.length)];
    }
    
    /**
     * Проверка наличия товара
     */
    private boolean checkHasItem(L2PcInstance player, String item, int quantity) {
        // В реальной реализации здесь будет проверка инвентаря
        return ThreadLocalRandom.current().nextBoolean();
    }
    
    /**
     * Получение текущих денег
     */
    private long getCurrentMoney(L2PcInstance player) {
        // В реальной реализации здесь будет получение количества адены
        return ThreadLocalRandom.current().nextLong(1000, 50000);
    }
    
    /**
     * Случайное движение
     */
    private void moveRandomly(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        // Случайное смещение в радиусе 800 пикселей
        int offsetX = ThreadLocalRandom.current().nextInt(-800, 801);
        int offsetY = ThreadLocalRandom.current().nextInt(-800, 801);
        
        int newX = currentX + offsetX;
        int newY = currentY + offsetY;
        
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to new location at (" + newX + ", " + newY + ", " + currentZ + ")");
    }
    
    /**
     * Использование зелья лечения
     */
    private void useHealingPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Using healing potion...");
    }
    
    /**
     * Использование зелья маны
     */
    private void useManaPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Using mana potion...");
    }
    
    /**
     * Проверка кулдауна торговли
     */
    private boolean isOnTradeCooldown(BotContext context) {
        Object lastTradeTime = context.getData("lastTradeTime");
        if (lastTradeTime instanceof Long) {
            long timeSinceLastTrade = System.currentTimeMillis() - (Long) lastTradeTime;
            return timeSinceLastTrade < TRADE_COOLDOWN;
        }
        return false;
    }
    
    /**
     * Получение текущего состояния торговли
     */
    private TradingState getTradingState(BotContext context) {
        Object state = context.getData("tradingState");
        if (state instanceof TradingState) {
            return (TradingState) state;
        }
        return TradingState.SEARCHING_MERCHANT;
    }
    
    /**
     * Установка состояния торговли
     */
    private void setTradingState(BotContext context, TradingState state) {
        context.setData("tradingState", state);
    }
    
    public boolean canExecute(BotContext context) {
        L2PcInstance player = context.getPlayerInstance();
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Проверяем минимальные требования для торговли
        double hpPercent = (player.getCurrentHp() / player.getMaxHp()) * 100;
        double mpPercent = (player.getCurrentMp() / player.getMaxMp()) * 100;
        
        return hpPercent >= MIN_HP_PERCENT && mpPercent >= MIN_MP_PERCENT;
    }
    
    public int getPriority(BotContext context) {
        // Приоритет торговли зависит от уровня и количества денег
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return 0;
        }
        
        int level = player.getLevel();
        int basePriority = 25; // Базовый приоритет
        
        // Увеличиваем приоритет с уровнем
        if (level >= 15) basePriority += 5;
        if (level >= 30) basePriority += 5;
        if (level >= 50) basePriority += 5;
        
        return basePriority;
    }
    
    @Override
    public String getStatistics() {
        return "Trading Behavior - Active: " + isActive() + 
               ", Min Interval: " + minExecutionInterval + 
               ", Max Interval: " + maxExecutionInterval;
    }
}
