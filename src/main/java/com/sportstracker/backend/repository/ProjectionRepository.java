package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.Projection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Projection entity
 * Provides database operations for Projections table
 */
@Repository
public interface ProjectionRepository extends JpaRepository<Projection, Integer> {
    
    /**
     * Find all projections for a specific player
     * 
     * @param playerid Player ID
     * @return List of projections for the player
     */
    List<Projection> findByPlayerid(Integer playerid);
    
    /**
     * Find projection for a player's specific upcoming game
     * 
     * @param playerid Player ID
     * @param nextGameid Game ID
     * @return Projection for that player-game combination
     */
    Optional<Projection> findByPlayeridAndNextGameid(Integer playerid, Integer nextGameid);
    
    /**
     * Find all projections for a specific game
     * 
     * @param nextGameid Game ID
     * @return List of all player projections for that game
     */
    List<Projection> findByNextGameid(Integer nextGameid);
    
    /**
     * Find projections with confidence above a threshold
     * 
     * @param minConfidence Minimum confidence level (0.0 to 1.0)
     * @return List of high-confidence projections
     */
    List<Projection> findByConfidenceGreaterThanEqual(Float minConfidence);
    
    /**
     * Find top projections by yards, ordered descending
     * 
     * @return List of projections sorted by projected yards
     */
    List<Projection> findAllByOrderByProjectedYardsDesc();
    
    /**
     * Find top projections by touchdowns, ordered descending
     * 
     * @return List of projections sorted by projected TDs
     */
    List<Projection> findAllByOrderByProjectedTdsDesc();
    
    /**
     * Find projections for a player ordered by confidence
     * 
     * @param playerid Player ID
     * @return List of player's projections sorted by confidence
     */
    List<Projection> findByPlayeridOrderByConfidenceDesc(Integer playerid);
    
    /**
     * Get highest projected yards for a specific game
     * 
     * @param nextGameid Game ID
     * @return Top projection by yards for that game
     */
    @Query("SELECT p FROM Projection p WHERE p.nextGameid = :gameid " +
           "ORDER BY p.projectedYards DESC")
    List<Projection> findTopProjectionsByYardsForGame(@Param("gameid") Integer nextGameid);
    
    /**
     * Check if projection exists for a player-game combination
     * 
     * @param playerid Player ID
     * @param nextGameid Game ID
     * @return true if projection exists
     */
    boolean existsByPlayeridAndNextGameid(Integer playerid, Integer nextGameid);
    
    /**
     * Delete old projections for a specific game
     * (Useful for updating projections)
     * 
     * @param nextGameid Game ID
     */
    void deleteByNextGameid(Integer nextGameid);
}
