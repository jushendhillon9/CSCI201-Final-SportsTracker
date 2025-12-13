package com.sportstracker.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer playerid;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private Integer teamid;
    
    @Column(length = 20)
    private String position;
    
    // Constructors
    public Player() {}
    
    public Player(String name, Integer teamid, String position) {
        this.name = name;
        this.teamid = teamid;
        this.position = position;
    }
    
    // Getters and Setters
    public Integer getPlayerid() {
        return playerid;
    }
    
    public void setPlayerid(Integer playerid) {
        this.playerid = playerid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getTeamid() {
        return teamid;
    }
    
    public void setTeamid(Integer teamid) {
        this.teamid = teamid;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
}