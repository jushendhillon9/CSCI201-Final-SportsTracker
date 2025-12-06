package com.example.cfbtracker.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "Teams")
public class Team {
    @Id
    private Integer teamid;

    private String name;
    private String conference;
    private String logo_url;
    private Integer wins;
    private Integer losses;
    private Integer ranking;
    
    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    private List<Player> players;
    public Integer getTeamid() { return teamid; }
    public String getName() { return name; }
    public String getConference() { return conference; }
    public String getLogo_url() { return logo_url; }
    public Integer getWins() { return wins; }
    public Integer getLosses() { return losses; }
    public Integer getRanking() { return ranking; }
    public List<Player> getPlayers() { return players; }
    public void setTeamid(Integer teamid) { this.teamid = teamid; }
    public void setName(String name) { this.name = name; }
    public void setConference(String conference) { this.conference = conference; }
    public void setLogo_url(String logo_url) { this.logo_url = logo_url; }
    public void setWins(Integer wins) { this.wins = wins; }
    public void setLosses(Integer losses) { this.losses = losses; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
