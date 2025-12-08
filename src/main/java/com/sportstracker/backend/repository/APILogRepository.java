package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.APILog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for APILog entity
 * Provides database operations for API_Log table
 */
@Repository
public interface APILogRepository extends JpaRepository<APILog, Integer> {
    
    /**
     * Find all logs from a specific API source
     * 
     * @param source API source name
     * @return List of logs from that source
     */
    List<APILog> findBySource(String source);
    
    /**
     * Find logs with a specific status
     * 
     * @param status Status (e.g., "success", "failed")
     * @return List of logs with that status
     */
    List<APILog> findByStatus(String status);
    
    /**
     * Find logs within a time range
     * 
     * @param start Start time
     * @param end End time
     * @return List of logs in that time range
     */
    List<APILog> findByFetchedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find failed API calls
     * 
     * @return List of failed logs
     */
    List<APILog> findByStatusOrderByFetchedAtDesc(String status);
    
    /**
     * Find recent logs for a source
     * 
     * @param source API source
     * @param limit Number of recent logs
     * @return Recent logs ordered by time descending
     */
    List<APILog> findTop10BySourceOrderByFetchedAtDesc(String source);
    
    /**
     * Count failed calls for a source
     * 
     * @param source API source
     * @param status Status to count
     * @return Number of logs matching criteria
     */
    long countBySourceAndStatus(String source, String status);
    
    /**
     * Delete old logs before a certain date
     * (Useful for cleanup)
     * 
     * @param date Cutoff date
     */
    void deleteByFetchedAtBefore(LocalDateTime date);
}
