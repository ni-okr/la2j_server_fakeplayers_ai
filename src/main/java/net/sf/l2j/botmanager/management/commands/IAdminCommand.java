package net.sf.l2j.botmanager.management.commands;

/**
 * Интерфейс для команд администратора
 * Определяет базовую структуру команд управления ботами
 */
public interface IAdminCommand {
    
    /**
     * Получить имя команды
     * @return имя команды
     */
    String getCommandName();
    
    /**
     * Получить описание команды
     * @return описание команды
     */
    String getDescription();
    
    /**
     * Получить синтаксис команды
     * @return синтаксис команды
     */
    String getSyntax();
    
    /**
     * Выполнить команду
     * @param args аргументы команды
     * @param executorId ID исполнителя команды
     * @return результат выполнения команды
     */
    CommandResult execute(String[] args, int executorId);
    
    /**
     * Проверить права доступа для выполнения команды
     * @param executorId ID исполнителя команды
     * @return true если есть права доступа
     */
    boolean hasPermission(int executorId);
    
    /**
     * Получить минимальный уровень доступа для команды
     * @return минимальный уровень доступа
     */
    int getRequiredAccessLevel();
}
