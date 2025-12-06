package com.example.cfbtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CFBDRankingDTO {
    private Integer season;
    private Integer week;
    private List<Poll> polls;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Poll {
        private String poll;
        private List<RankEntry> ranks;

        public String getPoll() { return poll; }
        public void setPoll(String poll) { this.poll = poll; }
        public List<RankEntry> getRanks() { return ranks; }
        public void setRanks(List<RankEntry> ranks) { this.ranks = ranks; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankEntry {
        @JsonProperty("team")
        private String team;
        @JsonProperty("teamId")
        private Integer teamId;
        @JsonProperty("school")
        private String school;
        private Integer rank;

        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public Integer getTeamId() { return teamId; }
        public void setTeamId(Integer teamId) { this.teamId = teamId; }
        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
    }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public List<Poll> getPolls() { return polls; }
    public void setPolls(List<Poll> polls) { this.polls = polls; }
}
