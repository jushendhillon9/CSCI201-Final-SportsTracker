package com.example.cfbtracker.services;

import com.example.cfbtracker.dto.ProjectionItem;
import com.example.cfbtracker.models.Game;
import com.example.cfbtracker.repositories.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.cfbtracker.services.CFBDService;
import com.example.cfbtracker.dto.CFBDGameDTO;
import com.example.cfbtracker.services.GameService;

@Service
public class ProjectionService {

    private final GameRepository gameRepository;
    private final CFBDService cfbdService;
    private final GameService gameService;

    public ProjectionService(GameRepository gameRepository, CFBDService cfbdService, GameService gameService) {
        this.gameRepository = gameRepository;
        this.cfbdService = cfbdService;
        this.gameService = gameService;
    }

    public List<ProjectionItem> getProjections(Integer gameId, Integer season, Integer week) {
        if (gameId != null) {
            gameService.ingestGames(cfbdService.fetchGames(null, null, null, null));
            return gameRepository.findById(gameId)
                    .map(game -> List.of(toProjection(game)))
                    .orElse(Collections.emptyList());
        }
        gameService.ingestGames(cfbdService.fetchGames(season, week, null, null));
        List<Game> games = gameRepository.searchGames(null, season, week, null);
        return games.stream().map(this::toProjection).collect(Collectors.toList());
    }

    private ProjectionItem toProjection(Game game) {
        ProjectionItem item = new ProjectionItem();
        item.setGameId(game.getGameid());
        item.setSeason(game.getSeason());
        item.setWeek(game.getWeek());
        item.setHomeTeam(game.getHomeTeam() != null ? game.getHomeTeam().getName() : null);
        item.setAwayTeam(game.getAwayTeam() != null ? game.getAwayTeam().getName() : null);
        // Simple placeholder model: use current scores as projections if present
        item.setProjectedHomeScore(game.getHome_score() != null ? game.getHome_score().doubleValue() : 0d);
        item.setProjectedAwayScore(game.getAway_score() != null ? game.getAway_score().doubleValue() : 0d);
        item.setHomeWinProbability(0.5d);
        item.setConfidence(0.5d);
        return item;
    }
}
