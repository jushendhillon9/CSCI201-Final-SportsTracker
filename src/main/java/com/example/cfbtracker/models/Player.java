package com.example.cfbtracker.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "Players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer playerid;

    private String name;
    private String position;
    private Integer jersey_number;

    @ManyToOne
    @JoinColumn(name = "teamid", nullable = false)
    @JsonBackReference
    private Team team;

    public Player() {}  // REQUIRED by JPA

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getJersey_number() {
        return jersey_number;
    }

    public void setJersey_number(Integer jersey_number) {
        this.jersey_number = jersey_number;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
