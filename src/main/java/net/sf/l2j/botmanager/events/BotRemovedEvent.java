package net.sf.l2j.botmanager.events;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;

/**
 * Событие удаления бота
 */
public class BotRemovedEvent extends BotEvent {

    public BotRemovedEvent(BotContext source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return "BOT_REMOVED";
    }

    /**
     * Получает ID удаленного бота
     */
    public int getBotId() {
        return getSource().getBotId();
    }

    /**
     * Получает тип удаленного бота
     */
    public BotType getBotType() {
        return getSource().getData("botType");
    }

    /**
     * Получает имя удаленного бота
     */
    public String getBotName() {
        return getSource().getData("botName");
    }
}
