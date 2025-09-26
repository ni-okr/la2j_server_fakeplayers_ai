package net.sf.l2j.botmanager.behaviors;

/**
 * Перечисление типов поведений ботов
 */
public enum BehaviorType {
    IDLE("Бездействие", "Бот не выполняет никаких действий", 0),
    FARMING("Фарм", "Бот занимается фармом мобов и сбором ресурсов", 1),
    QUESTING("Квесты", "Бот выполняет квесты", 2),
    PVP("PvP", "Бот участвует в PvP сражениях", 3),
    SUPPORTING("Поддержка", "Бот поддерживает союзников", 4),
    CRAFTING("Крафт", "Бот занимается созданием предметов", 5),
    TRADING("Торговля", "Бот занимается торговлей", 6),
    PATROLLING("Патрулирование", "Бот патрулирует территорию", 7),
    GUARDING("Охрана", "Бот охраняет определенную территорию", 8),
    FOLLOWING("Следование", "Бот следует за игроком", 9),
    ATTACKING("Атака", "Бот атакует цель", 10),
    DEFENDING("Защита", "Бот защищает союзников", 11),
    HEALING("Лечение", "Бот лечит союзников", 12),
    LOOTING("Сбор лута", "Бот собирает добычу", 13),
    MOVING("Движение", "Бот перемещается к цели", 14),
    RESTING("Отдых", "Бот восстанавливает здоровье и ману", 15),
    SOCIAL("Социальное", "Бот взаимодействует с другими игроками", 16);

    private final String name;
    private final String description;
    private final int priority;

    BehaviorType(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Проверяет, может ли поведение прервать другое поведение
     * 
     * @param otherType другой тип поведения
     * @return true если может прервать
     */
    public boolean canInterrupt(BehaviorType otherType) {
        return this.priority > otherType.priority;
    }

    /**
     * Проверяет, является ли поведение боевым
     * 
     * @return true если поведение боевое
     */
    public boolean isCombatBehavior() {
        return this == PVP || this == ATTACKING || this == DEFENDING || this == HEALING;
    }

    /**
     * Проверяет, является ли поведение мирным
     * 
     * @return true если поведение мирное
     */
    public boolean isPeacefulBehavior() {
        return this == FARMING || this == QUESTING || this == CRAFTING || this == TRADING || this == LOOTING;
    }

    /**
     * Проверяет, является ли поведение социальным
     * 
     * @return true если поведение социальное
     */
    public boolean isSocialBehavior() {
        return this == SUPPORTING || this == FOLLOWING || this == TRADING;
    }
}
