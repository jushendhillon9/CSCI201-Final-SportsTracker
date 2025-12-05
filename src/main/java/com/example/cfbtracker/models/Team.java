package com.example.cfbtracker.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "Teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamid;

    private String name;
    private String conference;
    private String logo_url;
    
    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    private List<Player> players;
    public Integer getTeamid() { return teamid; }
    public String getName() { return name; }
    public String getConference() { return conference; }
    public String getLogo_url() { return logo_url; }
}
