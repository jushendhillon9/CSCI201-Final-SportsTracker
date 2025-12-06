package com.example.cfbtracker.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "Players")
public class Player {

    @Id
    private Integer playerid;

    private String name;
    private String position;
    private Integer jersey_number;
    private String year;
    private Integer passing_yards;
    private Integer rushing_yards;
    private Integer receiving_yards;
    private String photo_url;

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getPassing_yards() {
        return passing_yards;
    }

    public void setPassing_yards(Integer passing_yards) {
        this.passing_yards = passing_yards;
    }

    public Integer getRushing_yards() {
        return rushing_yards;
    }

    public void setRushing_yards(Integer rushing_yards) {
        this.rushing_yards = rushing_yards;
    }

    public Integer getReceiving_yards() {
        return receiving_yards;
    }

    public void setReceiving_yards(Integer receiving_yards) {
        this.receiving_yards = receiving_yards;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
