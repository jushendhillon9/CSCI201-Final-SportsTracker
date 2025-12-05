package com.example.cfbtracker.controllers;

import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.repositories.TeamRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final TeamRepository teamRepository;

    public TeamsController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}
