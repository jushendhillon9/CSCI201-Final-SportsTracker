package com.example.cfbtracker.controllers;

import org.springframework.web.bind.annotation.*;

import com.example.cfbtracker.dto.GameListItem;
import com.example.cfbtracker.services.GameService;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GamesController {

    private final GameService gameService;

    public GamesController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameListItem> getAllGames() {
        return gameService.getGames(null, null, null, null);
    }
}
