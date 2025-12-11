package com.example.cfbtracker.dto;

public class GameListItem {
    private Integer gameId;
    private Integer season;
    private Integer week;
    private String date;
    private String homeTeam;
    private String awayTeam;
    private String homeLogo;
    private String awayLogo;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private String venue;
    private String quarter;
    private String timeRemaining;

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }
    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public String getHomeLogo() { return homeLogo; }
    public void setHomeLogo(String homeLogo) { this.homeLogo = homeLogo; }
    public String getAwayLogo() { return awayLogo; }
    public void setAwayLogo(String awayLogo) { this.awayLogo = awayLogo; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }
    public String getTimeRemaining() { return timeRemaining; }
    public void setTimeRemaining(String timeRemaining) { this.timeRemaining = timeRemaining; }
}
