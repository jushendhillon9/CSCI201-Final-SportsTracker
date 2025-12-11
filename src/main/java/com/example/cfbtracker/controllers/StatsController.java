package com.example.cfbtracker.controllers;

import org.springframework.web.bind.annotation.*;

import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.services.StatService;
import com.example.cfbtracker.services.GameService;
import com.example.cfbtracker.services.CFBDService;

import java.util.List;

// Use a unique bean name to avoid clashing with the backend StatsController.
@RestController("cfbStatsController")
@RequestMapping("/stats")
public class StatsController {

    private final StatService statService;
    private final GameService gameService;
    private final CFBDService cfbdService;

    public StatsController(StatService statService, GameService gameService, CFBDService cfbdService) {
        this.statService = statService;
        this.gameService = gameService;
        this.cfbdService = cfbdService;
    }

    @GetMapping
    public List<Stat> getAllStats() {
        // Placeholder: refresh games so stats could be derived in the future
        gameService.ingestGames(cfbdService.fetchGames(null, null, null, null));
        return statService.getAllStats();
    }
}
