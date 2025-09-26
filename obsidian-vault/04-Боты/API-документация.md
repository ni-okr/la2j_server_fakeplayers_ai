# 🔌 API документация системы ботов

## Обзор API

API системы ботов предоставляет полный доступ к функциональности для создания, управления и мониторинга ботов в игре Lineage 2.

## 📚 Основные компоненты API

### 1. BotManager
Основной менеджер для управления ботами.

```java
public class BotManager {
    // Получение экземпляра
    public static BotManager getInstance();

    // Создание ботов
    public BotContext createBot(BotType type, String botName) throws BotException;
    public BotContext createBot(BotType type, L2PcInstance playerInstance) throws BotException;

    // Управление ботами
    public void removeBot(int botId) throws BotException;
    public BotContext getBot(int botId);
    public Collection<BotContext> getAllBots();

    // Поиск ботов
    public Collection<BotContext> getBotsByType(BotType type);
    public Collection<BotContext> getBotsByState(BotState state);
    public Collection<BotContext> getBotsByOwner(L2PcInstance owner);

    // Статистика
    public String getBotStatistics();
    public int getBotCount();
}
```

### 2. CastleBotManager
Менеджер для управления ботами замков.

```java
public class CastleBotManager {
    // Получение экземпляра
    public static CastleBotManager getInstance();

    // Управление группами
    public CastleBotGroup createBotGroup(int castleId, L2PcInstance owner);
    public CastleBotGroup getBotGroup(int castleId);
    public void removeBotGroup(int castleId);

    // Покупка ботов
    public BotContext purchaseBot(int castleId, BotType botType, String botName, L2PcInstance owner);

    // Получение ботов
    public Collection<BotContext> getCastleBots(int castleId);
    public Collection<BotContext> getCastleBotsByType(int castleId, BotType type);

    // Управление режимами
    public void prepareForSiege(int castleId);
    public void enterPeaceMode(int castleId);

    // Статистика
    public String getCastleBotStatistics(int castleId);
}
```

### 3. BotMarketplace
Рынок ботов для обычных игроков.

```java
public class BotMarketplace {
    // Получение экземпляра
    public static BotMarketplace getInstance();

    // Покупка ботов
    public BotContext purchaseBot(L2PcInstance player, BotType botType, String botName);

    // Управление ботами игрока
    public Collection<BotContext> getPlayerBots(L2PcInstance player);
    public void removePlayerBot(L2PcInstance player, int botId);

    // Действия с ботами
    public void sendBotWithPlayer(L2PcInstance player, int botId);
    public void sendBotOnTask(L2PcInstance player, int botId, String task);
    public void returnBotToIdle(L2PcInstance player, int botId);

    // Цены и лимиты
    public int getBotPrice(BotType botType);
    public boolean canPlayerBuyMoreBots(L2PcInstance player, BotType type);

    // Статистика
    public String getMarketStatistics();
}
```

## 🎯 BotContext API

### Создание и управление контекстом
```java
// Создание контекста
BotContext context = new BotContext(1);

// Установка данных
context.setData("botName", "MyBot");
context.setData("owner", player);
context.setData("target", mob);

// Получение данных
String botName = context.getData("botName");
L2PcInstance owner = context.getData("owner");
L2Object target = context.getData("target");

// Типизированный доступ
BotType botType = context.getData("botType", BotType.SOLDIER);
int level = context.getData("level", 1);
```

### Управление состоянием
```java
// Получение текущего состояния
BotState currentState = context.getState();

// Смена состояния
context.setState(BotState.MOVING);
context.setState(BotState.FIGHTING);

// Проверка состояния
boolean isIdle = context.getState() == BotState.IDLE;
boolean isActive = context.getState() != BotState.DEAD;
```

### Работа с персонажем
```java
// Установка персонажа
context.setPlayerInstance(playerInstance);

// Получение персонажа
L2PcInstance player = context.getPlayerInstance();

// Проверка активности
boolean hasPlayer = context.getPlayerInstance() != null;
```

## 📊 EventManager API

### Подписка на события
```java
EventManager eventManager = EventManager.getInstance();

// Подписка на создание ботов
eventManager.subscribe(BotCreatedEvent.class, new IEventListener<BotCreatedEvent>() {
    @Override
    public void onEvent(BotCreatedEvent event) {
        System.out.println("Bot created: " + event.getBotName());
        // Дополнительная обработка
    }
});

// Подписка на удаление ботов
eventManager.subscribe(BotRemovedEvent.class, new IEventListener<BotRemovedEvent>() {
    @Override
    public void onEvent(BotRemovedEvent event) {
        System.out.println("Bot removed: " + event.getBotName());
        // Очистка ресурсов
    }
});
```

### Публикация событий
```java
// Создание события
BotCreatedEvent event = new BotCreatedEvent(botContext);

// Публикация события
eventManager.publish(event);

// Асинхронная публикация
eventManager.publishAsync(event);
```

## 🏰 CastleBotGroup API

### Управление группой
```java
CastleBotGroup group = castleManager.getBotGroup(castleId);

// Добавление бота в группу
group.addBot(botContext);

// Удаление бота из группы
group.removeBot(botId);

// Получение всех ботов
Collection<BotContext> allBots = group.getAllBots();

// Получение ботов по типу
Collection<BotContext> soldiers = group.getBotsByType(BotType.SOLDIER);
```

