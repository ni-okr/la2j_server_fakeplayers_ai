package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;

/**
 * Интерфейс слушателя обратной связи для обучения ботов.
 */
public interface IFeedbackListener {
    
    /**
     * Обработка обратной связи по действию.
     * 
     * @param bot бот, выполнивший действие
     * @param actionType тип действия
     * @param success успешность выполнения
     * @param executionTime время выполнения в миллисекундах
     * @param details дополнительные детали
     */
    void onActionFeedback(EnhancedFakePlayer bot, ActionType actionType, boolean success, 
                         long executionTime, String details);
    
    /**
     * Обработка обратной связи по поведению.
     * 
     * @param bot бот, выполнивший поведение
     * @param behaviorType тип поведения
     * @param success успешность выполнения
     * @param executionTime время выполнения в миллисекундах
     * @param actionsPerformed количество выполненных действий
     * @param details дополнительные детали
     */
    void onBehaviorFeedback(EnhancedFakePlayer bot, BehaviorType behaviorType, boolean success, 
                           long executionTime, int actionsPerformed, String details);
    
    /**
     * Обработка события обучения.
     * 
     * @param bot бот, для которого произошло событие
     * @param event событие обучения
     */
    void onLearningEvent(EnhancedFakePlayer bot, LearningEvent event);
}
