package com.example.cfbtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CFBDGameDTO {
    private Integer id;
    private Integer season;
    private Integer week;
    private String startDate;
    private Integer homeId;
    private Integer awayId;
    private String homeTeam;
    private String awayTeam;
    private Integer homePoints;
    private Integer awayPoints;
    private String venue;
    private String status;
    private Integer period;
    private String clock;
    private Boolean completed;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public Integer getHomeId() { return homeId; }
    public void setHomeId(Integer homeId) { this.homeId = homeId; }
    public Integer getAwayId() { return awayId; }
    public void setAwayId(Integer awayId) { this.awayId = awayId; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public Integer getHomePoints() { return homePoints; }
    public void setHomePoints(Integer homePoints) { this.homePoints = homePoints; }
    public Integer getAwayPoints() { return awayPoints; }
    public void setAwayPoints(Integer awayPoints) { this.awayPoints = awayPoints; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPeriod() { return period; }
    public void setPeriod(Integer period) { this.period = period; }
    public String getClock() { return clock; }
    public void setClock(String clock) { this.clock = clock; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
