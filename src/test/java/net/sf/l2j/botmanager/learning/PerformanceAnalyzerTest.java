package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.List;

/**
 * Тесты для PerformanceAnalyzer.
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
@DisplayName("Performance Analyzer Tests")
public class PerformanceAnalyzerTest {
    
    private FeedbackCollector feedbackCollector;
    private PerformanceAnalyzer performanceAnalyzer;
    
    @BeforeEach
    void setUp() {
        feedbackCollector = FeedbackCollector.getInstance();
        performanceAnalyzer = PerformanceAnalyzer.getInstance(feedbackCollector);
    }
    
    @Test
    @DisplayName("Should create singleton instance")
    void testSingletonInstance() {
        assertNotNull(performanceAnalyzer, "PerformanceAnalyzer instance should not be null");
        
        PerformanceAnalyzer anotherInstance = PerformanceAnalyzer.getInstance(feedbackCollector);
        assertSame(performanceAnalyzer, anotherInstance, "Should return same instance");
    }
    
    @Test
    @DisplayName("Should analyze bot performance with no data")
    void testAnalyzeBotPerformanceWithNoData() {
        // Arrange
        int botId = 1;
        
        // Act
        PerformanceAnalyzer.AnalysisResult result = performanceAnalyzer.analyzeBotPerformance(botId);
        
        // Assert
        assertNotNull(result, "Analysis result should not be null");
        assertEquals(botId, result.getBotId(), "Bot ID should match");
        assertEquals(0.0, result.getActionScore(), 0.001, "Action score should be 0 with no data");
        assertEquals(0.0, result.getBehaviorScore(), 0.001, "Behavior score should be 0 with no data");
        assertEquals(0.0, result.getOverallScore(), 0.001, "Overall score should be 0 with no data");
        assertFalse(result.getSummary().isEmpty(), "Summary should not be empty");
    }
    
    @Test
    @DisplayName("Should analyze all bots performance")
    void testAnalyzeAllBotsPerformance() {
        // Arrange
        // Note: No data setup as methods require EnhancedFakePlayer
        
        // Act
        Map<Integer, PerformanceAnalyzer.AnalysisResult> results = performanceAnalyzer.analyzeAllBotsPerformance();
        
        // Assert
        assertNotNull(results, "Results should not be null");
        // With no data, results should be empty
        assertTrue(results.isEmpty(), "Should be empty with no data");
    }
    
    @Test
    @DisplayName("Should compare bot performance")
    void testCompareBotPerformance() {
        // Arrange
        int botId1 = 1;
        int botId2 = 2;
        
        // Act
        PerformanceAnalyzer.ComparisonResult result = performanceAnalyzer.compareBotPerformance(botId1, botId2);
        
        // Assert
        assertNotNull(result, "Comparison result should not be null");
        assertEquals(botId1, result.getBotId1(), "First bot ID should match");
        assertEquals(botId2, result.getBotId2(), "Second bot ID should match");
        assertNotNull(result.getResult1(), "First result should not be null");
        assertNotNull(result.getResult2(), "Second result should not be null");
        assertTrue(result.getPerformanceDifference() >= 0, "Performance difference should be non-negative");
    }
    
    @Test
    @DisplayName("Should get top performers")
    void testGetTopPerformers() {
        // Arrange
        // No data setup
        
        // Act
        List<PerformanceAnalyzer.BotPerformanceRanking> topPerformers = performanceAnalyzer.getTopPerformers(2);
        
        // Assert
        assertNotNull(topPerformers, "Top performers should not be null");
        assertTrue(topPerformers.isEmpty(), "Should be empty with no data");
    }
    
    @Test
    @DisplayName("Should get bots needing improvement")
    void testGetBotsNeedingImprovement() {
        // Arrange
        // No data setup
        
        // Act
        List<PerformanceAnalyzer.BotPerformanceRanking> needingImprovement = 
            performanceAnalyzer.getBotsNeedingImprovement(0.5);
        
        // Assert
        assertNotNull(needingImprovement, "Bots needing improvement should not be null");
        assertTrue(needingImprovement.isEmpty(), "Should be empty with no data");
    }
    
    @Test
    @DisplayName("Should get improvement recommendations")
    void testGetImprovementRecommendations() {
        // Arrange
        int botId = 1;
        
        // Act
        List<String> recommendations = performanceAnalyzer.getImprovementRecommendations(botId);
        
        // Assert
        assertNotNull(recommendations, "Recommendations should not be null");
        assertFalse(recommendations.isEmpty(), "Should have some recommendations");
    }
    
    @Test
    @DisplayName("Should clear cache")
    void testClearCache() {
        // Act
        performanceAnalyzer.clearCache();
        
        // Assert
        // Cache should be cleared (we can't directly test this, but it shouldn't throw)
        assertDoesNotThrow(() -> performanceAnalyzer.clearCache(), "Clear cache should not throw");
    }
    
    @Test
    @DisplayName("Should get analyzer stats")
    void testGetAnalyzerStats() {
        // Act
        String stats = performanceAnalyzer.getAnalyzerStats();
        
        // Assert
        assertNotNull(stats, "Stats should not be null");
        assertFalse(stats.isEmpty(), "Stats should not be empty");
        assertTrue(stats.contains("Performance Analyzer Statistics"), "Should contain header");
    }
    
    @Test
    @DisplayName("Should handle trend analysis")
    void testAnalyzePerformanceTrends() {
        // Arrange
        int botId = 1;
        int days = 7;
        
        // Act
        PerformanceAnalyzer.TrendAnalysis trend = performanceAnalyzer.analyzePerformanceTrends(botId, days);
        
        // Assert
        assertNotNull(trend, "Trend analysis should not be null");
        assertEquals(botId, trend.getBotId(), "Bot ID should match");
        assertEquals(days, trend.getDays(), "Days should match");
        assertNotNull(trend.getAnalysis(), "Analysis should not be null");
    }
}