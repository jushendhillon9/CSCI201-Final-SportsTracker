package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Player entity
 * Provides database operations for Players table
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    
    /**
     * Find all players on a specific team
     * 
     * @param teamid Team ID
     * @return List of players on the team
     */
    List<Player> findByTeamid(Integer teamid);
    
    /**
     * Find all players at a specific position
     * 
     * @param position Position (e.g., "QB", "RB", "WR")
     * @return List of players at that position
     */
    List<Player> findByPosition(String position);
    
    /**
     * Find all players on a team at a specific position
     * 
     * @param teamid Team ID
     * @param position Position
     * @return List of players matching both criteria
     */
    List<Player> findByTeamidAndPosition(Integer teamid, String position);
    
    /**
     * Find players by name (partial match, case-insensitive)
     * 
     * @param name Player name or partial name
     * @return List of matching players
     */
    List<Player> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all players on a team, ordered by position
     * 
     * @param teamid Team ID
     * @return List of players sorted by position
     */
    List<Player> findByTeamidOrderByPosition(Integer teamid);
    
    /**
     * Count players on a team
     * 
     * @param teamid Team ID
     * @return Number of players on the team
     */
    long countByTeamid(Integer teamid);
}
