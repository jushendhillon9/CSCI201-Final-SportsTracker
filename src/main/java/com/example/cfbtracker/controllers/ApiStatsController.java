package com.example.cfbtracker.controllers;

import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.services.StatService;
import com.example.cfbtracker.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class ApiStatsController {

    private final StatService statService;
    private final PlayerService playerService;

    public ApiStatsController(StatService statService, PlayerService playerService) {
        this.statService = statService;
        this.playerService = playerService;
    }

    @GetMapping
    public List<Stat> getStats(
            @RequestParam(required = false) Integer season,
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer playerId,
            @RequestParam(required = false) Integer gameId,
            @RequestParam(required = false, defaultValue = "false") boolean force
    ) {
        List<Stat> stats = statService.getStats(season, teamId, playerId, gameId);
        if (force || stats.isEmpty()) {
            playerService.fetchAndIngestPlayerStats(season, teamId, playerId);
            stats = statService.getStats(season, teamId, playerId, gameId);
        }
        return stats;
    }
}
