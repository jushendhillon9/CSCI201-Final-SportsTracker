package com.example.cfbtracker.services;

import com.example.cfbtracker.dto.*;
import com.example.cfbtracker.models.Game;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.repositories.GameRepository;
import com.example.cfbtracker.repositories.TeamRepository;
import com.example.cfbtracker.services.CFBDService;
import com.example.cfbtracker.services.TeamService;
import com.example.cfbtracker.services.GameService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final CFBDService cfbdService;
    private final TeamService teamService;
    private final GameService gameService;

    public DashboardService(TeamRepository teamRepository, GameRepository gameRepository, CFBDService cfbdService, TeamService teamService, GameService gameService) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.cfbdService = cfbdService;
        this.teamService = teamService;
        this.gameService = gameService;
    }

    public DashboardResponse getDashboard() {
        // simple pull before serving; replace with last-updated check for production
        teamService.ingestTeams(cfbdService.fetchTeams());
        teamService.ingestRecords(null);
        gameService.ingestGames(cfbdService.fetchGames(null, null, null, null));

        DashboardResponse resp = new DashboardResponse();
        resp.setTeamsByConference(buildTeamsByConference());
        resp.setUpcomingGamesToday(buildUpcomingGamesToday());
        resp.setTopTeamsByWins(buildTopTeamsByWins());
        resp.setLiveGames(buildLiveGames());
        return resp;
    }

    private List<TeamsByConferenceItem> buildTeamsByConference() {
        Map<String, Long> grouped = teamRepository.findAll().stream()
                .collect(Collectors.groupingBy(t -> t.getConference() == null ? "Unknown" : t.getConference(), Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> new TeamsByConferenceItem(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(TeamsByConferenceItem::getConference))
                .collect(Collectors.toList());
    }

    private List<DashboardGameItem> buildUpcomingGamesToday() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return gameRepository.findAll().stream()
                .filter(g -> g.getGame_date() != null && g.getGame_date().toLocalDate().equals(today))
                .filter(g -> "scheduled".equalsIgnoreCase(g.getStatus()))
                .map(this::toDashboardGame)
                .collect(Collectors.toList());
    }

    private List<TopTeamByWinsItem> buildTopTeamsByWins() {
        return teamRepository.findAll().stream()
                .sorted(Comparator.comparing((Team t) -> t.getWins() == null ? 0 : t.getWins()).reversed())
                .limit(10)
                .map(t -> new TopTeamByWinsItem(t.getTeamid(), t.getName(), t.getWins()))
                .collect(Collectors.toList());
    }

    private List<DashboardLiveGameItem> buildLiveGames() {
        return gameRepository.findAll().stream()
                .filter(g -> "in_progress".equalsIgnoreCase(g.getStatus()))
                .map(this::toDashboardLive)
                .collect(Collectors.toList());
    }

    private DashboardGameItem toDashboardGame(Game game) {
        DashboardGameItem item = new DashboardGameItem();
        item.setGameId(game.getGameid());
        item.setSeason(game.getSeason());
        item.setWeek(game.getWeek());
        item.setDate(game.getGame_date() != null ? game.getGame_date().toString() : null);
        item.setHomeTeam(game.getHomeTeam() != null ? game.getHomeTeam().getName() : null);
        item.setAwayTeam(game.getAwayTeam() != null ? game.getAwayTeam().getName() : null);
        item.setVenue(game.getVenue());
        item.setStatus(game.getStatus());
        return item;
    }

    private DashboardLiveGameItem toDashboardLive(Game game) {
        DashboardLiveGameItem item = new DashboardLiveGameItem();
        item.setGameId(game.getGameid());
        item.setSeason(game.getSeason());
        item.setWeek(game.getWeek());
        item.setHomeTeam(game.getHomeTeam() != null ? game.getHomeTeam().getName() : null);
        item.setAwayTeam(game.getAwayTeam() != null ? game.getAwayTeam().getName() : null);
        item.setHomeScore(game.getHome_score());
        item.setAwayScore(game.getAway_score());
        item.setQuarter(game.getQuarter());
        item.setTimeRemaining(game.getTime_remaining());
        item.setStatus(game.getStatus());
        return item;
    }
}
