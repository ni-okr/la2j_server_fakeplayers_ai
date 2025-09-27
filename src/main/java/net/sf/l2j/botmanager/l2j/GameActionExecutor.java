package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.serverpackets.MoveToLocation;
import net.sf.l2j.gameserver.network.serverpackets.Attack;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.skills.Skill;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Исполнитель базовых игровых действий для ботов
 */
public class GameActionExecutor {
    
    private static final Logger _log = Logger.getLogger(GameActionExecutor.class);
    
    // ==================== SINGLETON ====================
    
    private static volatile GameActionExecutor instance;
    
    public static GameActionExecutor getInstance() {
        if (instance == null) {
            synchronized (GameActionExecutor.class) {
                if (instance == null) {
                    instance = new GameActionExecutor();
                }
            }
        }
        return instance;
    }
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    private GameActionExecutor() {
        _log.info("GameActionExecutor initialized");
    }
    
    // ==================== ДВИЖЕНИЕ ====================
    
    /**
     * Перемещает бота в указанную точку
     * 
     * @param bot бот
     * @param targetX целевая X координата
     * @param targetY целевая Y координата
     * @param targetZ целевая Z координата
     * @return true если движение начато
     */
    public boolean moveTo(EnhancedFakePlayer bot, int targetX, int targetY, int targetZ) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            // Проверка, не слишком ли далеко цель
            double distance = player.getDistance(targetX, targetY, targetZ);
            if (distance > 10000) { // Максимальная дистанция движения
                _log.warn("Target too far for movement: " + distance);
                return false;
            }
            
            // Установка цели движения
            player.setTarget(null);
            player.getAI().setIntention(net.sf.l2j.gameserver.ai.CtrlIntention.MOVE_TO, 
                new Location(targetX, targetY, targetZ));
            
