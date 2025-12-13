package com.sportstracker.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gameid;
    
    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;
    
    @Column(name = "home_team", nullable = false)
    private Integer homeTeam;
    
    @Column(name = "away_team", nullable = false)
    private Integer awayTeam;
    
    @Column(columnDefinition = "TEXT")
    private String scores;
    
    @Column(name = "key_statistics", columnDefinition = "TEXT")
    private String keyStatistics;
    
    // Constructors
    public Game() {}
    
    public Game(LocalDateTime gameDate, Integer homeTeam, Integer awayTeam) {
        this.gameDate = gameDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }
    
    // Getters and Setters
    public Integer getGameid() {
        return gameid;
    }
    
    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }
    
    public LocalDateTime getGameDate() {
        return gameDate;
    }
    
    public void setGameDate(LocalDateTime gameDate) {
        this.gameDate = gameDate;
    }
    
    public Integer getHomeTeam() {
        return homeTeam;
    }
    
    public void setHomeTeam(Integer homeTeam) {
        this.homeTeam = homeTeam;
    }
    
    public Integer getAwayTeam() {
        return awayTeam;
    }
    
    public void setAwayTeam(Integer awayTeam) {
        this.awayTeam = awayTeam;
    }
    
    public String getScores() {
        return scores;
    }
    
    public void setScores(String scores) {
        this.scores = scores;
    }
    
    public String getKeyStatistics() {
        return keyStatistics;
    }
    
    public void setKeyStatistics(String keyStatistics) {
        this.keyStatistics = keyStatistics;
    }
}