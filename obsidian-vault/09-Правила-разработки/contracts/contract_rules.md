# ПРАВИЛА КОНТРАКТНОГО ВЗАИМОДЕЙСТВИЯ L2J BOT SYSTEM

## ОБЩИЕ ПРИНЦИПЫ КОНТРАКТОВ

### 1. INTERFACE SEGREGATION PRINCIPLE
- **Принцип**: Клиенты не должны зависеть от интерфейсов, которые они не используют
- **Реализация**: Мелкие, специализированные интерфейсы
- **Пример**: `IMoveable`, `IAttackable`, `IHealable` вместо одного большого `IBot`

### 2. DEPENDENCY INVERSION PRINCIPLE
- **Принцип**: Зависимости должны быть на абстракциях, а не на конкретных классах
- **Реализация**: Использование интерфейсов для всех зависимостей
- **Пример**: `IBotManager` вместо `BotManager`

### 3. LISKOV SUBSTITUTION PRINCIPLE
- **Принцип**: Объекты должны быть заменяемы экземплярами их подтипов
- **Реализация**: Правильная иерархия наследования
- **Пример**: `FarmingBehavior` должен работать как `AbstractBehavior`

## СТАНДАРТЫ ИНТЕРФЕЙСОВ

### 1. NAMING CONVENTIONS
```java
// Интерфейсы начинаются с I
public interface IBehavior { }
public interface IAction { }
public interface IEventListener { }

// Абстрактные классы начинаются с Abstract
public abstract class AbstractBehavior implements IBehavior { }
public abstract class AbstractAction implements IAction { }

// Конкретные классы имеют описательные имена
public class FarmingBehavior extends AbstractBehavior { }
public class MoveAction extends AbstractAction { }
```

### 2. METHOD SIGNATURES
```java
// Все методы должны быть явно типизированы
public interface IBehavior {
    // Возвращаемый тип всегда указан
    boolean canExecute();
    
    // Параметры явно типизированы
    void execute(BotContext context);
    
    // Приоритет всегда int
    int getPriority();
}

// Исключения явно объявлены
public interface IAction {
    void execute() throws ActionException;
    void undo() throws ActionException;
}
```

### 3. GENERIC CONSTRAINTS
```java
// Использование дженериков для типобезопасности
public interface IEventListener<T extends BotEvent> {
    void onEvent(T event);
}

// Ограничения типов
public interface IBehavior<T extends BotContext> {
    boolean canExecute(T context);
    void execute(T context);
}
```

## КОНТРАКТЫ КОМПОНЕНТОВ

### 1. BEHAVIOR CONTRACTS
```java
/**
 * Контракт для поведения бота
 */
public interface IBehavior {
    /**
     * Проверяет, может ли бот выполнить поведение
     * @param context контекст бота
     * @return true если поведение может быть выполнено
     * @throws IllegalArgumentException если context null
     */
    boolean canExecute(@NonNull BotContext context);
    
    /**
     * Выполняет поведение бота
     * @param context контекст бота
     * @throws BehaviorException если поведение не может быть выполнено
     * @throws IllegalArgumentException если context null
     */
    void execute(@NonNull BotContext context) throws BehaviorException;
    
    /**
     * Получает приоритет поведения
     * @return приоритет (чем больше, тем выше)
     */
    int getPriority();
    
    /**
     * Получает тип поведения
     * @return тип поведения
     */
    BehaviorType getType();
    
    /**
     * Проверяет, является ли поведение активным
     * @return true если поведение активно
     */
    boolean isActive();
    
    /**
     * Активирует поведение
     */
    void activate();
    
    /**
     * Деактивирует поведение
     */
    void deactivate();
}
```

### 2. ACTION CONTRACTS
```java
/**
 * Контракт для действия бота
 */
public interface IAction {
    /**
     * Выполняет действие
     * @throws ActionException если действие не может быть выполнено
     */
    void execute() throws ActionException;
    
    /**
     * Отменяет действие
     * @throws ActionException если действие не может быть отменено
     */
    void undo() throws ActionException;
    
    /**
     * Проверяет, может ли действие быть выполнено
     * @return true если действие может быть выполнено
     */
    boolean canExecute();
    
    /**
     * Получает приоритет действия
     * @return приоритет (чем больше, тем выше)
     */
    int getPriority();
    
    /**
     * Получает тип действия
     * @return тип действия
     */
    ActionType getType();
    
    /**
     * Получает время выполнения действия
     * @return время в миллисекундах
     */
    long getExecutionTime();
}
```

### 3. EVENT CONTRACTS
```java
/**
 * Контракт для события бота
 */
public interface IBotEvent {
    /**
     * Получает тип события
     * @return тип события
     */
    EventType getType();
    
    /**
     * Получает время события
     * @return время события
     */
    long getTimestamp();
    
    /**
     * Получает источник события
     * @return источник события
     */
    Object getSource();
    
    /**
     * Получает данные события
     * @return данные события
     */
    Map<String, Object> getData();
}
```

## КОНТРАКТЫ МЕНЕДЖЕРОВ

### 1. BOT MANAGER CONTRACT
```java
/**
 * Контракт для менеджера ботов
 */
public interface IBotManager {
    /**
     * Создает нового бота
     * @param botId ID бота
     * @param type тип бота
     * @return созданный бот
     * @throws BotException если бот не может быть создан
     */
    FakePlayer createBot(int botId, BotType type) throws BotException;
    
    /**
     * Удаляет бота
     * @param botId ID бота
     * @throws BotException если бот не может быть удален
     */
    void removeBot(int botId) throws BotException;
    
    /**
     * Получает бота по ID
     * @param botId ID бота
     * @return бот или null если не найден
     */
    FakePlayer getBot(int botId);
    
    /**
     * Получает всех ботов
     * @return коллекция ботов
     */
    Collection<FakePlayer> getAllBots();
    
    /**
     * Получает количество ботов
     * @return количество ботов
     */
    int getBotCount();
    
    /**
     * Проверяет, существует ли бот
     * @param botId ID бота
     * @return true если бот существует
     */
    boolean hasBot(int botId);
}
```

