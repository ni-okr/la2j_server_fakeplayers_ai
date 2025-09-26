package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Оптимизированный пул потоков для системы ботов.
 * 
 * Предоставляет настраиваемый пул потоков с мониторингом производительности,
 * автоматической настройкой размера и обработкой перегрузки.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class OptimizedThreadPool {
    
    private static final Logger logger = Logger.getLogger(OptimizedThreadPool.class);
    private static OptimizedThreadPool instance;
    
    /** Основной пул потоков */
    private ScheduledThreadPoolExecutor executor;
    
    /** Планировщик для мониторинга */
    private ScheduledExecutorService monitorScheduler;
    
    /** Статистика пула */
    private final AtomicLong totalTasks;
    private final AtomicLong completedTasks;
    private final AtomicLong rejectedTasks;
    private final AtomicLong activeThreads;
    
    /** Настройки пула */
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final int queueCapacity;
    
    /** Флаг активности */
    private volatile boolean active;
    
    /**
     * Конструктор.
     */
    private OptimizedThreadPool() {
        // Настройки по умолчанию
        this.corePoolSize = Runtime.getRuntime().availableProcessors();
        this.maxPoolSize = corePoolSize * 2;
        this.keepAliveTime = 60L;
        this.queueCapacity = 1000;
        
        this.totalTasks = new AtomicLong(0);
        this.completedTasks = new AtomicLong(0);
        this.rejectedTasks = new AtomicLong(0);
        this.activeThreads = new AtomicLong(0);
        
        this.active = false;
    }
    
    /**
     * Получить экземпляр пула потоков.
     * 
     * @return экземпляр пула потоков
     */
    public static synchronized OptimizedThreadPool getInstance() {
        if (instance == null) {
            instance = new OptimizedThreadPool();
        }
        return instance;
    }
    
    /**
     * Инициализировать пул потоков.
     */
    public void initialize() {
        if (active) {
            logger.warn("Thread pool already initialized");
            return;
        }
        
        // Создаем очередь с приоритетом
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);
        
        // Создаем фабрику потоков
        ThreadFactory threadFactory = new BotThreadFactory();
        
        // Создаем обработчик переполнения
        RejectedExecutionHandler rejectionHandler = new BotRejectionHandler();
        
        // Создаем пул потоков
        executor = new ScheduledThreadPoolExecutor(
            corePoolSize,
            threadFactory,
            rejectionHandler
        );
        executor.setMaximumPoolSize(maxPoolSize);
        executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
        
        // Настраиваем мониторинг
        setupMonitoring();
        
        active = true;
        logger.info("Optimized thread pool initialized: core=" + corePoolSize + 
                   ", max=" + maxPoolSize + ", queue=" + queueCapacity);
    }
    
    /**
     * Выполнить задачу.
     * 
     * @param task задача
     * @return Future для результата
     */
    public Future<?> submit(Runnable task) {
        if (!active || task == null) {
            return null;
        }
        
        totalTasks.incrementAndGet();
        return executor.submit(new MonitoredTask(task));
    }
    
    /**
     * Выполнить задачу с результатом.
     * 
     * @param task задача
     * @return Future для результата
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (!active || task == null) {
            return null;
        }
        
        totalTasks.incrementAndGet();
        return executor.submit(new MonitoredCallable<>(task));
    }
    
    /**
     * Выполнить задачу с задержкой.
     * 
     * @param task задача
     * @param delay задержка в миллисекундах
     * @return Future для результата
     */
    public ScheduledFuture<?> schedule(Runnable task, long delay) {
        if (!active || task == null) {
            return null;
        }
        
        totalTasks.incrementAndGet();
        return executor.schedule(new MonitoredTask(task), delay, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Выполнить задачу периодически.
     * 
     * @param task задача
     * @param initialDelay начальная задержка в миллисекундах
     * @param period период в миллисекундах
     * @return Future для результата
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
        if (!active || task == null) {
            return null;
        }
        
        totalTasks.incrementAndGet();
        return executor.scheduleAtFixedRate(new MonitoredTask(task), 
            initialDelay, period, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Получить статистику пула.
     * 
     * @return статистика пула
     */
    public ThreadPoolStats getStats() {
        if (!active) {
            return new ThreadPoolStats(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        
        return new ThreadPoolStats(
            executor.getCorePoolSize(),
            executor.getMaximumPoolSize(),
            executor.getPoolSize(),
            executor.getActiveCount(),
            executor.getTaskCount(),
            executor.getCompletedTaskCount(),
            executor.getQueue().size(),
            totalTasks.get(),
            rejectedTasks.get()
        );
    }
    
    /**
     * Получить детальную статистику пула.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        if (!active) {
            return "Thread pool not initialized";
        }
        
        ThreadPoolStats stats = getStats();
        return String.format(
            "Thread Pool Statistics:\n" +
            "  Core Pool Size: %d\n" +
            "  Max Pool Size: %d\n" +
            "  Current Pool Size: %d\n" +
            "  Active Threads: %d\n" +
            "  Total Tasks: %d\n" +
            "  Completed Tasks: %d\n" +
            "  Queue Size: %d\n" +
            "  Rejected Tasks: %d\n" +
            "  Utilization: %.2f%%",
            stats.getCorePoolSize(),
            stats.getMaxPoolSize(),
            stats.getCurrentPoolSize(),
            stats.getActiveThreads(),
            stats.getTotalTasks(),
            stats.getCompletedTasks(),
            stats.getQueueSize(),
            stats.getRejectedTasks(),
            stats.getUtilization() * 100
        );
    }
    
    /**
     * Остановить пул потоков.
     */
    public void shutdown() {
        if (!active) {
            return;
        }
        
        active = false;
        
        // Останавливаем мониторинг
        if (monitorScheduler != null) {
            monitorScheduler.shutdown();
        }
        
        // Останавливаем пул потоков
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Thread pool shutdown");
    }
    
    /**
     * Настроить мониторинг пула.
     */
    private void setupMonitoring() {
        monitorScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ThreadPoolMonitor");
            t.setDaemon(true);
            return t;
        });
        
        // Мониторинг каждые 30 секунд
        monitorScheduler.scheduleWithFixedDelay(
            this::monitorPool,
            30, 30, TimeUnit.SECONDS
        );
    }
    
    /**
     * Мониторинг пула потоков.
     */
    private void monitorPool() {
        if (!active) {
            return;
        }
        
        ThreadPoolStats stats = getStats();
        
        // Логируем предупреждения при высокой нагрузке
        if (stats.getUtilization() > 0.8) {
            logger.warn("High thread pool utilization: " + 
                       String.format("%.1f%%", stats.getUtilization() * 100));
        }
        
        if (stats.getRejectedTasks() > 0) {
            logger.warn("Thread pool rejected " + stats.getRejectedTasks() + " tasks");
        }
        
        logger.debug("Thread pool stats: " + stats.getShortStats());
    }
    
    /**
     * Фабрика потоков для ботов.
     */
    private class BotThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix = "BotThread-";
        
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    
    /**
     * Обработчик переполнения пула.
     */
    private class BotRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejectedTasks.incrementAndGet();
            logger.warn("Task rejected by thread pool: " + r.getClass().getSimpleName());
        }
    }
    
    /**
     * Задача с мониторингом.
     */
    private class MonitoredTask implements Runnable {
        private final Runnable task;
        
        public MonitoredTask(Runnable task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            activeThreads.incrementAndGet();
            try {
                task.run();
                completedTasks.incrementAndGet();
            } catch (Exception e) {
                logger.error("Error in monitored task", e);
            } finally {
                activeThreads.decrementAndGet();
            }
        }
    }
    
    /**
     * Вызываемая задача с мониторингом.
     */
    private class MonitoredCallable<T> implements Callable<T> {
        private final Callable<T> task;
        
        public MonitoredCallable(Callable<T> task) {
            this.task = task;
        }
        
        @Override
        public T call() throws Exception {
            activeThreads.incrementAndGet();
            try {
                T result = task.call();
                completedTasks.incrementAndGet();
                return result;
            } catch (Exception e) {
                logger.error("Error in monitored callable", e);
                throw e;
            } finally {
                activeThreads.decrementAndGet();
            }
        }
    }
}
