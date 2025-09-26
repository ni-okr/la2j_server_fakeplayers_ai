package net.sf.l2j.botmanager.ai.impl;

import net.sf.l2j.botmanager.ai.*;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация движка принятия решений для ботов.
 * Анализирует текущую ситуацию и принимает решения о дальнейших действиях.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class DecisionEngineImpl implements DecisionEngine {
    
    private static final Logger logger = Logger.getLogger(DecisionEngineImpl.class);
    
    /** Максимальный возраст анализа ситуации (в миллисекундах) */
    private static final long MAX_ANALYSIS_AGE = 5000; // 5 секунд
    
    /** Кэш анализа ситуаций */
    private final Map<Integer, SituationAnalysis> analysisCache;
    
    /** Счетчик анализов */
    private final AtomicLong analysisCount;
    
    /** Счетчик решений */
    private final AtomicLong decisionCount;
    
    /**
     * Конструктор.
     */
    public DecisionEngineImpl() {
        this.analysisCache = new HashMap<>();
        this.analysisCount = new AtomicLong(0);
        this.decisionCount = new AtomicLong(0);
    }
    
    @Override
    public SituationAnalysis analyzeSituation(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot analyze situation: bot is null");
            return new SituationAnalysis();
        }
        
        int botId = bot.getBotId();
        
        // Проверяем кэш
        SituationAnalysis cachedAnalysis = analysisCache.get(botId);
        if (cachedAnalysis != null && cachedAnalysis.isUpToDate(MAX_ANALYSIS_AGE)) {
            return cachedAnalysis;
        }
        
        // Создаем новый анализ
        SituationAnalysis analysis = new SituationAnalysis();
        
        try {
            // Анализ здоровья и маны
            double healthLevel = calculateHealthLevel(bot);
            double manaLevel = calculateManaLevel(bot);
            analysis.setHealthLevel(healthLevel);
            analysis.setManaLevel(manaLevel);
            
            // Анализ опасности
            double dangerLevel = calculateDangerLevel(bot);
            analysis.setDangerLevel(dangerLevel);
            
            // Анализ окружения
            int nearbyEnemies = countNearbyEnemies(bot);
            int nearbyAllies = countNearbyAllies(bot);
            analysis.setNearbyEnemies(nearbyEnemies);
            analysis.setNearbyAllies(nearbyAllies);
            
            // Анализ доступных поведений
            Map<BehaviorType, Double> availableBehaviors = analyzeAvailableBehaviors(bot);
            analysis.setAvailableBehaviors(availableBehaviors);
            
            // Анализ доступных действий
            Map<ActionType, Double> availableActions = analyzeAvailableActions(bot);
            analysis.setAvailableActions(availableActions);
            
            // Расчет рекомендуемого приоритета
            double recommendedPriority = calculateRecommendedPriority(analysis);
            analysis.setRecommendedPriority(recommendedPriority);
            
            // Обновление времени
            analysis.updateTimestamp();
            
            // Кэширование
            analysisCache.put(botId, analysis);
            analysisCount.incrementAndGet();
            
            logger.debug("Situation analyzed for bot " + botId + ": " + analysis);
            
        } catch (Exception e) {
            logger.error("Error analyzing situation for bot " + botId, e);
        }
        
        return analysis;
    }
    
    @Override
    public Decision makeDecision(EnhancedFakePlayer bot, SituationAnalysis analysis) {
        if (bot == null || analysis == null) {
            logger.warn("Cannot make decision: bot or analysis is null");
            return null;
        }
        
        try {
            Decision decision = new Decision();
            
            // Выбор поведения на основе анализа
            BehaviorType selectedBehavior = selectBestBehavior(analysis);
            decision.setSelectedBehavior(selectedBehavior);
            
            // Планирование последовательности действий
            List<ActionType> actionSequence = planActionSequence(bot, analysis, selectedBehavior);
            decision.setActionSequence(actionSequence);
            
            // Расчет приоритета и уверенности
            double priority = calculateDecisionPriority(analysis);
            double confidence = calculateDecisionConfidence(analysis);
            decision.setPriority(priority);
            decision.setConfidence(confidence);
            
            decisionCount.incrementAndGet();
            
            logger.debug("Decision made for bot " + bot.getBotId() + ": " + decision);
            
            return decision;
            
        } catch (Exception e) {
            logger.error("Error making decision for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    @Override
    public double evaluateBehaviorPriority(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            return 0.0;
        }
        
        try {
            SituationAnalysis analysis = analyzeSituation(bot);
            BehaviorType behaviorType = behavior.getType();
            return analysis.getBehaviorPriority(behaviorType);
        } catch (Exception e) {
            logger.error("Error evaluating behavior priority for bot " + bot.getBotId(), e);
            return 0.0;
        }
    }
    
    @Override
    public double evaluateActionPriority(EnhancedFakePlayer bot, IAction action) {
        if (bot == null || action == null) {
            return 0.0;
        }
        
        try {
            SituationAnalysis analysis = analyzeSituation(bot);
            ActionType actionType = action.getType();
            return analysis.getActionPriority(actionType);
        } catch (Exception e) {
            logger.error("Error evaluating action priority for bot " + bot.getBotId(), e);
            return 0.0;
        }
    }
    
    @Override
    public boolean canExecuteAction(EnhancedFakePlayer bot, IAction action) {
        if (bot == null || action == null) {
            return false;
        }
        
        try {
            // Проверяем базовые условия
            if (bot.getPlayerInstance() != null && bot.getPlayerInstance().isDead()) {
                return false;
            }
            
            // Проверяем специфичные для действия условия
            return action.canExecute(bot.getContext());
            
        } catch (Exception e) {
            logger.error("Error checking action execution for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    @Override
    public void updateContext(EnhancedFakePlayer bot, net.sf.l2j.botmanager.core.BotContext context) {
        if (bot == null || context == null) {
            return;
        }
        
        // Очищаем кэш для этого бота
        analysisCache.remove(bot.getBotId());
        
        logger.debug("Context updated for bot " + bot.getBotId());
    }
    
    @Override
    public String getStatistics(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        return String.format("Decision Engine Stats for bot %d: Analyses=%d, Decisions=%d",
                bot.getBotId(), analysisCount.get(), decisionCount.get());
    }
    
    // Приватные методы для анализа
    
    private double calculateHealthLevel(EnhancedFakePlayer bot) {
        try {
            int currentHp = (int) bot.getCurrentHp();
            int maxHp = (int) bot.getMaxHp();
            return maxHp > 0 ? (double) currentHp / maxHp : 0.0;
        } catch (Exception e) {
            logger.warn("Error calculating health level for bot " + bot.getBotId() + ": " + e.getMessage());
            return 0.0;
        }
    }
    
    private double calculateManaLevel(EnhancedFakePlayer bot) {
        try {
            int currentMp = (int) bot.getCurrentMp();
            int maxMp = (int) bot.getMaxMp();
            return maxMp > 0 ? (double) currentMp / maxMp : 0.0;
        } catch (Exception e) {
            logger.warn("Error calculating mana level for bot " + bot.getBotId() + ": " + e.getMessage());
            return 0.0;
        }
    }
    
    private double calculateDangerLevel(EnhancedFakePlayer bot) {
        try {
            // Простая эвристика: больше врагов = больше опасности
            int enemies = countNearbyEnemies(bot);
            int allies = countNearbyAllies(bot);
            
            if (enemies == 0) {
                return 0.0;
            }
            
            double ratio = allies > 0 ? (double) enemies / allies : enemies;
            return Math.min(1.0, ratio / 3.0); // Нормализуем к 0-1
            
        } catch (Exception e) {
            logger.warn("Error calculating danger level for bot " + bot.getBotId() + ": " + e.getMessage());
            return 0.0;
        }
    }
    
    private int countNearbyEnemies(EnhancedFakePlayer bot) {
        // Заглушка - в реальной реализации нужно сканировать окружение
        return 0;
    }
    
    private int countNearbyAllies(EnhancedFakePlayer bot) {
        // Заглушка - в реальной реализации нужно сканировать окружение
        return 0;
    }
    
    private Map<BehaviorType, Double> analyzeAvailableBehaviors(EnhancedFakePlayer bot) {
        Map<BehaviorType, Double> behaviors = new HashMap<>();
        
        // Базовые поведения всегда доступны
        behaviors.put(BehaviorType.IDLE, 0.5);
        behaviors.put(BehaviorType.FARMING, 0.7);
        behaviors.put(BehaviorType.QUESTING, 0.6);
        behaviors.put(BehaviorType.PVP, 0.3);
        
        return behaviors;
    }
    
    private Map<ActionType, Double> analyzeAvailableActions(EnhancedFakePlayer bot) {
        Map<ActionType, Double> actions = new HashMap<>();
        
        // Базовые действия всегда доступны
        actions.put(ActionType.MOVE, 0.8);
        actions.put(ActionType.ATTACK, 0.6);
        actions.put(ActionType.CAST_SKILL, 0.5);
        actions.put(ActionType.PICKUP, 0.4);
        
        return actions;
    }
    
    private double calculateRecommendedPriority(SituationAnalysis analysis) {
        double dangerLevel = analysis.getDangerLevel();
        double healthLevel = analysis.getHealthLevel();
        
        // Приоритет выше при опасности и низком здоровье
        return Math.min(1.0, dangerLevel + (1.0 - healthLevel) * 0.5);
    }
    
    private BehaviorType selectBestBehavior(SituationAnalysis analysis) {
        Map<BehaviorType, Double> behaviors = analysis.getAvailableBehaviors();
        
        BehaviorType bestBehavior = null;
        double bestPriority = 0.0;
        
        for (Map.Entry<BehaviorType, Double> entry : behaviors.entrySet()) {
            if (entry.getValue() > bestPriority) {
                bestPriority = entry.getValue();
                bestBehavior = entry.getKey();
            }
        }
        
        return bestBehavior;
    }
    
    private List<ActionType> planActionSequence(EnhancedFakePlayer bot, SituationAnalysis analysis, BehaviorType behavior) {
        List<ActionType> sequence = new ArrayList<>();
        
        // Простое планирование на основе типа поведения
        switch (behavior) {
            case FARMING:
                sequence.add(ActionType.MOVE);
                sequence.add(ActionType.ATTACK);
                sequence.add(ActionType.PICKUP);
                break;
            case QUESTING:
                sequence.add(ActionType.MOVE);
                sequence.add(ActionType.CAST_SKILL);
                break;
            case PVP:
                sequence.add(ActionType.MOVE);
                sequence.add(ActionType.ATTACK);
                sequence.add(ActionType.CAST_SKILL);
                break;
            default:
                sequence.add(ActionType.MOVE);
                break;
        }
        
        return sequence;
    }
    
    private double calculateDecisionPriority(SituationAnalysis analysis) {
        return analysis.getRecommendedPriority();
    }
    
    private double calculateDecisionConfidence(SituationAnalysis analysis) {
        // Уверенность зависит от количества доступных опций
        int behaviorCount = analysis.getAvailableBehaviors().size();
        int actionCount = analysis.getAvailableActions().size();
        
        double confidence = Math.min(1.0, (behaviorCount + actionCount) / 8.0);
        return Math.max(0.1, confidence); // Минимум 10% уверенности
    }
}
