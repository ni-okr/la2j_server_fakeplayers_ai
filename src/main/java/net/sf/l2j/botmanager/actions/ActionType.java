package net.sf.l2j.botmanager.actions;

/**
 * Перечисление типов действий ботов
 */
public enum ActionType {
    // Базовые действия
    IDLE("Бездействие", "Бот не выполняет никаких действий", 0),
    MOVE("Движение", "Бот перемещается к цели", 1),
    TURN("Поворот", "Бот поворачивается к цели", 2),
    STOP("Остановка", "Бот останавливается", 3),
    
    // Боевые действия
    ATTACK("Атака", "Бот атакует цель", 10),
    CAST_SKILL("Использование навыка", "Бот использует навык", 11),
    USE_ITEM("Использование предмета", "Бот использует предмет", 12),
    DEFEND("Защита", "Бот защищается от атаки", 13),
    ESCAPE("Отступление", "Бот отступает от опасности", 14),
    
    // Действия с предметами
    PICKUP("Подбор предмета", "Бот подбирает предмет", 20),
    DROP("Выброс предмета", "Бот выбрасывает предмет", 21),
    USE("Использование", "Бот использует предмет", 22),
    EQUIP("Экипировка", "Бот экипирует предмет", 23),
    UNEQUIP("Снятие экипировки", "Бот снимает предмет", 24),
    
    // Социальные действия
    TALK("Разговор", "Бот разговаривает с NPC", 30),
    TRADE("Торговля", "Бот торгует с NPC", 31),
    JOIN_PARTY("Вступление в группу", "Бот вступает в группу", 32),
    LEAVE_PARTY("Покидание группы", "Бот покидает группу", 33),
    
    // Действия восстановления
    REST("Отдых", "Бот отдыхает для восстановления", 40),
    HEAL("Лечение", "Бот лечится", 41),
    MEDITATE("Медитация", "Бот медитирует для восстановления маны", 42),
    
    // Действия поиска
    SEARCH("Поиск", "Бот ищет цель", 50),
    SCAN("Сканирование", "Бот сканирует область", 51),
    INVESTIGATE("Исследование", "Бот исследует объект", 52),
    
    // Специальные действия
    FOLLOW("Следование", "Бот следует за целью", 60),
    GUARD("Охрана", "Бот охраняет позицию", 61),
    PATROL("Патрулирование", "Бот патрулирует область", 62),
    WAIT("Ожидание", "Бот ждет указанное время", 63);

    private final String name;
    private final String description;
    private final int priority;

    ActionType(String name, String description, int priority) {
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
     * Проверяет, может ли действие прервать другое действие
     * 
     * @param otherType другой тип действия
     * @return true если может прервать
     */
    public boolean canInterrupt(ActionType otherType) {
        return this.priority > otherType.priority;
    }

    /**
     * Проверяет, является ли действие боевым
     * 
     * @return true если действие боевое
     */
    public boolean isCombatAction() {
        return this.priority >= 10 && this.priority < 20;
    }

    /**
     * Проверяет, является ли действие мирным
     * 
     * @return true если действие мирное
     */
    public boolean isPeacefulAction() {
        return this.priority < 10 || (this.priority >= 20 && this.priority < 30);
    }

    /**
     * Проверяет, является ли действие социальным
     * 
     * @return true если действие социальное
     */
    public boolean isSocialAction() {
        return this.priority >= 30 && this.priority < 40;
    }

    /**
     * Проверяет, является ли действие восстановительным
     * 
     * @return true если действие восстановительное
     */
    public boolean isRecoveryAction() {
        return this.priority >= 40 && this.priority < 50;
    }

    /**
     * Проверяет, является ли действие поисковым
     * 
     * @return true если действие поисковое
     */
    public boolean isSearchAction() {
        return this.priority >= 50 && this.priority < 60;
    }

    /**
     * Проверяет, является ли действие специальным
     * 
     * @return true если действие специальное
     */
    public boolean isSpecialAction() {
        return this.priority >= 60;
    }

    /**
     * Проверяет, требует ли действие цели
     * 
     * @return true если действие требует цели
     */
    public boolean requiresTarget() {
        return this == ATTACK || this == CAST_SKILL || this == TALK || 
               this == TRADE || this == FOLLOW || this == GUARD;
    }

    /**
     * Проверяет, требует ли действие предмета
     * 
     * @return true если действие требует предмета
     */
    public boolean requiresItem() {
        return this == USE_ITEM || this == EQUIP || this == UNEQUIP || 
               this == PICKUP || this == DROP || this == USE;
    }

    /**
     * Проверяет, требует ли действие позиции
     * 
     * @return true если действие требует позиции
     */
    public boolean requiresPosition() {
        return this == MOVE || this == TURN || this == GUARD || 
               this == PATROL || this == ESCAPE;
    }
}
