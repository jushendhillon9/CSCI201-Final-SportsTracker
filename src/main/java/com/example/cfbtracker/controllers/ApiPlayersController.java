package com.example.cfbtracker.controllers;

import com.example.cfbtracker.dto.PlayerListItem;
import com.example.cfbtracker.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class ApiPlayersController {

    private final PlayerService playerService;

    public ApiPlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerListItem> getPlayers(
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "force", required = false, defaultValue = "false") boolean force
    ) {
        return playerService.getPlayers(teamId, position, search, force);
    }
}
