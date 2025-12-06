package com.example.cfbtracker.controllers;

import com.example.cfbtracker.services.CFBDService;
import com.example.cfbtracker.services.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final CFBDService cfbdService;
    private final TeamService teamService;

    public SyncController(CFBDService cfbdService, TeamService teamService) {
        this.cfbdService = cfbdService;
        this.teamService = teamService;
    }

    @PostMapping("/teams")
    public String syncTeams() {
        var apiTeams = cfbdService.fetchTeams();
        int savedCount = teamService.ingestTeams(apiTeams);
        return "Synced " + savedCount + " teams.";
    }
}
