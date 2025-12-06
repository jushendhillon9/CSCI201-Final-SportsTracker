package com.example.cfbtracker.dto;

public class TeamsByConferenceItem {
    private String conference;
    private Long teamCount;

    public TeamsByConferenceItem() {}
    public TeamsByConferenceItem(String conference, Long teamCount) {
        this.conference = conference;
        this.teamCount = teamCount;
    }

    public String getConference() { return conference; }
    public void setConference(String conference) { this.conference = conference; }
    public Long getTeamCount() { return teamCount; }
    public void setTeamCount(Long teamCount) { this.teamCount = teamCount; }
}
