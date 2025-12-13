package com.example.cfbtracker.controllers;

import com.example.cfbtracker.services.TeamService;
import com.example.cfbtracker.services.StatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final TeamService teamService;
    private final StatService statService;

    public SyncController(TeamService teamService, StatService statService) {
        this.teamService = teamService;
        this.statService = statService;
    }

    @PostMapping("/teams")
    public String syncTeamsFromCFBD() {
        int saved = teamService.syncTeamsFromCFBD();
        return "Synced " + saved + " teams from CFBD API.";
    }

    @PostMapping("/stats")
    public String syncStats(@RequestParam(required = false) Integer season,
                            @RequestParam(defaultValue = "false") boolean force,
                            @RequestParam(required = false) Integer teamId) {
        int saved = statService.ingestSeasonStats(season, force, teamId);
        String teamMsg = teamId != null ? " for team " + teamId : " for all teams";
        return "Ingested " + saved + " player stat rows" + (season != null ? " for season " + season : "") + teamMsg + (force ? " (force=true cleared existing rows)" : "");
    }

    // Allow simple GET from the browser to trigger the same ingest.
    @GetMapping("/stats")
    public String syncStatsGet(@RequestParam(required = false) Integer season,
                               @RequestParam(defaultValue = "false") boolean force,
                               @RequestParam(required = false) Integer teamId) {
        return syncStats(season, force, teamId);
    }
}
