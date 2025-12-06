package com.example.cfbtracker.dto;

public class PlayerListItem {
    private Integer playerId;
    private String name;
    private Integer teamId;
    private String teamName;
    private String position;
    private String year;
    private Integer passingYards;
    private Integer rushingYards;
    private Integer receivingYards;

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public Integer getPassingYards() { return passingYards; }
    public void setPassingYards(Integer passingYards) { this.passingYards = passingYards; }
    public Integer getRushingYards() { return rushingYards; }
    public void setRushingYards(Integer rushingYards) { this.rushingYards = rushingYards; }
    public Integer getReceivingYards() { return receivingYards; }
    public void setReceivingYards(Integer receivingYards) { this.receivingYards = receivingYards; }
}
