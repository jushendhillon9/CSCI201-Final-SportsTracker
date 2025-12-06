package com.sportstracker.backend.controller;

import com.sportstracker.backend.service.LiveStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for live stats API endpoints.
 * Demonstrates async/non-blocking request handling with multi-threading.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private LiveStatsService liveStatsService;

    /**
     * GET /api/stats/live
     * Returns all cached live game stats.
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> getLiveStats() {
        Map<String, Object> response = new HashMap<>();
        Collection<Map<String, Object>> games = liveStatsService.getAllLiveGames();
        
        response.put("success", true);
        response.put("games", games);
        response.put("count", games.size());
        response.put("source", "cache");
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/stats/game/{id}
     * Fetches stats for a specific game asynchronously.
     */
    @GetMapping("/game/{gameId}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getGameStats(@PathVariable int gameId) {
        return liveStatsService.fetchGameStatsAsync(gameId)
                .thenApply(gameData -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("game", gameData);
                    response.put("async", true);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * GET /api/stats/game/{id}/cached
     * Returns cached stats for a game (instant).
     */
    @GetMapping("/game/{gameId}/cached")
    public ResponseEntity<Map<String, Object>> getCachedGameStats(@PathVariable int gameId) {
        Map<String, Object> response = new HashMap<>();
        response.put("game", liveStatsService.getCachedGameStats(gameId));
        response.put("source", "cache");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/stats/games/fetch
     * Fetches multiple games in parallel using multi-threading.
     * Body: { "gameIds": [1, 2, 3, 4, 5] }
     */
    @PostMapping("/games/fetch")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> fetchMultipleGames(
            @RequestBody Map<String, List<Integer>> request) {
        
        List<Integer> gameIds = request.getOrDefault("gameIds", Arrays.asList(1, 2, 3));
        long startTime = System.currentTimeMillis();

        return liveStatsService.fetchMultipleGamesAsync(gameIds)
                .thenApply(games -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("games", games);
                    response.put("count", games.size());
                    response.put("parallelProcessing", true);
                    response.put("totalTimeMs", elapsed);
                    
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * GET /api/stats/cache
     * Returns cache statistics.
     */
    @GetMapping("/cache")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("cacheStats", liveStatsService.getCacheStats());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/stats/thread-info
     * Returns threading information.
     */
    @GetMapping("/thread-info")
    public ResponseEntity<Map<String, Object>> getThreadInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("currentThread", Thread.currentThread().getName());
        info.put("activeThreads", Thread.activeCount());
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        Map<String, String> threadPools = new HashMap<>();
        threadPools.put("statsTaskExecutor", "4-8 threads for stats processing");
        threadPools.put("liveGameExecutor", "2-4 threads for live updates");
        info.put("configuredThreadPools", threadPools);
        
        info.put("features", Arrays.asList(
            "@Async methods run on separate threads",
            "CompletableFuture for non-blocking I/O",
            "@Scheduled for background updates every 30s",
            "ConcurrentHashMap for thread-safe caching"
        ));
        
        return ResponseEntity.ok(info);
    }

    /**
     * GET /api/stats/demo
     * Demonstrates multi-threading by fetching 5 games in parallel.
     */
    @GetMapping("/demo")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> runDemo() {
        System.out.println("\n========================================");
        System.out.println("MULTI-THREADING DEMO STARTED");
        System.out.println("========================================\n");

        List<Integer> gameIds = Arrays.asList(1, 2, 3, 4, 5);
        long startTime = System.currentTimeMillis();

        return liveStatsService.fetchMultipleGamesAsync(gameIds)
                .thenApply(games -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    
                    System.out.println("\n========================================");
                    System.out.println("DEMO COMPLETED in " + elapsed + "ms");
                    System.out.println("========================================\n");

                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Multi-threading demo completed!");
                    response.put("gamesProcessed", games.size());
                    response.put("totalTimeMs", elapsed);
                    response.put("estimatedSequentialTimeMs", gameIds.size() * 500);
                    response.put("speedup", String.format("%.1fx faster", (gameIds.size() * 500.0) / elapsed));
                    response.put("games", games);
                    
                    return ResponseEntity.ok(response);
                });
    }
}
