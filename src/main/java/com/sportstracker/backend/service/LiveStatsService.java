package com.sportstracker.backend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling live stats with multi-threaded async processing.
 * Uses CompletableFuture for non-blocking operations.
 */
@Service
public class LiveStatsService {

    // Thread-safe cache for live game data
    private final Map<Integer, Map<String, Object>> liveGamesCache = new ConcurrentHashMap<>();
    
    // Track update timestamps
    private final Map<Integer, LocalDateTime> lastUpdated = new ConcurrentHashMap<>();

    /**
     * Asynchronously fetch stats for a specific game.
     * Runs on a separate thread from the statsTaskExecutor pool.
     */
    @Async("statsTaskExecutor")
    public CompletableFuture<Map<String, Object>> fetchGameStatsAsync(int gameId) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Fetching stats for game " + gameId);
        
        // Simulate network latency (external API call)
        simulateNetworkDelay(300, 800);

        // Build game data
        Map<String, Object> gameData = buildGameData(gameId);
        
        // Cache the result thread-safely
        liveGamesCache.put(gameId, gameData);
        lastUpdated.put(gameId, LocalDateTime.now());

        System.out.println("[" + threadName + "] Completed fetch for game " + gameId);
        return CompletableFuture.completedFuture(gameData);
    }

    /**
     * Fetch multiple games in parallel using multi-threading.
     */
    @Async("statsTaskExecutor")
    public CompletableFuture<List<Map<String, Object>>> fetchMultipleGamesAsync(List<Integer> gameIds) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Starting parallel fetch for " + gameIds.size() + " games");
        long startTime = System.currentTimeMillis();

        // Launch all fetches in parallel
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        for (Integer gameId : gameIds) {
            futures.add(fetchGameStatsAsync(gameId));
        }

        // Wait for all to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        List<Map<String, Object>> results = allFutures.thenApply(v ->
            futures.stream()
                .map(CompletableFuture::join)
                .toList()
        ).join();

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("[" + threadName + "] Parallel fetch completed in " + elapsed + "ms");

        return CompletableFuture.completedFuture(results);
    }

    /**
     * Scheduled background task - updates live games every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    public void scheduledLiveUpdate() {
        String threadName = Thread.currentThread().getName();
        System.out.println("\n=== [" + threadName + "] Scheduled live update at " + LocalDateTime.now() + " ===");
        
        List<Integer> activeGameIds = Arrays.asList(1, 2, 3);
        fetchMultipleGamesAsync(activeGameIds)
            .thenAccept(games -> System.out.println("=== Scheduled update: " + games.size() + " games updated ===\n"));
    }

    /**
     * Get cached stats for a game.
     */
    public Map<String, Object> getCachedGameStats(int gameId) {
        Map<String, Object> cached = liveGamesCache.get(gameId);
        if (cached == null) {
            Map<String, Object> notFound = new HashMap<>();
            notFound.put("error", "Game not found in cache");
            notFound.put("gameId", gameId);
            return notFound;
        }
        return cached;
    }

    /**
     * Get all cached live games.
     */
    public Collection<Map<String, Object>> getAllLiveGames() {
        return liveGamesCache.values();
    }

    /**
     * Get cache statistics.
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cachedGames", liveGamesCache.size());
        stats.put("gameIds", new ArrayList<>(liveGamesCache.keySet()));
        stats.put("lastUpdates", new HashMap<>(lastUpdated));
        return stats;
    }

    // ========== Helper Methods ==========

    private void simulateNetworkDelay(int minMs, int maxMs) {
        try {
            int delay = minMs + (int) (Math.random() * (maxMs - minMs));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Object> buildGameData(int gameId) {
        Map<String, Object> game = new HashMap<>();
        game.put("gameId", gameId);
        game.put("homeTeam", getTeamName(gameId, true));
        game.put("awayTeam", getTeamName(gameId, false));
        game.put("homeScore", randomScore());
        game.put("awayScore", randomScore());
        game.put("quarter", (int) (Math.random() * 4) + 1);
        game.put("timeRemaining", formatTime());
        game.put("status", "live");
        game.put("lastUpdated", LocalDateTime.now().toString());
        game.put("fetchedBy", Thread.currentThread().getName());
        return game;
    }

    private String getTeamName(int gameId, boolean home) {
        String[][] matchups = {
            {"Georgia", "Alabama"},
            {"Michigan", "Ohio State"},
            {"USC", "Oregon"},
            {"Texas", "Oklahoma"},
            {"Clemson", "Florida State"}
        };
        int idx = (gameId - 1) % matchups.length;
        return home ? matchups[idx][0] : matchups[idx][1];
    }

    private int randomScore() {
        return (int) (Math.random() * 35) + 7;
    }

    private String formatTime() {
        int minutes = (int) (Math.random() * 15);
        int seconds = (int) (Math.random() * 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
