package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Game entity
 * Provides database operations for Games table
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    
    /**
     * Find all games where a team is playing at home
     * 
     * @param homeTeam Team ID
     * @return List of home games
     */
    List<Game> findByHomeTeam(Integer homeTeam);
    
    /**
     * Find all games where a team is playing away
     * 
     * @param awayTeam Team ID
     * @return List of away games
     */
    List<Game> findByAwayTeam(Integer awayTeam);
    
    /**
     * Find all games involving a specific team (home or away)
     * 
     * @param homeTeam Team ID to check as home team
     * @param awayTeam Team ID to check as away team (same as homeTeam)
     * @return List of all games involving the team
     */
    List<Game> findByHomeTeamOrAwayTeam(Integer homeTeam, Integer awayTeam);
    
    /**
     * Find games within a date range
     * 
     * @param start Start date/time
     * @param end End date/time
     * @return List of games in the date range
     */
    List<Game> findByGameDateBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find games after a specific date, ordered by date
     * 
     * @param date Date/time threshold
     * @return List of upcoming games
     */
    List<Game> findByGameDateAfterOrderByGameDateAsc(LocalDateTime date);
    
    /**
     * Find games before a specific date, ordered by date descending
     * 
     * @param date Date/time threshold
     * @return List of past games (most recent first)
     */
    List<Game> findByGameDateBeforeOrderByGameDateDesc(LocalDateTime date);
    
    /**
     * Find all games ordered by date
     * 
     * @return List of all games sorted by date
     */
    List<Game> findAllByOrderByGameDateDesc();
    
    /**
     * Find matchup between two specific teams
     * 
     * @param team1 First team ID
     * @param team2 Second team ID
     * @return List of games between these two teams
     */
    @Query("SELECT g FROM Game g WHERE " +
           "(g.homeTeam = :team1 AND g.awayTeam = :team2) OR " +
           "(g.homeTeam = :team2 AND g.awayTeam = :team1)")
    List<Game> findGamesBetweenTeams(@Param("team1") Integer team1, 
                                     @Param("team2") Integer team2);
}
