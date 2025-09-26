package net.sf.l2j.botmanager.events;

/**
 * Интерфейс слушателя событий
 */
public interface IEventListener<T extends BotEvent> {

    /**
     * Обрабатывает событие
     */
    void onEvent(T event);
}
