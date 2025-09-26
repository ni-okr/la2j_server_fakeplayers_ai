package net.sf.l2j.botmanager.integration;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Планировщик задач для ботов.
 * 
 * Управляет выполнением задач ботов, планированием действий
 * и координацией между различными системами.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class TaskScheduler {
    
    private static final Logger logger = Logger.getLogger(TaskScheduler.class);
    
    /** Активные задачи ботов */
    private final Map<Integer, BotTaskQueue> botTasks;
    
    /** Глобальная очередь задач */
    private final PriorityQueue<ScheduledTask> globalTaskQueue;
    
    /** Счетчик выполненных задач */
    private final AtomicLong completedTasks;
    
    /** Счетчик отмененных задач */
    private final AtomicLong cancelledTasks;
    
    /** Счетчик ошибок задач */
    private final AtomicLong taskErrors;
    
    /** Флаг активности */
    private volatile boolean isActive = false;
    
    /**
     * Конструктор.
     */
    public TaskScheduler() {
        this.botTasks = new ConcurrentHashMap<>();
        this.globalTaskQueue = new PriorityQueue<>(Comparator.comparing(ScheduledTask::getPriority).reversed());
        this.completedTasks = new AtomicLong(0);
        this.cancelledTasks = new AtomicLong(0);
        this.taskErrors = new AtomicLong(0);
    }
    
    /**
     * Инициализация планировщика.
     */
    public void initialize() {
        if (isActive) {
            logger.warn("TaskScheduler already initialized");
            return;
        }
        
        isActive = true;
        logger.info("TaskScheduler initialized");
    }
    
    /**
     * Остановка планировщика.
     */
    public void shutdown() {
        if (!isActive) {
            logger.warn("TaskScheduler already shutdown");
            return;
        }
        
        // Отменяем все задачи
        for (BotTaskQueue taskQueue : botTasks.values()) {
            taskQueue.cancelAllTasks();
        }
        
        botTasks.clear();
        globalTaskQueue.clear();
        isActive = false;
        logger.info("TaskScheduler shutdown");
    }
    
    /**
     * Добавление задачи для бота.
     * 
     * @param bot бот
     * @param task задача
     * @param priority приоритет задачи
     * @return ID задачи или -1 если не удалось добавить
     */
    public long addTask(EnhancedFakePlayer bot, ITask task, double priority) {
        if (bot == null || task == null) {
            logger.warn("Cannot add task: bot or task is null");
            return -1;
        }
        
        if (!isActive) {
            logger.warn("TaskScheduler not active");
            return -1;
        }
        
        int botId = bot.getBotId();
        
        try {
            // Создаем очередь задач для бота если её нет
            BotTaskQueue taskQueue = botTasks.computeIfAbsent(botId, k -> new BotTaskQueue(bot));
            
            // Создаем запланированную задачу
            ScheduledTask scheduledTask = new ScheduledTask(task, priority, System.currentTimeMillis());
            long taskId = taskQueue.addTask(scheduledTask);
            
            // Добавляем в глобальную очередь
            globalTaskQueue.offer(scheduledTask);
            
            logger.debug("Task added for bot " + botId + ": " + task.getType() + " (priority: " + priority + ")");
            return taskId;
            
        } catch (Exception e) {
            logger.error("Error adding task for bot " + botId, e);
            taskErrors.incrementAndGet();
            return -1;
        }
    }
    
    /**
     * Отмена задачи.
     * 
     * @param bot бот
     * @param taskId ID задачи
     * @return true если задача отменена
     */
    public boolean cancelTask(EnhancedFakePlayer bot, long taskId) {
        if (bot == null) {
            logger.warn("Cannot cancel task: bot is null");
            return false;
        }
        
        int botId = bot.getBotId();
        BotTaskQueue taskQueue = botTasks.get(botId);
        
        if (taskQueue == null) {
            logger.warn("No task queue found for bot " + botId);
            return false;
        }
        
        try {
            boolean cancelled = taskQueue.cancelTask(taskId);
            if (cancelled) {
                cancelledTasks.incrementAndGet();
                logger.debug("Task " + taskId + " cancelled for bot " + botId);
            }
            return cancelled;
            
        } catch (Exception e) {
            logger.error("Error cancelling task " + taskId + " for bot " + botId, e);
            taskErrors.incrementAndGet();
            return false;
        }
    }
    
    /**
     * Отмена всех задач бота.
     * 
     * @param bot бот
     * @return количество отмененных задач
     */
    public int cancelAllTasks(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot cancel tasks: bot is null");
            return 0;
        }
        
        int botId = bot.getBotId();
        BotTaskQueue taskQueue = botTasks.get(botId);
        
        if (taskQueue == null) {
            return 0;
        }
        
        try {
            int cancelledCount = taskQueue.cancelAllTasks();
            cancelledTasks.addAndGet(cancelledCount);
            logger.info("Cancelled " + cancelledCount + " tasks for bot " + botId);
            return cancelledCount;
            
        } catch (Exception e) {
            logger.error("Error cancelling all tasks for bot " + botId, e);
            taskErrors.incrementAndGet();
            return 0;
        }
    }
    
    /**
     * Обработка одного цикла планировщика.
     * 
     * @return количество обработанных задач
     */
    public int processCycle() {
        if (!isActive) {
            return 0;
        }
        
        int processedTasks = 0;
        
        try {
            // Обрабатываем задачи по приоритету
            while (!globalTaskQueue.isEmpty()) {
                ScheduledTask scheduledTask = globalTaskQueue.poll();
                if (scheduledTask == null || scheduledTask.isCancelled()) {
                    continue;
                }
                
                if (processTask(scheduledTask)) {
                    processedTasks++;
                }
            }
            
        } catch (Exception e) {
            logger.error("Error processing task cycle", e);
            taskErrors.incrementAndGet();
        }
        
        return processedTasks;
    }
    
    /**
     * Обработка одной задачи.
     * 
     * @param scheduledTask запланированная задача
     * @return true если задача обработана успешно
     */
    private boolean processTask(ScheduledTask scheduledTask) {
        try {
            ITask task = scheduledTask.getTask();
            EnhancedFakePlayer bot = scheduledTask.getBot();
            
            if (bot == null || bot.getContext().getState() == BotState.DISCONNECTED) {
                return false;
            }
            
            // Выполняем задачу
            boolean success = task.execute(bot);
            
            if (success) {
                completedTasks.incrementAndGet();
                logger.debug("Task completed for bot " + bot.getBotId() + ": " + task.getType());
            } else {
                logger.debug("Task failed for bot " + bot.getBotId() + ": " + task.getType());
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("Error processing task", e);
            taskErrors.incrementAndGet();
            return false;
        }
    }
    
    /**
     * Получение очереди задач бота.
     * 
     * @param bot бот
     * @return очередь задач или null если бот не найден
     */
    public BotTaskQueue getBotTaskQueue(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        return botTasks.get(bot.getBotId());
    }
    
    /**
     * Получение статистики планировщика.
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        long completed = completedTasks.get();
        long cancelled = cancelledTasks.get();
        long errors = taskErrors.get();
        int activeBots = botTasks.size();
        int globalQueueSize = globalTaskQueue.size();
        
        return String.format("TaskScheduler Stats: Active=%s, Bots=%d, Global Queue=%d, " +
                           "Completed=%d, Cancelled=%d, Errors=%d",
                isActive, activeBots, globalQueueSize, completed, cancelled, errors);
    }
    
    /**
     * Получение статистики бота.
     * 
     * @param botId ID бота
     * @return статистика бота или null если бот не найден
     */
    public String getBotStatistics(int botId) {
        BotTaskQueue taskQueue = botTasks.get(botId);
        if (taskQueue == null) {
            return null;
        }
        
        return taskQueue.getStatistics();
    }
    
    /**
     * Проверка активности планировщика.
     * 
     * @return true если планировщик активен
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Интерфейс задачи.
     */
    public interface ITask {
        /**
         * Выполнение задачи.
         * 
         * @param bot бот
         * @return true если задача выполнена успешно
         */
        boolean execute(EnhancedFakePlayer bot);
        
        /**
         * Получение типа задачи.
         * 
         * @return тип задачи
         */
        String getType();
        
        /**
         * Получение приоритета задачи.
         * 
         * @return приоритет (0.0 - 1.0)
         */
        double getPriority();
    }
    
    /**
     * Запланированная задача.
     */
    public static class ScheduledTask {
        private final ITask task;
        private final double priority;
        private final long scheduledTime;
        private final long taskId;
        private volatile boolean cancelled = false;
        
        private static final AtomicLong taskIdCounter = new AtomicLong(1);
        
        public ScheduledTask(ITask task, double priority, long scheduledTime) {
            this.task = task;
            this.priority = priority;
            this.scheduledTime = scheduledTime;
            this.taskId = taskIdCounter.getAndIncrement();
        }
        
        public ITask getTask() {
            return task;
        }
        
        public double getPriority() {
            return priority;
        }
        
        public long getScheduledTime() {
            return scheduledTime;
        }
        
        public long getTaskId() {
            return taskId;
        }
        
        public boolean isCancelled() {
            return cancelled;
        }
        
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
        
        public EnhancedFakePlayer getBot() {
            // Предполагаем, что задача знает о боте
            return null; // Это нужно будет реализовать в конкретных задачах
        }
    }
    
    /**
     * Очередь задач бота.
     */
    public static class BotTaskQueue {
        private final EnhancedFakePlayer bot;
        private final PriorityQueue<ScheduledTask> tasks;
        private final Map<Long, ScheduledTask> taskMap;
        private final AtomicLong completedTasks;
        private final AtomicLong cancelledTasks;
        
        public BotTaskQueue(EnhancedFakePlayer bot) {
            this.bot = bot;
            this.tasks = new PriorityQueue<>(Comparator.comparing(ScheduledTask::getPriority).reversed());
            this.taskMap = new ConcurrentHashMap<>();
            this.completedTasks = new AtomicLong(0);
            this.cancelledTasks = new AtomicLong(0);
        }
        
        public long addTask(ScheduledTask task) {
            tasks.offer(task);
            taskMap.put(task.getTaskId(), task);
            return task.getTaskId();
        }
        
        public boolean cancelTask(long taskId) {
            ScheduledTask task = taskMap.get(taskId);
            if (task != null) {
                task.setCancelled(true);
                taskMap.remove(taskId);
                tasks.remove(task);
                cancelledTasks.incrementAndGet();
                return true;
            }
            return false;
        }
        
        public int cancelAllTasks() {
            int count = taskMap.size();
            for (ScheduledTask task : taskMap.values()) {
                task.setCancelled(true);
            }
            taskMap.clear();
            tasks.clear();
            cancelledTasks.addAndGet(count);
            return count;
        }
        
        public String getStatistics() {
            return String.format("Bot %d Tasks: Queue=%d, Completed=%d, Cancelled=%d",
                    bot.getBotId(), tasks.size(), completedTasks.get(), cancelledTasks.get());
        }
    }
}
