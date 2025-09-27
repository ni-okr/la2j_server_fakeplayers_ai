package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Универсальный пул объектов для оптимизации производительности
 * 
 * @param <T> тип объектов в пуле
 */
public class ObjectPool<T> {
    
    private static final Logger _log = Logger.getLogger(ObjectPool.class);
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** Очередь доступных объектов */
    private final ConcurrentLinkedQueue<T> availableObjects;
    
    /** Фабрика для создания новых объектов */
    private final Supplier<T> objectFactory;
    
    /** Максимальный размер пула */
    private final int maxSize;
    
    /** Текущий размер пула */
    private final AtomicInteger currentSize;
    
    /** Счетчик созданных объектов */
    private final AtomicInteger createdCount;
    
    /** Счетчик переиспользованных объектов */
    private final AtomicInteger reusedCount;
    
    /** Счетчик запросов объектов */
    private final AtomicInteger requestCount;
    
    /** Счетчик возвращенных объектов */
    private final AtomicInteger returnCount;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Размер пула по умолчанию */
    private static final int DEFAULT_MAX_SIZE = 100;
    
    /** Минимальный размер пула */
    private static final int MIN_POOL_SIZE = 1;
    
    /** Максимальный размер пула */
    private static final int MAX_POOL_SIZE = 10000;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает пул объектов с фабрикой и размером по умолчанию
     * 
     * @param objectFactory фабрика для создания объектов
     */
    public ObjectPool(Supplier<T> objectFactory) {
        this(objectFactory, DEFAULT_MAX_SIZE);
    }
    
