package com.example.cfbtracker.controllers;

import com.example.cfbtracker.models.Player;
import com.example.cfbtracker.repositories.PlayerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayersController {

    private final PlayerRepository playerRepository;

    public PlayersController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
