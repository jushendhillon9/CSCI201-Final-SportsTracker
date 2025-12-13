package com.sportstracker.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Projections")
public class Projection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proj_id")
    private Integer projId;
    
    @Column(nullable = false)
    private Integer playerid;
    
    @Column(name = "next_gameid", nullable = false)
    private Integer nextGameid;
    
    @Column(name = "projected_yards")
    private Float projectedYards;
    
    @Column(name = "projected_tds")
    private Float projectedTds;
    
    private Float confidence;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Projection() {}
    
    public Projection(Integer playerid, Integer nextGameid, Float projectedYards, 
                      Float projectedTds, Float confidence) {
        this.playerid = playerid;
        this.nextGameid = nextGameid;
        this.projectedYards = projectedYards;
        this.projectedTds = projectedTds;
        this.confidence = confidence;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getProjId() {
        return projId;
    }
    
    public void setProjId(Integer projId) {
        this.projId = projId;
    }
    
    public Integer getPlayerid() {
        return playerid;
    }
    
    public void setPlayerid(Integer playerid) {
        this.playerid = playerid;
    }
    
    public Integer getNextGameid() {
        return nextGameid;
    }
    
    public void setNextGameid(Integer nextGameid) {
        this.nextGameid = nextGameid;
    }
    
    public Float getProjectedYards() {
        return projectedYards;
    }
    
    public void setProjectedYards(Float projectedYards) {
        this.projectedYards = projectedYards;
    }
    
    public Float getProjectedTds() {
        return projectedTds;
    }
    
    public void setProjectedTds(Float projectedTds) {
        this.projectedTds = projectedTds;
    }
    
    public Float getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}