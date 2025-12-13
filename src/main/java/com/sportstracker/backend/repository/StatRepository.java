package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Stat entity
 * Provides database operations for Stats table
 */
@Repository
public interface StatRepository extends JpaRepository<Stat, Integer> {
    
    /**
     * Find all stats for a specific player
     * 
     * @param playerid Player ID
     * @return List of all stats for the player
     */
    List<Stat> findByPlayerid(Integer playerid);
    
    /**
     * Find all stats from a specific game
     * 
     * @param gameid Game ID
     * @return List of all stats from the game
     */
    List<Stat> findByGameid(Integer gameid);
    
    /**
     * Find player stats for a specific season
     * 
     * @param playerid Player ID
     * @param season Season year
     * @return List of stats for player in that season
     */
    List<Stat> findByPlayeridAndSeason(Integer playerid, Integer season);
    
    /**
     * Find specific stat type for a player
     * 
     * @param playerid Player ID
     * @param statType Type of stat (e.g., "passing_yards", "rushing_yards")
     * @return List of that stat type for the player
     */
    List<Stat> findByPlayeridAndStatType(Integer playerid, String statType);
    
    /**
     * Find stats for a player in a specific game
     * 
     * @param playerid Player ID
     * @param gameid Game ID
     * @return List of stats for player in that game
     */
    List<Stat> findByPlayeridAndGameid(Integer playerid, Integer gameid);
    
    /**
     * Find all stats of a specific type in a season
     * 
     * @param season Season year
     * @param statType Type of stat
     * @return List of all stats of that type in the season
     */
    List<Stat> findBySeasonAndStatType(Integer season, String statType);
    
    /**
     * Get all stat types for a player
     * 
     * @param playerid Player ID
     * @return List of distinct stat types
     */
    @Query("SELECT DISTINCT s.statType FROM Stat s WHERE s.playerid = :playerid")
    List<String> findDistinctStatTypesByPlayerid(@Param("playerid") Integer playerid);
    
    /**
     * Calculate total stat value for a player in a season
     * 
     * @param playerid Player ID
     * @param season Season year
     * @param statType Type of stat
     * @return Sum of stat values
     */
    @Query("SELECT SUM(s.value) FROM Stat s WHERE s.playerid = :playerid " +
           "AND s.season = :season AND s.statType = :statType")
    Float calculateTotalStatForSeason(@Param("playerid") Integer playerid, 
                                      @Param("season") Integer season,
                                      @Param("statType") String statType);
    
    /**
     * Get average stat value for a player
     * 
     * @param playerid Player ID
     * @param statType Type of stat
     * @return Average value
     */
    @Query("SELECT AVG(s.value) FROM Stat s WHERE s.playerid = :playerid " +
           "AND s.statType = :statType")
    Float calculateAverageStatForPlayer(@Param("playerid") Integer playerid,
                                        @Param("statType") String statType);
}
