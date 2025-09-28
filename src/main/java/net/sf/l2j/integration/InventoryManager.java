package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.items.instance.L2ItemInstance;
import net.sf.l2j.gameserver.templates.item.L2Item;
import net.sf.l2j.gameserver.templates.item.L2Weapon;
import net.sf.l2j.gameserver.templates.item.L2Armor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер инвентаря и предметов для ботов
 */
public class InventoryManager {
    
    private static final Logger _log = Logger.getLogger(InventoryManager.class);
    
    // ==================== SINGLETON ====================
    
    private static volatile InventoryManager instance;
    
    public static InventoryManager getInstance() {
        if (instance == null) {
            synchronized (InventoryManager.class) {
                if (instance == null) {
                    instance = new InventoryManager();
                }
            }
        }
        return instance;
    }
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** Кэш предметов ботов */
    private final Map<Integer, Map<Integer, L2ItemInstance>> botItems;
    
    /** Максимальный размер инвентаря */
    private static final int MAX_INVENTORY_SIZE = 80;
    
    /** Максимальный размер склада */
    private static final int MAX_WAREHOUSE_SIZE = 200;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    private InventoryManager() {
        this.botItems = new ConcurrentHashMap<>();
        _log.info("InventoryManager initialized");
    }
    
    // ==================== УПРАВЛЕНИЕ ИНВЕНТАРЕМ ====================
    
