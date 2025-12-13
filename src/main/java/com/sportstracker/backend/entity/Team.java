package com.sportstracker.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamid;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 50)
    private String conference;
    
    @Column(name = "logo_url", length = 255)
    private String logoUrl;
    
    // Constructors
    public Team() {}
    
    public Team(String name, String conference, String logoUrl) {
        this.name = name;
        this.conference = conference;
        this.logoUrl = logoUrl;
    }
    
    // Getters and Setters
    public Integer getTeamid() {
        return teamid;
    }
    
    public void setTeamid(Integer teamid) {
        this.teamid = teamid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getConference() {
        return conference;
    }
    
    public void setConference(String conference) {
        this.conference = conference;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}