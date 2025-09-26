package net.sf.l2j.botmanager.core;

/**
 * Исключение для системы ботов
 */
public class BotException extends Exception {

    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
}
