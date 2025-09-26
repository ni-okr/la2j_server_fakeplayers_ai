package net.sf.l2j.botmanager.core;

/**
 * Перечисление состояний бота
 */
public enum BotState {
    IDLE("Простаивает", "Бот не выполняет никаких действий"),
    MOVING("Двигается", "Бот перемещается к цели"),
    FIGHTING("Сражается", "Бот участвует в бою"),
    FARMING("Собирает ресурсы", "Бот занимается фармом"),
    GUARDING("Охраняет", "Бот охраняет территорию"),
    PATROLLING("Патрулирует", "Бот патрулирует территорию"),
    TRADING("Торгует", "Бот занимается торговлей"),
    RESTING("Отдыхает", "Бот восстанавливает здоровье/ману"),
    DEAD("Мертв", "Бот мертв"),
    DISCONNECTED("Отключен", "Бот отключен от сервера");

    private final String name;
    private final String description;

    BotState(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
