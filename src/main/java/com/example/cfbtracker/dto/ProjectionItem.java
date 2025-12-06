package com.example.cfbtracker.dto;

public class ProjectionItem {
    private Integer gameId;
    private Integer season;
    private Integer week;
    private String homeTeam;
    private String awayTeam;
    private Double homeWinProbability;
    private Double projectedHomeScore;
    private Double projectedAwayScore;
    private Double confidence;

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }
    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public Double getHomeWinProbability() { return homeWinProbability; }
    public void setHomeWinProbability(Double homeWinProbability) { this.homeWinProbability = homeWinProbability; }
    public Double getProjectedHomeScore() { return projectedHomeScore; }
    public void setProjectedHomeScore(Double projectedHomeScore) { this.projectedHomeScore = projectedHomeScore; }
    public Double getProjectedAwayScore() { return projectedAwayScore; }
    public void setProjectedAwayScore(Double projectedAwayScore) { this.projectedAwayScore = projectedAwayScore; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
