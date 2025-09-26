package net.sf.l2j.botmanager.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для веб-сервера
 */
public class WebServerTest {
    
    private WebServer webServer;
    
    @BeforeEach
    void setUp() {
        webServer = WebServer.getInstance();
    }
    
    @Test
    @DisplayName("WebServer should be created successfully")
    void testWebServerCreation() {
        assertNotNull(webServer);
        assertFalse(webServer.isRunning());
    }
    
    @Test
    @DisplayName("WebServer should start and stop correctly")
    void testWebServerStartAndStop() {
        // Test starting server
        boolean started = webServer.start(8080);
        assertTrue(started);
        assertTrue(webServer.isRunning());
        assertEquals(8080, webServer.getPort());
        
        // Test stopping server
        webServer.stop();
        assertFalse(webServer.isRunning());
    }
    
    @Test
    @DisplayName("WebServer should handle multiple start attempts")
    void testWebServerMultipleStarts() {
        // Start server first time
        boolean started1 = webServer.start(8080);
        assertTrue(started1);
        assertTrue(webServer.isRunning());
        
        // Try to start again
        boolean started2 = webServer.start(8081);
        assertTrue(started2); // Should return true but not actually start
        assertTrue(webServer.isRunning());
        assertEquals(8080, webServer.getPort()); // Port should remain the same
        
        // Stop server
        webServer.stop();
        assertFalse(webServer.isRunning());
    }
    
    @Test
    @DisplayName("WebServer should handle multiple stop attempts")
    void testWebServerMultipleStops() {
        // Start server
        webServer.start(8080);
        assertTrue(webServer.isRunning());
        
        // Stop server first time
        webServer.stop();
        assertFalse(webServer.isRunning());
        
        // Try to stop again
        webServer.stop();
        assertFalse(webServer.isRunning());
    }
    
    @Test
    @DisplayName("WebServer should handle different ports")
    void testWebServerDifferentPorts() {
        // Test port 8080
        boolean started1 = webServer.start(8080);
        assertTrue(started1);
        assertEquals(8080, webServer.getPort());
        webServer.stop();
        
        // Test port 8081
        boolean started2 = webServer.start(8081);
        assertTrue(started2);
        assertEquals(8081, webServer.getPort());
        webServer.stop();
    }
    
    @Test
    @DisplayName("WebServer should be singleton")
    void testWebServerSingleton() {
        WebServer instance1 = WebServer.getInstance();
        WebServer instance2 = WebServer.getInstance();
        
        assertSame(instance1, instance2);
    }
}
