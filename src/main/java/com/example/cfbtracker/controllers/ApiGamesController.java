package com.example.cfbtracker.controllers;

import com.example.cfbtracker.dto.GameListItem;
import com.example.cfbtracker.services.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class ApiGamesController {

    private final GameService gameService;

    public ApiGamesController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameListItem> getGames(
            @RequestParam(value = "season", required = false) Integer season,
            @RequestParam(value = "week", required = false) Integer week,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "status", required = false) String status
    ) {
        return gameService.getGames(season, week, teamId, status);
    }
}
