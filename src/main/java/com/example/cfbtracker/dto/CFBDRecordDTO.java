package com.example.cfbtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CFBDRecordDTO {
    private String team;
    private Integer teamId;
    private String conference;
    private Totals total;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Totals {
        private Integer wins;
        private Integer losses;

        public Integer getWins() { return wins; }
        public void setWins(Integer wins) { this.wins = wins; }
        public Integer getLosses() { return losses; }
        public void setLosses(Integer losses) { this.losses = losses; }
    }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    @JsonProperty("team_id")
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getConference() { return conference; }
    public void setConference(String conference) { this.conference = conference; }
    public Totals getTotal() { return total; }
    public void setTotal(Totals total) { this.total = total; }
}
