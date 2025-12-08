package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Team entity
 * Provides database operations for Teams table
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    
    /**
     * Find all teams in a specific conference
     * 
     * @param conference Conference name (e.g., "SEC", "Big Ten", "Pac-12")
     * @return List of teams in the conference
     */
    List<Team> findByConference(String conference);
    
    /**
     * Find a team by exact name
     * 
     * @param name Team name
     * @return Team object if found, null otherwise
     */
    Team findByName(String name);
    
    /**
     * Find teams whose name contains the search term (case-insensitive)
     * 
     * @param name Search term
     * @return List of matching teams
     */
    List<Team> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all teams ordered by name
     * 
     * @return List of all teams sorted alphabetically
     */
    List<Team> findAllByOrderByNameAsc();
    
    /**
     * Find all teams in a conference ordered by name
     * 
     * @param conference Conference name
     * @return List of teams in conference, sorted alphabetically
     */
    List<Team> findByConferenceOrderByNameAsc(String conference);
}
