# СТАНДАРТЫ КОДИРОВАНИЯ L2J BOT SYSTEM

## ОБЩИЕ ПРИНЦИПЫ

### 1. АРХИТЕКТУРНАЯ ПАРАДИГМА
- **Паттерн**: Component-Based Architecture + State Machine
- **Принцип**: Single Responsibility Principle (SRP)
- **Стиль**: Clean Code + SOLID принципы
- **Язык**: Java 8+ (совместимость с L2J)

### 2. СТРУКТУРА ПАКЕТОВ
```
net.sf.l2j.gameserver.fakeplayer
├── core/           # Основные компоненты
├── ai/             # ИИ система
├── behaviors/      # Поведения ботов
├── actions/        # Действия ботов
├── events/         # Обработчики событий
├── managers/       # Менеджеры
├── config/         # Конфигурация
└── utils/          # Утилиты
```

### 3. НАИМЕНОВАНИЕ КЛАССОВ
- **Интерфейсы**: `I` + описание (например: `IBotBehavior`)
- **Абстрактные классы**: `Abstract` + описание (например: `AbstractBotAI`)
- **Конкретные классы**: Описание + суффикс (например: `FarmingBehavior`)
- **Менеджеры**: Описание + `Manager` (например: `BotManager`)
- **Конфигурация**: Описание + `Config` (например: `BotConfig`)

### 4. НАИМЕНОВАНИЕ МЕТОДОВ
- **Публичные методы**: camelCase (например: `executeAction()`)
- **Приватные методы**: camelCase с префиксом `_` (например: `_calculateDistance()`)
- **Константы**: UPPER_SNAKE_CASE (например: `MAX_BOT_COUNT`)
- **Переменные**: camelCase (например: `botState`)

### 5. СТРУКТУРА КЛАССА
```java
public class ExampleBotBehavior implements IBotBehavior {
    // 1. Константы
    private static final int MAX_ATTEMPTS = 3;
    
    // 2. Поля
    private final BotContext context;
    private BotState currentState;
    
    // 3. Конструктор
    public ExampleBotBehavior(BotContext context) {
        this.context = context;
        this.currentState = BotState.IDLE;
    }
    
    // 4. Публичные методы
    @Override
    public void execute() {
        // Реализация
    }
    
    // 5. Приватные методы
    private void _processState() {
        // Реализация
    }
}
```

### 6. КОММЕНТАРИИ И ДОКУМЕНТАЦИЯ
- **JavaDoc**: Обязательно для всех публичных методов
- **Комментарии**: Только для сложной логики
- **TODO**: Для незавершенной функциональности
- **FIXME**: Для известных проблем

### 7. ОБРАБОТКА ОШИБОК
- **Исключения**: Использовать специфичные исключения
- **Логирование**: Всегда логировать ошибки
- **Восстановление**: Graceful degradation при ошибках

### 8. КОНФИГУРАЦИЯ
- **Файлы**: `.properties` для настроек
- **Константы**: Enum для перечислений
- **Валидация**: Проверка конфигурации при загрузке

### 9. ТЕСТИРОВАНИЕ
- **Unit тесты**: Для каждого публичного метода
- **Integration тесты**: Для взаимодействия компонентов
- **Mock объекты**: Для изоляции тестируемых компонентов

### 10. ПРОИЗВОДИТЕЛЬНОСТЬ
- **Пулы объектов**: Переиспользование объектов
- **Кэширование**: Для часто используемых данных
- **Асинхронность**: Для длительных операций
- **Профилирование**: Регулярная проверка производительности

## ПРИМЕРЫ КОДА

### Интерфейс поведения
```java
/**
 * Интерфейс для поведения бота
 */
public interface IBotBehavior {
    /**
     * Выполняет поведение бота
     */
    void execute();
    
    /**
     * Проверяет, может ли бот выполнить поведение
     * @return true если поведение может быть выполнено
     */
    boolean canExecute();
    
    /**
     * Получает приоритет поведения
     * @return приоритет (чем больше, тем выше)
     */
    int getPriority();
}
```

### Конфигурация
```java
/**
 * Конфигурация бота
 */
public class BotConfig {
    private static final String CONFIG_FILE = "config/bot.properties";
    
    private static BotConfig instance;
    
    private final Properties properties;
    
    private BotConfig() {
        this.properties = new Properties();
        loadConfig();
    }
    
    public static BotConfig getInstance() {
        if (instance == null) {
            instance = new BotConfig();
        }
        return instance;
    }
    
    private void loadConfig() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Failed to load bot config", e);
        }
    }
}
```

### Менеджер
```java
/**
 * Менеджер ботов
 */
public class BotManager {
    private static final Logger LOG = LoggerFactory.getLogger(BotManager.class);
    
    private final Map<Integer, FakePlayer> bots = new ConcurrentHashMap<>();
    private final BotConfig config;
    
    public BotManager() {
        this.config = BotConfig.getInstance();
    }
    
    /**
     * Создает нового бота
     * @param botId ID бота
     * @return созданный бот
     */
    public FakePlayer createBot(int botId) {
        if (bots.containsKey(botId)) {
            throw new IllegalArgumentException("Bot with ID " + botId + " already exists");
        }
        
        FakePlayer bot = new FakePlayer(botId);
        bots.put(botId, bot);
        
        LOG.info("Created bot with ID: {}", botId);
        return bot;
    }
}
```

## ПРОВЕРКА СООТВЕТСТВИЯ

### Автоматические проверки
- **Checkstyle**: Проверка стиля кода
- **SpotBugs**: Поиск потенциальных ошибок
- **PMD**: Анализ качества кода
- **JaCoCo**: Покрытие тестами

### Ручные проверки
- **Code Review**: Обязательно для всех изменений
- **Архитектурный ревью**: Для новых компонентов
- **Производительность**: Для критических компонентов

## ИНСТРУМЕНТЫ РАЗРАБОТКИ

### IDE
- **IntelliJ IDEA**: Основная IDE
- **Eclipse**: Альтернативная IDE
- **VS Code**: Для конфигурационных файлов

### Сборка
- **Maven**: Система сборки
- **Gradle**: Альтернативная система сборки

### Тестирование
- **JUnit 5**: Unit тесты
- **Mockito**: Mock объекты
- **TestContainers**: Integration тесты

### Мониторинг
- **SLF4J + Logback**: Логирование
- **Micrometer**: Метрики
- **JMX**: Управление

## ЗАКЛЮЧЕНИЕ

Эти стандарты обеспечивают:
- **Единообразие**: Все разработчики пишут код в одном стиле
- **Читаемость**: Код легко понимать и поддерживать
- **Надежность**: Меньше ошибок и багов
- **Производительность**: Оптимизированный код
- **Масштабируемость**: Легко добавлять новую функциональность
