package com.example.cfbtracker.controllers;

import com.example.cfbtracker.services.CFBDService;
import com.example.cfbtracker.services.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingest")
public class TeamInjestController {

    private final CFBDService cfbdService;
    private final TeamService teamService;

    public TeamInjestController(CFBDService cfbdService, TeamService teamService) {
        this.cfbdService = cfbdService;
        this.teamService = teamService;
    }

    @PostMapping("/teams")
    public String ingestTeams() {
        var teams = cfbdService.fetchTeams();
        teamService.ingestTeams(teams);
        return "Teams imported successfully!";
    }
}
