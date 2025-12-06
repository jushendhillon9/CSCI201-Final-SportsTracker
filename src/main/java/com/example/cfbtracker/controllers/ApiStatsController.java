package com.example.cfbtracker.controllers;

import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.services.StatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class ApiStatsController {

    private final StatService statService;

    public ApiStatsController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    public List<Stat> getStats() {
        return statService.getAllStats();
    }
}
