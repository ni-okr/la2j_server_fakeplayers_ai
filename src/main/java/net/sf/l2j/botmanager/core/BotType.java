package net.sf.l2j.botmanager.core;

/**
 * Перечисление типов ботов
 */
public enum BotType {
    SOLDIER("Солдат", "Рядовой боец для защиты замка", 1),
    OFFICER("Офицер", "Командир группы солдат", 2),
    HIGH_OFFICER("Высший офицер", "Стратегический командир", 3),
    VICE_GUILDMASTER("Заместитель гильдии", "Главный стратег", 4),
    FARMER("Фермер", "Бот для сбора ресурсов", 5),
    MERCHANT("Торговец", "Бот для торговли", 6),
    GUARD("Стражник", "Бот для охраны территории", 7);

    private final String name;
    private final String description;
    private final int hierarchyLevel;

    BotType(String name, String description, int hierarchyLevel) {
        this.name = name;
        this.description = description;
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean canCommand(BotType subordinateType) {
        return this.hierarchyLevel > subordinateType.hierarchyLevel;
    }
}
