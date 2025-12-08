package com.sportstracker.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statid;
    
    @Column(nullable = false)
    private Integer playerid;
    
    @Column(nullable = false)
    private Integer gameid;
    
    @Column(nullable = false)
    private Integer season;
    
    @Column(name = "stat_type", nullable = false, length = 50)
    private String statType;
    
    private Float value;
    
    // Constructors
    public Stat() {}
    
    public Stat(Integer playerid, Integer gameid, Integer season, String statType, Float value) {
        this.playerid = playerid;
        this.gameid = gameid;
        this.season = season;
        this.statType = statType;
        this.value = value;
    }
    
    // Getters and Setters
    public Integer getStatid() {
        return statid;
    }
    
    public void setStatid(Integer statid) {
        this.statid = statid;
    }
    
    public Integer getPlayerid() {
        return playerid;
    }
    
    public void setPlayerid(Integer playerid) {
        this.playerid = playerid;
    }
    
    public Integer getGameid() {
        return gameid;
    }
    
    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }
    
    public Integer getSeason() {
        return season;
    }
    
    public void setSeason(Integer season) {
        this.season = season;
    }
    
    public String getStatType() {
        return statType;
    }
    
    public void setStatType(String statType) {
        this.statType = statType;
    }
    
    public Float getValue() {
        return value;
    }
    
    public void setValue(Float value) {
        this.value = value;
    }
}