### Иерархическое управление
```java
// Получение командиров
BotContext viceGuildmaster = group.getViceGuildmaster();
Collection<BotContext> highOfficers = group.getHighOfficers();
Collection<BotContext> officers = group.getOfficers();

// Проверка лимитов
boolean canAddMore = group.canAddMoreBots(BotType.SOLDIER);
int maxSoldiers = group.getMaxBots(BotType.SOLDIER);
```

### Режимы работы
```java
// Подготовка к осаде
group.prepareForSiege();

// Переход в мирный режим
group.enterPeaceMode();

// Получение статистики
String stats = group.getGroupStatistics();
```

## 🛒 PersonalBotGroup API

### Управление личными ботами
```java
PersonalBotGroup personalGroup = marketplace.getPlayerBotGroup(player);

// Получение всех ботов
Collection<BotContext> myBots = personalGroup.getAllBots();

// Получение активных ботов
Collection<BotContext> activeBots = personalGroup.getActiveBots();

// Получение ботов в режиме ожидания
Collection<BotContext> idleBots = personalGroup.getIdleBots();
```

### Назначение задач
```java
// Назначение задачи
personalGroup.assignTask(botId, "farm_resources");

// Завершение задачи
personalGroup.completeTask(botId);

// Получение текущей задачи
String currentTask = personalGroup.getBotTask(botId);
```

### Групповые операции
```java
// Отправка всех ботов с игроком
personalGroup.sendBotsWithPlayer(allBots);

// Возврат всех ботов в режим ожидания
personalGroup.returnAllBotsToIdle();

// Получение статистики группы
String stats = personalGroup.getGroupStatistics();
```

## ⚙️ CastleMenuManager API

### Отображение меню
```java
CastleMenuManager menuManager = CastleMenuManager.getInstance();

// Главное меню замка
menuManager.showMainMenu(player, castleId);

// Меню покупки ботов
menuManager.showPurchaseMenu(player, castleId);

// Меню управления осадой
menuManager.showSiegeMenu(player, castleId);

// Меню мирного времени
menuManager.showPeaceMenu(player, castleId);

// Меню семи печатей
menuManager.showSevenSealsMenu(player, castleId);

// Меню олимпиады
menuManager.showOlympiadMenu(player, castleId);

// Статистика замка
menuManager.showStatistics(player, castleId);
```

## 📈 Мониторинг и статистика

### Сбор метрик
```java
// Статистика всех ботов
String globalStats = BotManager.getInstance().getBotStatistics();

// Статистика замка
String castleStats = CastleBotManager.getInstance().getCastleBotStatistics(castleId);

// Статистика рынка
String marketStats = BotMarketplace.getInstance().getMarketStatistics();

// Статистика игрока
String playerStats = BotMarketplace.getInstance().getPlayerBotStatistics(player);
```

### EventManager статистика
```java
EventManager eventManager = EventManager.getInstance();

// Количество обработанных событий
int totalEvents = eventManager.getTotalEventCount();

// Количество подписчиков
int totalListeners = eventManager.getTotalListenerCount();

// Статистика по типам событий
int botCreatedEvents = eventManager.getEventCount(BotCreatedEvent.class);
```

## 🔧 Конфигурация

### BotConfig API
```java
// Доступ к конфигурации (если реализовано)
BotConfig config = BotConfig.getInstance();

// Основные настройки
int maxBotCount = config.getMaxBotCount();
BotType defaultType = config.getDefaultBotType();
boolean autoCreate = config.isAutoCreateEnabled();

// Настройки ИИ
int decisionInterval = config.getDecisionInterval();
int behaviorSwitchDelay = config.getBehaviorSwitchDelay();

// Производительность
int maxMemoryPerBot = config.getMaxMemoryPerBot();
int threadPoolSize = config.getThreadPoolSize();
```

## 🚨 Обработка ошибок

### Исключения
```java
try {
    BotContext bot = botManager.createBot(BotType.SOLDIER, "MySoldier");
} catch (BotException e) {
    // Обработка ошибки создания бота
    System.err.println("Failed to create bot: " + e.getMessage());
    e.printStackTrace();
}
```

### Типы исключений
- **BotException**: Общее исключение системы ботов
- **BotLimitExceededException**: Превышен лимит ботов
- **BotNotFoundException**: Бот не найден
- **InsufficientFundsException**: Недостаточно средств

## 📝 Логирование

### Уровни логирования
```java
// Debug информация
_log.debug("Bot " + botId + " changed state from " + oldState + " to " + newState);

// Информационные сообщения
_log.info("Player " + playerName + " purchased bot " + botName);

// Предупреждения
_log.warning("Bot " + botId + " is running low on health: " + healthPercent + "%");

// Ошибки
_log.error("Failed to create bot for player " + playerName, exception);
```

### Конфигурация логирования
```xml
<!-- logback.xml -->
<logger name="net.sf.l2j.botmanager" level="DEBUG" additivity="false">
    <appender-ref ref="BOT_FILE"/>
</logger>
```

## 🔒 Безопасность

### Проверки доступа
```java
// Проверка прав на управление замком
boolean canManage = castleGroup.canPlayerManage(player);

// Проверка владения ботом
boolean ownsBot = player.equals(botContext.getData("owner"));

// Проверка лимитов
boolean canBuyMore = marketplace.canPlayerBuyMoreBots(player, botType);
```

### Валидация данных
```java
// Валидация входных параметров
if (botName == null || botName.trim().isEmpty()) {
    throw new BotException("Bot name cannot be empty");
}

if (price < 0) {
    throw new BotException("Price cannot be negative");
}
```

---

**Примечание**: Это базовая API документация. Для полного понимания рекомендуется изучить исходный код и JavaDoc комментарии.
