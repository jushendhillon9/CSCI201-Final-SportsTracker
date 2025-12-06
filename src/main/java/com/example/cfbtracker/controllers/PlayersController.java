package com.example.cfbtracker.controllers;

import org.springframework.web.bind.annotation.*;

import com.example.cfbtracker.dto.PlayerListItem;
import com.example.cfbtracker.services.PlayerService;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayersController {

    private final PlayerService playerService;

    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerListItem> getAllPlayers() {
        return playerService.getPlayers(null, null, null, false);
    }
}
