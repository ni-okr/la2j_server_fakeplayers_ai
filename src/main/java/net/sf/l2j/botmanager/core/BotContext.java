package net.sf.l2j.botmanager.core;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Контекст состояния бота
 */
public class BotContext {
    private final int botId;
    private final Map<String, Object> data = new HashMap<>();
    private final AtomicReference<BotState> state = new AtomicReference<>(BotState.IDLE);
    private final AtomicReference<L2PcInstance> playerInstance = new AtomicReference<>();
    private final long creationTime;
    private volatile long lastActivityTime;

    public BotContext(int botId) {
        this.botId = botId;
        this.creationTime = System.currentTimeMillis();
        this.lastActivityTime = creationTime;
    }

    public int getBotId() {
        return botId;
    }

    public BotState getState() {
        return state.get();
    }

    public void setState(BotState newState) {
        BotState oldState = state.getAndSet(newState);
        if (oldState != newState) {
            updateLastActivity();
            // Здесь можно добавить логирование смены состояния
        }
    }

    public L2PcInstance getPlayerInstance() {
        return playerInstance.get();
    }

    public void setPlayerInstance(L2PcInstance instance) {
        playerInstance.set(instance);
        updateLastActivity();
    }
    
    public void setBot(EnhancedFakePlayer bot) {
        setData("bot", bot);
        updateLastActivity();
    }
    
    public EnhancedFakePlayer getBot() {
        return getData("bot");
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    public void removeData(String key) {
        data.remove(key);
    }

    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void updateLastActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    public long getLifetime() {
        return System.currentTimeMillis() - creationTime;
    }

    public long getTimeSinceLastActivity() {
        return System.currentTimeMillis() - lastActivityTime;
    }

    public Map<String, Object> getAllData() {
        return new HashMap<>(data);
    }

    public void clearData() {
        data.clear();
    }
}
