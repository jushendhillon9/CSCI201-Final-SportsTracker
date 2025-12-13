package com.example.cfbtracker.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Stats")
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statid;

    private Integer season;
    private String stat_type;
    private Double value;

    @ManyToOne
    @JoinColumn(name = "playerid", nullable = false)
    private Player player;

    @ManyToOne(optional = true)
    @JoinColumn(name = "gameid", nullable = true)
    private Game game;

    public Stat() {} // required

    // getters and setters
    public Integer getStatid() { return statid; }
    public void setStatid(Integer statid) { this.statid = statid; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }

    public String getStat_type() { return stat_type; }
    public void setStat_type(String stat_type) { this.stat_type = stat_type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
}
