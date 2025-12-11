package com.example.cfbtracker.controllers;

import com.example.cfbtracker.dto.TeamListItem;
import com.example.cfbtracker.services.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class ApiTeamsController {

    private final TeamService teamService;

    public ApiTeamsController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamListItem> getTeams(
            @RequestParam(value = "conference", required = false) String conference,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "force", required = false, defaultValue = "false") boolean force
    ) {
        return teamService.getTeams(conference, search, force);
    }
}
