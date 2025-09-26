package net.sf.l2j.botmanager.managers;

import net.sf.l2j.botmanager.events.BotEvent;
import net.sf.l2j.botmanager.events.IEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Менеджер событий для системы ботов
 */
public class EventManager {
    private static final Logger _log = Logger.getLogger(EventManager.class.getName());

    private static EventManager instance;

    private final Map<Class<? extends BotEvent>, List<IEventListener<?>>> listeners = new HashMap<>();
    private final ExecutorService eventExecutor = Executors.newCachedThreadPool();
    private final Map<Class<? extends BotEvent>, Integer> eventCounts = new HashMap<>();

    private EventManager() {
        _log.info("EventManager initialized");
    }

    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    /**
     * Подписывает слушатель на события определенного типа
     */
    public <T extends BotEvent> void subscribe(Class<T> eventType, IEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        _log.info("Subscribed listener " + listener.getClass().getSimpleName() + " to event type " + eventType.getSimpleName());
    }

    /**
     * Отписывает слушатель от событий определенного типа
     */
    @SuppressWarnings("unchecked")
    public <T extends BotEvent> void unsubscribe(Class<T> eventType, IEventListener<T> listener) {
        List<IEventListener<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            _log.info("Unsubscribed listener " + listener.getClass().getSimpleName() + " from event type " + eventType.getSimpleName());
        }
    }

    /**
     * Публикует событие
     */
    @SuppressWarnings("unchecked")
    public <T extends BotEvent> void publish(T event) {
        List<IEventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null && !eventListeners.isEmpty()) {
            eventExecutor.submit(() -> {
                try {
                    for (IEventListener<?> listener : eventListeners) {
                        ((IEventListener<T>) listener).onEvent(event);
                    }
                    eventCounts.merge((Class<? extends BotEvent>) event.getClass(), 1, Integer::sum);
                } catch (Exception e) {
                    _log.warning("Error processing event " + event.getClass().getSimpleName() + ": " + e.getMessage());
                }
            });
        }
    }

    /**
     * Публикует событие синхронно
     */
    @SuppressWarnings("unchecked")
    public <T extends BotEvent> void publishSync(T event) {
        List<IEventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null && !eventListeners.isEmpty()) {
            try {
                for (IEventListener<?> listener : eventListeners) {
                    ((IEventListener<T>) listener).onEvent(event);
                }
                eventCounts.merge((Class<? extends BotEvent>) event.getClass(), 1, Integer::sum);
            } catch (Exception e) {
                _log.warning("Error processing event " + event.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Получает количество обработанных событий определенного типа
     */
    public int getEventCount(Class<? extends BotEvent> eventType) {
        return eventCounts.getOrDefault(eventType, 0);
    }

    /**
     * Получает общее количество обработанных событий
     */
    public int getTotalEventCount() {
        return eventCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Получает количество подписчиков на определенный тип событий
     */
    public int getListenerCount(Class<? extends BotEvent> eventType) {
        List<IEventListener<?>> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }

    /**
     * Получает общее количество подписчиков
     */
    public int getTotalListenerCount() {
        return listeners.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Получает статистику событий
     */
    public String getEventStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("Event Statistics:\n");
        stats.append("Total events processed: ").append(getTotalEventCount()).append("\n");
        stats.append("Total listeners: ").append(getTotalListenerCount()).append("\n");

        for (Map.Entry<Class<? extends BotEvent>, List<IEventListener<?>>> entry : listeners.entrySet()) {
            int eventCount = eventCounts.getOrDefault(entry.getKey(), 0);
            stats.append(entry.getKey().getSimpleName())
                 .append(": ").append(eventCount).append(" events, ")
                 .append(entry.getValue().size()).append(" listeners\n");
        }

        return stats.toString();
    }

    /**
     * Останавливает обработку событий
     */
    public void shutdown() {
        eventExecutor.shutdown();
        _log.info("EventManager shutdown");
    }
}