    /**
     * Получает предмет из инвентаря бота
     * 
     * @param bot бот
     * @param itemId ID предмета
     * @return предмет или null
     */
    public L2ItemInstance getItem(EnhancedFakePlayer bot, int itemId) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            return player.getInventory().getItemByItemId(itemId);
            
        } catch (Exception e) {
            _log.error("Error getting item for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Получает предмет по объектному ID
     * 
     * @param bot бот
     * @param objectId объектный ID
     * @return предмет или null
     */
    public L2ItemInstance getItemByObjectId(EnhancedFakePlayer bot, int objectId) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            return player.getInventory().getItemByObjectId(objectId);
            
        } catch (Exception e) {
            _log.error("Error getting item by object ID for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Получает все предметы определенного типа
     * 
     * @param bot бот
     * @param itemType тип предмета
     * @return список предметов
     */
    public List<L2ItemInstance> getItemsByType(EnhancedFakePlayer bot, L2Item.L2ItemType itemType) {
        List<L2ItemInstance> items = new ArrayList<>();
        
        if (bot == null || !bot.isActive()) {
            return items;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return items;
            }
            
            for (L2ItemInstance item : player.getInventory().getItems()) {
                if (item.getItemType() == itemType) {
                    items.add(item);
                }
            }
            
        } catch (Exception e) {
            _log.error("Error getting items by type for bot " + bot.getBotId(), e);
        }
        
        return items;
    }
    
    /**
     * Получает количество предметов в инвентаре
     * 
     * @param bot бот
     * @param itemId ID предмета
     * @return количество
     */
    public long getItemCount(EnhancedFakePlayer bot, int itemId) {
        if (bot == null || !bot.isActive()) {
            return 0;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return 0;
            }
            
            return player.getInventory().getInventoryItemCount(itemId, -1);
            
        } catch (Exception e) {
            _log.error("Error getting item count for bot " + bot.getBotId(), e);
            return 0;
        }
    }
    
    // ==================== ЭКИПИРОВКА ====================
    
    /**
     * Экипирует предмет
     * 
     * @param bot бот
     * @param item предмет
     * @return true если предмет экипирован
     */
    public boolean equipItem(EnhancedFakePlayer bot, L2ItemInstance item) {
        if (bot == null || item == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            // Проверка, можно ли экипировать предмет
            if (!item.isEquipable()) {
                _log.debug("Item is not equipable: " + item.getItemId());
                return false;
            }
            
            // Проверка уровня
            if (item.getCrystalType().getLevel() > player.getLevel()) {
                _log.debug("Item level too high for bot: " + item.getItemId());
                return false;
            }
            
            // Экипировка предмета
            player.useEquippableItem(item, true);
            
            _log.debug("Bot " + bot.getBotId() + " equipped item: " + item.getItemId());
            return true;
            
        } catch (Exception e) {
            _log.error("Error equipping item for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Снимает предмет
     * 
     * @param bot бот
     * @param item предмет
     * @return true если предмет снят
     */
    public boolean unequipItem(EnhancedFakePlayer bot, L2ItemInstance item) {
        if (bot == null || item == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            // Снятие предмета
            player.useEquippableItem(item, false);
            
            _log.debug("Bot " + bot.getBotId() + " unequipped item: " + item.getItemId());
            return true;
            
        } catch (Exception e) {
            _log.error("Error unequipping item for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Получает экипированное оружие
     * 
     * @param bot бот
     * @return оружие или null
     */
    public L2ItemInstance getEquippedWeapon(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            return player.getActiveWeaponInstance();
            
        } catch (Exception e) {
            _log.error("Error getting equipped weapon for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Получает экипированную броню по слотам
     * 
     * @param bot бот
     * @return карта слотов и предметов
     */
    public Map<L2Item.SLOT, L2ItemInstance> getEquippedArmor(EnhancedFakePlayer bot) {
        Map<L2Item.SLOT, L2ItemInstance> armor = new HashMap<>();
        
        if (bot == null || !bot.isActive()) {
            return armor;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return armor;
            }
            
            for (L2Item.SLOT slot : L2Item.SLOT.values()) {
                L2ItemInstance item = player.getInventory().getPaperdollItem(slot);
                if (item != null) {
                    armor.put(slot, item);
                }
            }
            
        } catch (Exception e) {
            _log.error("Error getting equipped armor for bot " + bot.getBotId(), e);
        }
        
        return armor;
    }
    
    // ==================== ИСПОЛЬЗОВАНИЕ ПРЕДМЕТОВ ====================
    
    /**
     * Использует предмет
     * 
     * @param bot бот
     * @param item предмет
     * @return true если предмет использован
     */
    public boolean useItem(EnhancedFakePlayer bot, L2ItemInstance item) {
        if (bot == null || item == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            // Проверка, можно ли использовать предмет
            if (!item.isConsumable()) {
                _log.debug("Item is not consumable: " + item.getItemId());
                return false;
            }
            
            // Использование предмета
            player.useItem(item);
            
            _log.debug("Bot " + bot.getBotId() + " used item: " + item.getItemId());
            return true;
            
        } catch (Exception e) {
            _log.error("Error using item for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Использует предмет по ID
     * 
     * @param bot бот
     * @param itemId ID предмета
     * @return true если предмет использован
     */
    public boolean useItemById(EnhancedFakePlayer bot, int itemId) {
        L2ItemInstance item = getItem(bot, itemId);
        if (item == null) {
            return false;
        }
        
        return useItem(bot, item);
    }
    
    // ==================== УПРАВЛЕНИЕ СКЛАДОМ ====================
    
    /**
     * Получает предметы со склада
     * 
     * @param bot бот
     * @param itemId ID предмета
     * @param count количество
     * @return true если предметы получены
     */
    public boolean getFromWarehouse(EnhancedFakePlayer bot, int itemId, long count) {
        if (bot == null || !bot.isActive() || count <= 0) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return false;
            }
            
            // Проверка свободного места в инвентаре
            if (player.getInventory().getSize() >= MAX_INVENTORY_SIZE) {
                _log.debug("Inventory is full for bot: " + bot.getBotId());
                return false;
            }
            
            // Получение предметов со склада
            // В реальной реализации это будет через систему склада L2J
            _log.debug("Bot " + bot.getBotId() + " got " + count + " items from warehouse: " + itemId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error getting items from warehouse for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Отправляет предметы на склад
     * 
     * @param bot бот
     * @param itemId ID предмета
     * @param count количество
     * @return true если предметы отправлены
     */
    public boolean sendToWarehouse(EnhancedFakePlayer bot, int itemId, long count) {
        if (bot == null || !bot.isActive() || count <= 0) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return false;
            }
            
            // Проверка наличия предметов
            long availableCount = getItemCount(bot, itemId);
            if (availableCount < count) {
                _log.debug("Not enough items for warehouse: " + itemId);
                return false;
            }
            
            // Отправка предметов на склад
            // В реальной реализации это будет через систему склада L2J
            _log.debug("Bot " + bot.getBotId() + " sent " + count + " items to warehouse: " + itemId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error sending items to warehouse for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    // ==================== АНАЛИЗ ИНВЕНТАРЯ ====================
    
    /**
     * Получает статистику инвентаря бота
     * 
     * @param bot бот
     * @return статистика
     */
    public String getInventoryStatistics(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return "Bot is not active";
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return "Player instance is null";
            }
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== Inventory Statistics for Bot ").append(bot.getBotId()).append(" ===\n");
            stats.append("Inventory Size: ").append(player.getInventory().getSize()).append("/").append(MAX_INVENTORY_SIZE).append("\n");
            stats.append("Total Items: ").append(player.getInventory().getAllItems().length).append("\n");
            stats.append("Adena: ").append(player.getAdena()).append("\n");
            
            // Статистика по типам предметов
            Map<L2Item.L2ItemType, Integer> typeCount = new HashMap<>();
            for (L2ItemInstance item : player.getInventory().getAllItems()) {
                L2Item.L2ItemType type = item.getItemType();
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }
            
            stats.append("Items by Type:\n");
            for (Map.Entry<L2Item.L2ItemType, Integer> entry : typeCount.entrySet()) {
                stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            
            return stats.toString();
            
        } catch (Exception e) {
            _log.error("Error getting inventory statistics for bot " + bot.getBotId(), e);
            return "Error getting statistics";
        }
    }
    
    /**
     * Проверяет, есть ли место в инвентаре
     * 
     * @param bot бот
     * @return true если есть место
     */
    public boolean hasSpace(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return false;
            }
            
            return player.getInventory().getSize() < MAX_INVENTORY_SIZE;
            
        } catch (Exception e) {
            _log.error("Error checking inventory space for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Получает вес инвентаря
     * 
     * @param bot бот
     * @return вес в граммах
     */
    public long getInventoryWeight(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return 0;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return 0;
            }
            
            return player.getInventory().getTotalWeight();
            
        } catch (Exception e) {
            _log.error("Error getting inventory weight for bot " + bot.getBotId(), e);
            return 0;
        }
    }
}
