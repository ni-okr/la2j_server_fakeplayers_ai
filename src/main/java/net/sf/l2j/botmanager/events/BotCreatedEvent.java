package net.sf.l2j.botmanager.events;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;

/**
 * Событие создания бота
 */
public class BotCreatedEvent extends BotEvent {

    public BotCreatedEvent(BotContext source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return "BOT_CREATED";
    }

    /**
     * Получает ID созданного бота
     */
    public int getBotId() {
        return getSource().getBotId();
    }

    /**
     * Получает тип созданного бота
     */
    public BotType getBotType() {
        return getSource().getData("botType");
    }

    /**
     * Получает имя созданного бота
     */
    public String getBotName() {
        return getSource().getData("botName");
    }
}
