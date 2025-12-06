package com.example.cfbtracker.dto;

public class TopTeamByWinsItem {
    private Integer teamId;
    private String teamName;
    private Integer wins;

    public TopTeamByWinsItem() {}

    public TopTeamByWinsItem(Integer teamId, String teamName, Integer wins) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.wins = wins;
    }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Integer getWins() { return wins; }
    public void setWins(Integer wins) { this.wins = wins; }
}
