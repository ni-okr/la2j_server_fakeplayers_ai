package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.actions.MoveAction;
import net.sf.l2j.botmanager.actions.AttackAction;
import net.sf.l2j.botmanager.actions.CastAction;
import net.sf.l2j.botmanager.actions.LootAction;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Менеджер пулов объектов для оптимизации производительности
 */
public class PoolManager {
    
    private static final Logger _log = Logger.getLogger(PoolManager.class);
    
    // ==================== SINGLETON ====================
    
    private static volatile PoolManager instance;
    
    /**
     * Получает экземпляр менеджера пулов
     * 
     * @return экземпляр PoolManager
     */
    public static PoolManager getInstance() {
        if (instance == null) {
            synchronized (PoolManager.class) {
                if (instance == null) {
                    instance = new PoolManager();
                }
            }
        }
        return instance;
    }
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** Карта пулов объектов по типам */
    private final Map<Class<?>, ObjectPool<?>> pools;
    
    /** Максимальный размер пула по умолчанию */
    private static final int DEFAULT_POOL_SIZE = 100;
    
    /** Размер пула для действий */
    private static final int ACTION_POOL_SIZE = 200;
    
    /** Размер пула для контекстов ботов */
    private static final int CONTEXT_POOL_SIZE = 50;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Приватный конструктор для Singleton
     */
    private PoolManager() {
        this.pools = new ConcurrentHashMap<>();
        initializeDefaultPools();
        _log.info("PoolManager initialized");
    }
    
    // ==================== ИНИЦИАЛИЗАЦИЯ ====================
    
    /**
     * Инициализирует пулы по умолчанию
     */
    private void initializeDefaultPools() {
        // Пул для действий
        registerPool(MoveAction.class, () -> new MoveAction(), ACTION_POOL_SIZE);
        registerPool(AttackAction.class, () -> new AttackAction(), ACTION_POOL_SIZE);
        registerPool(CastAction.class, () -> new CastAction(), ACTION_POOL_SIZE);
        registerPool(LootAction.class, () -> new LootAction(), ACTION_POOL_SIZE);
        
        // Пул для контекстов ботов
        registerPool(BotContext.class, () -> new BotContext(0), CONTEXT_POOL_SIZE);
        
        _log.info("Default pools initialized");
    }
    
    // ==================== УПРАВЛЕНИЕ ПУЛАМИ ====================
    
    /**
     * Регистрирует пул для указанного типа
     * 
     * @param type тип объектов
     * @param factory фабрика для создания объектов
     * @param maxSize максимальный размер пула
     * @param <T> тип объектов
     */
    public <T> void registerPool(Class<T> type, Supplier<T> factory, int maxSize) {
        if (type == null || factory == null) {
            throw new IllegalArgumentException("Type and factory cannot be null");
        }
        
        ObjectPool<T> pool = new ObjectPool<>(factory, maxSize);
        pools.put(type, pool);
        
        _log.info("Pool registered for type: " + type.getSimpleName() + 
                 " with max size: " + maxSize);
    }
    
    /**
     * Регистрирует пул с размером по умолчанию
     * 
     * @param type тип объектов
     * @param factory фабрика для создания объектов
     * @param <T> тип объектов
     */
    public <T> void registerPool(Class<T> type, Supplier<T> factory) {
        registerPool(type, factory, DEFAULT_POOL_SIZE);
    }
    
    /**
     * Удаляет пул для указанного типа
     * 
     * @param type тип объектов
     * @return true если пул был удален
     */
    public boolean removePool(Class<?> type) {
        if (type == null) {
            return false;
        }
        
        ObjectPool<?> pool = pools.remove(type);
        if (pool != null) {
            pool.clear();
            _log.info("Pool removed for type: " + type.getSimpleName());
            return true;
        }
        
        return false;
    }
    
    /**
     * Проверяет, зарегистрирован ли пул для указанного типа
     * 
     * @param type тип объектов
     * @return true если пул зарегистрирован
     */
    public boolean hasPool(Class<?> type) {
        return pools.containsKey(type);
    }
    
    // ==================== РАБОТА С ПУЛАМИ ====================
    
    /**
     * Получает объект из пула
     * 
     * @param type тип объекта
     * @return объект из пула
     * @param <T> тип объекта
     */
    @SuppressWarnings("unchecked")
    public <T> T acquire(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        ObjectPool<T> pool = (ObjectPool<T>) pools.get(type);
        if (pool == null) {
            throw new IllegalStateException("No pool registered for type: " + type.getSimpleName());
        }
        
        T object = pool.acquire();
        if (object instanceof ObjectPool.PoolableObject) {
            ((ObjectPool.PoolableObject) object).markInUse();
        }
        
        return object;
    }
    
