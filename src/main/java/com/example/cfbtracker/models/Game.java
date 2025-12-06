package com.example.cfbtracker.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Games")
public class Game {

    @Id
    private Integer gameid;

    private Integer season;
    private Integer week;
    private java.time.OffsetDateTime game_date;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    private Integer home_score;
    private Integer away_score;
    private String venue;
    private String status;
    private String quarter;
    private String time_remaining;

    public Game() {}  // REQUIRED by Hibernate

    // === GETTERS AND SETTERS ===
    public Integer getGameid() { return gameid; }
    public void setGameid(Integer gameid) { this.gameid = gameid; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public java.time.OffsetDateTime getGame_date() { return game_date; }
    public void setGame_date(java.time.OffsetDateTime game_date) { this.game_date = game_date; }

    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }

    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }

    public Integer getHome_score() { return home_score; }
    public void setHome_score(Integer home_score) { this.home_score = home_score; }

    public Integer getAway_score() { return away_score; }
    public void setAway_score(Integer away_score) { this.away_score = away_score; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }

    public String getTime_remaining() { return time_remaining; }
    public void setTime_remaining(String time_remaining) { this.time_remaining = time_remaining; }
}
