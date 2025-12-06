package com.example.cfbtracker.dto;

import java.util.List;

public class DashboardResponse {
    private List<TeamsByConferenceItem> teamsByConference;
    private List<DashboardGameItem> upcomingGamesToday;
    private List<TopTeamByWinsItem> topTeamsByWins;
    private List<DashboardLiveGameItem> liveGames;

    public List<TeamsByConferenceItem> getTeamsByConference() { return teamsByConference; }
    public void setTeamsByConference(List<TeamsByConferenceItem> teamsByConference) { this.teamsByConference = teamsByConference; }

    public List<DashboardGameItem> getUpcomingGamesToday() { return upcomingGamesToday; }
    public void setUpcomingGamesToday(List<DashboardGameItem> upcomingGamesToday) { this.upcomingGamesToday = upcomingGamesToday; }

    public List<TopTeamByWinsItem> getTopTeamsByWins() { return topTeamsByWins; }
    public void setTopTeamsByWins(List<TopTeamByWinsItem> topTeamsByWins) { this.topTeamsByWins = topTeamsByWins; }

    public List<DashboardLiveGameItem> getLiveGames() { return liveGames; }
    public void setLiveGames(List<DashboardLiveGameItem> liveGames) { this.liveGames = liveGames; }
}