    /**
     * Возвращает объект в пул
     * 
     * @param object объект для возврата
     * @return true если объект успешно возвращен
     */
    public boolean release(Object object) {
        if (object == null) {
            return false;
        }
        
        Class<?> type = object.getClass();
        ObjectPool<?> pool = pools.get(type);
        
        if (pool == null) {
            _log.warn("No pool registered for type: " + type.getSimpleName());
            return false;
        }
        
        if (object instanceof ObjectPool.PoolableObject) {
            ((ObjectPool.PoolableObject) object).markFree();
        }
        
        @SuppressWarnings("unchecked")
        ObjectPool<Object> typedPool = (ObjectPool<Object>) pool;
        boolean released = typedPool.release(object);
        return released;
    }
    
    /**
     * Предварительно заполняет пул для указанного типа
     * 
     * @param type тип объектов
     * @param count количество объектов для создания
     * @return количество созданных объектов
     */
    public int prefillPool(Class<?> type, int count) {
        if (type == null) {
            return 0;
        }
        
        ObjectPool<?> pool = pools.get(type);
        if (pool == null) {
            _log.warn("No pool registered for type: " + type.getSimpleName());
            return 0;
        }
        
        return pool.prefill(count);
    }
    
    /**
     * Очищает пул для указанного типа
     * 
     * @param type тип объектов
     */
    public void clearPool(Class<?> type) {
        if (type == null) {
            return;
        }
        
        ObjectPool<?> pool = pools.get(type);
        if (pool != null) {
            pool.clear();
            _log.info("Pool cleared for type: " + type.getSimpleName());
        }
    }
    
    /**
     * Очищает все пулы
     */
    public void clearAllPools() {
        for (ObjectPool<?> pool : pools.values()) {
            pool.clear();
        }
        
        _log.info("All pools cleared");
    }
    
    // ==================== МОНИТОРИНГ ====================
    
    /**
     * Возвращает статистику для указанного типа
     * 
     * @param type тип объектов
     * @return статистика пула или null если пул не найден
     */
    public String getPoolStatistics(Class<?> type) {
        if (type == null) {
            return null;
        }
        
        ObjectPool<?> pool = pools.get(type);
        if (pool == null) {
            return null;
        }
        
        return pool.getStatistics();
    }
    
    /**
     * Возвращает общую статистику всех пулов
     * 
     * @return общая статистика
     */
    public String getAllStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== PoolManager Statistics ===\n");
        stats.append("Total Pools: ").append(pools.size()).append("\n\n");
        
        for (Map.Entry<Class<?>, ObjectPool<?>> entry : pools.entrySet()) {
            stats.append("Pool: ").append(entry.getKey().getSimpleName()).append("\n");
            stats.append(entry.getValue().getStatistics()).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Возвращает количество зарегистрированных пулов
     * 
     * @return количество пулов
     */
    public int getPoolCount() {
        return pools.size();
    }
    
    /**
     * Возвращает общее количество доступных объектов во всех пулах
     * 
     * @return общее количество доступных объектов
     */
    public int getTotalAvailableObjects() {
        return pools.values().stream()
            .mapToInt(ObjectPool::getAvailableCount)
            .sum();
    }
    
    /**
     * Возвращает общее количество созданных объектов во всех пулах
     * 
     * @return общее количество созданных объектов
     */
    public int getTotalCreatedObjects() {
        return pools.values().stream()
            .mapToInt(ObjectPool::getCreatedCount)
            .sum();
    }
    
    /**
     * Возвращает общий коэффициент переиспользования
     * 
     * @return общий коэффициент переиспользования
     */
    public double getOverallReuseRatio() {
        if (pools.isEmpty()) {
            return 0.0;
        }
        
        double totalReuseRatio = pools.values().stream()
            .mapToDouble(ObjectPool::getReuseRatio)
            .sum();
        
        return totalReuseRatio / pools.size();
    }
    
    /**
     * Сбрасывает статистику всех пулов
     */
    public void resetAllStatistics() {
        for (ObjectPool<?> pool : pools.values()) {
            pool.resetStatistics();
        }
        
        _log.info("All pool statistics reset");
    }
    
    // ==================== УТИЛИТЫ ====================
    
    /**
     * Выполняет операцию с объектом из пула и автоматически возвращает его
     * 
     * @param type тип объекта
     * @param operation операция для выполнения
     * @param <T> тип объекта
     * @return результат операции
     */
    public <T> T withPooledObject(Class<T> type, PooledOperation<T> operation) {
        T object = acquire(type);
        try {
            return operation.execute(object);
        } finally {
            release(object);
        }
    }
    
    /**
     * Функциональный интерфейс для операций с объектами из пула
     * 
     * @param <T> тип объекта
     */
    @FunctionalInterface
    public interface PooledOperation<T> {
        T execute(T object);
    }
    
    // ==================== ЗАВЕРШЕНИЕ ====================
    
    /**
     * Завершает работу менеджера пулов
     */
    public void shutdown() {
        clearAllPools();
        pools.clear();
        _log.info("PoolManager shutdown completed");
    }
}
