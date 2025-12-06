package com.example.cfbtracker.controllers;

import com.example.cfbtracker.dto.ProjectionItem;
import com.example.cfbtracker.services.ProjectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projections")
public class ApiProjectionsController {

    private final ProjectionService projectionService;

    public ApiProjectionsController(ProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @GetMapping
    public List<ProjectionItem> getProjections(
            @RequestParam(value = "gameId", required = false) Integer gameId,
            @RequestParam(value = "week", required = false) Integer week,
            @RequestParam(value = "season", required = false) Integer season
    ) {
        return projectionService.getProjections(gameId, season, week);
    }
}