### 2. BEHAVIOR MANAGER CONTRACT
```java
/**
 * Контракт для менеджера поведений
 */
public interface IBehaviorManager {
    /**
     * Регистрирует поведение
     * @param behavior поведение
     * @throws BehaviorException если поведение не может быть зарегистрировано
     */
    void registerBehavior(IBehavior behavior) throws BehaviorException;
    
    /**
     * Отменяет регистрацию поведения
     * @param behavior поведение
     */
    void unregisterBehavior(IBehavior behavior);
    
    /**
     * Получает поведение по типу
     * @param type тип поведения
     * @return поведение или null если не найдено
     */
    IBehavior getBehavior(BehaviorType type);
    
    /**
     * Получает все поведения
     * @return коллекция поведений
     */
    Collection<IBehavior> getAllBehaviors();
    
    /**
     * Выбирает лучшее поведение для контекста
     * @param context контекст бота
     * @return лучшее поведение или null если не найдено
     */
    IBehavior selectBestBehavior(BotContext context);
}
```

## КОНТРАКТЫ ИСКЛЮЧЕНИЙ

### 1. EXCEPTION HIERARCHY
```java
/**
 * Базовое исключение для ботов
 */
public class BotException extends Exception {
    public BotException(String message) {
        super(message);
    }
    
    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Исключение для поведений
 */
public class BehaviorException extends BotException {
    public BehaviorException(String message) {
        super(message);
    }
    
    public BehaviorException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Исключение для действий
 */
public class ActionException extends BotException {
    public ActionException(String message) {
        super(message);
    }
    
    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 2. EXCEPTION CONTRACTS
```java
/**
 * Контракт для исключений
 */
public interface IExceptionContract {
    /**
     * Получает код ошибки
     * @return код ошибки
     */
    String getErrorCode();
    
    /**
     * Получает детали ошибки
     * @return детали ошибки
     */
    Map<String, Object> getErrorDetails();
    
    /**
     * Проверяет, является ли ошибка критической
     * @return true если ошибка критическая
     */
    boolean isCritical();
    
    /**
     * Получает время ошибки
     * @return время ошибки
     */
    long getTimestamp();
}
```

## КОНТРАКТЫ КОНФИГУРАЦИИ

### 1. CONFIG CONTRACTS
```java
/**
 * Контракт для конфигурации
 */
public interface IConfig {
    /**
     * Получает значение конфигурации
     * @param key ключ конфигурации
     * @return значение конфигурации
     */
    String getString(String key);
    
    /**
     * Получает значение конфигурации с значением по умолчанию
     * @param key ключ конфигурации
     * @param defaultValue значение по умолчанию
     * @return значение конфигурации
     */
    String getString(String key, String defaultValue);
    
    /**
     * Получает целочисленное значение конфигурации
     * @param key ключ конфигурации
     * @return целочисленное значение
     */
    int getInt(String key);
    
    /**
     * Получает целочисленное значение конфигурации с значением по умолчанию
     * @param key ключ конфигурации
     * @param defaultValue значение по умолчанию
     * @return целочисленное значение
     */
    int getInt(String key, int defaultValue);
    
    /**
     * Получает логическое значение конфигурации
     * @param key ключ конфигурации
     * @return логическое значение
     */
    boolean getBoolean(String key);
    
    /**
     * Получает логическое значение конфигурации с значением по умолчанию
     * @param key ключ конфигурации
     * @param defaultValue значение по умолчанию
     * @return логическое значение
     */
    boolean getBoolean(String key, boolean defaultValue);
}
```

## КОНТРАКТЫ ВАЛИДАЦИИ

### 1. VALIDATION CONTRACTS
```java
/**
 * Контракт для валидации
 */
public interface IValidator<T> {
    /**
     * Валидирует объект
     * @param object объект для валидации
     * @return результат валидации
     */
    ValidationResult validate(T object);
    
    /**
     * Проверяет, может ли валидатор валидировать объект
     * @param object объект для проверки
     * @return true если валидатор может валидировать объект
     */
    boolean canValidate(T object);
}

/**
 * Результат валидации
 */
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
```

## ПРАВИЛА СОБЛЮДЕНИЯ КОНТРАКТОВ

### 1. COMPILE-TIME SAFETY
- **Принцип**: Максимальная проверка на этапе компиляции
- **Реализация**: Использование дженериков, аннотаций
- **Пример**: `@NonNull`, `@Nullable` аннотации

### 2. RUNTIME VALIDATION
- **Принцип**: Проверка контрактов во время выполнения
- **Реализация**: Assertions, валидация параметров
- **Пример**: Проверка null параметров

### 3. DOCUMENTATION
- **Принцип**: Документирование всех контрактов
- **Реализация**: JavaDoc с описанием контрактов
- **Пример**: Описание поведения при null параметрах

### 4. TESTING
- **Принцип**: Тестирование соблюдения контрактов
- **Реализация**: Unit тесты для каждого контракта
- **Пример**: Тесты с некорректными параметрами

## ЗАКЛЮЧЕНИЕ

Эти правила контрактного взаимодействия обеспечивают:
- **Типобезопасность**: Минимум ошибок на этапе компиляции
- **Предсказуемость**: Четкое поведение компонентов
- **Надежность**: Обработка всех возможных ситуаций
- **Поддерживаемость**: Легко понимать и изменять код
- **Тестируемость**: Четкие контракты для тестирования
