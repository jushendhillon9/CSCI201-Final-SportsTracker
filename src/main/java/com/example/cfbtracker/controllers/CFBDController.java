package com.example.cfbtracker.controllers;

import com.example.cfbtracker.dto.TeamDTO;
import com.example.cfbtracker.services.CFBDService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cfbd")
public class CFBDController {

    private final CFBDService cfbdService;

    public CFBDController(CFBDService cfbdService) {
        this.cfbdService = cfbdService;
    }

    @GetMapping("/teams")
    public TeamDTO[] getTeamsFromApi() {
        return cfbdService.fetchTeams();
    }
}
