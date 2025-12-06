package com.example.cfbtracker.services;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.cfbtracker.models.Game;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.repositories.GameRepository;
import com.example.cfbtracker.repositories.TeamRepository;
import com.example.cfbtracker.dto.GameListItem;
import com.example.cfbtracker.dto.CFBDGameDTO;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;

import com.example.cfbtracker.services.CFBDService;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final CFBDService cfbdService;

    public GameService(GameRepository gameRepository, TeamRepository teamRepository, CFBDService cfbdService) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.cfbdService = cfbdService;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<GameListItem> getGames(Integer season, Integer week, Integer teamId, String status) {
        // simple refresh before serve; replace with last-updated check in production
        String teamName = null;
        if (teamId != null) {
            teamName = teamRepository.findById(teamId).map(Team::getName).orElse(null);
        }
        ingestGames(cfbdService.fetchGames(season, week, status, teamName));

        List<Game> games = gameRepository.searchGames(status, season, week, teamId);
        return games.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public int ingestGames(CFBDGameDTO[] apiGames) {
        if (apiGames == null) return 0;
        int count = 0;
        for (CFBDGameDTO dto : apiGames) {
            if (dto.getId() == null) continue;
            Game game = gameRepository.findById(dto.getId()).orElse(new Game());
            game.setGameid(dto.getId());
            game.setSeason(dto.getSeason());
            game.setWeek(dto.getWeek());
            if (dto.getStartDate() != null) {
                try {
                    game.setGame_date(OffsetDateTime.parse(dto.getStartDate()));
                } catch (Exception ignored) {}
            }

            Team home = null;
            if (dto.getHomeId() != null) {
                home = teamRepository.findById(dto.getHomeId()).orElseGet(() -> {
                    Team t = new Team();
                    t.setTeamid(dto.getHomeId());
                    t.setName(dto.getHomeTeam());
                    return teamRepository.save(t);
                });
            }
            Team away = null;
            if (dto.getAwayId() != null) {
                away = teamRepository.findById(dto.getAwayId()).orElseGet(() -> {
                    Team t = new Team();
                    t.setTeamid(dto.getAwayId());
                    t.setName(dto.getAwayTeam());
                    return teamRepository.save(t);
                });
            }

            game.setHomeTeam(home);
            game.setAwayTeam(away);
            game.setHome_score(dto.getHomePoints());
            game.setAway_score(dto.getAwayPoints());
            game.setVenue(dto.getVenue());
            game.setStatus(dto.getStatus());
            game.setQuarter(dto.getPeriod() != null ? "Q" + dto.getPeriod() : null);
            game.setTime_remaining(dto.getClock());

            gameRepository.save(game);
            count++;
        }
        return count;
    }

    private GameListItem toDto(Game game) {
        GameListItem dto = new GameListItem();
        dto.setGameId(game.getGameid());
        dto.setSeason(game.getSeason());
        dto.setWeek(game.getWeek());
        dto.setDate(game.getGame_date() != null ? game.getGame_date().toString() : null);
        dto.setHomeTeam(game.getHomeTeam() != null ? game.getHomeTeam().getName() : null);
        dto.setAwayTeam(game.getAwayTeam() != null ? game.getAwayTeam().getName() : null);
        dto.setHomeScore(game.getHome_score());
        dto.setAwayScore(game.getAway_score());
        dto.setStatus(game.getStatus());
        dto.setVenue(game.getVenue());
        dto.setQuarter(game.getQuarter());
        dto.setTimeRemaining(game.getTime_remaining());
        return dto;
    }
}
