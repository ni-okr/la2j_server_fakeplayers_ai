package net.sf.l2j.botmanager.events;

import net.sf.l2j.botmanager.core.BotContext;

/**
 * Базовый класс для всех событий ботов
 */
public abstract class BotEvent {
    private final BotContext source;
    private final long timestamp;

    protected BotEvent(BotContext source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Получает источник события
     */
    public BotContext getSource() {
        return source;
    }

    /**
     * Получает время создания события
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Получает тип события
     */
    public abstract String getEventType();
}
