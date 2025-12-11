package com.example.cfbtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CFBDPlayerStatDTO {
    private Integer id;
    private String player;
    private String team;
    private Integer gameId;
    private Integer week;
    private Integer season;
    private String category;
    private String statType;
    private Double stat;

    @JsonProperty("playerId")
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPlayer() { return player; }
    public void setPlayer(String player) { this.player = player; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatType() { return statType; }
    public void setStatType(String statType) { this.statType = statType; }

    public Double getStat() { return stat; }
    public void setStat(Double stat) { this.stat = stat; }
}
