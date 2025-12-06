package com.sportstracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for multi-threaded async processing.
 * Defines thread pools for handling concurrent stats updates.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool executor for stats-related async tasks.
     * - Core pool: 4 threads always available
     * - Max pool: 8 threads under high load
     * - Queue: 100 tasks can wait in queue
     */
    @Bean(name = "statsTaskExecutor")
    public Executor statsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Stats-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Separate thread pool for live game updates.
     */
    @Bean(name = "liveGameExecutor")
    public Executor liveGameExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("LiveGame-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
