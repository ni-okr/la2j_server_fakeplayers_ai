package net.sf.l2j.botmanager.utils;

import java.util.logging.Level;

/**
 * Обертка над стандартным логгером Java
 * 
 * Предоставляет удобные методы для логирования в системе ботов.
 */
public class Logger {
    
    private final java.util.logging.Logger logger;
    
    private Logger(String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }
    
    public static Logger getLogger(String name) {
        return new Logger(name);
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getName());
    }
    
    public void debug(String message) {
        logger.fine(message);
    }
    
    public void info(String message) {
        logger.info(message);
    }
    
    public void warn(String message) {
        logger.warning(message);
    }
    
    public void error(String message) {
        logger.severe(message);
    }
    
    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
    
    public void trace(String message) {
        logger.finest(message);
    }
    
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }
    
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }
}