    /**
     * Создает пул объектов с указанной фабрикой и размером
     * 
     * @param objectFactory фабрика для создания объектов
     * @param maxSize максимальный размер пула
     */
    public ObjectPool(Supplier<T> objectFactory, int maxSize) {
        if (objectFactory == null) {
            throw new IllegalArgumentException("Object factory cannot be null");
        }
        
        if (maxSize < MIN_POOL_SIZE || maxSize > MAX_POOL_SIZE) {
            throw new IllegalArgumentException("Pool size must be between " + 
                MIN_POOL_SIZE + " and " + MAX_POOL_SIZE);
        }
        
        this.objectFactory = objectFactory;
        this.maxSize = maxSize;
        this.availableObjects = new ConcurrentLinkedQueue<>();
        this.currentSize = new AtomicInteger(0);
        this.createdCount = new AtomicInteger(0);
        this.reusedCount = new AtomicInteger(0);
        this.requestCount = new AtomicInteger(0);
        this.returnCount = new AtomicInteger(0);
        
        _log.info("ObjectPool created with max size: " + maxSize);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Получает объект из пула
     * 
     * @return объект из пула или новый объект, если пул пуст
     */
    public T acquire() {
        requestCount.incrementAndGet();
        
        T object = availableObjects.poll();
        
        if (object != null) {
            reusedCount.incrementAndGet();
            _log.debug("Object reused from pool, current size: " + currentSize.get());
            return object;
        }
        
        // Создаем новый объект, если пул пуст и не достигнут максимум
        if (currentSize.get() < maxSize) {
            object = objectFactory.get();
            currentSize.incrementAndGet();
            createdCount.incrementAndGet();
            _log.debug("New object created, current size: " + currentSize.get());
        } else {
            // Если пул полон, создаем временный объект
            object = objectFactory.get();
            _log.debug("Temporary object created (pool full)");
        }
        
        return object;
    }
    
    /**
     * Возвращает объект в пул
     * 
     * @param object объект для возврата
     * @return true если объект успешно возвращен
     */
    public boolean release(T object) {
        if (object == null) {
            _log.warn("Cannot release null object");
            return false;
        }
        
        returnCount.incrementAndGet();
        
        // Проверяем, не переполнен ли пул
        if (availableObjects.size() >= maxSize) {
            _log.debug("Pool is full, object discarded");
            return false;
        }
        
        // Сбрасываем состояние объекта перед возвратом в пул
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
        
        availableObjects.offer(object);
        _log.debug("Object returned to pool, available: " + availableObjects.size());
        
        return true;
    }
    
    /**
     * Очищает пул, удаляя все объекты
     */
    public void clear() {
        int clearedCount = availableObjects.size();
        availableObjects.clear();
        currentSize.set(0);
        createdCount.set(0);
        reusedCount.set(0);
        requestCount.set(0);
        returnCount.set(0);
        
        _log.info("Pool cleared, removed " + clearedCount + " objects");
    }
    
    /**
     * Предварительно заполняет пул объектами
     * 
     * @param count количество объектов для создания
     * @return количество созданных объектов
     */
    public int prefill(int count) {
        if (count <= 0) {
            return 0;
        }
        
        int actualCount = Math.min(count, maxSize - currentSize.get());
        int created = 0;
        
        for (int i = 0; i < actualCount; i++) {
            T object = objectFactory.get();
            availableObjects.offer(object);
            currentSize.incrementAndGet();
            createdCount.incrementAndGet();
            created++;
        }
        
        _log.info("Pool prefilled with " + created + " objects");
        return created;
    }
    
    // ==================== МЕТОДЫ МОНИТОРИНГА ====================
    
    /**
     * Возвращает количество доступных объектов
     * 
     * @return количество доступных объектов
     */
    public int getAvailableCount() {
        return availableObjects.size();
    }
    
    /**
     * Возвращает текущий размер пула
     * 
     * @return текущий размер пула
     */
    public int getCurrentSize() {
        return currentSize.get();
    }
    
    /**
     * Возвращает максимальный размер пула
     * 
     * @return максимальный размер пула
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Возвращает количество созданных объектов
     * 
     * @return количество созданных объектов
     */
    public int getCreatedCount() {
        return createdCount.get();
    }
    
    /**
     * Возвращает количество переиспользованных объектов
     * 
     * @return количество переиспользованных объектов
     */
    public int getReusedCount() {
        return reusedCount.get();
    }
    
    /**
     * Возвращает количество запросов объектов
     * 
     * @return количество запросов объектов
     */
    public int getRequestCount() {
        return requestCount.get();
    }
    
    /**
     * Возвращает количество возвращенных объектов
     * 
     * @return количество возвращенных объектов
     */
    public int getReturnCount() {
        return returnCount.get();
    }
    
    /**
     * Возвращает коэффициент переиспользования
     * 
     * @return коэффициент переиспользования (0.0 - 1.0)
     */
    public double getReuseRatio() {
        int totalRequests = requestCount.get();
        if (totalRequests == 0) {
            return 0.0;
        }
        
        return (double) reusedCount.get() / totalRequests;
    }
    
    /**
     * Возвращает коэффициент заполнения пула
     * 
     * @return коэффициент заполнения пула (0.0 - 1.0)
     */
    public double getUtilizationRatio() {
        return (double) availableObjects.size() / maxSize;
    }
    
    /**
     * Возвращает статистику пула
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ObjectPool Statistics ===\n");
        stats.append("Max Size: ").append(maxSize).append("\n");
        stats.append("Current Size: ").append(currentSize.get()).append("\n");
        stats.append("Available: ").append(availableObjects.size()).append("\n");
        stats.append("Created: ").append(createdCount.get()).append("\n");
        stats.append("Reused: ").append(reusedCount.get()).append("\n");
        stats.append("Requests: ").append(requestCount.get()).append("\n");
        stats.append("Returns: ").append(returnCount.get()).append("\n");
        stats.append("Reuse Ratio: ").append(String.format("%.2f%%", getReuseRatio() * 100)).append("\n");
        stats.append("Utilization: ").append(String.format("%.2f%%", getUtilizationRatio() * 100)).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Сбрасывает статистику пула
     */
    public void resetStatistics() {
        createdCount.set(0);
        reusedCount.set(0);
        requestCount.set(0);
        returnCount.set(0);
        
        _log.info("Pool statistics reset");
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    /**
     * Интерфейс для объектов, которые могут быть переиспользованы
     */
    public interface Poolable {
        /**
         * Сбрасывает состояние объекта для переиспользования
         */
        void reset();
    }
    
    /**
     * Базовый класс для объектов пула
     */
    public static abstract class PoolableObject implements Poolable {
        private boolean inUse = false;
        
        /**
         * Помечает объект как используемый
         */
        public void markInUse() {
            this.inUse = true;
        }
        
        /**
         * Помечает объект как свободный
         */
        public void markFree() {
            this.inUse = false;
        }
        
        /**
         * Проверяет, используется ли объект
         * 
         * @return true если объект используется
         */
        public boolean isInUse() {
            return inUse;
        }
        
        @Override
        public void reset() {
            this.inUse = false;
        }
    }
}