            _log.debug("Bot " + bot.getBotId() + " moving to: " + targetX + ", " + targetY + ", " + targetZ);
            return true;
            
        } catch (Exception e) {
            _log.error("Error moving bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Перемещает бота к цели
     * 
     * @param bot бот
     * @param target цель
     * @return true если движение начато
     */
    public boolean moveToTarget(EnhancedFakePlayer bot, L2Character target) {
        if (bot == null || target == null || !bot.isActive()) {
            return false;
        }
        
        return moveTo(bot, target.getX(), target.getY(), target.getZ());
    }
    
    /**
     * Случайное движение в радиусе
     * 
     * @param bot бот
     * @param radius радиус
     * @return true если движение начато
     */
    public boolean moveRandom(EnhancedFakePlayer bot, int radius) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        // Генерация случайной точки в радиусе
        int angle = ThreadLocalRandom.current().nextInt(360);
        int distance = ThreadLocalRandom.current().nextInt(radius);
        
        int targetX = currentX + (int) (Math.cos(Math.toRadians(angle)) * distance);
        int targetY = currentY + (int) (Math.sin(Math.toRadians(angle)) * distance);
        
        return moveTo(bot, targetX, targetY, currentZ);
    }
    
    // ==================== АТАКА ====================
    
    /**
     * Атакует цель
     * 
     * @param bot бот
     * @param target цель
     * @return true если атака начата
     */
    public boolean attack(EnhancedFakePlayer bot, L2Character target) {
        if (bot == null || target == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead() || target.isDead()) {
                return false;
            }
            
            // Проверка дистанции атаки
            double distance = player.getDistance(target);
            if (distance > player.getPhysicalAttackRange()) {
                _log.debug("Target too far for attack: " + distance);
                return false;
            }
            
            // Установка цели
            player.setTarget(target);
            
            // Начало атаки
            player.getAI().setIntention(net.sf.l2j.gameserver.ai.CtrlIntention.ATTACK, target);
            
            _log.debug("Bot " + bot.getBotId() + " attacking target: " + target.getName());
            return true;
            
        } catch (Exception e) {
            _log.error("Error attacking with bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Останавливает атаку
     * 
     * @param bot бот
     * @return true если атака остановлена
     */
    public boolean stopAttack(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return false;
            }
            
            player.setTarget(null);
            player.getAI().setIntention(net.sf.l2j.gameserver.ai.CtrlIntention.IDLE);
            
            _log.debug("Bot " + bot.getBotId() + " stopped attacking");
            return true;
            
        } catch (Exception e) {
            _log.error("Error stopping attack for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    // ==================== ИСПОЛЬЗОВАНИЕ УМЕНИЙ ====================
    
    /**
     * Использует умение на цели
     * 
     * @param bot бот
     * @param skillId ID умения
     * @param target цель
     * @return true если умение использовано
     */
    public boolean useSkill(EnhancedFakePlayer bot, int skillId, L2Character target) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            Skill skill = player.getKnownSkill(skillId);
            if (skill == null) {
                _log.warn("Bot " + bot.getBotId() + " doesn't know skill: " + skillId);
                return false;
            }
            
            // Проверка кулдауна
            if (player.isSkillDisabled(skill)) {
                _log.debug("Skill " + skillId + " is on cooldown for bot " + bot.getBotId());
                return false;
            }
            
            // Проверка MP
            if (player.getCurrentMp() < skill.getMpConsume()) {
                _log.debug("Not enough MP for skill " + skillId + " for bot " + bot.getBotId());
                return false;
            }
            
            // Установка цели
            if (target != null) {
                player.setTarget(target);
            }
            
            // Использование умения
            player.useMagic(skill, false, false);
            
            _log.debug("Bot " + bot.getBotId() + " used skill " + skillId + " on target: " + 
                (target != null ? target.getName() : "self"));
            return true;
            
        } catch (Exception e) {
            _log.error("Error using skill " + skillId + " with bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Использует умение на себе
     * 
     * @param bot бот
     * @param skillId ID умения
     * @return true если умение использовано
     */
    public boolean useSkillOnSelf(EnhancedFakePlayer bot, int skillId) {
        return useSkill(bot, skillId, null);
    }
    
    // ==================== ВЗАИМОДЕЙСТВИЕ С NPC ====================
    
    /**
     * Взаимодействует с NPC
     * 
     * @param bot бот
     * @param npc NPC
     * @return true если взаимодействие начато
     */
    public boolean interactWithNpc(EnhancedFakePlayer bot, L2NpcInstance npc) {
        if (bot == null || npc == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead() || npc.isDead()) {
                return false;
            }
            
            // Проверка дистанции
            double distance = player.getDistance(npc);
            if (distance > 150) { // Дистанция взаимодействия с NPC
                _log.debug("NPC too far for interaction: " + distance);
                return false;
            }
            
            // Установка цели
            player.setTarget(npc);
            
            // Начало взаимодействия
            player.getAI().setIntention(net.sf.l2j.gameserver.ai.CtrlIntention.INTERACT, npc);
            
            _log.debug("Bot " + bot.getBotId() + " interacting with NPC: " + npc.getName());
            return true;
            
        } catch (Exception e) {
            _log.error("Error interacting with NPC for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    // ==================== ПОИСК ЦЕЛЕЙ ====================
    
    /**
     * Находит ближайшего моба в радиусе
     * 
     * @param bot бот
     * @param radius радиус поиска
     * @return ближайший моб или null
     */
    public L2MonsterInstance findNearestMonster(EnhancedFakePlayer bot, int radius) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            L2MonsterInstance nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            
            List<L2Character> characters = L2World.getInstance().getVisibleObjects(player, L2Character.class);
            for (L2Character character : characters) {
                if (character instanceof L2MonsterInstance) {
                    L2MonsterInstance monster = (L2MonsterInstance) character;
                    if (monster.isDead() || !monster.isTargetable()) {
                        continue;
                    }
                    
                    double distance = player.getDistance(monster);
                    if (distance <= radius && distance < nearestDistance) {
                        nearest = monster;
                        nearestDistance = distance;
                    }
                }
            }
            
            return nearest;
            
        } catch (Exception e) {
            _log.error("Error finding nearest monster for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Находит ближайшего NPC в радиусе
     * 
     * @param bot бот
     * @param radius радиус поиска
     * @return ближайший NPC или null
     */
    public L2NpcInstance findNearestNpc(EnhancedFakePlayer bot, int radius) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            L2NpcInstance nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            
            List<L2Character> characters = L2World.getInstance().getVisibleObjects(player, L2Character.class);
            for (L2Character character : characters) {
                if (character instanceof L2NpcInstance) {
                    L2NpcInstance npc = (L2NpcInstance) character;
                    if (npc.isDead()) {
                        continue;
                    }
                    
                    double distance = player.getDistance(npc);
                    if (distance <= radius && distance < nearestDistance) {
                        nearest = npc;
                        nearestDistance = distance;
                    }
                }
            }
            
            return nearest;
            
        } catch (Exception e) {
            _log.error("Error finding nearest NPC for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    // ==================== УТИЛИТЫ ====================
    
    /**
     * Проверяет, может ли бот выполнить действие
     * 
     * @param bot бот
     * @return true если бот может действовать
     */
    public boolean canAct(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        return player != null && !player.isDead() && !player.isAlikeDead();
    }
    
    /**
     * Получает текущее состояние бота
     * 
     * @param bot бот
     * @return описание состояния
     */
    public String getBotStatus(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return "Player instance is null";
        }
        
        if (player.isDead()) {
            return "Dead";
        }
        
        if (player.isAlikeDead()) {
            return "Alike dead";
        }
        
        return "Active (HP: " + player.getCurrentHp() + "/" + player.getMaxHp() + 
               ", MP: " + player.getCurrentMp() + "/" + player.getMaxMp() + ")";
    }
}
